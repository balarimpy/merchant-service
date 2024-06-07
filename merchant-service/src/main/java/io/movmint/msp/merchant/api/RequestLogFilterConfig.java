package io.movmint.msp.merchant.api;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Log4j2
@Configuration
public class RequestLogFilterConfig {

    @Value("${logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter}")
    private String filterLoggingLevel;

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        if (filterLoggingLevel.isEmpty() || !filterLoggingLevel.equals("DEBUG")) {
            log.warn("CommonsRequestLoggingFilter set to [%s], requests will not be logged"
                    .formatted(filterLoggingLevel));
        }

        final CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        return filter;
    }
}
