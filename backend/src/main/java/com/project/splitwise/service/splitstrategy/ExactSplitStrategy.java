package com.project.splitwise.service.splitstrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.project.splitwise.dto.ParticipantDto;

@Component("EXACT")
public class ExactSplitStrategy implements SplitStrategy {

	@Override
	public Map<Long, BigDecimal> calculateShares(BigDecimal totalAmount, List<ParticipantDto> participants) {
		// TODO Auto-generated method stub
		Map<Long, BigDecimal> owedAmounts = new HashMap<>();
		
        BigDecimal totalOwed = BigDecimal.ZERO;
        
        for(ParticipantDto participant: participants ) {
        	
        	if(participant.getOwedAmount()!= null) {
        		totalOwed = totalOwed.add(participant.getOwedAmount());
        	}
        	
        	
        }

        //Validation: total amount should be equal to total owed amount
        if (totalOwed.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException("Total owed (" + totalOwed + ") does not match total amount (" + totalAmount + ")");
        }
        
        for (ParticipantDto p : participants) {
            BigDecimal owed = p.getOwedAmount() != null ? p.getOwedAmount() : BigDecimal.ZERO;
            owedAmounts.put(p.getUserId(), owed);
        }

        return owedAmounts;
        
        

	}

}
