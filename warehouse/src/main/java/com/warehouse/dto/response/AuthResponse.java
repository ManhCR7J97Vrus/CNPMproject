package com.warehouse.dto.response;

import com.warehouse.entity.enums.Role;

/** Dữ liệu trả về của phân hệ xác thực. */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String username,
        String fullName,
        Role role,
        String carrierName) {}
