package com.project.splitwise.controller;

import com.project.splitwise.dto.AllGroupsSettledDTO;
import com.project.splitwise.dto.ConsolidatedSettlementDTO;
import com.project.splitwise.dto.GroupTransactionDTO;
import com.project.splitwise.dto.SettleUpDTO;
import com.project.splitwise.service.GroupService;
import com.project.splitwise.service.SettlementService;
import com.project.splitwise.service.UserService;
import com.project.splitwise.repository.ExpenseRepository;
import com.project.splitwise.repository.SettlementRepository;
import com.project.splitwise.strategies.SettleUpStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;

@RestController
@Slf4j
@RequestMapping("/settle")
public class SettlementController {

    @Autowired
    private SettleUpStrategy settleUpStrategy;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @GetMapping("/group/{inputGroupName}")
    public ResponseEntity<?> getSettledUpGroup(@PathVariable String inputGroupName){
        try {
            Map<String, BigDecimal> userShareOfGroup = new HashMap<>();
            userShareOfGroup = groupService.findShareOfUsers(inputGroupName , userShareOfGroup);

            List<SettleUpDTO> output = settleUpStrategy.settleUpUsingHeap(userShareOfGroup);

            return new ResponseEntity<>(output,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("error in creating group",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/getAllSettled/user/group/{inputGroupName}")
    public ResponseEntity<?> getSettledUpGroupsOfUser(@PathVariable String inputGroupName){
        //get the logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String inputUserName = authentication.getName();
        try {

            List<ConsolidatedSettlementDTO> serviceOutput = settlementService.getSettledUpGroupsOfUser(inputUserName,inputGroupName);

            BigDecimal totalShare = BigDecimal.ZERO;
            for(ConsolidatedSettlementDTO i : serviceOutput){
                totalShare = totalShare.add(i.getTotalAmount());
            }

            AllGroupsSettledDTO output = new AllGroupsSettledDTO(totalShare,serviceOutput);

            return new ResponseEntity<>(output,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("error in creating group",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/getAllSettled/user")
    public ResponseEntity<?> getSettledUpAllGroupsOfUser(){
        //get the logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String inputUserName = authentication.getName();
        try {

            List<ConsolidatedSettlementDTO> serviceOutput = settlementService.getSettledUpAllGroupsOfUser(inputUserName);

            BigDecimal totalShare = BigDecimal.ZERO;
            for(ConsolidatedSettlementDTO i : serviceOutput){
                totalShare = totalShare.add(i.getTotalAmount());
            }

            AllGroupsSettledDTO output = new AllGroupsSettledDTO(totalShare,serviceOutput);

            return new ResponseEntity<>(output,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("error in creating group",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/group/{inputGroupName}")
    @Transactional
    public ResponseEntity<?> settleUpGroupAndRecordTransaction(
            @PathVariable String inputGroupName,
            @RequestBody SettleUpDTO inputTransactionDetails
    ){
        try {
            boolean serviceOutput = settlementService.settleUpGroupAndRecordTransaction(inputTransactionDetails,inputGroupName);

            String output = "Error in recording transaction";
            if(serviceOutput) output = "Transaction added successfully!";

            return new ResponseEntity<>(output,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("error in creating group",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/group/{groupName}/timeline")
    public ResponseEntity<?> getGroupTimeline(@PathVariable String groupName) {
        try {
            List<GroupTransactionDTO> timeline = new ArrayList<>();

            List<com.project.splitwise.entity.Expense> expenses =
                    expenseRepository.findByGroup_GroupNameOrderByDateDesc(groupName);
            List<com.project.splitwise.entity.Settlement> settlements =
                    settlementRepository.findByGroup_GroupNameOrderByDateDesc(groupName);

            // both lists are sorted newest‑first by query; perform a linear merge
            int ei = 0, si = 0;
            while ((expenses != null && ei < expenses.size()) ||
                    (settlements != null && si < settlements.size())) {
                if (expenses != null && ei < expenses.size() &&
                        (settlements == null || si >= settlements.size() ||
                                !expenses.get(ei).getDate().before(settlements.get(si).getDate()))) {
                    com.project.splitwise.entity.Expense e = expenses.get(ei++);
                    timeline.add(new GroupTransactionDTO(
                            e.getDate(),
                            "EXPENSE",
                            e.getDescription(),
                            e.getPayer() != null ? e.getPayer().getUserName() : null,
                            null,
                            e.getAmount()
                    ));
                } else {
                    com.project.splitwise.entity.Settlement s = settlements.get(si++);
                    timeline.add(new GroupTransactionDTO(
                            s.getDate(),
                            "SETTLEMENT",
                            s.getDescription(),
                            s.getPayer() != null ? s.getPayer().getUserName() : null,
                            s.getReceiver() != null ? s.getReceiver().getUserName() : null,
                            s.getAmount()
                    ));
                }
            }

            // timeline is already merged in correct order; logging for debug
            log.debug("merged timeline (with timestamps) = {}", timeline);

            return new ResponseEntity<>(timeline, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error fetching group timeline", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
