package com.closedwallet.init;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.closedwallet.Entity.Merchant;
import com.closedwallet.Entity.Transaction;
import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.enums.Currency;
import com.closedwallet.enums.KycStatus;
import com.closedwallet.enums.MerchantCategory;
import com.closedwallet.enums.Role;
import com.closedwallet.enums.TransactionStatus;
import com.closedwallet.enums.TransactionType;
import com.closedwallet.enums.WalletStatus;

@Component
public class TestDataInitializer implements CommandLineRunner {

    public static final String PASSWORD = "password123";
    private static final int USER_ONE_TRANSACTION_COUNT = 100;

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;

    private final Random random = new Random();

    public TestDataInitializer(
            UserRepository userRepository,
            MerchantRepository merchantRepository,
            WalletRepository walletRepository,
            PasswordEncoder passwordEncoder,
            TransactionRepository transactionRepository) {

        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionRepository = transactionRepository;
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

        List<User> users = new ArrayList<>();
        List<Wallet> userWallets = new ArrayList<>();
        List<Merchant> merchants = new ArrayList<>();
        List<Wallet> merchantWallets = new ArrayList<>();

        // ==========================================
        // 1. CREATE USERS
        // ==========================================

        for (int i = 1; i <= 20; i++) {

            User user = new User(
                    "+2011100000" + String.format("%02d", i),
                    "User " + i,
                    passwordEncoder.encode(PASSWORD),
                    "user" + i + "@test.com"
            );

            user.setRole(Role.USER);
            user.setKycStatus(KycStatus.APPROVED);

            userRepository.save(user);

            Wallet wallet = new Wallet();
            wallet.setUser(user);

            // Give users different balances
            BigDecimal balance = new BigDecimal(
                    random.nextInt(10000) + ".00"
            );

            wallet.setBalance(balance);
            wallet.setCurrency(Currency.EGP);
            wallet.setStatus(WalletStatus.ACTIVE);

            walletRepository.save(wallet);

            users.add(user);
            userWallets.add(wallet);
        }

        // ==========================================
        // 2. CREATE MERCHANTS
        // ==========================================

        MerchantCategory[] categories = MerchantCategory.values();

        for (int i = 1; i <= 10; i++) {

            Merchant merchant = new Merchant();

            merchant.setName("Merchant " + i);
            merchant.setEmail("merchant" + i + "@test.com");
            merchant.setPhone(
                    "+2010100000" + String.format("%02d", i)
            );

            merchant.setCategory(
                    categories[random.nextInt(categories.length)]
            );

            merchant.setLogoPath(
                    "/logos/merchant-" + i + ".png"
            );

            merchantRepository.save(merchant);

            Wallet wallet = new Wallet();

            wallet.setMerchant(merchant);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setCurrency(Currency.EGP);
            wallet.setStatus(WalletStatus.ACTIVE);

            walletRepository.save(wallet);

            merchants.add(merchant);
            merchantWallets.add(wallet);
        }

        // ==========================================
        // 3. CREATE ADMINS
        // ==========================================

        for (int i = 1; i <= 5; i++) {

            User admin = new User();

            admin.setEmail("admin" + i + "@test.com");
            admin.setName("Admin " + i);
            admin.setPassword(passwordEncoder.encode(PASSWORD));
            admin.setPhoneNumber(
                    "+2010200000" + String.format("%02d", i)
            );

            admin.setRole(Role.ADMIN);
            admin.setKycStatus(KycStatus.APPROVED);

            userRepository.save(admin);
        }

        // ==========================================
// 4. CREATE TRANSACTIONS
// ==========================================

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 200; i++) {

            // ==========================================
            // SENDER
            // ==========================================

            // Give User 1 enough outgoing transactions to be visible in reports.
            // The remaining transactions still use a random sender.
            int senderIndex = i < USER_ONE_TRANSACTION_COUNT
                    ? 0
                    : random.nextInt(userWallets.size());

            // ==========================================
            // RECEIVER
            // ==========================================

            int receiverIndex;

            do {
                receiverIndex = random.nextInt(userWallets.size());
            } while (receiverIndex == senderIndex);

            Wallet sender = userWallets.get(senderIndex);
            Wallet receiver = userWallets.get(receiverIndex);

            // ==========================================
            // TRANSACTION
            // ==========================================

            Transaction transaction = new Transaction();

            transaction.setSenderWallet(sender);
            transaction.setReceiverWallet(receiver);

            // Amount between 50 and 1500
            BigDecimal amount = BigDecimal.valueOf(
                    50 + random.nextInt(1451)
            );

            transaction.setAmount(amount);

            // Random transaction type
            TransactionType[] types = TransactionType.values();

            transaction.setType(
                    types[random.nextInt(types.length)]
            );

            // Mostly successful transactions
            int statusRandom = random.nextInt(100);

            if (statusRandom < 80) {
                transaction.setStatus(TransactionStatus.SUCCESS);
            } else if (statusRandom < 90) {
                transaction.setStatus(TransactionStatus.PENDING);
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
            }

            transaction.setReferenceId(
                    UUID.randomUUID().toString()
            );

            // ==========================================
            // DATE
            // ==========================================

            /*
             * Force transactions to be distributed
             * across the last 30 days.
             *
             * Every transaction gets a day between:
             *
             * today
             * and
             * 29 days ago
             */

            int daysAgo = i % 30;

            int hour = 8 + random.nextInt(12); // 08:00 - 19:59
            int minute = random.nextInt(60);
            int second = random.nextInt(60);

            LocalDateTime transactionDate = now
                    .minusDays(daysAgo)
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(second)
                    .withNano(0);

            transaction.setCreatedAt(transactionDate);
            transaction.setUpdatedAt(transactionDate);

            transactionRepository.save(transaction);
        }

        System.out.println(
                "[TestDataInitializer] Created 20 users."
        );

        System.out.println(
                "[TestDataInitializer] Created 10 merchants."
        );

        System.out.println(
                "[TestDataInitializer] Created 5 admins."
        );

        System.out.println(
                "[TestDataInitializer] Created 200 transactions."
        );

        System.out.println(
                "[TestDataInitializer] Test password: "
                        + PASSWORD
        );
    }
}
