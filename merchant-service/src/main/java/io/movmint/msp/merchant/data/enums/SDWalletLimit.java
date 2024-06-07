package io.movmint.msp.merchant.data.enums;

import lombok.Getter;

@Getter
public enum SDWalletLimit {
    LITE("Simplified KYC"),
    REGULAR("Regular KYC"),
    ENHANCED("Enhanced KYC"),
    CUSTOM("Custom");

    private final String value;
    SDWalletLimit(String value) {
        this.value = value;
    }


}
