package com.closedwallet.init;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.closedwallet.Entity.Merchant;
import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.enums.Currency;
import com.closedwallet.enums.KycStatus;
import com.closedwallet.enums.MerchantCategory;
import com.closedwallet.enums.Role;
import com.closedwallet.enums.WalletStatus;

@Component
public class TestDataInitializer implements CommandLineRunner {

    public static final String PASSWORD = "password123";

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataInitializer(
            UserRepository userRepository,
            MerchantRepository merchantRepository,
            WalletRepository walletRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        /*
         * Do not generate data if database already contains users.
         */
        if (userRepository.count() > 0) {
            System.out.println("[TestDataInitializer] Data already exists. Skipping.");
            return;
        }

        System.out.println("[TestDataInitializer] Creating test data...");

        // ==========================================
        // 1. USERS
        // ==========================================

        createUser("user1@test.com", "Ahmed Hassan",  "+201111000001", KycStatus.APPROVED, "5000.00", WalletStatus.ACTIVE);
        createUser("user2@test.com", "Sara Mahmoud",  "+201111000002", KycStatus.APPROVED, "1250.50", WalletStatus.ACTIVE);
        createUser("user3@test.com", "Omar Khaled",   "+201111000003", KycStatus.APPROVED,  "300.00", WalletStatus.ACTIVE);
        createUser("user4@test.com", "Mona Adel",     "+201111000004", KycStatus.PENDING,     "0.00", WalletStatus.ACTIVE);
        createUser("user5@test.com", "Youssef Ali",   "+201111000005", KycStatus.APPROVED, "7500.75", WalletStatus.FROZEN);

        // ==========================================
        // 2. MERCHANTS
        // ==========================================

        createMerchant("merchant1@test.com", "Cairo Coffee",     "+201222000001", MerchantCategory.RESTAURANT,    "0.00", WalletStatus.ACTIVE);
        createMerchant("merchant2@test.com", "Nile Market",      "+201222000002", MerchantCategory.GROCERY,     "500.00", WalletStatus.ACTIVE);
        createMerchant("merchant3@test.com", "Zamalek Fashion",  "+201222000003", MerchantCategory.CLOTHING,      "0.00", WalletStatus.ACTIVE);
        createMerchant("merchant4@test.com", "TechZone Egypt",   "+201222000004", MerchantCategory.ELECTRONICS, "250.25", WalletStatus.ACTIVE);
        createMerchant("merchant5@test.com", "Giza Cinema",      "+201222000005", MerchantCategory.ENTERTAINMENT, "0.00", WalletStatus.FROZEN);

        // ==========================================
        // 3. ADMINS
        // ==========================================

        createAdmin("admin1@test.com", "Admin One",   "+201333000001");
        createAdmin("admin2@test.com", "Admin Two",   "+201333000002");
        createAdmin("admin3@test.com", "Admin Three", "+201333000003");
        createAdmin("admin4@test.com", "Admin Four",  "+201333000004");
        createAdmin("admin5@test.com", "Admin Five",  "+201333000005");

        System.out.println("[TestDataInitializer] Created 5 users, 5 merchants, 5 admins.");
        System.out.println("[TestDataInitializer] Test password: " + PASSWORD);
    }

    private void createUser(String email, String name, String phone,
                            KycStatus kycStatus, String balance, WalletStatus walletStatus) {

        User user = new User(phone, name, passwordEncoder.encode(PASSWORD), email);
        user.setRole(Role.USER);
        user.setKycStatus(kycStatus);
        userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal(balance));
        wallet.setCurrency(Currency.EGP);
        wallet.setStatus(walletStatus);
        walletRepository.save(wallet);
    }

    private void createMerchant(String email, String name, String phone,
                                MerchantCategory category, String balance, WalletStatus walletStatus) {

        Merchant merchant = new Merchant();
        merchant.setName(name);
        merchant.setEmail(email);
        merchant.setPhone(phone);
        merchant.setCategory(category);
        merchant.setLogoPath("/logos/" + name.toLowerCase().replace(' ', '-') + ".png");
        merchantRepository.save(merchant);

        Wallet wallet = new Wallet();
        wallet.setMerchant(merchant);
        wallet.setBalance(new BigDecimal(balance));
        wallet.setCurrency(Currency.EGP);
        wallet.setStatus(walletStatus);
        walletRepository.save(wallet);
    }

    private void createAdmin(String email, String name, String phone) {

        User admin = new User(phone, name, passwordEncoder.encode(PASSWORD), email);
        admin.setRole(Role.ADMIN);
        admin.setKycStatus(KycStatus.APPROVED);
        userRepository.save(admin);
    }
}
