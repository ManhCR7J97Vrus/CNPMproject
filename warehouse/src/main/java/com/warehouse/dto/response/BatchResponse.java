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
        BatchStatus status) {}
