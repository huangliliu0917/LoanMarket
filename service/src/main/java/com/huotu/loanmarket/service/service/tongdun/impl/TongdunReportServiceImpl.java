package com.huotu.loanmarket.service.service.tongdun.impl;

import com.huotu.loanmarket.common.utils.ApiResult;
import com.huotu.loanmarket.service.config.LoanMarkConfigProvider;
import com.huotu.loanmarket.service.entity.order.Order;
import com.huotu.loanmarket.service.entity.tongdun.TongdunRequestLog;
import com.huotu.loanmarket.service.entity.user.User;
import com.huotu.loanmarket.service.enums.AppCode;
import com.huotu.loanmarket.service.enums.OrderEnum;
import com.huotu.loanmarket.service.enums.TongdunEnum;
import com.huotu.loanmarket.service.enums.UserResultCode;
import com.huotu.loanmarket.service.model.tongdun.*;
import com.huotu.loanmarket.service.model.tongdun.report.ReportDetailVo;
import com.huotu.loanmarket.service.model.tongdun.report.RiskItem;
import com.huotu.loanmarket.service.repository.order.OrderRepository;
import com.huotu.loanmarket.service.service.tongdun.PreLoanRiskService;
import com.huotu.loanmarket.service.service.tongdun.TongdunReportService;
import com.huotu.loanmarket.service.service.tongdun.TongdunRequestLogService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wm
 */
@Service
public class TongdunReportServiceImpl implements TongdunReportService {
    private static Log log = LogFactory.getLog(TongdunReportServiceImpl.class);
    @Autowired
    private TongdunRequestLogService tongdunRequestLogService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private LoanMarkConfigProvider loanMarkConfigProvider;
    @Autowired
    private PreLoanRiskService preLoanRiskService;

    @Override
    public ApiResult getRiskReport(String orderId) {
        try {
            //1、检查订单
            Order orderInfo = orderRepository.findOne(orderId);
            if (orderInfo == null) {
                return ApiResult.resultWith(AppCode.ERROR, "订单不存在");
            }
            if (!orderInfo.getPayStatus().equals(OrderEnum.PayStatus.PAY_SUCCESS)) {
                return ApiResult.resultWith(AppCode.ERROR, "订单非支付状态");
            }
            User userInfo = orderInfo.getUser();
            Integer merchantId = userInfo.getMerchantId();
            TongdunRequestLog requestLog = tongdunRequestLogService.findByOrderId(orderId);
            //2、同盾报告信息检查
            //--正在生成中，其他请求进入直接返回
            if (requestLog != null && requestLog.getState() == TongdunEnum.ReportStatus.BUILDING.getCode()) {
                return ApiResult.resultWith(UserResultCode.REPORT_TONGDUN_BUILDING);
            }
            //--同盾报告不存在或者是失败的，要生成
            boolean needBuild = requestLog == null || requestLog.getState() == TongdunEnum.ReportStatus.FAIL.getCode();
            //--如果超过指定时间已过期的，重新生成
            if (!needBuild) {
                int overdueDay = 180;
                Duration duration = Duration.between(requestLog.getApplyTime(), LocalDateTime.now());
                if (duration.getSeconds() > overdueDay * 86400) {
                    needBuild = true;
                }
            }
            if (needBuild) {
                //region 生成同盾报告
                TongdunApiConfig tdConfig = loanMarkConfigProvider.getTongdunApiConfig(merchantId);
                //---同盾参数：关键信息
                PreLoanSubmitRequest submitRequest = new PreLoanSubmitRequest();
                submitRequest.setMobile(orderInfo.getMobile());
                submitRequest.setIdNumber(orderInfo.getIdCardNo());
                submitRequest.setName(orderInfo.getRealName());

                //---报告请求结果实体准备，立即入库，防止获取详情前多次提交
                TongdunReviewResult reviewResult = new TongdunReviewResult();
                reviewResult.setUserId(userInfo.getUserId());
                reviewResult.setMerchantId(new Long(merchantId));
                reviewResult.setState(TongdunEnum.ReportStatus.BUILDING.getCode());
                reviewResult.setName(submitRequest.getName());
                reviewResult.setMobile(submitRequest.getMobile());
                reviewResult.setIdNumber(submitRequest.getIdNumber());
                PreLoanSubmitResponse submitResponse = preLoanRiskService.apply(tdConfig, submitRequest);
                reviewResult.setSubmitResponse(submitResponse);
                reviewResult.setOrderId(orderId);
                if (!submitResponse.getSuccess()) {
                    reviewResult.setErrorDesc(submitResponse.getReason_desc());
                    reviewResult.setState(TongdunEnum.ReportStatus.FAIL.getCode());
                    tongdunRequestLogService.save(reviewResult);
                    return ApiResult.resultWith(UserResultCode.REPORT_TONGDUN_BUILD_ERROR);
                } else {
                    tongdunRequestLogService.save(reviewResult);
                }
                try {
                    //---获取报告详情
                    Thread.sleep(5500);
                    String responseJson = preLoanRiskService.queryReport(tdConfig, submitResponse.getReport_id());
                    PreLoanQueryResponse queryResponse = preLoanRiskService.parseReport(responseJson);
                    reviewResult.setQueryResponse(queryResponse);
                    reviewResult.setState(TongdunEnum.ReportStatus.SUCCESS.getCode());
                } catch (Exception e) {
                    reviewResult.setState(TongdunEnum.ReportStatus.FAIL.getCode());
                    reviewResult.setErrorDesc(e.getMessage());
                }
                //---再次保存
                requestLog = tongdunRequestLogService.save(reviewResult);
                if (!reviewResult.getState().equals(TongdunEnum.ReportStatus.SUCCESS.getCode())) {
                    return ApiResult.resultWith(UserResultCode.REPORT_TONGDUN_BUILD_ERROR);
                }
                //endregion
            }
            //3、转换报告为前台viewModel
            ReportDetailVo reportDetailVo = tongdunRequestLogService.convertRequestLogToReport(requestLog);
            ApiRiskControlVo riskControlVo = this.analyzeRiskControl(reportDetailVo, new Long(merchantId));
            return ApiResult.resultWith(AppCode.SUCCESS, riskControlVo);
        } catch (Exception ex) {
            log.error("[getRiskReport] throw exception, details: " + ex.getMessage() + "|" + Arrays.toString(ex.getStackTrace()));
            return ApiResult.resultWith(AppCode.ERROR.getCode(), "getRiskReport异常");
        }
    }

    //region 风控分析相关的方法

    /**
     * 分析同盾风险信息
     *
     * @param report
     * @return
     */
    private ApiRiskControlVo analyzeRiskControl(ReportDetailVo report, Long merchantId) {
        ApiRiskControlVo riskControlVo = new ApiRiskControlVo();
        List<RiskItem> riskItemAllList = new ArrayList<>();
        report.getRiskItems().forEach((k, v) -> riskItemAllList.addAll(v));

        TongdunRiskRuleIdConfig tongdunRiskRuleIdConfig = loanMarkConfigProvider.getTongdunRiskRuleIdConfig(merchantId.intValue());
        riskControlVo.setCourtLoseFaith(this.checkRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getCourtCaseRuleIds()));
        riskControlVo.setCourtExecution(this.checkRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getCourtExecutionRuleIds()));
        riskControlVo.setCourtCase(this.checkRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getCourtCaseRuleIds()));
        riskControlVo.setCriminalWanted(this.checkRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getCriminalWantedRuleIds()));
        riskControlVo.setDiscredit(this.checkRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getDiscreditRuleIds()));
        riskControlVo.setPlatformApply7(this.getRiskPlatformCount(this.findRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getPlatformApply7RuleIds())));
        riskControlVo.setPlatformApply30(this.getRiskPlatformCount(this.findRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getPlatformApply30RuleIds())));
        riskControlVo.setPlatformApply90(this.getRiskPlatformCount(this.findRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getPlatformApply90RuleIds())));
        riskControlVo.setFuzzyNameHits(this.getRiskFuzzyName(this.findRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getFuzzyNameHitsRuleIds())));
        riskControlVo.setPhoneNumberRisks(this.getRiskPhoneNumber(this.findRiskItemById(riskItemAllList, tongdunRiskRuleIdConfig.getPhoneNumberRisksRuleIds())));
        riskControlVo.setFinalScore(report.getFinalScore());
        riskControlVo.setFinalDecision(report.getFinalDecision());
        return riskControlVo;
    }

    /**
     * 检查指定的风险规则id是否存在
     *
     * @param riskItemList
     * @param itemIdList
     * @return
     */
    private int checkRiskItemById(List<RiskItem> riskItemList, List<Integer> itemIdList) {
        return riskItemList.stream().filter(x -> itemIdList.contains(x.getItem_id().intValue())).count() > 0 ? 1 : 0;
    }

    /**
     * 找到风险项
     *
     * @param riskItemList
     * @param itemIdList
     * @return
     */
    private List<RiskItem> findRiskItemById(List<RiskItem> riskItemList, List<Integer> itemIdList) {
        return riskItemList.stream().filter(x -> itemIdList.contains(x.getItem_id().intValue())).collect(Collectors.toList());
    }

    /**
     * 得到多平台数量
     *
     * @param riskItems
     * @return
     */
    private int getRiskPlatformCount(List<RiskItem> riskItems) {
        int num = 0;
        for (RiskItem item : riskItems) {
            if (item.getItem_detail().getPlatform_count() != null) {
                num += item.getItem_detail().getPlatform_count();
            }
        }
        return num;
    }

    /**
     * 手机号风险
     *
     * @param riskItems
     * @return
     */
    private List<String> getRiskPhoneNumber(List<RiskItem> riskItems) {
        return riskItems.stream().map(RiskItem::getItem_name).collect(Collectors.toList());
    }

    /**
     * 模糊名单
     *
     * @param riskItems
     * @return
     */
    private List<String> getRiskFuzzyName(List<RiskItem> riskItems) {
        return riskItems.stream().map(RiskItem::getItem_name).collect(Collectors.toList());
    }
    //endregion
}