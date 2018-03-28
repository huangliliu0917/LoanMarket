/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2016-2018. All rights reserved.
 */

package com.huotu.loanmarket.webapi.controller.user;

import com.huotu.loanmarket.service.entity.user.User;
import com.huotu.loanmarket.service.model.user.UserInfoVo;
import com.huotu.loanmarket.service.service.user.YouXinUserService;
import com.huotu.loanmarket.webapi.controller.base.BaseTest;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

public class UserControllerTest extends BaseTest {

    @Autowired
    private YouXinUserService youXinUserService;

    @Test
    public void login() throws Exception {

        //用户名不是手机号,期望返回4101, "手机号码输入有误"
//        mockMvc.perform(post("/api/sys/checkUpdate")
//                .header(Constant.APP_VERSION_KEY, "1.0.0")
//                .header(Constant.APP_SYSTEM_TYPE_KEY, "android"))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(jsonPath(RESULT_CODE_PATH).value(AppCode.SUCCESS.getCode()));
    }

    @Test
    public void register() throws Exception {
    }

    @Test
    public void updatePassword() throws Exception {
    }

    @Test
    public void userCenter() throws Exception {
    }

    @Test
    public void myInviteList() throws Exception {
    }

    @Test
    public void syncUser() throws UnsupportedEncodingException {
        User user =mockUser();

        UserInfoVo vo= youXinUserService.syncUser(user.getUserName(),2);

        Assert.isNotNull(vo,"kong");

    }

}