package com.huotu.loanmarket.service.model.sesame;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author hxh
 * @Date 2018/1/30 17:32
 */
@Getter
@Setter
public class SesameConfig {
    /**
     * API 账户 商汤相关
     */
    private String apiId;
    /**
     * API密钥 商汤相关
     */
    private String apiSecret;
    /**
     * 比对身份证号和姓名是否一致
     */
    private String verifyIdAndNameUrl;
    /**
     * 芝麻应用标识
     */
    private String appId;
    /**
     * 商家私钥
     */
    private String privateKey;
    /**
     * 芝麻公钥
     */
    private String publicKey;
}
