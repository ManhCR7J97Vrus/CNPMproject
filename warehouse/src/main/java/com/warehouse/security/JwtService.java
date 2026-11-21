package com.warehouse.security;

import com.warehouse.config.JwtConfig;
import com.warehouse.entity.enums.Role;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

/** Kiểm tra chữ ký, thời hạn và danh tính của JWT dùng cho API. */
@Component
public class JwtService {
    private static final Set<String> ROLES = Arrays.stream(Role.values())
            .map(Role::name).collect(Collectors.toUnmodifiableSet());
    private final JwtConfig config;

    public JwtService(JwtConfig config) {
        this.config = config;
    }

    public JwtDecoder decoder() {
        byte[] key = Base64.getDecoder().decode(config.secret());
        if (key.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET cần ít nhất 32 byte sau khi giải mã Base64");
        }
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key, "HmacSHA256")).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer("warehouse"), this::validateClaims));
        return decoder;
    }

    private OAuth2TokenValidatorResult validateClaims(Jwt jwt) {
        Instant issued = jwt.getIssuedAt();
        Instant expires = jwt.getExpiresAt();
        Object role = jwt.getClaims().get("role");
        if (issued == null || expires == null || !expires.isAfter(issued)
                || issued.isAfter(Instant.now().plusSeconds(60))
                || Duration.between(issued, expires).compareTo(Duration.ofSeconds(config.expirationSeconds())) > 0
                || jwt.getSubject() == null || jwt.getSubject().isBlank()
                || !(role instanceof String name) || !ROLES.contains(name)) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        }
        return OAuth2TokenValidatorResult.success();
    }

    public UsernamePasswordAuthenticationToken authentication(Jwt jwt) {
        var user = new CustomUserDetails(jwt.getSubject(), Role.valueOf(jwt.getClaimAsString("role")));
        return UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
    }
}
