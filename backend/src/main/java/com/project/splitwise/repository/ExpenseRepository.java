package com.project.splitwise.repository;

import com.project.splitwise.entity.Expense;
import com.project.splitwise.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Modifying
    @Query("""
            update
                Expense e
            set
                e.status = 'SETTLED'
            where
                e.group.groupName = :groupName
            """)
    int updateExpenseStatusToSettledForGroup(
            @Param("groupName") String groupName
    );

    @Modifying
    @Query("""
            update
                Expense e
            set
                e.status = 'PARTIALLY_SETTLED'
            where
                e.group.groupName = :groupName
            """)
    int updateExpenseStatusToPartiallySettledForGroup(
            @Param("groupName") String groupName
    );
}
