package com.warehouse.dto.request;

import com.warehouse.entity.enums.Role;
import jakarta.validation.constraints.*;

public final class UserRequest {
    private UserRequest() {}

    public record Create(@NotBlank @Size(max = 255) String username,
                         @NotBlank @Size(max = 72) String password,
                         @NotBlank @Size(max = 255) String fullName,
                         @Email @Size(max = 255) String email,
                         @Size(max = 255) String phone,
                         @Size(max = 255) String addr,
                         @NotNull Role role, @Positive Long supplierId, @Size(max = 255) String carrierName) {}

    public record Update(@NotBlank @Size(max = 255) String fullName,
                         @Email @Size(max = 255) String email,
                         @Size(max = 255) String phone,
                         @Size(max = 255) String addr,
                         @NotNull Role role, boolean active, @Positive Long supplierId, @Size(max = 255) String carrierName) {}
}
