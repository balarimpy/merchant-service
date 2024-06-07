package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.api.v1.model.request.BusinessRequest;
import io.movmint.msp.merchant.data.entity.Business;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.external.kafka.message.SDWalletMessage;

public interface BusinessService {
    Business create(BusinessRequest businessRequest) throws MerchantServiceException;

    void updateWalletReference(SDWalletMessage sdWalletMessage) throws MerchantServiceException;
}
