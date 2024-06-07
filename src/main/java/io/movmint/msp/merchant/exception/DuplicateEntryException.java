package io.movmint.msp.merchant.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class DuplicateEntryException extends MerchantServiceException {

    public static final String CODE_DEFAULT = ExceptionCode.DUPLICATE_ENTRY.getCode();

    public DuplicateEntryException() {
        super(CODE_DEFAULT, "duplicate record detected. Please try again.");
    }

    public DuplicateEntryException(final String target) {
        super(CODE_DEFAULT, "duplicate %s. Please try again.".formatted(target));
    }

    public DuplicateEntryException(final String target, Map<String, Object> detail) {
        super(CODE_DEFAULT, "duplicate %s. Please try again.".formatted(target), detail);
    }
}