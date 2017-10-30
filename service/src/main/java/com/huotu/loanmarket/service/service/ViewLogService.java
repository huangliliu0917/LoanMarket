package com.huotu.loanmarket.service.service;

import com.huotu.loanmarket.service.base.CrudService;
import com.huotu.loanmarket.service.entity.LoanUserViewLog;

/**
 * @author hxh
 * @date 2017-10-27
 */
public interface ViewLogService extends CrudService<LoanUserViewLog, Integer> {
    /**
     * 记录一条浏览日志
     *
     * @param userId    用户id
     * @param projectId 产品id
     * @return
     */
    LoanUserViewLog log(int userId, int projectId);
}
