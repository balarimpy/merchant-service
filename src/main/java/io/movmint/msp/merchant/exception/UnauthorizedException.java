package io.movmint.msp.merchant.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException  extends MerchantServiceException {

    public static final String CODE_DEFAULT = ExceptionCode.UNAUTHORIZED_ERROR.getCode();
    public UnauthorizedException() {
        super(CODE_DEFAULT, "Access denied. You are not authorized to access this resource.");
    }

    public UnauthorizedException(final String message) {
        super(CODE_DEFAULT, message);
    }
}
