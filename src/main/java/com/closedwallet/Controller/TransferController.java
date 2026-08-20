package com.closedwallet.Controller;
import com.closedwallet.Service.TransferService;
import com.closedwallet.dto.TransferRequest;
import com.closedwallet.dto.TransferResponse;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        //transferService.transfer(request);

        return new TransferResponse(
                "Transfer successful",
                request.getSenderWalletId(),
                request.getAmount()
        );
    }
}
