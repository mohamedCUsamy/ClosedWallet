package com.closedwallet.Repository;

import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    public Optional<Wallet> findByUser(User user);
}
