package io.movmint.msp.merchant.api.v1.model.request;

import io.movmint.msp.merchant.data.enums.BusinessStatus;
import io.movmint.msp.merchant.data.enums.BusinessType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a business request.
 * Date: 2024-05-02
 *
 * This record encapsulates various details related to a business request.
 * It includes the {@link #name}, {@link #type}, {@link #status}, {@link #wspId},
 * {@link #account} it refer to {@link AccountReq},
 * and {@link #wallet} it refer to {@link WalletReq} information.
 */
public record BusinessRequest(
        @NotBlank(message = "Name is required")
        String name,
        BusinessType type,

        @NotNull(message = "allocation is required")
        @DecimalMin(value = "0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal allocation,

        BusinessStatus status,
        @NotNull(message = "wspId is required")
        UUID wspId,
        @NotNull(message = "Account is required")
        AccountReq account
) {
}
