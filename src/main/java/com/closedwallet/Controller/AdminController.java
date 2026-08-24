package com.closedwallet.Controller;

import com.closedwallet.Entity.Merchant;
import com.closedwallet.Service.AdminService;
import com.closedwallet.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/admin")
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
    @PostMapping("/createadmin")
    public CreateAdminResponse createAdmin(@Valid @RequestBody CreateAdminRequest createAdminRequest, Authentication authentication) throws Exception{
        return adminService.createAdmin(createAdminRequest,authentication);
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
    public CreateMerchantResponse createMerchant(@RequestBody CreateMerchantRequest request, Authentication authentication) {
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
    @GetMapping("/wallets/{id}")
    public AdminWalletResponse getWallet(@PathVariable Long id, Authentication authentication) throws Exception{
        return adminService.getWalletDetails(id,authentication);
    }

    @GetMapping("/users/{id}")
    public AdminUserResponse getUser(@PathVariable Long id, Authentication authentication) throws Exception{
        return adminService.getUserDetails(id,authentication);
    }
    @GetMapping("/merchants/{id}")
    public AdminMerchantResponse getMerchant(@PathVariable Long id, Authentication authentication) throws Exception{
        return adminService.getMerchantDetails(id,authentication);
    }
    @GetMapping("/transactions/{id}")
    public AdminTransactionResponse getTransaction(@PathVariable Long id, Authentication authentication) throws Exception{
        return adminService.getTransactionDetails(id,authentication);
    }
    @GetMapping("/audit-log")
    public List<AdminAuditLogResponse> getAllAuditLogs(Authentication authentication) throws Exception{
        return adminService.getAllAuditLogs(authentication);
    }

}
