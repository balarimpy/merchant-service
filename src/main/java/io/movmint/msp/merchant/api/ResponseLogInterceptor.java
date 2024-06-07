package io.movmint.msp.merchant.api;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class ResponseLogInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            final HttpRequest request,
            final byte[] body,
            final ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);


        return response;
    }
}
