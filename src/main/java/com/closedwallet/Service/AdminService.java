package com.closedwallet.Service;

import com.closedwallet.Entity.AdminAuditLog;
import com.closedwallet.Entity.Merchant;
import com.closedwallet.Entity.Transaction;
import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Exception.AdminResourceNotFoundException;
import com.closedwallet.Exception.UserExisitsException;
import com.closedwallet.Repository.AdminAuditLogRepository;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.dto.*;
import com.closedwallet.enums.AdminAction;
import com.closedwallet.enums.Role;
import com.closedwallet.enums.WalletStatus;
import com.fasterxml.jackson.core.Base64Variant;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final AdminAuditLogRepository adminAuditLogRepository;
    private final PasswordEncoder passwordEncoder;
    public AdminService(UserRepository userRepository,
                        WalletRepository walletRepository,
                        TransactionRepository transactionRepository,
                        MerchantRepository merchantRepository,
                        AdminAuditLogRepository adminAuditLogRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.merchantRepository = merchantRepository;
        this.adminAuditLogRepository = adminAuditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminUserResponse> getAllUsers() {
        List<AdminUserResponse> response = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            Wallet wallet = user.getUserWallet();
            AdminUserResponse item;
            if(user.getRole()== null){
                continue;
            }
            if((user.getRole() == Role.USER) && wallet != null){
                item = new AdminUserResponse();
                item.setId(user.getId());
                item.setName(user.getName());
                item.setEmail(user.getEmail());
                item.setPhoneNumber(user.getPhoneNumber());
                item.setRole(user.getRole());
                item.setWalletId(wallet.getId());
                item.setBalance(wallet.getBalance());
                item.setWalletStatus(wallet.getStatus());
                response.add(item);
            }


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

        if (wallet.getUser() != null) {
            item.setUserId(wallet.getUser().getId());
        }

        if (wallet.getMerchant() != null) {
            item.setMerchantId(wallet.getMerchant().getId());
        }

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
    public CreateMerchantResponse createMerchant(CreateMerchantRequest request, Authentication authentication) {
        if (request == null) {
            throw new IllegalArgumentException("Merchant request is required");
        }

        String name = request.getName() == null ? "" : request.getName().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String phone = request.getPhone() == null ? "" : request.getPhone().trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Merchant name is required");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Merchant email is required");
        }
        if (phone.isEmpty()) {
            throw new IllegalArgumentException("Merchant phone is required");
        }
        if (request.getCategory() == null) {
            throw new IllegalArgumentException("Merchant category is required");
        }

        Merchant merchant = new Merchant();
        merchant.setName(name);
        merchant.setEmail(email);
        merchant.setPhone(phone);
        merchant.setLogoPath(request.getLogoPath());
        merchant.setCategory(request.getCategory());

        Merchant saved = merchantRepository.save(merchant);
        if (saved == null || saved.getId() == null) {
            throw new IllegalStateException("Merchant could not be created");
        }

        Wallet wallet = new Wallet();
        wallet.setMerchant(saved);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(com.closedwallet.enums.Currency.EGP);
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);

        saveAuditLog(authentication, AdminAction.CREATE_MERCHANT, "Merchant", saved.getId());
        CreateMerchantResponse response = new CreateMerchantResponse("200","success");

        return response;
    }

    @Transactional
    public AdminActionResponse freezeWallet(Long walletId, Authentication authentication) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new AdminResourceNotFoundException("Wallet not found"));

        if (wallet.getStatus() == WalletStatus.FROZEN) {
            throw new IllegalStateException("Wallet is already frozen");
        }

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
            .orElseThrow(() -> new AdminResourceNotFoundException("Wallet not found"));

        if (wallet.getStatus() == WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is already active");
        }

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

    public CreateAdminResponse createAdmin(CreateAdminRequest createAdminRequest, Authentication authentication) throws Exception {
        if (userRepository.existsByEmail(createAdminRequest.getEmail())) {
            throw new UserExisitsException("Email already exists");
        }

        User admin = new User();
        admin.setEmail(createAdminRequest.getEmail());
        admin.setPassword(passwordEncoder.encode(createAdminRequest.getPassword()));
        admin.setRole(Role.ADMIN);
        User savedAdmin = userRepository.save(admin);

        User actor = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        AdminAuditLog log = new AdminAuditLog();
        log.setAdminId(actor.getId());
        log.setAction(AdminAction.CREATE_ADMIN);
        log.setTargetEntity("User:" + savedAdmin.getId());
        adminAuditLogRepository.save(log);

        CreateAdminResponse response = new CreateAdminResponse("200", "Admin created successfully");
        return response;
    }

    public AdminWalletResponse getWalletDetails(Long id, Authentication authentication) throws Exception {

        Wallet wallet = walletRepository.findById(id).orElseThrow();
        AdminWalletResponse response = new AdminWalletResponse();
        response.setBalance(wallet.getBalance());
        response.setCurrency(wallet.getCurrency());
        response.setStatus(wallet.getStatus());
        response.setId(wallet.getId());
        if(wallet.getMerchant() == null){
            response.setMerchantId(null);
        }
        else{
            response.setMerchantId(wallet.getMerchant().getId());
        }
        if(wallet.getUser() == null){
            response.setUserId(null);
        }
        else{
            response.setUserId(wallet.getUser().getId());
        }
        AdminAuditLog log = new AdminAuditLog();
        log.setAction(AdminAction.FETCH_WALLET);
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        log.setAdminId((admin.getId()));
        log.setTargetEntity("wallet " +  wallet.getId());
        adminAuditLogRepository.save(log);
        return response;
    }

    public AdminUserResponse getUserDetails(Long id, Authentication authentication) {
        User user = userRepository.findById(id).orElseThrow();
        AdminUserResponse response = new AdminUserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setWalletId(user.getWallet().getId());
        response.setBalance(user.getWallet().getBalance());
        response.setWalletStatus(user.getWallet().getStatus());

        AdminAuditLog log = new AdminAuditLog();
        log.setAction(AdminAction.FETCH_USER);
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        log.setAdminId((admin.getId()));
        log.setTargetEntity("User " +  user.getId());
        adminAuditLogRepository.save(log);
        return response;
    }

    public AdminMerchantResponse getMerchantDetails(Long id, Authentication authentication) {
        Merchant merchant = merchantRepository.findById(id).orElseThrow();
        AdminMerchantResponse response = new AdminMerchantResponse();
        response.setId(merchant.getId());
        response.setName(merchant.getName());
        response.setEmail(merchant.getEmail());
        response.setPhoneNumber(merchant.getPhone());
        response.setCategory(merchant.getCategory());
        response.setWalletId(merchant.getWallet().getId());
        response.setBalance(merchant.getWallet().getBalance());
        response.setWalletStatus(merchant.getWallet().getStatus());

        AdminAuditLog log = new AdminAuditLog();
        log.setAction(AdminAction.FETCH_MERCHANT);
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        log.setAdminId((admin.getId()));
        log.setTargetEntity("Merchant " +  merchant.getId());
        adminAuditLogRepository.save(log);
        return response;
    }

    public AdminTransactionResponse getTransactionDetails(Long id, Authentication authentication) throws Exception {
        Transaction transaction = transactionRepository.findById(id).orElseThrow();
        AdminTransactionResponse  response = new AdminTransactionResponse();
        response.setId(transaction.getId());
        response.setSenderWalletId(transaction.getSenderWallet().getId());
        response.setReceiverWalletId(transaction.getReceiverWallet().getId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setType(transaction.getType());
        response.setReferenceId(transaction.getReferenceId());
        response.setCreatedAt(transaction.getCreatedAt());

        AdminAuditLog log = new AdminAuditLog();
        log.setAction(AdminAction.FETCH_TRANSACTIONS);
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        log.setAdminId((admin.getId()));
        log.setTargetEntity("Transaction  " +  transaction.getId());
        adminAuditLogRepository.save(log);
        return response;
    }

    public List<AdminAuditLogResponse> getAllAuditLogs(Authentication authentication) {
        List<AdminAuditLogResponse> response = new ArrayList<>();
        for (AdminAuditLog log : adminAuditLogRepository.findAll()) {
            AdminAuditLogResponse item = new AdminAuditLogResponse();
            item.setId(log.getId());
            item.setAdminId(log.getAdminId());
            item.setAction(log.getAction());
            item.setTargetEntity(log.getTargetEntity());
            item.setTimestamp(log.getTimestamp());
            response.add(item);
        }
        AdminAuditLog log = new AdminAuditLog();
        log.setAction(AdminAction.FETCH_LOG);
        User admin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        log.setAdminId((admin.getId()));
        log.setTargetEntity("Admin Audit-log ");
        adminAuditLogRepository.save(log);
        return response;
    }
}
