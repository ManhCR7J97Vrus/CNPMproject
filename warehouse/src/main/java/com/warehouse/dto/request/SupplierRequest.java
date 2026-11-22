package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class SupplierRequest {
    private SupplierRequest() {}

    public record Write(@NotBlank @Size(max = 255) String name, @Size(max = 255) String phone,
                        @Size(max = 255) String address, @Size(max = 255) String licenseNo,
                        boolean active, @NotBlank @Size(max = 255) String code) {}
}
