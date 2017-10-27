package com.huotu.loanmarket.service.searchable;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author allan
 * @date 23/10/2017
 */
@Getter
@Setter
public class ProjectSearchCondition {
    /**
     * 0：全部，4：top4条
     */
    private int topNum;
    /**
     * 是否热卖
     */
    private int isHot;
    /**
     * 是否新品
     */
    private int isNew;
    /**
     * 排序,0:asc,1:desc,默认0
     */
    private int sortType = 0;
    /**
     * 排序字段，默认按时间
     */
    private int descField;
    /**
     * 分类编号
     */
    private int sid;
    /**
     * 贷款金额
     */
    private float money;
    /**
     * 最长期限，单位月
     */
    private int deadline;
    /**
     * 名称
     */
    private String name;
    /**
     * 页码，默认1
     */
    private int pageIndex;
    /**
     * 每页数量，默认10
     */
    private int pageSize = 10;
}
