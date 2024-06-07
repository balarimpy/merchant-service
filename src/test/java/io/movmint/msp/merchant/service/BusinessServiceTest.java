package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.MerchantApplicationTests;
import io.movmint.msp.merchant.TestUtil;
import io.movmint.msp.merchant.api.v1.model.request.BusinessRequest;
import io.movmint.msp.merchant.data.entity.Account;
import io.movmint.msp.merchant.data.entity.AccountMetadata;
import io.movmint.msp.merchant.data.entity.Business;
import io.movmint.msp.merchant.data.enums.SDWalletStatus;
import io.movmint.msp.merchant.exception.DuplicateEntryException;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
public class BusinessServiceTest extends MerchantApplicationTests {
    @Autowired
    private BusinessService businessService;

    public static String HQ_WALLET_ID = "6630f19f4757b43a54eba5c1";
    public static UUID WSP_ID = UUID.randomUUID();

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
    }

    @Test
    @DisplayName(
            "Create business : Success if Given business object to save in mongo db and update HqWallet")
    public void createBusiness_Success() throws MerchantServiceException {
        BusinessRequest request = TestUtil.createHqWalletForTesting(WSP_ID, UUID.randomUUID());

        Business business =
                Business.builder()
                        .id(UUID.randomUUID().toString())
                        .name(request.name())
                        .wspId(WSP_ID)
                        .status(request.status())
                        .type(request.type())
                        .account(
                                Account.builder()
                                        .agentId(request.account().agentId())
                                        .accountName(request.account().accountName())
                                        .accountNo(request.account().accountNo())
                                        .customName(request.account().customName())
                                        .walletLimit(request.account().walletLimit())
                                        .walletLimitId(request.account().walletLimitId())
                                        .status(SDWalletStatus.PENDING_VERIFICATION)
                                        .metadata(new AccountMetadata(
                                                request.account().metadata().district(),
                                                request.account().metadata().industry()
                                        ))
                                        .build()
                        )
                        .build();

        when(businessRepository.save(any(Business.class))).thenReturn(business);
        Business response = businessService.create(request);

        assertThat(response.getName()).isEqualTo(request.name());
        assertThat(response.getWspId()).isEqualTo(request.wspId());

    }

    @Test
    @DisplayName("Create business : Fail when existing business name insert")
    public void createBusiness_duplicate_fail() throws MerchantServiceException {
        BusinessRequest request = TestUtil.createHqWalletForTesting(WSP_ID, UUID.randomUUID());
        when(businessRepository.existsByName("Test Business")).thenReturn(true);
        assertThatThrownBy(() -> businessService.create(request)).isInstanceOf(DuplicateEntryException.class);
    }

}
