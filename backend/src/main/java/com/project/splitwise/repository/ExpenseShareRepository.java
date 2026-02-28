package com.project.splitwise.repository;

import com.project.splitwise.entity.ExpenseShare;
import com.project.splitwise.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    @Query("""
            select es
            from
            	ExpenseShare es
            where
            	es.user.userName = :userName and
            	es.balanceLeft != 0 and
            	es.expense.status != 'SETTLED' and
            	es.expense.group.groupName = :groupName
            """)
    List<ExpenseShare> findExpenseSharesOfUserInAGroup(
            @Param("userName") String userName,
            @Param("groupName") String groupName
    );

    @Query("""
            select es
            from
            	ExpenseShare es
            where
            	es.user.userName = :userName and
            	es.balanceLeft != 0 and
            	es.expense.status != 'SETTLED' and
            	es.expense.group.groupName = :groupName and
            	es.balanceLeft < 0
            """)
    List<ExpenseShare> findNegativeExpenseSharesOfUserInAGroup(
            @Param("userName") String userName,
            @Param("groupName") String groupName
    );

    @Query("""
            select es
            from
            	ExpenseShare es
            where
            	es.user.userName = :userName and
            	es.balanceLeft != 0 and
            	es.expense.status != 'SETTLED' and
            	es.expense.group.groupName = :groupName and
            	es.balanceLeft > 0
            """)
    List<ExpenseShare> findPositiveExpenseSharesOfUserInAGroup(
            @Param("userName") String userName,
            @Param("groupName") String groupName
    );

    @Query("""
            select sum(es.balanceLeft)
            from
            	ExpenseShare es
            where
            	es.user.userName = :userName and
            	es.balanceLeft != 0 and
            	es.expense.status != 'SETTLED' and
            	es.expense.group.groupName = :groupName and
            	es.balanceLeft < 0
            """)
    BigDecimal findNegativeSumOfExpenseSharesOfUserInAGroup(
            @Param("userName") String userName,
            @Param("groupName") String groupName
    );

    @Modifying
    @Query("""
            update
                ExpenseShare es
            set
                es.balanceLeft = 0
            where
                es.balanceLeft < 0 and
                es.expense.status != 'SETTLED' and
                es.user.userName = :userName and
                es.expense.group.groupName = :groupName
            """)
    int updateNegativeBalanceLeftOfPositiveUsersToZero(
            @Param("userName") String userName,
            @Param("groupName") String groupName
    );

    @Modifying
    @Query("""
            update
                ExpenseShare es
            set
                es.status = 'PAID'
            where
                es.user.userName = :userName and
                es.expense.group.groupName = :groupName
            """)
    int updateStatusToPaidForUserInGroup(
            @Param("userName") String userName,
            @Param("groupName") String groupName
    );

    @Modifying
    @Query("""
            update
                ExpenseShare es
            set
                es.status = 'PAID'
            where
                es.expense.group.groupName = :groupName
            """)
    int updateStatusToPaidForAllUsersInGroup(
            @Param("groupName") String groupName
    );

}
