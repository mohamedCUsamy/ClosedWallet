package com.closedwallet.Service;

import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Repository.AdminAuditLogRepository;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Repository.TransactionRepository;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.dto.CreateMerchantRequest;
import com.closedwallet.enums.MerchantCategory;
import com.closedwallet.enums.WalletStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private AdminAuditLogRepository adminAuditLogRepository;

    @Mock
    private Authentication authentication;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PasswordEncoder passwordEncoder = null;
        adminService = new AdminService(
                userRepository,
                walletRepository,
                transactionRepository,
                merchantRepository,
                adminAuditLogRepository,
                passwordEncoder
        );
    }

    @Test
    void freezeWallet_shouldRejectAlreadyFrozenWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setStatus(WalletStatus.FROZEN);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(authentication.getName()).thenReturn("admin@closedwallet.com");

        User admin = new User();
        admin.setId(50L);
        admin.setEmail("admin@closedwallet.com");
        when(userRepository.findByEmail("admin@closedwallet.com")).thenReturn(Optional.of(admin));

        assertThrows(IllegalStateException.class, () -> adminService.freezeWallet(1L, authentication));
        verify(walletRepository, never()).save(any());
    }

    @Test
    void createMerchant_shouldRejectBlankRequiredFields() {
        CreateMerchantRequest request = new CreateMerchantRequest();
        request.setName("   ");
        request.setEmail("merchant@shop.com");
        request.setPhone("1234567890");
        request.setCategory(MerchantCategory.RESTAURANT);

        when(authentication.getName()).thenReturn("admin@closedwallet.com");

        User admin = new User();
        admin.setId(50L);
        admin.setEmail("admin@closedwallet.com");
        when(userRepository.findByEmail("admin@closedwallet.com")).thenReturn(Optional.of(admin));

        assertThrows(IllegalArgumentException.class, () -> adminService.createMerchant(request, authentication));
        verify(merchantRepository, never()).save(any());
    }
}
