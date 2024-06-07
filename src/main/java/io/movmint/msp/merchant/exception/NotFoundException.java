package io.movmint.msp.merchant.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class NotFoundException extends MerchantServiceException {

    public static final String CODE_DEFAULT = ExceptionCode.RESOURCE_NOT_FOUND.getCode();

    public NotFoundException(final String target) {
        super(CODE_DEFAULT, "%s not found".formatted(target));
    }

    public NotFoundException(final String target, Map<String, Object> detail) {
        super(CODE_DEFAULT, "%s not found".formatted(target), detail);
    }
}