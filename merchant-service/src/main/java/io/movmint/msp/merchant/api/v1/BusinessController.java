package io.movmint.msp.merchant.api.v1;

import io.movmint.msp.merchant.api.v1.model.request.BusinessRequest;
import io.movmint.msp.merchant.api.v1.model.response.BusinessResponse;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.service.BusinessService;

import jakarta.validation.Valid;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestController
@RestControllerAdvice
@RequestMapping(
        value = "/business",
        method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE}
)
public class BusinessController {
    private BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    /**
     * POST /business
     * Create Business details and link with hqWallets.
     * Date: 2024-05-02
     *
     * @param businessRequest {@link BusinessRequest} (optional)
     * @return ResponseEntity with the OK (status code 200) and BusinessResponse details if success,
     *          or ResponseEntity with error code and object ErrorResponse.
     */
    @PostMapping()
    public ResponseEntity<?> saveBusiness(@Valid @RequestBody BusinessRequest businessRequest) throws MerchantServiceException {
        return ResponseEntity.ok(BusinessResponse.create(businessService.create(businessRequest)));
    }
}
