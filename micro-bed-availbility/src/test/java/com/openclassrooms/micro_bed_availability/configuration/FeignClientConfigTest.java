package com.openclassrooms.micro_bed_availability.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeignClientConfigTest {

    private final FeignClientConfig config = new FeignClientConfig();

    @Test
    void shouldAddAuthorizationHeader() {
        RequestInterceptor interceptor = config.basicAuthRequestInterceptor();

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertTrue(template.headers().containsKey("Authorization"));
    }
}
