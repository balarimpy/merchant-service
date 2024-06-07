package io.movmint.msp.merchant.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    SERVER_ERROR("MVC-0001", "unexpected server error"),
    GENERIC_ERROR("MVC-0002", "Oops! Something went wrong"),
    REQUEST_INVALID("MVC-0003", "request invalid, could not be completed"),
    RESOURCE_NOT_FOUND("MVC-0004", "the resource requested does not exist"),
    UNAUTHORIZED_SIGNATURE_EXCEPTION("MCS-0005", "signature verification failed"),
    REQUEST_MALFORMED("MVC-0006", "malformed request payload"),
    DUPLICATE_ENTRY("MVC-0007", "duplicate record detected"),
    EXTERNAL_SERVICE_CALL("MVC-0008", "Failed to communicate with external service"),
    UNAUTHORIZED_ERROR("MVC-0401","Access denied. You are not authorized to access this resource.");

    private final String code;
    private final String description;
    public static ExceptionCode getByCode(String code) {
        for (ExceptionCode exceptionCode : values()) {
            if (exceptionCode.code.equals(code)) {
                return exceptionCode;
            }
        }
        return GENERIC_ERROR;
    }
}
