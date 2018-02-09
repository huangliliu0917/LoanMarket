package com.huotu.loanmarket.service.enums;


import com.huotu.loanmarket.common.enums.ICommonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hxh
 * @date 2017-11-29
 */
@Getter
@AllArgsConstructor
public enum MerchantConfigEnum implements ICommonEnum {
    /**
     * 第三方参数类型
     */
    SESAME(0, "芝麻信用接口"),
    MESSAGE(1, "短信接口参数"),
    CARRIER(2, "运营商接口参数"),
    AUTH_FEE(3,"认证费参数配置"),
    ALIPAY(4,"支付宝支付接口参数"),
    TONGDUN(5, "同盾接口参数"),
    TONGDUN_RULE(6,"同盾规则ID参数"),
    GENERAL(7,"通用参数"),
    ;

    private int code;

    private String name;


}