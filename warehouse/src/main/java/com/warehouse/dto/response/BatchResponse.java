package com.warehouse.dto.response;

import com.warehouse.entity.enums.BatchStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ lô hàng. */
public record BatchResponse(
        Long id,
        Long productId,
        Long supplierId,
        String batchNo,
        LocalDate mfgDate,
        LocalDate expiryDate,
        LocalDateTime receivedAt,
        BatchStatus status,
        String skuCode,
        String productName,
        String storageCondition) {
    public record Alerts(LocalDate asOf, java.util.List<BatchResponse> expired, java.util.List<BatchResponse> expiring) {}
}
