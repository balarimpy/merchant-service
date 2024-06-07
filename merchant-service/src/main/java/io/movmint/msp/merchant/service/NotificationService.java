package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.exception.MerchantServiceException;

public interface NotificationService {
    void sendEmailConformation(String recipient,String userConfirmationUrl) throws MerchantServiceException;
}
