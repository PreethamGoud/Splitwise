package com.project.splitwise.controller;

import com.project.splitwise.dto.AllGroupsSettledDTO;
import com.project.splitwise.dto.ConsolidatedSettlementDTO;
import com.project.splitwise.dto.SettleUpDTO;
import com.project.splitwise.service.GroupService;
import com.project.splitwise.service.SettlementService;
import com.project.splitwise.service.UserService;
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
}
