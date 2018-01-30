package com.huotu.loanmarket.service.entity.carrier;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 *  运营商认证
 * @author luyuanyuan on 2018/1/30.
 */
@Getter
@Setter
@Entity
@Table(name = "zx_user_carrier")
public class UserCarrier {
    /** id */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 商户id
     */
    @Column(name = "merchant_id")
    private Integer merchant;

    /** order_id */
    @Column(name = "order_id")
    private Long orderId;

    /** 爬取结果 */
    @Column(name = "info",columnDefinition = "longtext")
    private String info;

    /**
     * 运营商认证姓名
     */
    @Column(name = "real_name")
    private String realName;

    /**
     * 身份证号
     */
    @Column(name = "identity_code")
    private String identityCode;

    /**
     * 入网时间
     */
    @Column(name = "net_time")
    private int netTime;
    /**
     * 手机号
     */
    @Column(name = "mobile")
    private String mobile;

    /**
     * 号码归属地
     */
    @Column(name = "mobile_addr",length = 50)
    private String mobileAddr;

    /**
     * 状态
     */
    @Column(name = "status",length = 20)
    private String status;

    /**
     * 账户余额
     */
    @Column(name="remain",length = 20)
    private String remain;

    /**
     * 近6个月互通号码数量
     */
    @Column(name = "mutual_number")
    private int mutualNumber;

    /**
     * 前10联系人黑名单人数占比
     */
    @Column(name = "black_radio")
    private Double blackRadio;

    /**
     * 前10联系人信贷逾期名单人数占比
     */
    @Column(name = "black_credit_radio")
    private Double blackCreditRadio;

    /**
     * 前10联系人近3月平均申请平台数
     */
    @Column(name = "apply_count")
    private int applyCount;

    /**
     * 前10联系人近3月申请2个及以上平台的人数
     */
    @Column(name = "apply_count_over_two")
    private int applyCountOverTwo;

    /**
     * 任务id
     */
    @Column(name = "task_id")
    private String taskId;

    /** 第一次更新时间 */
    @Column(name = "first_updatetime",columnDefinition = "timestamp")
    private LocalDateTime firstUpdatetime;

    /** 最后更新时间 */
    @Column(name = "last_updatetime",columnDefinition = "timestamp")
    private LocalDateTime lastUpdatetime;
}
