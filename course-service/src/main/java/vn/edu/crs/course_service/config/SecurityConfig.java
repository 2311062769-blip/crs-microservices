package vn.edu.crs.course_service.config;

import vn.edu.crs.course_service.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/internal/**")
                    .permitAll()

                .requestMatchers(HttpMethod.GET, "/courses/**")
                    .permitAll()

                .requestMatchers(HttpMethod.GET, "/courses")
                    .permitAll()

                .requestMatchers(HttpMethod.POST, "/courses/**")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/courses")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/courses/**")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/courses")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/courses/**")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/courses")
                    .hasRole("ADMIN")

                .anyRequest()
                    .authenticated()
            )
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
