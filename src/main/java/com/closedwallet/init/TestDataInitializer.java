package com.closedwallet.init;

import java.math.BigDecimal;

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

/**
 * Inserts a small set of known accounts so the APIs can be tested by hand.
 * Everything here is fixed: same emails, same password, same balances, same ids
 * on every run. Merchants get a wallet too, otherwise a payment to them fails.
 *
 * Users:     user1..user5@test.com, password "password123"
 * Merchants: ids 1..5, all start at 0.00
 */
@Component
public class TestDataInitializer implements CommandLineRunner {

    /** Same password for every test user. */
    public static final String PASSWORD = "password123";

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;

    public TestDataInitializer(UserRepository userRepository, MerchantRepository merchantRepository,
            WalletRepository walletRepository, PasswordEncoder passwordEncoder, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || merchantRepository.count() > 0) {
            System.out.println("[TestDataInitializer] data already present, skipping.");
            return;
        }

        // balance 0.00 on user5 is there to test the "Insufficient balance" path
        createUser("user1@test.com", "User One",   "+201110000001", new BigDecimal("10000.00"));
        createUser("user2@test.com", "User Two",   "+201110000002", new BigDecimal("5000.00"));
        createUser("user3@test.com", "User Three", "+201110000003", new BigDecimal("2500.00"));
        createUser("user4@test.com", "User Four",  "+201110000004", new BigDecimal("100.00"));
        createUser("user5@test.com", "User Five",  "+201110000005", new BigDecimal("0.00"));

        createMerchant("merchant1@test.com", "Cairo Grill",   "+201010000001", MerchantCategory.RESTAURANT);
        createMerchant("merchant2@test.com", "Fresh Basket",  "+201010000002", MerchantCategory.GROCERY);
        createMerchant("TransactionType", "Urban Thread",  "+201010000003", MerchantCategory.CLOTHING);
        createMerchant("merchant4@test.com", "QuickFix",      "+201010000004", MerchantCategory.SERVICES);
        createMerchant("merchant5@test.com", "Tech Point",    "+201010000005", MerchantCategory.ELECTRONICS);

        createAdmin("admin1@test.com", "Admin1",   "+201010000001", KycStatus.APPROVED);
        createAdmin("admin2@test.com", " Admin2",  "+201010000002",  KycStatus.APPROVED);
        createAdmin("admin3@test.com", "Admin3",  "+201010000003",  KycStatus.APPROVED);
        createAdmin("admin4@test.com", "Admin4",      "+201010000004",  KycStatus.APPROVED);
        createAdmin("admin5@test.com", " Admin5",    "+201010000005", KycStatus.APPROVED);

        createTransactions(userRepository.findByEmail("user1@test.com").orElse(null), "merchant1@test.com");
        createTransactions(userRepository.findByEmail("user2@test.com").orElse(null), "merchant2@test.com");
        createTransactions(userRepository.findByEmail("user3@test.com").orElse(null), "user1@test.com");
        createTransactions(userRepository.findByEmail("user4@test.com").orElse(null), "user2@test.com");
        createTransactions(userRepository.findByEmail("user5@test.com").orElse(null), "user3@test.com");

        System.out.println("[TestDataInitializer] inserted 5 users and 5 merchants, all with wallets.");
        System.out.println("[TestDataInitializer] login with user1@test.com .. user5@test.com / " + PASSWORD);
    }
    

    private void createUser(String email, String name, String phone, BigDecimal balance) {
        User user = new User(phone, name, passwordEncoder.encode(PASSWORD), email);
        user.setRole(Role.USER);
        user.setKycStatus(KycStatus.APPROVED);
        userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(balance);
        wallet.setCurrency(Currency.EGP);
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);
    }

    private void createMerchant(String email, String name, String phone, MerchantCategory category) {
        Merchant merchant = new Merchant();
        merchant.setName(name);
        merchant.setEmail(email);
        merchant.setPhone(phone);
        merchant.setCategory(category);
        merchant.setLogoPath("/logos/" + name.toLowerCase().replace(' ', '-') + ".png");
        merchantRepository.save(merchant);

        Wallet wallet = new Wallet();
        wallet.setMerchant(merchant);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(Currency.EGP);
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);
    }

    private void createAdmin(String email, String name,String phone, KycStatus status) {
        User admin = new User();
        admin.setEmail(email);
        admin.setName(name);
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setPhoneNumber(phone);
        admin.setRole(Role.ADMIN);
        admin.setKycStatus(status);
        userRepository.save(admin);
    }

    private void createTransactions(User  user , String receiverEmail){
        Transaction transaction = new Transaction();
        transaction.setSenderWallet(user.getWallet());
        transaction.setReceiverWallet(userRepository.findByEmail(receiverEmail).map(User::getWallet).orElse(null));
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setReferenceId("TXN" + System.currentTimeMillis());
        transaction.setCreatedAt(java.time.LocalDateTime.now());
        transaction.setUpdatedAt(java.time.LocalDateTime.now());
        transactionRepository.save(transaction);
    }
}
