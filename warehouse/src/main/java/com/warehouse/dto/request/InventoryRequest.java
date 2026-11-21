package com.warehouse.dto.request;

import jakarta.validation.constraints.Positive;

/** Dữ liệu đầu vào của phân hệ tồn kho. */
public final class InventoryRequest {
    private InventoryRequest() {}

    public record Search(
            @Positive Long batchId,
            @Positive Long locationId) {}
}
