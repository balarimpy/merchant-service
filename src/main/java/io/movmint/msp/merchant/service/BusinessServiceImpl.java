package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.api.v1.model.request.BusinessRequest;
import io.movmint.msp.merchant.data.entity.Account;
import io.movmint.msp.merchant.data.entity.AccountMetadata;
import io.movmint.msp.merchant.data.entity.Business;
import io.movmint.msp.merchant.data.enums.SDWalletStatus;
import io.movmint.msp.merchant.data.enums.WalletType;
import io.movmint.msp.merchant.data.repository.BusinessRepository;
import io.movmint.msp.merchant.exception.DuplicateEntryException;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.exception.NotFoundException;
import io.movmint.msp.merchant.external.kafka.message.MSPAccountCreationMessage;
import io.movmint.msp.merchant.external.kafka.message.SDWalletMessage;
import io.movmint.msp.merchant.external.kafka.producer.MiddlewareProducer;
import io.movmint.msp.merchant.external.rest.request.VirtualWalletRequest;
import io.movmint.msp.merchant.utils.ExternalRestCallUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Log4j2
@Service
public class BusinessServiceImpl implements BusinessService {
    private final BusinessRepository businessRepository;
    private final MiddlewareProducer middlewareProducer;
    private final ExternalRestCallUtil externalRestCallUtil;

    @Autowired
    public BusinessServiceImpl(BusinessRepository businessRepository,
                               MiddlewareProducer middlewareProducer,
                               ExternalRestCallUtil externalRestCallUtil) {
        this.businessRepository = businessRepository;
        this.middlewareProducer = middlewareProducer;
        this.externalRestCallUtil = externalRestCallUtil;
    }

    /**
     * Create Business details and link with hqWallets.
     * Date: 2024-05-02
     *
     * @param businessRequest {@link io.movmint.msp.merchant.api.v1.model.request.BusinessRequest}
     * @return BusinessResponse with the created business details if success,
     * or throw MerchantServiceException based on the validation.
     */
    @Transactional
    public Business create(BusinessRequest businessRequest) throws MerchantServiceException {
        // Check is name already exists in DB
        if (businessRepository.existsByName(businessRequest.name())) {
            log.error("name [%s] already exists ", businessRequest.name());
            throw new DuplicateEntryException("name");
        }
        // Save the business entity to the database and return saved business entity.
        log.info("saving business " + businessRequest.name());
        Business business = businessRepository.save(createBusinessFactory(businessRequest));
        MSPAccountCreationMessage message = new MSPAccountCreationMessage();
        message.setAccountName(business.getAccount().getAccountName());
        message.setAccountNo(business.getAccount().getAccountNo());
        message.setAgentId(business.getAccount().getAgentId());
        message.setBusinessId(UUID.fromString(business.getId()));
        message.setCustomName(business.getAccount().getCustomName());
        message.setWalletLimit(business.getAccount().getWalletLimit());
        message.setWalletLimitId(business.getAccount().getWalletLimitId());
        message.setMetadata(
                new io.movmint.msp.merchant.external.kafka.message.AccountMetadata(
                        business.getAccount().getMetadata().getDistrict()
                        , business.getAccount().getMetadata().getIndustry()
                ));
        middlewareProducer.accountCreationPublish(message);
        externalRestCallUtil.createHQWalletInVWS(new VirtualWalletRequest(
                businessRequest.name(),
                null,
                businessRequest.allocation(),
                BigDecimal.ZERO,
                false,
                null,
                WalletType.ROOT_WALLET
        ), businessRequest.wspId());
        return business;
    }

    /**
     * Convert Business request record to Business entity.
     * Date: 2024-05-03
     *
     * @param businessRequest {@link BusinessRequest}
     * @return Business with the Account and Wallet details
     */
    private Business createBusinessFactory(BusinessRequest businessRequest) {
        if (businessRequest == null) {
            return null;
        }
        Business business = Business.builder()
                .name(businessRequest.name())
                .wspId(businessRequest.wspId())
                .status(businessRequest.status())
                .type(businessRequest.type()).build();

        if (businessRequest.account() != null) {
            Account account = Account.builder()
                    .agentId(businessRequest.account().agentId())
                    .accountName(businessRequest.account().accountName())
                    .accountNo(businessRequest.account().accountNo())
                    .customName(businessRequest.account().customName())
                    .walletLimit(businessRequest.account().walletLimit())
                    .walletLimitId(businessRequest.account().walletLimitId())
                    .status(SDWalletStatus.PENDING_VERIFICATION)
                    .build();

            // add wallet if the account not null and wallet not null in request
            if (businessRequest.account().metadata() != null) {
                account.setMetadata(
                        AccountMetadata.builder()
                                .industry(businessRequest.account().metadata().industry())
                                .district(businessRequest.account().metadata().district())
                                .build()
                );
            }
            business.setAccount(account);
        }
        return business;
    }

    /**
     * Update Business account details bool wallet id from kafka consumer.
     * Date: 2024-05-31
     *
     * @param sdWalletMessage {@link SDWalletMessage}
     * @return void,
     * @throw MerchantServiceException.
     */
    @Override
    public void updateWalletReference(SDWalletMessage sdWalletMessage) throws MerchantServiceException {
        Business business = businessRepository.findById(sdWalletMessage.businessId().toString())
                .orElseThrow(() -> new NotFoundException("business id " + sdWalletMessage.businessId().toString()));
        business.getAccount().setStatus(sdWalletMessage.status());
        if (sdWalletMessage.status().equals(SDWalletStatus.SUCCESS))
            business.getAccount().setReferenceId(sdWalletMessage.walletId());
        businessRepository.save(business);
        log.info("update business with account status {}", sdWalletMessage.status());
    }
}
