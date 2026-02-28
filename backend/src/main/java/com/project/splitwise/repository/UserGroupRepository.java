package com.project.splitwise.repository;

import com.project.splitwise.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    Optional<UserGroup> findByGroup_IdAndUser_Id(Long groupId, Long userId);

    Optional<List<UserGroup>> findByGroup_id(Long groupId);

    Optional<UserGroup> findByGroup_GroupNameAndUser_UserName(String groupName,String userName);
}
