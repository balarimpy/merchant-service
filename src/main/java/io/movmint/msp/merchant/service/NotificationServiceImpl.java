package io.movmint.msp.merchant.service;

import io.movmint.msp.merchant.exception.MerchantServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Log4j2
@Service
public class NotificationServiceImpl implements NotificationService{
    @Value("notification-service.url.post.email-conformation")
    private String emailConformationUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public NotificationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendEmailConformation(String recipient, String userConfirmationUrl) throws MerchantServiceException {
        restTemplate.getForEntity(
                emailConformationUrl,
                Object.class,
                Map.of("recipient",recipient,"userConfirmationUrl",userConfirmationUrl));
    }
}
