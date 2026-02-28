package com.project.splitwise.service;

import com.project.splitwise.entity.User;
import com.project.splitwise.entity.UserGroup;
import com.project.splitwise.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserGroupService {

    @Autowired
    private UserGroupRepository userGroupRepository;

    public UserGroup saveUserGroup(UserGroup inputuserGroup){
        return userGroupRepository.save(inputuserGroup);
    }

    public Optional<UserGroup> findByGroupIdAndUserId(Long groupId,Long userId){
        return userGroupRepository.findByGroup_IdAndUser_Id(groupId,userId);
    }

    public Optional<List<UserGroup>> findByGroupId(Long groupId){
        return userGroupRepository.findByGroup_id(groupId);
    }

    public Optional<UserGroup> findByGroupNameAndUserName(String groupName,String userName){
        return userGroupRepository.findByGroup_GroupNameAndUser_UserName(groupName,userName);
    }
}
