package io.movmint.msp.merchant.api.exception;

import io.movmint.msp.merchant.exception.ExceptionCode;
import io.movmint.msp.merchant.exception.MerchantServiceException;
import lombok.Getter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorResponse {

  private final String code;
  private final String description;

  private final Map<String, Object> details;

  private ErrorResponse(String code, String description, Map<String, Object> details) {
    this.code = code;
    this.description = description;
    this.details = details;
  }

  public static ErrorResponse create(MerchantServiceException ex) {
    Map<String, Object> detail = new HashMap<>(Map.of("message", ex.getMessage()));
    if (!CollectionUtils.isEmpty(ex.getDetail())) {
      detail.putAll(ex.getDetail());
    }
    if (!CollectionUtils.isEmpty(ex.getDetail())) {
      detail.put("additionalInfo",ex.getDetail());
    }
    return new ErrorResponse(ObjectUtils.isEmpty(ex.getCode()) ? MerchantServiceException.CODE_DEFAULT : ex.getCode(), ex.getDescription(), detail);
  }

  public static ErrorResponse create(Exception ex) {
    return new ErrorResponse(ExceptionCode.SERVER_ERROR.getCode(), ExceptionCode.SERVER_ERROR.getDescription(), Map.of("message", "unexpected server error - please contact support"));
  }

  public static ErrorResponse createRequestInvalidResponse(final BindException ex) {
    Map<String, Object> details = new HashMap<>();
    details.put("message", "request invalid, could not be completed");
    details.put("additionalInfo",getIndividualErrors(ex));
    return new ErrorResponse(ExceptionCode.REQUEST_INVALID.getCode(), ExceptionCode.REQUEST_INVALID.getDescription(), details);
  }

  private static Map<String, String> getIndividualErrors(final BindException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

}
