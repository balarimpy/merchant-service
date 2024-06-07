package io.movmint.msp.merchant.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ExternalServiceCallException extends MerchantServiceException {

    public static final String CODE_DEFAULT = ExceptionCode.EXTERNAL_SERVICE_CALL.getCode();

    public ExternalServiceCallException() {
        super(CODE_DEFAULT, "Failed to communicate with external service. Please retry or contact support.");
    }

    public ExternalServiceCallException(final String message) {
        super(CODE_DEFAULT, message);
    }

    public ExternalServiceCallException(final String message, Map<String, Object> detail) {
        super(CODE_DEFAULT, message, detail);
    }
}