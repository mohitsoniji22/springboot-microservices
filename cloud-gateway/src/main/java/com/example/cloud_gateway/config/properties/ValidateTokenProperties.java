package com.example.cloud_gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "microservices.security-service.endpoints.validate-token")
public class ValidateTokenProperties {

    private String scheme;
    private String serviceName;
    private String path;
    private String tokenParam;
}
