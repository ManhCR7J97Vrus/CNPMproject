package com.warehouse.dto.response;

import com.warehouse.entity.enums.BatchStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InventoryResponse(Long id, Long batchId, Long locationId, BigDecimal qtyOnHand, BigDecimal qtyReserved,
        LocalDateTime updatedAt, BigDecimal qtyAvailable, Long productId, String skuCode, String productName,
        String batchNo, String warehouseName, String locationCode, boolean locked, boolean quarantine,
        boolean eligible, LocalDate expiryDate, BatchStatus batchStatus) {

    public record Summary(List<InventoryResponse> items, BigDecimal qtyOnHand, BigDecimal qtyReserved, BigDecimal qtyAvailable) {}
}
