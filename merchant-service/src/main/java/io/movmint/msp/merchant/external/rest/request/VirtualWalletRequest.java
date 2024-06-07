package io.movmint.msp.merchant.external.rest.request;

import io.movmint.msp.merchant.data.enums.WalletType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record VirtualWalletRequest(
        @NotNull(message = "Name is required")
        @Size(min = 3, max = 100)
        String name,

        String location,

        @NotNull(message = "allocation is required")
        @DecimalMin(value = "0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal allocation,

        @Digits(integer = 10, fraction = 2)
        BigDecimal spendableAmount,

        boolean isReceiveOnly,

        @Valid
        List<VirtualWalletRequest> children,

        WalletType walletType
) {
}
