package com.huotu.loanmarket.service.service;

import com.huotu.loanmarket.service.base.CrudService;
import com.huotu.loanmarket.service.entity.AppVersion;

public interface AppVersionService extends CrudService<AppVersion, Integer> {
    /**
     *
     * @param versionCode
     * @return
     */
    AppVersion check(int versionCode);
}
