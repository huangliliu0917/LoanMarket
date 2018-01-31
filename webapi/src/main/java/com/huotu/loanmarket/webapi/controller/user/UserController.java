/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2016-2018. All rights reserved.
 */

package com.huotu.loanmarket.webapi.controller.user;

import com.huotu.loanmarket.common.Constant;
import com.huotu.loanmarket.common.utils.ApiResult;
import com.huotu.loanmarket.common.utils.RegexUtils;
import com.huotu.loanmarket.common.utils.RequestUtils;
import com.huotu.loanmarket.common.utils.StringUtilsExt;
import com.huotu.loanmarket.service.entity.user.User;
import com.huotu.loanmarket.service.enums.AppCode;
import com.huotu.loanmarket.service.enums.UserResultCode;
import com.huotu.loanmarket.service.exceptions.ErrorMessageException;
import com.huotu.loanmarket.service.model.user.UserInfoVo;
import com.huotu.loanmarket.service.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author guomw
 * @date 30/01/2018
 */
@Controller
@RequestMapping(value = "/api/user", method = RequestMethod.POST)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据手机号、密码实现用户登录
     * 密码跟短信验证码二选一 必填
     *
     * @param username   用户名
     * @param input      密码(md5)或验证码
     * @param loginType  登录方式[0:密码登录 1:验证码登录]
     * @param request
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ApiResult login(@RequestParam String username,
                           @RequestParam String input,
                           @RequestParam(required = false, defaultValue = "0") int loginType,
                           HttpServletRequest request) {

        if (!RegexUtils.checkMobile(username)) {
            return ApiResult.resultWith(UserResultCode.CODE1);
        }
        User user;
        //0:密码登录
        if (loginType == 0) {
            if (StringUtils.isEmpty(input) || input.length() != Constant.PASS_WORD_LENGTH) {
                return ApiResult.resultWith(UserResultCode.CODE2);
            }
        } else {
            if (StringUtils.isEmpty(input) || input.length() != Constant.VERIFY_CODE_LENGTH) {
                return ApiResult.resultWith(UserResultCode.CODE9);
            }
        }

        try {
            user = userService.login(username, input, loginType, request);
        } catch (ErrorMessageException e) {
            return ApiResult.resultWith(e.code, e.getMessage());
        }
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUserId(user.getUserId());
        userInfoVo.setUserName(user.getUserName());
        userInfoVo.setUserToken(user.getUserToken());
        userInfoVo.setHeadimg(user.getHeadimg());
        userInfoVo.setAuthStatus(user.getAuthStatus().getCode());
        return ApiResult.resultWith(AppCode.SUCCESS, userInfoVo);

    }


    /**
     * 根据手机号、密码和验证码注册用户
     *
     * @param username   用户名
     * @param password   密码(md5)
     * @param verifyCode 短信验证码
     * @param inviter    邀请者Id
     * @param request
     * @return
     */
    @RequestMapping("/register")
    @ResponseBody
    public ApiResult register(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String verifyCode,
                              @RequestParam(required = false, defaultValue = "0") Long inviter,
                              HttpServletRequest request) {


        if (!RegexUtils.checkMobile(username)) {
            return ApiResult.resultWith(UserResultCode.CODE1);
        }

        if (StringUtils.isEmpty(password) || password.length() != Constant.PASS_WORD_LENGTH) {
            return ApiResult.resultWith(UserResultCode.CODE2);
        }

        if (StringUtils.isEmpty(verifyCode) || verifyCode.length() != Constant.VERIFY_CODE_LENGTH) {
            return ApiResult.resultWith(UserResultCode.CODE9);
        }

        User user = setUserHeaderInfo(request);
        user.setUserName(username);
        user.setPassword(password);
        user.setMerchantId(Constant.MERCHANT_ID);
        user.setRegTime(LocalDateTime.now());
        user.setLastLoginIp(StringUtilsExt.getClientIp(request));
        user.setChannelId(RequestUtils.getHeader(request, Constant.APP_CHANNELID_KEY, "default"));
        user.setInviterId(inviter);
        try {
            return ApiResult.resultWith(AppCode.SUCCESS, userService.register(user, verifyCode));
        } catch (ErrorMessageException e) {
            return ApiResult.resultWith(e.code, e.getMessage(), null);
        }


    }

    /**
     * 设置用户header数据
     *
     * @param request
     * @return
     */
    private User setUserHeaderInfo(HttpServletRequest request) {
        User user = new User();
        user.setAppVersion(RequestUtils.getHeader(request, Constant.APP_VERSION_KEY));
        user.setOsType(RequestUtils.getHeader(request, Constant.APP_SYSTEM_TYPE_KEY));
        user.setOsVersion(RequestUtils.getHeader(request, Constant.APP_OS_VERSION_KEY));
        user.setMobileType(RequestUtils.getHeader(request, Constant.APP_MOBILE_TYPE_KEY));
        user.setEquipmentId(RequestUtils.getHeader(request, Constant.APP_EQUIPMENT_NUMBER_KEY));
        return user;
    }

    /**
     * 用户修改（忘记）密码接口
     *
     * @param username
     * @param newPassword
     * @param verifyCode
     * @return
     */
    @RequestMapping("/updatePassword")
    @ResponseBody
    public ApiResult updatePassword(String username,
            String newPassword, String verifyCode) {

        if (!RegexUtils.checkMobile(username)) {
            return ApiResult.resultWith(UserResultCode.CODE1);
        }

        //判断密码长度
        if (StringUtils.isEmpty(newPassword) || newPassword.length() != Constant.PASS_WORD_LENGTH) {
            return ApiResult.resultWith(UserResultCode.CODE2);
        }
        //判断验证码长度
        if (StringUtils.isEmpty(verifyCode) || verifyCode.length() != Constant.VERIFY_CODE_LENGTH) {
            return ApiResult.resultWith(UserResultCode.CODE9);
        }
        try {
            userService.updatePassword(username, newPassword, verifyCode);
            return ApiResult.resultWith(AppCode.SUCCESS);
        } catch (ErrorMessageException e) {
            return ApiResult.resultWith(e.code, e.getMessage());
        }
    }
}
