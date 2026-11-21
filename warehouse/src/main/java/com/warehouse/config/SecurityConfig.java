package com.warehouse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.exception.ErrorCode;
import com.warehouse.exception.GlobalExceptionHandler;
import com.warehouse.security.JwtAuthFilter;
import com.warehouse.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Thiết lập xác thực stateless, BCrypt và phân quyền ở controller. */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtConfig.class)
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtService jwt, ObjectMapper json) throws Exception {
        var provider = new JwtAuthenticationProvider(jwt.decoder());
        provider.setJwtAuthenticationConverter(jwt::authentication);
        var filter = new JwtAuthFilter(new ProviderManager(provider));
        AuthenticationEntryPoint unauthorized = (request, response, error) -> {
            response.setHeader("WWW-Authenticate", "Bearer");
            writeError(response, json, ErrorCode.UNAUTHORIZED);
        };
        filter.setAuthenticationEntryPoint(unauthorized);
        return http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler((req, res, ex) -> writeError(res, json, ErrorCode.FORBIDDEN)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/pages/**", "/css/**", "/js/**",
                                "/error", "/api/v1/health", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().denyAll())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private static void writeError(HttpServletResponse res, ObjectMapper json, ErrorCode code)
            throws IOException {
        res.setStatus(code.getStatus().value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        json.writeValue(res.getOutputStream(), GlobalExceptionHandler.envelope(code, Map.of()));
    }
}
