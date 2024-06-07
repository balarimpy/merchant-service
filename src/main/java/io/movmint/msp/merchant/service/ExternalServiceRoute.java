package io.movmint.msp.merchant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.movmint.msp.merchant.api.ApiHeader;
import io.movmint.msp.merchant.data.entity.User;
import io.movmint.msp.merchant.data.enums.UserRole;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Log4j2
@Component
@WebFilter("/*")
@Order(1)
public class ExternalServiceRoute implements Filter {

    private final RestTemplate restTemplate;
    private final UserService userService;
    @Value("${virtual-wallet-service.domain}")
    private String virtualWalletWerviceDomain;
    @Value("${virtual-wallet-service.url.post.virtual-wallet-patch}")
    private String virtualWalletPatchUrl;
    @Value("${virtual-wallet-service.url.post.virtual-wallet-link}")
    private String virtualWalletLinkUrl;

    public ExternalServiceRoute(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestUrl = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        // Perform routing logic based on request URI
        if (pathMatcher.match(virtualWalletPatchUrl, requestUrl) && HttpMethod.POST.name().equals(method)) {
            makeExternalCallForStringResponse(httpRequest, httpResponse, virtualWalletWerviceDomain, UserRole.OWNER);

        } else if (pathMatcher.match(virtualWalletLinkUrl, requestUrl) && HttpMethod.POST.name().equals(method)) {
            makeExternalCallForJsonResponse(httpRequest, httpResponse, virtualWalletWerviceDomain, UserRole.OWNER, UserRole.MANAGER);

        } else {
            // Pass the request through the filter chain
            chain.doFilter(request, response);
        }
    }

    private void makeExternalCallForJsonResponse(HttpServletRequest request, HttpServletResponse response, String domain, UserRole... role) throws ServletException, IOException {
        String userId = request.getHeader(ApiHeader.USER_ID);
        if (userId == null) {
            log.error("The given id is null");
            request.getRequestDispatcher("/error/user").forward(request, response);
            return;
        }
        Optional<User> user = userService.getUserById(userId);
        // check user exist or not
        if (!user.isPresent()) {
            log.error("user [%s] not fount", userId);
            request.getRequestDispatcher("/error/user").forward(request, response);
            return;
        }
        // check user role
        if (Arrays.stream(role).filter(r -> r == user.get().getRole()).count() == 0) {
            log.error("This action can only be carried out by a %s or expected role is %s, but found [%s]", role, role, user.get().getRole());
            request.getRequestDispatcher("/error/role").forward(request, response);
            return;
        }
        String targetUrl = domain + request.getRequestURI();
        // Prepare headers if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        headers.set(ApiHeader.USER_ID, userId);

        // Prepare request entity
        HttpEntity<Object> requestEntity = new HttpEntity<>(extractRequestBody(request), headers);
        // Forward request using RestTemplate
        try {
            log.info("send request to [%s %s]".formatted(request.getMethod(), targetUrl));
            ResponseEntity<Object> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.valueOf(request.getMethod()), requestEntity, Object.class);
            // Convert success response object to appropriate format
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            log.info("receive  response form [%s]".formatted(targetUrl));
            response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            //catch the bad request
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("error form [%s] with status 400", targetUrl);
                response.setStatus(ex.getStatusCode().value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                // Write error body to servlet response
                response.getWriter().write(ex.getResponseBodyAsString());
            } else {
                log.error("unown error form [%s] with status [%s]", targetUrl, ex.getStatusCode());
                request.getRequestDispatcher("/error").forward(request, response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            request.getRequestDispatcher("/error").forward(request, response);
        }
    }

    private void makeExternalCallForStringResponse(HttpServletRequest request, HttpServletResponse response, String domain, UserRole... role) throws ServletException, IOException {
        String userId = request.getHeader(ApiHeader.USER_ID);
        if (userId == null) {
            log.error("The given id is null");
            request.getRequestDispatcher("/error/user").forward(request, response);
            return;
        }
        Optional<User> user = userService.getUserById(userId);
        // check user exist or not
        if (!user.isPresent()) {
            log.error("user [%s] not fount", userId);
            request.getRequestDispatcher("/error/user").forward(request, response);
            return;
        }
        // check user role
        if (Arrays.stream(role).filter(r -> r == user.get().getRole()).count() == 0) {
            log.error("This action can only be carried out by a %s or expected role is %s, but found [%s]", role, role, user.get().getRole());
            request.getRequestDispatcher("/error/role").forward(request, response);
            return;
        }
        String targetUrl = domain + request.getRequestURI();
        // Prepare headers if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        headers.set(ApiHeader.USER_ID, userId);

        // Prepare request entity
        HttpEntity<Object> requestEntity = new HttpEntity<>(extractRequestBody(request), headers);
        // Forward request using RestTemplate
        try {
            log.info("send request to [%s %s]".formatted(request.getMethod(), targetUrl));
            ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.valueOf(request.getMethod()), requestEntity, String.class);
            log.info("receive  response form [%s]".formatted(targetUrl));
            response.getWriter().write(responseEntity.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            //catch the bad request
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("error form [%s] with status 400", targetUrl);
                response.setStatus(ex.getStatusCode().value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                // Write error body to servlet response
                response.getWriter().write(ex.getResponseBodyAsString());
            } else {
                log.error("unknown error from [%s] with status [%s]", targetUrl, ex.getStatusCode());
                request.getRequestDispatcher("/error").forward(request, response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            request.getRequestDispatcher("/error").forward(request, response);
        }
    }

    public String extractRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody.toString();
    }

}