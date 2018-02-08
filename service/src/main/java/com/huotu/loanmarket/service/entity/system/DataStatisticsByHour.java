package com.huotu.loanmarket.service.entity.system;

import com.huotu.loanmarket.common.Constant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 每小时数据统计
 *
 * @author helloztt
 */
@Getter
@Setter
@Entity
@Table(name = "zx_statistics_hour")
public class DataStatisticsByHour {
    @Id
    @Column(name = "data_id", unique = true, nullable = false, length = 10)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dataId;
    /**
     * 商户id
     */
    @Column(name = "merchant_id")
    private Integer merchantId = Constant.MERCHANT_ID;
    /**
     * 订单成功金额
     */
    @Column(name = "order_amount", scale = Constant.SCALE, precision = Constant.PRECISION)
    private BigDecimal orderAmount = BigDecimal.ZERO;
    /**
     * 用户人数
     */
    @Column(name = "user_count")
    private int userCount;
    /**
     * 订单数量
     */
    @Column(name = "order_count")
    private int orderCount;
    /**
     * 订单支付数量
     */
    @Column(name = "order_pay_count")
    private int orderPayCount;
    /**
     * 认证成功数量
     */
    @Column(name = "auth_success_count")
    private int authSuccessCount;
    /**
     * 认证失败数量
     */
    @Column(name = "auth_failure_count")
    private int authFailureCount;
    /**
     * 统计时间(创建时间）
     */
    @Column(name = "statistics_time", columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createTime = LocalDateTime.now();
    /**
     * 统计结束时间
     */
    @Column(name = "statistics_end_time", columnDefinition = "datetime")
    private LocalDateTime endTime = createTime.withMinute(0).withSecond(0).withNano(0);
    /**
     * 统计起始时间
     */
    @Column(name = "statistics_begin_time", columnDefinition = "datetime")
    private LocalDateTime beginTime = endTime.minusHours(1);

}
