package com.warehouse.dto.request;

import com.warehouse.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Dữ liệu đầu vào của phân hệ người dùng. */
public final class UserRequest {
    private UserRequest() {}

    public record Create(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String fullName,
            @Email String email,
            String phone,
            String addr,
            @NotNull Role role) {}

    public record Update(
            @NotBlank String fullName,
            @Email String email,
            String phone,
            String addr,
            @NotNull Role role,
            boolean active) {}
}
