package com.warehouse.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ tồn kho. */
public record InventoryResponse(
        Long id,
        Long batchId,
        Long locationId,
        BigDecimal qtyOnHand,
        BigDecimal qtyReserved,
        LocalDateTime updatedAt) {}
