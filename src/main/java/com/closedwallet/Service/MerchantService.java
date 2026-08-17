package com.closedwallet.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.closedwallet.Entity.Merchant;
import java.util.List;

import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.Exception.MerchantNotFoundException;
import com.closedwallet.dto.MerchantResponse;
import java.util.ArrayList;

@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<MerchantResponse> getAllMerchants() {
        List<Merchant> merchants = merchantRepository.findAll();
        List<MerchantResponse> merchantResponses = new ArrayList<>();

        for(Merchant merchant: merchants){
            MerchantResponse merchantResponse = new MerchantResponse();
            merchantResponse.setId(merchant.getId());
            merchantResponse.setName(merchant.getName());
            merchantResponse.setCategory(merchant.getCategory());
            merchantResponse.setLogoPath(merchant.getLogoPath());
            merchantResponses.add(merchantResponse);
        }

        return merchantResponses;
    }

    public MerchantResponse getMerchantById(Long id) {

        Merchant merchant = merchantRepository.findById(id).orElse(null);
        MerchantResponse merchantResponse = new MerchantResponse();

        if (merchant == null) {
            throw new MerchantNotFoundException(id);
        }
        merchantResponse.setId(merchant.getId());
        merchantResponse.setName(merchant.getName());
        merchantResponse.setCategory(merchant.getCategory());
        merchantResponse.setLogoPath(merchant.getLogoPath());

        return merchantResponse;

    }

    
}
