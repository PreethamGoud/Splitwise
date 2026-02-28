package com.project.splitwise.service.splitstrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.project.splitwise.dto.ParticipantDto;

public interface SplitStrategy {
    Map<Long, BigDecimal> calculateShares(BigDecimal totalAmount, List<ParticipantDto> participants);

}
