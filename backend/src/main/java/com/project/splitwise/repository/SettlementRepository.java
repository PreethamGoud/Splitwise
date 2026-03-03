package com.project.splitwise.repository;

import com.project.splitwise.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    java.util.List<Settlement> findByGroup_GroupNameOrderByDateDesc(String groupName);
}
