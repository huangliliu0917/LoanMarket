package com.huotu.loanmarket.service.repository.order;

import com.huotu.loanmarket.service.entity.order.Order;
import com.huotu.loanmarket.service.enums.OrderEnum;
import com.huotu.loanmarket.service.enums.UserAuthorizedStatusEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author luyuanyuan on 2018/1/31.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * 修改认证状态
     *
     * @param orderId
     * @param authStatus
     */
    @Query("update Order o set o.authStatus=?2 where o.orderId=?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = RuntimeException.class)
    void updateOrderAuthStatusByOrderId(String orderId, UserAuthorizedStatusEnums authStatus);

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param orderStatus
     */
    @Query("update Order o set o.orderStatus=?2 where o.orderId=?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = RuntimeException.class)
    void updateOrderStatusByOrderId(String orderId, OrderEnum.OrderStatus orderStatus);

    /**
     * 修改订单支付状态
     * @param orderId
     * @param payStatus
     */
    @Query("update Order o set o.payStatus=?2 where o.orderId=?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = RuntimeException.class)
    void updateOrderPayStatusByOrderId(String orderId,OrderEnum.PayStatus payStatus);

    /**
     * 修改订单授权次数
     *
     * @param orderId
     */
    @Query("update Order o set o.authCount=o.authCount+1 where o.orderId=?1")
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = RuntimeException.class)
    void updateOrderAuthCountByOrderId(String orderId);
}
