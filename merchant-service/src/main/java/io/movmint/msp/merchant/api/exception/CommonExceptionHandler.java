package io.movmint.msp.merchant.api.exception;

import io.movmint.msp.merchant.api.ApiHeader;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@RestControllerAdvice("io.movmint.msp.merchant.api")
public class CommonExceptionHandler {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ExceptionHandler({MerchantServiceException.class})
    public ResponseEntity<Object> handleServerException(final MerchantServiceException ex, WebRequest request) {
        return this.handleEx(ex, ErrorResponse.create(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(final Exception ex, WebRequest request) {
        return handleEx(ex, ErrorResponse.create(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public final ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final WebRequest request) {
        return this.handleEx(ex, ErrorResponse.createRequestInvalidResponse(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<Object> handleConstraintViolationException(
            final BindException ex,
            final WebRequest request) {
        return this.handleEx(ex, ErrorResponse.createRequestInvalidResponse(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(
            final UnauthorizedException ex,
            final WebRequest request) {
        return this.handleEx(ex, ErrorResponse.create(ex), HttpStatus.UNAUTHORIZED);
    }


    private ResponseEntity<Object> handleEx(
            final Exception ex,
            final ErrorResponse response,
            final HttpStatus status) {

        String ERROR_LOG = "API ERROR: {}; PATH: {}; SOURCE: {}; DEVICE: {};";
        log.error(
                ERROR_LOG,
                ex.getMessage(),
                ex,
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteHost(),
                httpServletRequest.getHeader(ApiHeader.DEVICE_ID));
        return new ResponseEntity<>(response, status);
    }
}
