package com.project.splitwise.strategies;

import com.project.splitwise.dto.SettleUpDTO;
import com.project.splitwise.dto.SettlementShareDTO;
import com.project.splitwise.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class SettleUpStrategyImpl implements SettleUpStrategy{

    @Autowired
    private GroupService groupService;

    @Override
    public List<SettleUpDTO> settleUpUsingHeap(Map<String, BigDecimal> userShareOfGroup){

        //define min heap and max heap
        PriorityQueue<SettlementShareDTO> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getUserShare().compareTo(a.getUserShare())  // Descending order
        );
        PriorityQueue<SettlementShareDTO> minHeap = new PriorityQueue<>(
                (a, b) -> a.getUserShare().compareTo(b.getUserShare())  // ascending order
        );

//        //get individual total balances of every user in group
//        Map<String, BigDecimal> userShareOfGroup = new HashMap<>();
//        userShareOfGroup = groupService.findShareOfUsers(inputGroupName,userShareOfGroup);

        //insert balances of every user in heaps
        for (Map.Entry<String, BigDecimal> entry : userShareOfGroup.entrySet()) {
            if(entry.getValue().compareTo(BigDecimal.ZERO) > 0){
                maxHeap.add(new SettlementShareDTO(entry.getKey(),entry.getValue()));
            }
            else if(entry.getValue().compareTo(BigDecimal.ZERO) < 0){
                minHeap.add(new SettlementShareDTO(entry.getKey(),entry.getValue()));
            }
        }

        List<SettleUpDTO> output = new ArrayList<SettleUpDTO>();
        while(!minHeap.isEmpty() && !maxHeap.isEmpty()){
            SettlementShareDTO toPay = minHeap.poll();
            SettlementShareDTO toReceive = maxHeap.poll();

            BigDecimal amountToBePaid = toReceive.getUserShare().min(toPay.getUserShare().abs());

            SettleUpDTO settlementTransaction = new SettleUpDTO(toPay.getUserName(),toReceive.getUserName(),amountToBePaid);

            //if toReceive value is > toPay value
            if(toReceive.getUserShare().compareTo(amountToBePaid) > 0){
                maxHeap.add(new SettlementShareDTO(toReceive.getUserName(),toReceive.getUserShare().subtract(amountToBePaid)));
            }
            else if(toPay.getUserShare().abs().compareTo(amountToBePaid) > 0){
                minHeap.add(new SettlementShareDTO(toPay.getUserName(),toPay.getUserShare().add(amountToBePaid)));
            }

            output.add(settlementTransaction);
        }
        return output;
    }
}
