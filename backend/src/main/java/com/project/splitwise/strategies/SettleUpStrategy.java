package com.project.splitwise.strategies;

import com.project.splitwise.dto.SettleUpDTO;
import com.project.splitwise.dto.SettlementShareDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SettleUpStrategy {
    List<SettleUpDTO> settleUpUsingHeap(Map<String, BigDecimal> userShareOfGroup);
}
