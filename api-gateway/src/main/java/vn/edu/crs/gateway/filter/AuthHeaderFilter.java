package vn.edu.crs.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class AuthHeaderFilter
        extends AbstractGatewayFilterFactory<AuthHeaderFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public AuthHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path =
                    exchange.getRequest()
                            .getURI()
                            .getPath();

            HttpMethod method =
                    exchange.getRequest()
                            .getMethod();

            // =========================
            // PUBLIC API
            // =========================

            // Login không cần JWT
            if (path.equals("/api/auth/login")) {
                return chain.filter(exchange);
            }

            // Partner API:
            // Không cần JWT
            // ApiKeyFilter sẽ kiểm tra API Key
            if (path.equals("/api/public/courses")) {
                return chain.filter(exchange);
            }

            // GET courses public
            if (path.equals("/api/courses")
                    && method == HttpMethod.GET) {

                return chain.filter(exchange);
            }

            if (path.startsWith("/api/courses/")
                    && method == HttpMethod.GET) {

                return chain.filter(exchange);
            }

            // =========================
            // JWT REQUIRED
            // =========================

            String authHeader =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst("Authorization");

            if (authHeader == null
                    || !authHeader.startsWith("Bearer ")) {

                exchange.getResponse()
                        .setStatusCode(
                                HttpStatus.UNAUTHORIZED
                        );

                return exchange.getResponse()
                        .setComplete();
            }

            String token =
                    authHeader.substring(7);

            try {

                SecretKey key =
                        Keys.hmacShaKeyFor(
                                secret.getBytes()
                        );

                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token);

                return chain.filter(exchange);

            } catch (Exception e) {

                exchange.getResponse()
                        .setStatusCode(
                                HttpStatus.UNAUTHORIZED
                        );

                return exchange.getResponse()
                        .setComplete();
            }
        };
    }

    public static class Config {
    }
}