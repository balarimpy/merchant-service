package io.movmint.msp.merchant.api.v1;


import io.movmint.msp.merchant.api.ApiHeader;
import io.movmint.msp.merchant.exception.ExternalServiceCallException;
import io.movmint.msp.merchant.exception.InvalidParamException;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.exception.NotFoundException;
import io.movmint.msp.merchant.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * External Service call Controller.
 */
@Log4j2
@RestController
@RestControllerAdvice
@RequestMapping(
        value = "/error",
        method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT}
)
public class ExternalServiceController {
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
    public ResponseEntity<?> errorResponding() throws MerchantServiceException {
        throw new ExternalServiceCallException();
    }

    @RequestMapping("/user")
    public ResponseEntity<?> userNotFoundResponding(@RequestHeader(value = ApiHeader.USER_ID, required = false) String userId) throws MerchantServiceException {
        if (userId == null)
            throw new InvalidParamException("user id is required in header");
        throw new NotFoundException("[%s] id user".formatted(userId));
    }

    @RequestMapping("/role")
    public ResponseEntity<?> userNotFoundResponding() throws MerchantServiceException {
        throw new UnauthorizedException();
    }
}
