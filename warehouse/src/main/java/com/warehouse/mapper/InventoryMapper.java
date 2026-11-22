package com.warehouse.mapper;

import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.entity.Inventory;
import java.math.BigDecimal;

public interface InventoryMapper {
    static InventoryResponse toResponse(Inventory row, boolean eligible) {
        var batch = row.getBatch();
        var product = batch.getProduct();
        var location = row.getLocation();
        BigDecimal available = eligible ? row.getQtyOnHand().subtract(row.getQtyReserved()) : BigDecimal.ZERO;
        return new InventoryResponse(row.getId(), batch.getId(), location.getId(), row.getQtyOnHand(), row.getQtyReserved(),
                row.getUpdatedAt(), available, product.getId(), product.getSkuCode(), product.getName(), batch.getBatchNo(),
                location.getWarehouseName(), location.getCode(), location.isLocked(), location.isQuarantine(), eligible,
                batch.getExpiryDate(), batch.getStatus());
    }
}
