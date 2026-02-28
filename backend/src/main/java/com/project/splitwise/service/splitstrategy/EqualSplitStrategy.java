package com.project.splitwise.service.splitstrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.project.splitwise.dto.ParticipantDto;

@Component("Equal")
public class EqualSplitStrategy implements SplitStrategy{

	@Override
	public Map<Long, BigDecimal> calculateShares(BigDecimal totalAmount, List<ParticipantDto> participants) {
		// TODO Auto-generated method stub
		Map<Long, BigDecimal> owedAmounts = new HashMap<>();

        int numberOfParticipants = participants.size();
        if (numberOfParticipants == 0) {
            throw new IllegalArgumentException("No participants found for equal split.");
        }
        
        int includedParticipantsInSplit = 0;
        
        for(ParticipantDto participant : participants) {

        	if(participant.getIncludedInSplit()==true) {
        		includedParticipantsInSplit++;
        	}

        }

        // Calculate equal share (rounded to 2 decimal places)
        BigDecimal individualShare = totalAmount.divide(BigDecimal.valueOf(includedParticipantsInSplit), 2, RoundingMode.HALF_UP);

        for (ParticipantDto participant : participants) {
        	
        	if(participant.getIncludedInSplit()== true) {
            owedAmounts.put(participant.getUserId(), individualShare);
        	}
        	else {
        		owedAmounts.put(participant.getUserId(), BigDecimal.ZERO);
        	}
        }

        return owedAmounts;
	}

}
