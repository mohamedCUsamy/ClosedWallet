package com.closedwallet.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.closedwallet.Service.PaymentService;
import com.closedwallet.dto.*;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class PaymentController {

    public PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/api/payments")
    public PaymentResponse Payment(@RequestBody PaymentRequest paymentRequest, Authentication authentication) {
        PaymentResponse status = new PaymentResponse();
        
        String email = authentication.getName();
        status = paymentService.processPayment(paymentRequest, email);
 
        return status;
    }
    
}
