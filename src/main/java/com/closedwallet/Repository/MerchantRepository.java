package com.closedwallet.Repository;

import com.closedwallet.Entity.Merchant;
import com.closedwallet.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
