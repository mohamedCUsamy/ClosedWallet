package com.closedwallet.Controller;

import com.closedwallet.Entity.Merchant;
import com.closedwallet.Service.AdminService;
import com.closedwallet.dto.AdminActionResponse;
import com.closedwallet.dto.AdminTransactionResponse;
import com.closedwallet.dto.AdminUserResponse;
import com.closedwallet.dto.AdminWalletResponse;
import com.closedwallet.dto.CreateMerchantRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/wallets")
    public List<AdminWalletResponse> getAllWallets() {
        return adminService.getAllWallets();
    }

    @GetMapping("/transactions")
    public List<AdminTransactionResponse> getAllTransactions() {
        return adminService.getAllTransactions();
    }

    @PostMapping("/merchants")
    public Merchant createMerchant(@RequestBody CreateMerchantRequest request, Authentication authentication) {
        return adminService.createMerchant(request, authentication);
    }

    @PostMapping("/wallets/{id}/freeze")
    public AdminActionResponse freezeWallet(@PathVariable Long id, Authentication authentication) {
        return adminService.freezeWallet(id, authentication);
    }

    @PostMapping("/wallets/{id}/unfreeze")
    public AdminActionResponse unfreezeWallet(@PathVariable Long id, Authentication authentication) {
        return adminService.unfreezeWallet(id, authentication);
    }
}
