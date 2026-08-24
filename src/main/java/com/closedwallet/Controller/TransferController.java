package com.closedwallet.Controller;
import com.closedwallet.Service.TransferService;
import com.closedwallet.dto.TransactionsResponse;
import com.closedwallet.dto.TransferRequest;
import com.closedwallet.dto.TransferResponse;
import com.closedwallet.dto.WeeklySpendingResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transactions/transfer")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request, Authentication authentication) {
        return transferService.transfer(request, authentication);
    }

    @GetMapping("/transactions/weekly-spending")
    public WeeklySpendingResponse getWeeklySpending(Authentication authentication) {
        return transferService.getWeeklySpending(authentication);
    }

    @GetMapping("/transactions")
    public List<TransactionsResponse> getAllTransactions(Authentication authentication) {
        return transferService.getAllTransactions(authentication);
    }



}
