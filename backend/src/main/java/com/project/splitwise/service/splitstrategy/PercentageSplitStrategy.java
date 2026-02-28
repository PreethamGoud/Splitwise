package com.project.splitwise.service.splitstrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.splitwise.dto.ParticipantDto;

public class PercentageSplitStrategy implements SplitStrategy{

	@Override
	public Map<Long, BigDecimal> calculateShares(BigDecimal totalAmount, List<ParticipantDto> participants) {
		// TODO Auto-generated method stub
		Map<Long, BigDecimal> owedAmounts = new HashMap<>();
		BigDecimal totalPercentage = BigDecimal.ZERO;
		
		for(ParticipantDto participant : participants) {
			if(participant.getPercentage() == null) {
				participant.setPercentage(BigDecimal.ZERO);
			}
			else {
				totalPercentage = totalPercentage.add(participant.getPercentage());
			}
		}
		
		//Validation for percentage.
		if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new IllegalArgumentException("Total percentage must add up to 100. Found: " + totalPercentage);
        }
		
		for (ParticipantDto participant : participants) {
            BigDecimal owedAmount = totalAmount
                    .multiply(participant.getPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            owedAmounts.put(participant.getUserId(), owedAmount);
        }
		
		return owedAmounts;
		
	}

}
