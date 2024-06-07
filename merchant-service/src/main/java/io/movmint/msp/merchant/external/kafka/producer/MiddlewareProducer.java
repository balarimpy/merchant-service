package io.movmint.msp.merchant.external.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.movmint.msp.merchant.external.kafka.message.MSPAccountCreationMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MiddlewareProducer {
    private static final String ONBOARD_MERCHANT_TOPIC = "onboard_merchant_topic";
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public MiddlewareProducer(KafkaTemplate<Object, String> kafkaTemplate, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    public void accountCreationPublish(MSPAccountCreationMessage message) {
        try {
            kafkaTemplate.send(ONBOARD_MERCHANT_TOPIC, mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Failed to process JSON while publishing account creation message: {}", e);
        }
    }
}
