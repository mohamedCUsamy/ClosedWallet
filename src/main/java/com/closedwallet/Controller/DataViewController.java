package com.closedwallet.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.closedwallet.Entity.Transaction;
import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Entity.Merchant;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * View-only endpoints to inspect database content live
 * Useful for development and testing without external DB tools
 */
@RestController
@RequestMapping("/api/db")
public class DataViewController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;

    public DataViewController(UserRepository userRepository, WalletRepository walletRepository, TransactionRepository transactionRepository, MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.merchantRepository = merchantRepository;
    }

    // ============ USERS ============
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @GetMapping("/users/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/users/count")
    public long getUserCount() {
        return userRepository.count();
    }

    // ============ WALLETS ============
    
    @GetMapping("/wallets")
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @GetMapping("/wallets/{id}")
    public Optional<Wallet> getWalletById(@PathVariable Long id) {
        return walletRepository.findById(id);
    }

    @GetMapping("/wallets/count")
    public long getWalletCount() {
        return walletRepository.count();
    }

    // ============ TRANSACTIONS ============
    
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/transactions/{id}")
    public Optional<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionRepository.findById(id);
    }

    @GetMapping("/transactions/count")
    public long getTransactionCount() {
        return transactionRepository.count();
    }

    // ============ MERCHANTS ============

    @GetMapping("/merchants")
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    @GetMapping("/merchants/{id}")
    public Optional<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantRepository.findById(id);
    }

    @GetMapping("/merchants/count")
    public long getMerchantCount() {
        return merchantRepository.count();
    }

    // ============ COMBINED ============
    
    @GetMapping("/user/{id}/wallet")
    public Optional<Wallet> getUserWallet(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return Optional.ofNullable(user.get().getUserWallet());
        }
        return Optional.empty();
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("users", userRepository.count());
        stats.put("merchants", merchantRepository.count());
        stats.put("wallets", walletRepository.count());
        stats.put("transactions", transactionRepository.count());
        return stats;
    }
}
