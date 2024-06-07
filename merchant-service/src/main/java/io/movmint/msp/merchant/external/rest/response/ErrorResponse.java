package io.movmint.msp.merchant.external.rest.response;

public class ErrorResponse {
    private String type;
    private ErrorDetails details;

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ErrorDetails getDetails() {
        return details;
    }

    public void setDetails(ErrorDetails details) {
        this.details = details;
    }
}