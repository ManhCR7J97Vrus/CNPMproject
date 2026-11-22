package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class LocationRequest {
    private LocationRequest() {}

    public record Write(@NotBlank @Size(max = 255) String code,
                        @NotBlank @Size(max = 255) String warehouseName,
                        @NotBlank @Size(max = 255) String zoneName,
                        @Size(max = 255) String storageCondition, boolean active,
                        @Size(max = 255) String rackCode, boolean quarantine, boolean locked) {}
}
