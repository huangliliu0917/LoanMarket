package com.huotu.loanmarket.service.service.sesame;

/**
 * @Author hxh
 * @Date 2018/1/30 16:17
 */
public interface SesameService {
    /**
     * 判断身份证号和姓名一致
     *
     * @param merchantId
     * @param name
     * @param idCardNum
     * @return
     */
    boolean checkNameAndIdCardNum(Integer merchantId, String name, String idCardNum);
}
