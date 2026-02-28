package com.project.splitwise.controller;

import com.project.splitwise.dto.SettleUpDTO;
import com.project.splitwise.dto.UserDto;
import com.project.splitwise.entity.User;
import com.project.splitwise.service.GroupService;
import com.project.splitwise.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @GetMapping()
    public ResponseEntity<?> getUserByUserName(){
        try {
            //get the logged in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            if(userName.length() == 0){
                throw new IllegalArgumentException("userName can't be blank");
            }
            Optional<User> fetchedUser = userService.findByUserName(userName);

            if(fetchedUser.isEmpty())throw new IllegalArgumentException("Please enter valid userName");

            UserDto userDto = new UserDto(fetchedUser.get());

            return new ResponseEntity<>(userDto,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("error in creating user",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/getBalance/all")
    public ResponseEntity<?> fetchUsersFriendsBalancesOfAllGroups(){
        //get the logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        try {
            Map<String,BigDecimal> userWiseExpenseMap = userService.fetchUsersFriendsBalancesOfAllGroups(userName);

            return new ResponseEntity<>(userWiseExpenseMap,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("error in creating user",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/getBalance/all/settled")
    public ResponseEntity<?> fetchUsersSettledBalancesOfAllGroups(){
        //get the logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        try {
            List<SettleUpDTO> allSettledUpTransactions = userService.fetchUsersSettledBalancesOfAllGroups(userName);

            return new ResponseEntity<>(allSettledUpTransactions,HttpStatus.OK);
        }
        catch (Exception e){
            log.error("error in creating user",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/{userName}/role/{roleName}")
    public ResponseEntity<?> addRoleToUser(@PathVariable String userName,@PathVariable String roleName){
        try {
            if(userName.isEmpty()){
                throw new IllegalArgumentException("userName or Email can't be blank");
            }
            boolean boolVal = userService.addRoleToUser(userName,roleName);

            return new ResponseEntity<>(boolVal , HttpStatus.CREATED);
        }
        catch (Exception e){
            log.error("error in creating user",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }


}
