package com.huotu.loanmarket.service.repository.project;

import com.huotu.loanmarket.service.entity.project.ProjectApplyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hxh
 * @date 2017-10-26
 */
@Repository
public interface ProjectApplyLogRepository extends JpaRepository<ProjectApplyLog, Integer> {
    /**
     * 查询
     *
     * @param projectId
     * @return
     */
    List<ProjectApplyLog> findByProjectId(int projectId);
}