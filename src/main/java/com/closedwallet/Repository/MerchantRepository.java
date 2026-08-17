package com.closedwallet.Repository;

import java.util.List;
import com.closedwallet.enums.MerchantCategory;
import com.closedwallet.Entity.Merchant;
import com.closedwallet.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    List<Merchant> findByCategory(MerchantCategory category);


}
