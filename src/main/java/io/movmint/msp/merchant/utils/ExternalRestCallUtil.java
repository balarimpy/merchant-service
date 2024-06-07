package io.movmint.msp.merchant.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.movmint.msp.merchant.exception.ExternalServiceCallException;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import io.movmint.msp.merchant.external.rest.request.VirtualWalletRequest;
import io.movmint.msp.merchant.external.rest.response.ErrorResponse;
import io.movmint.msp.merchant.external.rest.response.WalletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

@Service
@Log4j2
public class ExternalRestCallUtil {
    private final ConstantURLUtil constantURLUtil;

    private final ObjectMapper mapper;

    public ExternalRestCallUtil(final ConstantURLUtil constantURLUtil,
                                final ObjectMapper mapper) {
        this.constantURLUtil = constantURLUtil;
        this.mapper = mapper;
    }

    public WalletResponse createHQWalletInVWS(VirtualWalletRequest payload, UUID agentId) throws MerchantServiceException {
        log.info("calling virtual-wallet/batch");
        WalletResponse errorResponse = new WalletResponse();
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(constantURLUtil.VW_Batch);
            HttpHeaders headers = getHeaders();
            headers.set("sign", getSignature(payload));
            headers.set("userid", String.valueOf(agentId));
            HttpEntity<VirtualWalletRequest> httpEntity = new HttpEntity<>(payload, headers);

            ResponseEntity<WalletResponse> response =
                    restTemplate.postForEntity(uri, httpEntity, WalletResponse.class);

            log.info("virtual-wallet/batch response " + response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException clientErrorException) {
            errorResponse.setErrorResponse(getErrorResponse(clientErrorException));
            return errorResponse;
        } catch (HttpServerErrorException serverErrorException) {
            errorResponse.setErrorResponse(getErrorResponse(serverErrorException));
            return errorResponse;
        }
        catch (Exception ex) {
            log.error("unable to call virtual-wallet/batch");
            throw new ExternalServiceCallException(ex.getMessage());
        }
    }

    private ErrorResponse getErrorResponse(HttpClientErrorException exception) throws ExternalServiceCallException {
        log.error("external call failed " + exception.getMessage());
        if (exception.getStatusCode().isSameCodeAs(HttpStatus.REQUEST_TIMEOUT)) {
            throw new ExternalServiceCallException("request timed out");
        }
        try {
            ErrorResponse errorResponse = mapper.readValue(exception.getResponseBodyAsString(), ErrorResponse.class);
            return errorResponse;
        } catch (JsonProcessingException ex) {
            log.error("unable to parse error response", ex.getMessage());
            throw new ExternalServiceCallException(ex.getMessage());
        }
    }

    private ErrorResponse getErrorResponse(HttpServerErrorException exception) throws ExternalServiceCallException {
        log.error("external call failed " + exception.getMessage());
        if (exception.getStatusCode().isSameCodeAs(HttpStatus.REQUEST_TIMEOUT)) {
            throw new ExternalServiceCallException("request timed out");
        }
        try {
            ErrorResponse errorResponse = mapper.readValue(exception.getResponseBodyAsString(), ErrorResponse.class);
            return errorResponse;
        } catch (JsonProcessingException ex) {
            log.error("unable to parse error response", ex.getMessage());
            throw new ExternalServiceCallException(ex.getMessage());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
//        headers.set(REQUEST_ORIGIN, APP_ORIGIN_NAME);
        return headers;
    }

    private String getSignature(Object payload) throws MerchantServiceException {
        // TODO integrate with cays and get the correct signature
        return "signature from merchantService";
//        try {
//            String signature = java.util.Base64.getMimeEncoder().encodeToString(
//                            CryptoUtil.signSha256WithRSAPrivateEncrypt(
//                                    new ObjectMapper().writeValueAsString(payload),
//                                    getPrivateKey()))
//                    .replaceAll("(\\r|\\n)", "");
//            return signature;
//        } catch (Exception ex) {
//            log.error("unable to create signature ", ex);
//            throw new CouldNotCompleteActionException("unable to create signature");
//        }
    }
}
