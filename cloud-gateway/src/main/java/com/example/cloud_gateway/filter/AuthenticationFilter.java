package com.example.cloud_gateway.filter;

import com.example.cloud_gateway.config.properties.ValidateTokenProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ValidateTokenProperties tokenProps;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            if (!validator.isSecured.test(exchange.getRequest())) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange, "Missing Authorization header");
            }

            String token = authHeader.substring(7);

            String validateUrl =
                    tokenProps.getScheme() + "://" +
                            tokenProps.getServiceName() +
                            tokenProps.getPath();

            URI securityServiceUri = UriComponentsBuilder
                    .fromUriString(validateUrl)
                    .queryParam(tokenProps.getTokenParam(), token)
                    .build(true)   // keep encoding
                    .toUri();

            log.info("Calling Security Service URI: {}", securityServiceUri);

            return webClientBuilder.build()
                    .get()
                    .uri(securityServiceUri)
                    .exchangeToMono(response -> {

                        if (response.statusCode().is2xxSuccessful()) {
                            return chain.filter(exchange);
                        }

                        if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                            return unauthorized(exchange, "Invalid or expired token");
                        }

                        if (response.statusCode().is5xxServerError()) {
                            return serviceUnavailable(exchange, "Security service unavailable");
                        }

                        return unauthorized(exchange, "Unauthorized access");
                    })
                    .onErrorResume(ex -> {
                        log.error("Security service call failed", ex);
                        return serviceUnavailable(exchange, "Authentication service unreachable");
                    });
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", HttpStatus.UNAUTHORIZED.value());
        errorBody.put("error", "Unauthorized");
        errorBody.put("message", message);
        errorBody.put("path", exchange.getRequest().getURI().toString());

        byte[] bytes;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(errorBody);
        } catch (Exception e) {
            return Mono.error(e);
        }

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    private Mono<Void> serviceUnavailable(ServerWebExchange exchange, String message) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.empty();
        }

        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "status", 503,
                "error", "Service Unavailable",
                "message", message
        );

        byte[] bytes;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(body);
        } catch (Exception e) {
            return Mono.error(e);
        }

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    public static class Config {
    }
}