package vn.edu.crs.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ApiKeyGatewayFilterFactory.Config> {

    @Value("${partner.api-key}")
    private String expectedApiKey;

    public ApiKeyGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String normalized = path == null ? "" : path.replaceAll("/+$", "");

            if (normalized.equals("/api/public/courses")
                    || normalized.startsWith("/api/public/courses/")) {
                String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
                if (apiKey == null || !apiKey.equals(expectedApiKey)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}