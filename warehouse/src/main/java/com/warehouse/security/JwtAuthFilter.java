package com.warehouse.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

/** Xác thực Bearer token qua bộ lọc chuẩn của Spring Security. */
public class JwtAuthFilter extends BearerTokenAuthenticationFilter {
    public JwtAuthFilter(AuthenticationManager manager) {
        super(manager);
    }
}
