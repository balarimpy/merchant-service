package io.movmint.msp.merchant.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class MerchantServiceException extends Exception {

    public static final String CODE_DEFAULT = ExceptionCode.GENERIC_ERROR.getCode();

    protected final String code;

    protected String description;

    protected Map<String,Object> detail;

    protected MerchantServiceException(final String message) {
        super(message);
        this.code = CODE_DEFAULT;
        this.description = ExceptionCode.GENERIC_ERROR.getDescription();
    }

    public MerchantServiceException(final String message, Map<String,Object> detail) {
        super(message);
        this.detail = detail;
        this.code = CODE_DEFAULT;
        this.description = ExceptionCode.GENERIC_ERROR.getDescription();
    }

    protected MerchantServiceException(final String code, final String message) {
        super(message);
        this.code = code;
        this.description = ExceptionCode.getByCode(code).getDescription();
    }

    public MerchantServiceException(final String code, final String message, final Map<String,Object> detail) {
        super(message);
        this.detail = detail;
        this.code = code;
        this.description = ExceptionCode.getByCode(code).getDescription();
    }
}
