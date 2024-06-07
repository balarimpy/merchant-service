package io.movmint.msp.merchant.exception;

import lombok.Getter;

@Getter
public class InvalidParamException extends MerchantServiceException{

    public static final String CODE_DEFAULT = ExceptionCode.REQUEST_INVALID.getCode();

    public InvalidParamException(final String message) {
        super(CODE_DEFAULT, message);
    }
}
