package io.movmint.msp.merchant;

import io.movmint.msp.merchant.api.v1.model.request.AccountMetadataReq;
import io.movmint.msp.merchant.api.v1.model.request.AccountReq;
import io.movmint.msp.merchant.api.v1.model.request.BusinessRequest;
import io.movmint.msp.merchant.api.v1.model.request.UserRequest;
import io.movmint.msp.merchant.data.enums.BusinessStatus;
import io.movmint.msp.merchant.data.enums.BusinessType;
import io.movmint.msp.merchant.data.enums.SDWalletLimit;
import io.movmint.msp.merchant.data.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public class TestUtil {

  public static BusinessRequest createHqWalletForTesting(UUID wspId, UUID walletId) {
    return new BusinessRequest(
        "Test Business",
        BusinessType.SIMPLE,
        BusinessStatus.OFFBOARDED,
        wspId,
        new AccountReq("example-accountNo-456",
                "example-accountName",
                "example-customName",
                UUID.randomUUID(),
                new AccountMetadataReq(
                        "Exuma",
                        "F&B"
                ),
                SDWalletLimit.LITE,
                null));
  }

  public static UserRequest createUserRequestForTesting() {
    return new UserRequest(
            "movmint user",
            "712345678",
            "testuser@movmint.com",
            "password",
            UserRole.MANAGER
    );
    }
}
