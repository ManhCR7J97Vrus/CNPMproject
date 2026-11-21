package com.warehouse.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Thông số ký và thời hạn tối đa của access token. */
@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(@NotBlank String secret, @Positive long expirationSeconds) {}
