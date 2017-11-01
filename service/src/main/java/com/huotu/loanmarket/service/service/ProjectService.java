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
}
