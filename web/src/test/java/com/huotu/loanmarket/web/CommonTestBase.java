package com.huotu.loanmarket.web;

import com.huotu.loanmarket.service.entity.LoanCategory;
import com.huotu.loanmarket.service.repository.LoanCategoryRepository;
import com.huotu.loanmarket.web.config.MvcConfig;
import com.huotu.loanmarket.web.config.SecurityConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.UUID;

/**
 * Created by hxh on 2017-10-26.
 */
@WebAppConfiguration
@ActiveProfiles({"test", "unit_test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MvcConfig.class, SecurityConfig.class})
public class CommonTestBase extends SpringWebTest {
    @Autowired
    private LoanCategoryRepository loanCategoryRepository;

    protected LoanCategory mockLoanCategory() {
        LoanCategory loanCategory = new LoanCategory();
        loanCategory.setName(UUID.randomUUID().toString());
        loanCategory.setIcon(UUID.randomUUID().toString());
        return loanCategoryRepository.saveAndFlush(loanCategory);
    }
}
