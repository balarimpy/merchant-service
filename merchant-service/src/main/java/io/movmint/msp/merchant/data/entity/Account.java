package io.movmint.msp.merchant.data.entity;

import io.movmint.msp.merchant.data.enums.SDWalletLimit;
import io.movmint.msp.merchant.data.enums.SDWalletStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String accountName;
    private String accountNo;
    private String customName;
    private UUID agentId;
    private AccountMetadata metadata;
    //this is reference to the pool walletId
    private UUID referenceId;
    private SDWalletLimit walletLimit;
    private UUID walletLimitId;
    private SDWalletStatus status;
}
