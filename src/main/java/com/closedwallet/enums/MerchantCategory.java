package com.closedwallet.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum MerchantCategory {
    RESTAURANT,
    GROCERY,
    CLOTHING,
    SERVICES,
    ENTERTAINMENT,
    ELECTRONICS,
    FOOD;

    @JsonCreator
    public static MerchantCategory fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        String upper = normalized.toUpperCase(Locale.ROOT);
        if ("FOOD".equals(upper)) {
            return RESTAURANT;
        }

        return MerchantCategory.valueOf(upper);
    }
}
