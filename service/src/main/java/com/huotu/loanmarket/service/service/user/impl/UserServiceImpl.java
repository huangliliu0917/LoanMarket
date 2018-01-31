package com.huotu.loanmarket.service.service.user.impl;

import com.huotu.loanmarket.common.Constant;
import com.huotu.loanmarket.common.utils.RandomUtils;
import com.huotu.loanmarket.common.utils.RequestUtils;
import com.huotu.loanmarket.common.utils.StringUtilsExt;
import com.huotu.loanmarket.service.entity.system.SmsTemple;
import com.huotu.loanmarket.service.entity.user.Invite;
import com.huotu.loanmarket.service.entity.user.User;
import com.huotu.loanmarket.service.entity.user.VerifyCode;
import com.huotu.loanmarket.service.enums.UserAuthorizedStatusEnums;
import com.huotu.loanmarket.service.enums.UserResultCode;
import com.huotu.loanmarket.service.exceptions.ErrorMessageException;
import com.huotu.loanmarket.service.model.PageListView;
import com.huotu.loanmarket.service.model.user.UserInviteVo;
import com.huotu.loanmarket.service.repository.system.VerifyCodeRepository;
import com.huotu.loanmarket.service.repository.user.InviteRepository;
import com.huotu.loanmarket.service.repository.user.UserRepository;
import com.huotu.loanmarket.service.service.system.SmsTemplateService;
import com.huotu.loanmarket.service.service.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hxh
 * @Date 2018/1/30 15:58
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Log log = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerifyCodeRepository verifyCodeRepository;
    @Autowired
    private SmsTemplateService verifyCodeService;
    @Autowired
    private InviteRepository inviteRepository;


    @Override
    public User findByMerchantIdAndUserId(Integer merchantId, Long userId) {
        return userRepository.findByMerchantIdAndUserId(merchantId, userId);
    }

    /**
     * 注册
     *
     * @param user
     * @param verifyCode
     * @return
     * @throws ErrorMessageException
     */
    @Override
    public User register(User user, String verifyCode) throws ErrorMessageException {
        /**
         * 验证验证码是否有效
         */
        if (verifyCodeService.checkVerifyCode(user.getUserName(), verifyCode)) {

            //先看看用户是否已经存在
            if (userRepository.countByUserName(user.getUserName()) > 0) {
                throw new ErrorMessageException(UserResultCode.CODE3);
            }


            user.setUserToken(RandomUtils.randomString());
            String inviterName = "";
            //判断当前注册人身份是否是借款人，且判断是否存在邀请人
            if (user.getInviterId() != null
                    && user.getInviterId() > 0) {
                inviterName = userRepository.findUserNameByUserId(user.getInviterId());
            }
            user = userRepository.saveAndFlush(user);
            if (!StringUtils.isEmpty(inviterName)) {
                //添加邀请
                addInviteLog(user.getUserId(), user.getUserName(), user.getRealName(), user.getInviterId(), inviterName);
            }


            /**
             * 设置验证码使用状态
             */
            VerifyCode code = verifyCodeRepository.findByMobileAndMerchantId(user.getUserName());
            code.setUseStatus(true);

            return user;
        }
        throw new ErrorMessageException(UserResultCode.CODE9);
    }

    /**
     * 登录
     *
     * @param loginName
     * @param loginPassword 密码(md5)或验证码
     * @param loginType     登录方式[0:密码登录 1:验证码登录]
     * @param request
     * @return
     * @throws ErrorMessageException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User login(String loginName, String loginPassword,
                      @RequestParam(required = false, defaultValue = "0") int loginType,
                      @RequestParam(required = false) HttpServletRequest request) throws ErrorMessageException {

        if (loginType == 1) {
            if (!verifyCodeService.checkVerifyCode(loginName, loginPassword)) {
                throw new ErrorMessageException(UserResultCode.CODE9);
            }
        }

        User userInfo = userRepository.findByUserName(loginName);
        if (userInfo == null) {
            throw new ErrorMessageException(UserResultCode.CODE5);
        }

        //是否删除 是否锁定
        if (userInfo.isDisabled() || userInfo.isLocked()) {
            throw new ErrorMessageException(UserResultCode.CODE7);
        }

        if (loginType == 0) {
            if (!userInfo.getPassword().equalsIgnoreCase(loginPassword)) {
                throw new ErrorMessageException(UserResultCode.CODE6);
            }
        } else {
            VerifyCode code = verifyCodeRepository.findByMobileAndMerchantId(loginName);
            code.setUseStatus(true);
        }
        userInfo.setUserToken(RandomUtils.randomString());
        userInfo.setLastLoginTime(LocalDateTime.now());
        userInfo.setLoginCount(userInfo.getLoginCount() + 1);
        if (request != null) {
            userInfo.setLastLoginIp(StringUtilsExt.getClientIp(request));
            userInfo.setAppVersion(RequestUtils.getHeader(request, Constant.APP_VERSION_KEY));
            userInfo.setOsType(RequestUtils.getHeader(request, Constant.APP_SYSTEM_TYPE_KEY));
            userInfo.setOsVersion(RequestUtils.getHeader(request, Constant.APP_OS_VERSION_KEY));
            userInfo.setMobileType(RequestUtils.getHeader(request, Constant.APP_MOBILE_TYPE_KEY));
            userInfo.setEquipmentId(RequestUtils.getHeader(request, Constant.APP_EQUIPMENT_NUMBER_KEY));
        }
        return userInfo;
    }


    /**
     * 修改最后登录时间
     *
     * @param userId
     */
    @Override
    public void updateLastLoginTime(long userId) {
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
    }

    /**
     * 修改、忘记密码
     *
     * @param username
     * @param newPassword
     * @param verifyCode
     * @return
     * @throws ErrorMessageException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(String username, String newPassword, String verifyCode) throws ErrorMessageException {
        /**
         * 检查验证码是否有效
         */
        if (!verifyCodeService.checkVerifyCode(username, verifyCode)) {
            throw new ErrorMessageException(UserResultCode.CODE9);
        }

        User myUser = userRepository.findByUserName(username);

        if (myUser == null) {
            throw new ErrorMessageException(UserResultCode.CODE5);
        }
        myUser.setPassword(newPassword);

        /**
         * 设置验证码使用状态
         */
        VerifyCode code = verifyCodeRepository.findByMobileAndMerchantId(myUser.getUserName());
        code.setUseStatus(true);

        return true;
    }

    /**
     * 检查用户token是否有效
     * 无效判断：
     * 1、用户是否存在
     * 2、是否删除 是否锁定
     * 3、userToken是否匹配
     *
     * @param merchantId
     * @param userId
     * @param userToken
     * @return
     */
    @Override
    public boolean checkLoginToken(int merchantId, long userId, String userToken) {
        User myUser = userRepository.findByMerchantIdAndUserId(merchantId, userId);
        if (myUser == null) {
            return false;
        }
        //是否删除 是否锁定
        if (myUser.isDisabled() || myUser.isLocked()) {
            return false;
        }

        if (myUser.getUserToken().equals(userToken)) {
            return true;
        }
        return false;
    }

    /**
     * 获取邀请数
     * @param userId 用户ID
     * @param isAuthSuccess 认证是否成功
     * @return
     */
    @Override
    public Long countByMyInvite(Long userId, boolean isAuthSuccess) {
       return inviteRepository.count(getInviteSpecification(userId,isAuthSuccess));
    }

    /**
     * 获取我的邀请列表
     * @param userId
     * @param isAuthSuccess
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public PageListView<UserInviteVo> getMyInviteList(Long userId, boolean isAuthSuccess,int pageIndex,int pageSize) {
        PageListView<UserInviteVo> result=new PageListView<>();
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, new Sort(Sort.Direction.ASC, "id"));

        Specification<Invite> specification= getInviteSpecification(userId,isAuthSuccess);
        Page<Invite> page= inviteRepository.findAll(specification,pageable);
        result.setTotalCount(page.getTotalElements());
        result.setPageCount(page.getTotalPages());
        List<UserInviteVo> list=new ArrayList<>();
        for (Invite it:page.getContent()
             ) {
            UserInviteVo userInviteVo=new UserInviteVo();
            userInviteVo.setInviteTime(it.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            userInviteVo.setName(it.getRealName());
            userInviteVo.setStatus(it.getAuthStatus().getCode());
            userInviteVo.setStatusName(it.getAuthStatus().getName());
            userInviteVo.setUserId(it.getUserId());
            userInviteVo.setUserName(StringUtilsExt.safeGetMobile(it.getUserName()));
            list.add(userInviteVo);
        }
        result.setList(list);
        return result;
    }

    /**
     * 获取邀请筛选添加
     * @param userId
     * @param isAuthSuccess
     * @return
     */
    private Specification<Invite> getInviteSpecification(Long userId, boolean isAuthSuccess){
        Specification<Invite> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("inviterId").as(Long.class),userId));
            if (isAuthSuccess) {
                predicates.add(criteriaBuilder.equal(root.get("authStatus").as(UserAuthorizedStatusEnums.class), UserAuthorizedStatusEnums.AUTH_SUCCESS));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("authStatus").as(UserAuthorizedStatusEnums.class), UserAuthorizedStatusEnums.AUTH_NOT));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return specification;
    }

    /**
     * 添加邀请日志
     *
     * @param userId        用户ID
     * @param userName      用户名
     * @param realName      用户姓名
     * @param inviterUserId 邀请者ID
     */
    private void addInviteLog(Long userId, String userName, String realName, Long inviterUserId, String inviterName) {
        try {
            //添加邀请
            Invite invite = new Invite();
            invite.setMerchantId(Constant.MERCHANT_ID);
            invite.setUserId(userId);
            invite.setUserName(userName);
            invite.setRealName(realName);
            invite.setInviterId(inviterUserId);
            invite.setTime(LocalDateTime.now());
            if (inviterName != null && !StringUtils.isEmpty(inviterName)) {
                invite.setInviterName(inviterName);
                inviteRepository.save(invite);
            }
        } catch (Exception ex) {
            log.error(ex);
        }
    }
}
