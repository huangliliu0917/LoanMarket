package com.huotu.loanmarket.service.service;

import com.huotu.loanmarket.service.base.CrudService;
import com.huotu.loanmarket.service.entity.LoanProject;
import com.huotu.loanmarket.service.searchable.ProjectSearchCondition;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author allan
 * @date 23/10/2017
 */
public interface ProjectService extends CrudService<LoanProject, Integer> {
    /**
     * 按条件分组查询
     *
     * @param pageIndex       页码，索引从1开始
     * @param pageSize        每页条数
     * @param searchCondition {@link ProjectSearchCondition}
     * @return
     */
    Page<LoanProject> findAll(int pageIndex, int pageSize, ProjectSearchCondition searchCondition);

    /**
     * 按条件查询
     *
     * @param categoryId
     * @param amount
     * @param deadline
     * @return
     */
    List<LoanProject> findAll(int categoryId, double amount, int deadline);

    /**
     * 查询所有
     *
     * @return
     */
    List<LoanProject> findAll();

    /**
     * 获得最热数据
     *
     * @return
     */
    List<LoanProject> getHotProject();

    /**
     * 获得最新数据
     *
     * @return
     */
    List<LoanProject> getNewProject();

    /**
     * 设置热卖属性
     *
     * @param isHot
     * @param projectIdsStr
     */
    void setHot(int isHot, String projectIdsStr);

    /**
     * 设置新品属性
     *
     * @param isNew
     * @param projectIdsStr
     */
    void setNew(int isNew, String projectIdsStr);

    /**
     * 根据类目编号查询
     *
     * @param tag 类目编号
     * @return
     */
    List<LoanProject> findByTag(int tag);

    /**
     * 记录浏览记录
     *
     * @param project
     */
    void projectViewLog(int project);

    /**
     * 记录申请记录
     *
     * @param projectId
     */
    void projectApplyLog(int projectId);

    /**
     * 查询产品总浏览量
     *
     * @param projectId
     * @return
     */
    int countProjectView(int projectId);

    /**
     * 查询产品总申请量
     *
     * @param projectId
     * @return
     */
    int countProjectApply(int projectId);
}
