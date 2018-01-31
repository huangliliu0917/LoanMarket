/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2016-2018. All rights reserved.
 */

package com.huotu.loanmarket.webapi.service;

import com.huotu.loanmarket.common.Constant;
import com.huotu.loanmarket.common.enums.IMerchantParameterEnum;
import com.huotu.loanmarket.service.config.MerchantConfigType;
import com.huotu.loanmarket.service.entity.merchant.MerchantConfigItem;
import com.huotu.loanmarket.service.entity.system.SmsTemple;
import com.huotu.loanmarket.service.enums.ConfigParameter;
import com.huotu.loanmarket.service.enums.MerchantConfigEnum;
import com.huotu.loanmarket.service.enums.SmsTemplateSceneTypeEnum;
import com.huotu.loanmarket.service.repository.merchant.MerchantConfigItemRepository;
import com.huotu.loanmarket.service.repository.system.SmsTemplateRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author helloztt
 */
@Service
public class InitService {

    private static final Log log = LogFactory.getLog(InitService.class);
    @Autowired
    private SmsTemplateRepository smsTemplateRepository;
    @Autowired
    private MerchantConfigItemRepository merchantConfigItemRepository;

    private List<MerchantConfigItem> merchantConfigItems = new ArrayList<>();


    @PostConstruct
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void init() {

        initTemp();
        initSmsMessage();
        initsesame();
    }


    @Transactional(rollbackFor = RuntimeException.class)
    public void initTemp() {
        if (smsTemplateRepository.count() > 0) {
            return;
        }
        SmsTemplateSceneTypeEnum[] smsTemplateEnums = SmsTemplateSceneTypeEnum.values();
        List<SmsTemple> smsTempleList = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (SmsTemplateSceneTypeEnum smsTempEnum : smsTemplateEnums) {
            SmsTemple smsTemple = new SmsTemple();
            smsTemple.setMerchantId(Constant.MERCHANT_ID);
            smsTemple.setSceneType(smsTempEnum.getCode());
            smsTemple.setTmplContent(smsTempEnum.getTemplate());
            smsTemple.setCreatetime(localDateTime);
            smsTempleList.add(smsTemple);
        }
        smsTemplateRepository.save(smsTempleList);
        smsTemplateRepository.flush();
    }


    /**
     * 初始化芝麻认证参数
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void initsesame() {
        Map<String, String> map = new HashMap<>();
        map.put("zhima_api_id", "6eadbeb657ab4128b2c0298c49f07157");
        map.put("zhima_api_secret", "8f385900e02440228734ef6ce471c61c");
        map.put("zhima_name_and_idcardnum_url", "https://cloudapi.linkface.cn/data/verify_id_name");
        map.put("zhima_app_id", "300001906");
        map.put("zhima_public_key", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQl/xB4ZcyV5Yn4Lz1DetwIeh/V3aP1Y30XRWE9VZJO6sWMjHF03RllZC9HQX54jsQ24lrk2zdytSDuEbkyYB8r/HRtRmF2qzCFfFGhvLGWAXIL/s7YxKtDw12zJtdSpgI0/+UKjKYlH3KjOdv0tr1NQbqA6xLTM8+8w9I8tWc/QIDAQAB");
        map.put("zhima_private_key", "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALUEqZqAL/q/zi2Jja6qgn7vV1DHBOv7HDkuIHfdyeT0QUZM4ZcWIUYFqCs7g17M7GsH4BEegJMb0wMyh1lWuOXV3xv7Y/ACiQjVVxbrBb8+CdoVQUbcqOv3RkOfULhWh2U8+YmkKwpweQ4qt2pJqAihiReLdDNYRPqsDLcm2pSVAgMBAAECgYBwkYSzOrIbmfmqYdcE3q4mJ8rBxfHumgr0GBZC1F7ADZEmGZtULIPmmo/4ypqocoE3Ef+qRbCpSu1PGLYIvaBe6Ws3elSNbTqtQpMmHECtHRjM36tM6I1c8FlFQ7MzQ8OOWaa7GcDRsixfvtDmFc7TCjF1WQIEIjUSBhjoVIwQgQJBAN0EPKYbSch1sJv8FUjVJ0alMYCt25VCvoehAw8HxyzXIF1PES11VbSYkv9Khrc6KmImC8+AiDG0EFBw+uwNVjUCQQDRq6lo61vjLXDgiigmGFa0I20MWWZrAcP/aeuQObjrSJl26oe8tFCiCNvVXzPLSHT1QatODkg5Dq5TORn4lJDhAkEA1Eg0hIrjRGId/sSSIQFylv0l3eWfae7Ql+7DxFUBLoV2NYkzA4mWhz6gugn3+NcqTFkksOpQZgUAQnMx3zY0HQJATvr5+tLFr0LbxUllrEv4xSyfFErM4Lwuh5TvrkmAFekoHfpknTnTMeZiYPLWwNmPbR+KlwGcRRn5otHcrZ3RIQJBAIZ9UaQKsdV30wxnWI576Ewg6uzbQVjLXmFh9Z+wU1j9WofBQA2aKgQtmfNd1lIOLOzuJ6mu3ftGr09Fak7Epds=");
        initMerchantConfigItem(map, MerchantConfigEnum.SESAME);
    }


    /**
     * 初始化短信接口参数数据
     *
     * @author guomw
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void initSmsMessage() {
        Map<String, String> map = new HashMap<>();
        map.put("message_url", "http://smssh1.253.com/msg/send/json");
        map.put("message_account", "N8310514");
        map.put("message_password", "Htlm888888");
        map.put("message_channelNo", "");
        map.put("message_passageway", "");
        initMerchantConfigItem(map, MerchantConfigEnum.MESSAGE);
    }


    /***
     * 初始化指定第三方参数配置
     * @param mapData
     * @param type
     * @author guomw
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void initMerchantConfigItem(Map<String, String> mapData, MerchantConfigEnum type) {
        try {
            List<MerchantConfigItem> itemList = merchantConfigItems.stream().filter(t -> type.equals(t.getType())).collect(Collectors.toList());
            List<MerchantConfigItem> items = new ArrayList<>();
            Class<?>[] declaredClasses = ConfigParameter.class.getDeclaredClasses();
            for (Class<?> declaredClass : declaredClasses) {
                if (declaredClass.isEnum()) {
                    MerchantConfigType fitConfigType = declaredClass.getAnnotation(MerchantConfigType.class);
                    if (fitConfigType != null && (type.equals(fitConfigType.type()))) {
                        Class<Enum> clazz = (Class<Enum>) declaredClass;
                        Enum[] arrEnums = clazz.getEnumConstants();
                        for (Enum obj : arrEnums) {
                            IMerchantParameterEnum parameter = (IMerchantParameterEnum) obj;
                            if (itemList.stream().noneMatch(t -> parameter.getKey().equals(t.getCode()))) {
                                MerchantConfigItem item = new MerchantConfigItem();
                                item.setCode(parameter.getKey());
                                item.setMerchantId(Constant.MERCHANT_ID);
                                item.setName(parameter.getMessage());
                                item.setValue(mapData.get(parameter.getKey()));
                                item.setType(type);
                                items.add(item);
                            }
                        }
                    }
                }
            }

            if (items.size() > 0) {
                merchantConfigItemRepository.save(items);
            }
        } catch (Exception ex) {
            log.error(ex);
        }
    }
}
