package io.movmint.msp.merchant.external.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.external.kafka.message.SDWalletMessage;
import io.movmint.msp.merchant.service.BusinessService;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@Service
public class MiddlewareConsumer {
    private final String MS_WALLET_REFERENCE_TOPIC = "ms-business-wallet-reference-topic";
    private static final String ONBOARD_MERCHANT_GROUP = "sd_onboard_group";
    private final BusinessService businessService;
    private final ObjectMapper mapper;

    public MiddlewareConsumer(BusinessService businessService, ObjectMapper mapper) {
        this.businessService = businessService;
        this.mapper = mapper;
    }

    /**
     * bool wallet reference listener.
     * Date: 2024-05-31
     *
     * @param message {@link SDWalletMessage} in json string form
     * @return void,
     * @throw MerchantServiceException.
     */
    @KafkaListener(topics = MS_WALLET_REFERENCE_TOPIC, groupId = ONBOARD_MERCHANT_GROUP)
    public void msWalletReferenceListener(String message) throws MerchantServiceException {
        log.info("Received MS wallet reference: {}" , message);
        try {
            businessService.updateWalletReference(mapper.readValue(message, SDWalletMessage.class));
        } catch (IOException e) {
            log.error("Error occurred while processing the wallet reference message: {}" , e.getMessage());
        }
    }
}
