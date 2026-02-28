package com.project.splitwise.service;

import com.project.splitwise.dto.ConsolidatedSettlementDTO;
import com.project.splitwise.dto.ConsolidatedSettlementMiniDTO;
import com.project.splitwise.dto.SettleUpDTO;
import com.project.splitwise.dto.SettlementShareDTO;
import com.project.splitwise.entity.Group;
import com.project.splitwise.entity.Settlement;
import com.project.splitwise.entity.User;
import com.project.splitwise.entity.UserGroup;
import com.project.splitwise.repository.SettlementRepository;
import com.project.splitwise.strategies.SettleUpStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.*;

@Component
public class SettlementService {

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SettleUpStrategy settleUpStrategy;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseShareService expenseShareService;

    public Settlement saveSettlement(Settlement input){
        return settlementRepository.save(input);
    }

    public List<ConsolidatedSettlementDTO> getSettledUpGroupsOfUser(String inputUserName,String inputGroupName){

        //Map< friendName , ConsolidatedSettlementDTO >
        //to map friend with the transaction in each group
        Map<String, ConsolidatedSettlementDTO> friendToTransactionMapping = new HashMap<>();

//        //fetch all groups name of user
//        List<String> groupList = userService.fetchGroupListForUser(inputUserName);


//        for(String groupName : groupList){
            //first settle up group
            Map<String, BigDecimal> userWiseExpenseMap = new HashMap<>();
            userWiseExpenseMap = groupService.findShareOfUsers(inputGroupName,userWiseExpenseMap);

            //settle up userWiseExpenseMap
            List<SettleUpDTO> settledTransactions = settleUpStrategy.settleUpUsingHeap(userWiseExpenseMap);

            //if in these settled up transactions,operate on transactions to which user belongs
            for(SettleUpDTO s : settledTransactions){
                //if user belongs to that transaction,include it
                if(s.getPaidFrom().equalsIgnoreCase(inputUserName) || s.getPaidTo().equalsIgnoreCase(inputUserName)){
                    //get friend name
                    String friend = s.getPaidFrom();
                    if(!s.getPaidTo().equalsIgnoreCase(inputUserName)) friend = s.getPaidTo();

                    ConsolidatedSettlementMiniDTO tempMiniDTO = new ConsolidatedSettlementMiniDTO(s,inputGroupName);

                    //if entry for friend already exists
                    if(friendToTransactionMapping.containsKey(friend)){
                        ConsolidatedSettlementDTO tempDTO = friendToTransactionMapping.get(friend);

                        if(tempMiniDTO.getPaidFrom().equalsIgnoreCase(inputUserName)){
                            tempDTO.setTotalAmount(tempDTO.getTotalAmount().subtract(tempMiniDTO.getAmount()));
                        }
                        else tempDTO.setTotalAmount(tempDTO.getTotalAmount().add(tempMiniDTO.getAmount()));

                        tempDTO.getIndividualGroupShare().add(tempMiniDTO);
                    }
                    else{
                        ConsolidatedSettlementDTO tempDTO;
                        if(tempMiniDTO.getPaidFrom().equalsIgnoreCase(inputUserName)){
                            tempDTO = new ConsolidatedSettlementDTO(tempMiniDTO.getAmount().negate());
                        }
                        else tempDTO = new ConsolidatedSettlementDTO(tempMiniDTO.getAmount());

                        tempDTO.getIndividualGroupShare().add(tempMiniDTO);

                        friendToTransactionMapping.put(friend,tempDTO);
                    }

                }
            }
//        }

        List<ConsolidatedSettlementDTO> output = new ArrayList<>();

        for (Map.Entry<String, ConsolidatedSettlementDTO> entry : friendToTransactionMapping.entrySet()) {
            ConsolidatedSettlementDTO tempDTO = entry.getValue();

            if(tempDTO.getTotalAmount().compareTo(BigDecimal.ZERO) > 0){
                tempDTO.setPaidTo(inputUserName);
                tempDTO.setPaidFrom(entry.getKey());
            }
            else if(tempDTO.getTotalAmount().compareTo(BigDecimal.ZERO) < 0){
                tempDTO.setPaidTo(entry.getKey());
                tempDTO.setPaidFrom(inputUserName);
            }

            output.add(tempDTO);
        }

        return output;
    }


    public List<ConsolidatedSettlementDTO> getSettledUpAllGroupsOfUser(String inputUserName){

        //Map< friendName , ConsolidatedSettlementDTO >
        //to map friend with the transaction in each group
        Map<String, ConsolidatedSettlementDTO> friendToTransactionMapping = new HashMap<>();

        //fetch all groups name of user
        List<String> groupList = userService.fetchGroupListForUser(inputUserName);


        for(String groupName : groupList){
            //first settle up all groups individually
            Map<String, BigDecimal> userWiseExpenseMap = new HashMap<>();
            userWiseExpenseMap = groupService.findShareOfUsers(groupName,userWiseExpenseMap);

            //settle up userWiseExpenseMap
            List<SettleUpDTO> settledTransactions = settleUpStrategy.settleUpUsingHeap(userWiseExpenseMap);

            //if in these settled up transactions,operate on transactions to which user belongs
            for(SettleUpDTO s : settledTransactions){
                //if user belongs to that transaction,include it
                if(s.getPaidFrom().equalsIgnoreCase(inputUserName) || s.getPaidTo().equalsIgnoreCase(inputUserName)){
                    //get friend name
                    String friend = s.getPaidFrom();
                    if(!s.getPaidTo().equalsIgnoreCase(inputUserName)) friend = s.getPaidTo();

                    ConsolidatedSettlementMiniDTO tempMiniDTO = new ConsolidatedSettlementMiniDTO(s,groupName);

                    //if entry for friend already exists
                    if(friendToTransactionMapping.containsKey(friend)){
                        ConsolidatedSettlementDTO tempDTO = friendToTransactionMapping.get(friend);

                        if(tempMiniDTO.getPaidFrom().equalsIgnoreCase(inputUserName)){
                            tempDTO.setTotalAmount(tempDTO.getTotalAmount().subtract(tempMiniDTO.getAmount()));
                        }
                        else tempDTO.setTotalAmount(tempDTO.getTotalAmount().add(tempMiniDTO.getAmount()));

                        tempDTO.getIndividualGroupShare().add(tempMiniDTO);
                    }
                    else{
                        ConsolidatedSettlementDTO tempDTO;
                        if(tempMiniDTO.getPaidFrom().equalsIgnoreCase(inputUserName)){
                            tempDTO = new ConsolidatedSettlementDTO(tempMiniDTO.getAmount().negate());
                        }
                        else tempDTO = new ConsolidatedSettlementDTO(tempMiniDTO.getAmount());

                        tempDTO.getIndividualGroupShare().add(tempMiniDTO);

                        friendToTransactionMapping.put(friend,tempDTO);
                    }

                }
            }
        }

        List<ConsolidatedSettlementDTO> output = new ArrayList<>();

        for (Map.Entry<String, ConsolidatedSettlementDTO> entry : friendToTransactionMapping.entrySet()) {
            ConsolidatedSettlementDTO tempDTO = entry.getValue();

            if(tempDTO.getTotalAmount().compareTo(BigDecimal.ZERO) > 0){
                tempDTO.setPaidTo(inputUserName);
                tempDTO.setPaidFrom(entry.getKey());
            }
            else if(tempDTO.getTotalAmount().compareTo(BigDecimal.ZERO) < 0){
                tempDTO.setPaidTo(entry.getKey());
                tempDTO.setPaidFrom(inputUserName);
            }

            output.add(tempDTO);
        }

        return output;
    }

    @Transactional
    public boolean settleUpGroupAndRecordTransaction(SettleUpDTO inputTransactionDetails,String inputGroupName){
        String receiver = inputTransactionDetails.getPaidTo();
        String sender = inputTransactionDetails.getPaidFrom();
        BigDecimal transactionAmount = inputTransactionDetails.getAmount();

        //********************UPDATE USERGROUP***************

        //fetch user Group of sender and receiver
        Optional<UserGroup> currReceiverUserGroup = userGroupService.findByGroupNameAndUserName(inputGroupName,receiver);
        if(currReceiverUserGroup.isEmpty())throw new IllegalArgumentException("Error in recording transaction!");

        Optional<UserGroup> currSenderUserGroup = userGroupService.findByGroupNameAndUserName(inputGroupName,sender);
        if(currSenderUserGroup.isEmpty())throw new IllegalArgumentException("Error in recording transaction!");

        //change totalBalance of sender and receiver
        currReceiverUserGroup.get().setTotalBalance(currReceiverUserGroup.get().getTotalBalance().subtract(transactionAmount));
        currSenderUserGroup.get().setTotalBalance(currSenderUserGroup.get().getTotalBalance().add(transactionAmount));

        //if balance is < 1 and > -1
        if(currReceiverUserGroup.get().getTotalBalance().abs().compareTo(BigDecimal.ONE) < 0){
            currReceiverUserGroup.get().setTotalBalance(BigDecimal.ZERO);
        }
        if(currSenderUserGroup.get().getTotalBalance().abs().compareTo(BigDecimal.ONE) < 0){
            currSenderUserGroup.get().setTotalBalance(BigDecimal.ZERO);
        }

        //save userGroup
        userGroupService.saveUserGroup(currReceiverUserGroup.get());
        userGroupService.saveUserGroup(currSenderUserGroup.get());

        //**************NOW RECORD TRANSACTION****************
        Optional<User> receiverUserObj = userService.findByUserName(receiver);
        if(receiverUserObj.isEmpty())throw new IllegalArgumentException("Error in recording transaction!");

        Optional<User> senderUserObj = userService.findByUserName(sender);
        if(senderUserObj.isEmpty())throw new IllegalArgumentException("Error in recording transaction!");

        Optional<Group> currGroupObj = groupService.findByGroupName(inputGroupName);
        if(currGroupObj.isEmpty())throw new IllegalArgumentException("Error in recording transaction!");

        String description = "Payment from " + sender + " to " + receiver + " in " + inputGroupName + " group";
        Settlement settlementObj = new Settlement(transactionAmount,new java.util.Date(),description,senderUserObj.get(),receiverUserObj.get(),currGroupObj.get());

        settlementRepository.save(settlementObj);

        //************UPDATE EXPENSE IF SETTLED UP****************

        Map<String, BigDecimal> userWiseExpenseMap = new HashMap<>();
        userWiseExpenseMap = groupService.findShareOfUsers(inputGroupName,userWiseExpenseMap);

        boolean toUpdateExpenseStatus = true;
        boolean toUpdateExpenseShareStatusToPaid = true;
        for(Map.Entry<String, BigDecimal> entry : userWiseExpenseMap.entrySet()){
            if(entry.getValue().compareTo(BigDecimal.ZERO) != 0){
                toUpdateExpenseStatus=false;
                break;
            }
        }

        //if all expenses are paid,set status as settled in expense and paid in expenseShare for all expense and users
        if(toUpdateExpenseStatus){
            expenseService.updateExpenseStatusToSettledForGroup(inputGroupName);
            expenseShareService.updateStatusToPaidForAllUsersInGroup(inputGroupName);
            toUpdateExpenseShareStatusToPaid = false;
        }
        //if user has paid all his expenses,update expenseShare status to paid
        if(toUpdateExpenseShareStatusToPaid && userWiseExpenseMap.get(sender).compareTo(BigDecimal.ZERO) == 0){
            expenseShareService.updateStatusToPaidForUserInGroup(sender,inputGroupName);
        }
        if(!toUpdateExpenseStatus){
            expenseService.updateExpenseStatusToPartiallySettledForGroup(inputGroupName);
        }

        return true;
    }
}
