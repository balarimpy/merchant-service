package io.movmint.msp.merchant.external.rest.response;

import io.movmint.msp.merchant.data.enums.WalletType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
    public String name;
    public String location;
    public UUID id;
    public BigDecimal allocation;
    public BigDecimal spendableAmount;
    public boolean isReceiveOnly;
    public WalletType walletType;
    public List<WalletResponse> children;
    public ErrorResponse errorResponse;
}
