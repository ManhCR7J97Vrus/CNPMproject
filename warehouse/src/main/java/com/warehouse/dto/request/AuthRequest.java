package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Dữ liệu đầu vào của phân hệ xác thực. */
public final class AuthRequest {
    private AuthRequest() {}

    public record Login(
            @NotBlank String username,
            @NotBlank String password) {}
}
