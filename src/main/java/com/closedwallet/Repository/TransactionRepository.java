package com.closedwallet.Repository;

import com.closedwallet.Entity.Transaction;
import com.closedwallet.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(
            """
        SELECT t
        FROM Transaction t
        WHERE (t.senderWallet = :wallet OR t.receiverWallet = :wallet) AND t.createdAt >= :startDate
"""
    )
    List<Transaction> findByWalletAndDateRange(@Param("wallet") Wallet wallet, @Param("startDate") LocalDateTime startDate);
}
