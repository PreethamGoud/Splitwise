package com.project.splitwise.service;

import com.project.splitwise.entity.ExpenseShare;
import com.project.splitwise.repository.ExpenseShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ExpenseShareService {

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    public ExpenseShare saveExpense(ExpenseShare expenseShare){
        return expenseShareRepository.save(expenseShare);
    }

    public List<ExpenseShare> getExpenseSharesOfAUserInAGroup(String inputUserName , String inputGroupName){
        return expenseShareRepository.findExpenseSharesOfUserInAGroup(inputUserName,inputGroupName);
    }

    public List<ExpenseShare> getNegativeExpenseSharesOfAUserInAGroup(String inputUserName , String inputGroupName){
        return expenseShareRepository.findNegativeExpenseSharesOfUserInAGroup(inputUserName,inputGroupName);
    }

    public List<ExpenseShare> getPositiveExpenseSharesOfAUserInAGroup(String inputUserName , String inputGroupName){
        return expenseShareRepository.findPositiveExpenseSharesOfUserInAGroup(inputUserName,inputGroupName);
    }

    public BigDecimal getNegativeSumOfExpenseSharesOfAUserInAGroup(String inputUserName , String inputGroupName){
        return expenseShareRepository.findNegativeSumOfExpenseSharesOfUserInAGroup(inputUserName,inputGroupName);
    }

    public int updateNegativeBalanceLeftOfPositiveUsersToZero(String inputUserName , String inputGroupName){
        return expenseShareRepository.updateNegativeBalanceLeftOfPositiveUsersToZero(inputUserName,inputGroupName);
    }

    public int updateStatusToPaidForUserInGroup(String inputUserName , String inputGroupName){
        return expenseShareRepository.updateStatusToPaidForUserInGroup(inputUserName,inputGroupName);
    }

    public int updateStatusToPaidForAllUsersInGroup(String inputGroupName){
        return expenseShareRepository.updateStatusToPaidForAllUsersInGroup(inputGroupName);
    }

}
