package com.project.splitwise.repository;

import com.project.splitwise.entity.Group;
import com.project.splitwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByGroupName(String groupName);

    Optional<Group> findById(Long groupId);
}
