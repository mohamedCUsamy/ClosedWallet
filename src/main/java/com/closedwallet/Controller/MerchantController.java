package com.closedwallet.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;


import org.springframework.web.bind.annotation.PathVariable;
import com.closedwallet.Service.MerchantService;
import com.closedwallet.dto.MerchantResponse;
import com.closedwallet.enums.MerchantCategory;
import com.closedwallet.Entity.Merchant;

@RestController("/api/auth")
public class MerchantController {

    MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

     @GetMapping("/merchants")
    public List<MerchantResponse> getMerchants() {
        return merchantService.getAllMerchants();
    }

    @GetMapping("/merchants/{id}")
    public MerchantResponse getMerchantById(@PathVariable Long id){
        return merchantService.getMerchantById(id);
    }

    @GetMapping("/merchants/category/{category}")
    public List<MerchantResponse> getMerchantsByCategory(@PathVariable MerchantCategory category){
        return merchantService.getMerchantsByCategory(category);
    }

    @GetMapping("/home")
    public String hello(){ 
        return "hello application";
    }
 
    
}
