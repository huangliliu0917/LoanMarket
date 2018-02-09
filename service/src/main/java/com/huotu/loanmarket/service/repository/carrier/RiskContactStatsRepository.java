package com.huotu.loanmarket.service.repository.carrier;

import com.huotu.loanmarket.service.entity.carrier.RiskContactStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author luyuanyuan on 2018/1/30.
 */
public interface RiskContactStatsRepository extends JpaRepository<RiskContactStats, Long> {
    List<RiskContactStats> findByOrderId(String orderId);
}