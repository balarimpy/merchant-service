package io.movmint.msp.merchant.api.v1.model.request;

import io.movmint.msp.merchant.data.enums.SDWalletLimit;
import lombok.NonNull;

import java.util.UUID;

/**
 * Represents an Account request for business.
 *  Date: 2024-05-02
 *
 * This record encapsulates various details related to an Account request.
 * It includes the {@link #id} and {@link #accountNo} information.
 */
public record AccountReq(
        @NonNull
        String accountNo,
        @NonNull
        String accountName,
        @NonNull
        String customName,
        @NonNull
        UUID agentId,
        AccountMetadataReq metadata,
        SDWalletLimit walletLimit,
        UUID walletLimitId) {
}
