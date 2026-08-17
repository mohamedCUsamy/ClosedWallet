package com.closedwallet.Exception;

public class MerchantNotFoundException extends RuntimeException {

    public MerchantNotFoundException(Long id) {
        super("Merchant with id " + id + " not found");
    }
}
