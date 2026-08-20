package vn.edu.crs.course_service.config;

import vn.edu.crs.course_service.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

                        // API nội bộ
                        .requestMatchers("/internal/**")
                        .permitAll()

                        // Xem danh sách / chi tiết môn học: PUBLIC
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/courses",
                                "/api/courses/**",
                                "/courses",
                                "/courses/**"
                        )
                        .permitAll()

                        // Thêm môn học: chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/courses",
                                "/api/courses/**"
                        )
                        .hasRole("ADMIN")

                        // Sửa môn học: chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/courses",
                                "/api/courses/**"
                        )
                        .hasRole("ADMIN")

                        // Xóa môn học: chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/courses",
                                "/api/courses/**"
                        )
                        .hasRole("ADMIN")

                        // Các request khác phải đăng nhập
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