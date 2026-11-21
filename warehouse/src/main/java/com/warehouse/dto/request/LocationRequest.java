package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Dữ liệu đầu vào của phân hệ vị trí lưu trữ. */
public final class LocationRequest {
    private LocationRequest() {}

    public record Write(
            @NotBlank String code,
            @NotBlank String warehouseName,
            @NotBlank String zoneName,
            String storageCondition,
            boolean active) {}
}
