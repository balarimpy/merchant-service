package io.movmint.msp.merchant.external.kafka.message;

import io.movmint.msp.merchant.data.enums.SDWalletLimit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MSPAccountCreationMessage {
    @NonNull
    private String accountNo;
    @NonNull
    private String accountName;
    @NonNull
    private String customName;
    @NonNull
    private UUID agentId;
    @NonNull
    private UUID businessId;
    private AccountMetadata metadata;
    private SDWalletLimit walletLimit;
    private UUID walletLimitId;
}
