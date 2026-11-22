package com.warehouse.dto.request;

import jakarta.validation.constraints.Positive;

public final class InventoryRequest {
    private InventoryRequest() {}

    public record Search(@Positive Long batchId, @Positive Long locationId, @Positive Long productId,
                         String sku, String batchNo, String locationCode, String warehouseName) {}
}
