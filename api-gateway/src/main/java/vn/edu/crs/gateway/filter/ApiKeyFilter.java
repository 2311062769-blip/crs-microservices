package vn.edu.crs.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiKeyFilter extends AbstractGatewayFilterFactory<ApiKeyFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    @Value("${partner.api-key}")
    private String expectedApiKey;

    public ApiKeyFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String normalized = path == null ? "" : path.replaceAll("/+$", "");

            // Log for debugging
            log.debug("ApiKeyFilter invoked for path='{}', expectedApiKey='{}'", path, expectedApiKey != null ? "(set)" : "(not-set)");

            // Check API Key for /api/public/courses and any trailing slash variants
            if (normalized.equals("/api/public/courses") || normalized.startsWith("/api/public/courses/")) {
                List<String> apiKeyHeaders =
                    exchange.getRequest()
                        .getHeaders()
                        .getOrEmpty("X-API-KEY");

                log.debug("Received X-API-KEY headers: {}", apiKeyHeaders);

                if (apiKeyHeaders.isEmpty() ||
                    !apiKeyHeaders.get(0).equals(expectedApiKey)) {
                    log.warn("Rejecting request to {} due missing/invalid API key", path);
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
