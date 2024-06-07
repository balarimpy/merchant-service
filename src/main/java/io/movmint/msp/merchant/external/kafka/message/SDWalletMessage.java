package io.movmint.msp.merchant.external.kafka.message;

import io.movmint.msp.merchant.data.enums.SDWalletStatus;

import java.util.UUID;

public record SDWalletMessage(SDWalletStatus status, UUID businessId, UUID walletId) {
}
