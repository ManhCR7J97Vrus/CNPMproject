package com.warehouse.dto.response;

/** Dữ liệu trả về của phân hệ xác thực. */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds) {}
