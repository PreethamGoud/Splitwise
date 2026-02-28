package com.project.splitwise.service;

import com.project.splitwise.dto.CreateExpenseDto;
import com.project.splitwise.dto.ParticipantDto;
import com.project.splitwise.entity.*;
import com.project.splitwise.repository.ExpenseRepository;
import com.project.splitwise.repository.ExpenseShareRepository;
import com.project.splitwise.repository.GroupRepository;
import com.project.splitwise.repository.UserRepository;
import com.project.splitwise.service.splitstrategy.SplitStrategy;
import com.project.splitwise.service.splitstrategy.SplitStrategyFactory;import jakarta.persistence.SharedCacheMode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExpenseService {
    
	@Autowired
    private UserRepository userRepository;
    
	@Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;
	
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private ExpenseShareService expenseShareService;

    @Autowired
    private UserGroupService userGroupService;

    public int updateExpenseStatusToSettledForGroup(String groupName){
        return expenseRepository.updateExpenseStatusToSettledForGroup(groupName);
    }

    public int updateExpenseStatusToPartiallySettledForGroup(String groupName){
        return expenseRepository.updateExpenseStatusToPartiallySettledForGroup(groupName);
    }

    @Transactional
    public void saveExpense(CreateExpenseDto dto,String createdByUserName){
       
    	// Fetch user and group
        User user = userRepository.findByUserName(createdByUserName).orElseThrow(()-> new RuntimeException("created by user not found"));
        Group group = groupRepository.findById(dto.getGroupId()).orElseThrow();
        
        //Sum of amount paid by participants should be equal to total amount of expense.
        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        
        for (ParticipantDto participant : dto.getParticipants()) {
            if (participant.getPaidAmount() != null) {
            	totalPaidAmount = totalPaidAmount.add(participant.getPaidAmount());
            }
        }
       
        //This validation is applicable for all the strategies.
        if(totalPaidAmount.compareTo(dto.getTotalAmount())!=0) {
            throw new IllegalArgumentException("Total paid (" + totalPaidAmount + ") does not match total amount (" + dto.getTotalAmount() + ")");
        }
        
       // Use split strategy
        SplitStrategy strategy = SplitStrategyFactory.getStrategy(dto.getSplitType());
        Map<Long, BigDecimal> calculatedOwedAmounts = strategy.calculateShares(dto.getTotalAmount(), dto.getParticipants());

        //Create Expense
        Expense expense = new Expense(dto.getDescription(),dto.getTotalAmount(),new java.util.Date(),ExpenseStatus.PENDING,dto.getSplitType(),user,group);
        expense = expenseRepository.save(expense);

        
        //Save Participants
        for(ParticipantDto participantDto : dto.getParticipants())
        {
        	ExpenseShare participants = new ExpenseShare();
        	participants.setExpense(expense);
        	participants.setUser(userRepository.findById(participantDto.getUserId()).orElseThrow());
        	
        	BigDecimal paid = participantDto.getPaidAmount();
            BigDecimal owed = calculatedOwedAmounts.get(participantDto.getUserId());
            BigDecimal balance = paid.subtract(owed);
            
            participants.setPaidAmount(paid);
            participants.setShareAmount(owed);
            participants.setBalanceLeft(balance);
            
            if (paid.compareTo(owed) >= 0) {
                participants.setStatus(ShareStatus.PAID);
            } else if (paid.compareTo(BigDecimal.ZERO) == 0) {
                participants.setStatus(ShareStatus.OWES);
            } else {
                participants.setStatus(ShareStatus.PARTIALLY_PAID);
            }
            
            ExpenseShare savedExpenseShare = expenseShareRepository.save(participants);
            expense.getShares().add(savedExpenseShare);

            Optional<UserGroup> currUserGroup = userGroupService.findByGroupIdAndUserId(dto.getGroupId(),savedExpenseShare.getUser().getId());
            if(currUserGroup.isEmpty())throw new RuntimeException("Error on creating expense as userGroup is not found");

            currUserGroup.get().setTotalBalance(currUserGroup.get().getTotalBalance().add(balance));
            userGroupService.saveUserGroup(currUserGroup.get());
        }
        expenseRepository.save(expense);
    	
    }


}
