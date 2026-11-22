package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dữ liệu đầu vào của phân hệ xác thực. */
public final class AuthRequest {
    private AuthRequest() {}

    public record Login(
            @NotBlank @Size(max = 255) String username,
            @NotBlank @Size(max = 72) String password) {}
}
