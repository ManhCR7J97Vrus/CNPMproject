package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Dữ liệu đầu vào của phân hệ nhà cung cấp. */
public final class SupplierRequest {
    private SupplierRequest() {}

    public record Write(
            @NotBlank String name,
            String phone,
            String address,
            String licenseNo,
            boolean active) {}
}
