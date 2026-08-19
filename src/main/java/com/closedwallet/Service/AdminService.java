package com.closedwallet.Service;

import com.closedwallet.Entity.AdminAuditLog;
import com.closedwallet.Entity.Merchant;
import com.closedwallet.Entity.Transaction;
import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Repository.AdminAuditLogRepository;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.dto.AdminActionResponse;
import com.closedwallet.dto.AdminTransactionResponse;
import com.closedwallet.dto.AdminUserResponse;
import com.closedwallet.dto.AdminWalletResponse;
import com.closedwallet.dto.CreateMerchantRequest;
import com.closedwallet.enums.AdminAction;
import com.closedwallet.enums.Role;
import com.closedwallet.enums.WalletStatus;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final AdminAuditLogRepository adminAuditLogRepository;

    public AdminService(UserRepository userRepository,
                        WalletRepository walletRepository,
                        TransactionRepository transactionRepository,
                        MerchantRepository merchantRepository,
                        AdminAuditLogRepository adminAuditLogRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.merchantRepository = merchantRepository;
        this.adminAuditLogRepository = adminAuditLogRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        List<AdminUserResponse> response = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            Wallet wallet = user.getWallet();
            AdminUserResponse item = new AdminUserResponse();
            item.setId(user.getId());
            item.setName(user.getName());
            item.setEmail(user.getEmail());
            item.setPhoneNumber(user.getPhoneNumber());
            item.setRole(user.getRole());
            if (wallet != null) {
                item.setWalletId(wallet.getId());
                item.setBalance(wallet.getBalance());
                item.setWalletStatus(wallet.getStatus());
            }
            response.add(item);
        }
        return response;
    }

    public List<AdminWalletResponse> getAllWallets() {
        List<AdminWalletResponse> response = new ArrayList<>();
        for (Wallet wallet : walletRepository.findAll()) {
            AdminWalletResponse item = new AdminWalletResponse();
            item.setId(wallet.getId());
            item.setBalance(wallet.getBalance());
            item.setCurrency(wallet.getCurrency());
            item.setStatus(wallet.getStatus());
            item.setUserId(wallet.getUser() != null ? wallet.getUser().getId() : null);
            item.setMerchantId(wallet.getMerchant() != null ? wallet.getMerchant().getId() : null);
            response.add(item);
        }
        return response;
    }

    public List<AdminTransactionResponse> getAllTransactions() {
        List<AdminTransactionResponse> response = new ArrayList<>();
        for (Transaction tx : transactionRepository.findAll()) {
            AdminTransactionResponse item = new AdminTransactionResponse();
            item.setId(tx.getId());
            item.setSenderWalletId(tx.getSenderWallet() != null ? tx.getSenderWallet().getId() : null);
            item.setReceiverWalletId(tx.getReceiverWallet() != null ? tx.getReceiverWallet().getId() : null);
            item.setAmount(tx.getAmount());
            item.setType(tx.getType());
            item.setStatus(tx.getStatus());
            item.setReferenceId(tx.getReferenceId());
            item.setCreatedAt(tx.getCreatedAt());
            response.add(item);
        }
        return response;
    }

    @Transactional
    public Merchant createMerchant(CreateMerchantRequest request, Authentication authentication) {
        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());
        merchant.setPhone(request.getPhone());
        merchant.setLogoPath(request.getLogoPath());
        merchant.setCategory(request.getCategory());

        Merchant saved = merchantRepository.save(merchant);

        Wallet wallet = new Wallet();
        wallet.setMerchant(saved);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(com.closedwallet.enums.Currency.EGP);
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);

        saveAuditLog(authentication, AdminAction.CREATE_MERCHANT, "Merchant", saved.getId());

        return saved;
    }

    @Transactional
    public AdminActionResponse freezeWallet(Long walletId, Authentication authentication) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setStatus(WalletStatus.FROZEN);
        walletRepository.save(wallet);

        saveAuditLog(authentication, AdminAction.FREEZE_WALLET, "Wallet", walletId);

        AdminActionResponse response = new AdminActionResponse();
        response.setMessage("Wallet frozen successfully");
        response.setWalletId(walletId);
        response.setStatus(wallet.getStatus().name());
        return response;
    }

    @Transactional
    public AdminActionResponse unfreezeWallet(Long walletId, Authentication authentication) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);

        saveAuditLog(authentication, AdminAction.UNFREEZE_WALLET, "Wallet", walletId);

        AdminActionResponse response = new AdminActionResponse();
        response.setMessage("Wallet unfrozen successfully");
        response.setWalletId(walletId);
        response.setStatus(wallet.getStatus().name());
        return response;
    }

    private void saveAuditLog(Authentication authentication, AdminAction action, String targetEntity, Long targetId) {
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        AdminAuditLog log = new AdminAuditLog();
        log.setAdminId(admin.getId());
        log.setAction(action);
        log.setTargetEntity(targetEntity + ":" + targetId);
        adminAuditLogRepository.save(log);
    }
}
