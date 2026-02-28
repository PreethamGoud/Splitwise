package com.project.splitwise.controller;

import com.project.splitwise.dto.GroupDto;
import com.project.splitwise.entity.Group;
import com.project.splitwise.entity.User;
import com.project.splitwise.entity.UserGroup;
import com.project.splitwise.service.GroupService;
import com.project.splitwise.service.UserGroupService;
import com.project.splitwise.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/group")
@Slf4j
public class GroupController {

    @Autowired
    private GroupService groupService ;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @GetMapping("/{groupName}")
    public ResponseEntity<?> fetchGroupByGroupName(@PathVariable String groupName){
        try {
            Optional<Group> currGroup = groupService.findByGroupName(groupName);

            if(currGroup.isEmpty())throw new NullPointerException("No such group found");

            GroupDto groupDto = new GroupDto(currGroup.get());

            return new ResponseEntity<>(groupDto,HttpStatus.OK);
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

    @PostMapping
    public ResponseEntity<?> saveGroup(@RequestBody Group inputGroup){
        try {
            if(inputGroup.getGroupName().length() == 0){
                throw new IllegalArgumentException("Group name can't be blank");
            }
            Group savedGroup = groupService.saveGroup(inputGroup);

            return new ResponseEntity<>(savedGroup , HttpStatus.CREATED);
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

    @Transactional
    @PostMapping("/{groupName}/user/{userName}")
    public ResponseEntity<?> addUserToGroup(@PathVariable String userName,@PathVariable String groupName){
        try {
            //find user
            Optional<User> currUser = userService.findByUserName(userName);
            if(currUser.isEmpty())throw new NullPointerException("No such user exist with given userName");

            //find group
            Optional<Group> currGroup = groupService.findByGroupName(groupName);
            if(currGroup.isEmpty())throw new NullPointerException("No such group exist with given groupName");

            //if user already exists in group,throw error
            Optional<UserGroup> isCurrUserGroupPresent = userGroupService.findByGroupIdAndUserId(currGroup.get().getId(),currUser.get().getId());
            if(!isCurrUserGroupPresent.isEmpty())throw new IllegalArgumentException("User already exists in group");

            //add user and group to the userGroup
            UserGroup currUserGroup = new UserGroup();
            currUserGroup.setGroup(currGroup.get());
            currUserGroup.setUser(currUser.get());

            UserGroup savedUserGroup = userGroupService.saveUserGroup(currUserGroup);

            if(savedUserGroup == null)throw new RuntimeException("Error in creating userGroup");

            //add userGroup in user and group and save them
            currGroup.get().getUsers().add(savedUserGroup);
            currUser.get().getGroups().add(savedUserGroup);

            User savedUser = userService.saveUser(currUser.get());
            Group savedGroup = groupService.saveGroup(currGroup.get());

            return new ResponseEntity<>("User added successfully!",HttpStatus.OK);
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

    @GetMapping("/getBalance/{groupName}")
    public Map<String, BigDecimal> fetchBalancesOfGroup(@PathVariable String groupName){
        Map<String,BigDecimal> userWiseExpenseMap = new HashMap<>();

        return groupService.findShareOfUsers(groupName,userWiseExpenseMap);
    }
}
