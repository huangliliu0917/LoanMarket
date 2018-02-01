/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2016-2018. All rights reserved.
 */

package com.huotu.loanmarket.service.service.order;

import com.huotu.loanmarket.service.entity.order.Order;
import com.huotu.loanmarket.service.enums.OrderEnum;
import com.huotu.loanmarket.service.enums.UserAuthorizedStatusEnums;

/**
 * @author guomw
 * @date 01/02/2018
 */
public interface OrderService {

    /**
     * 创建订单
     * @param userId
     * @param mobile
     * @param name
     * @param idCardNo
     * @param orderType
     * @return
     */
    Order create(Long userId,String mobile,String name,String idCardNo, OrderEnum.OrderType orderType);

    /**
     * 创建订单
     * @param userId
     * @param orderType
     * @return
     */
    Order create(Long userId, OrderEnum.OrderType orderType);

    /**
     * 查询
     *
     * @param orderId
     * @return
     */
    Order findByOrderId(String orderId);


    /**
     * 更新订单支付状态
     * @param orderId
     * @param payType
     */
    void updateOrderPayStatus(String orderId,OrderEnum.PayType payType);

    /**
     * 更新订单状态
     * @param orderId
     * @param orderStatus
     */
    void updateOrderStatus(String orderId,OrderEnum.OrderStatus orderStatus);

    /**
     * 更新订单认证状态
     * @param orderId
     * @param authStatus
     */
    void updateOrderAuthStatus(String orderId, UserAuthorizedStatusEnums authStatus);

    /**
     * 更新订单认证次数
     * @param orderId
     */
    void updateOrderAuthCount(String orderId);

    /**
     * 保存
     *
     * @param order
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Order save(Order order);
}
