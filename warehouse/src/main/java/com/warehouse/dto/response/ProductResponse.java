package com.warehouse.dto.response;

import java.math.BigDecimal;

/** Dữ liệu trả về của phân hệ sản phẩm. */
public record ProductResponse(
        Long id,
        String skuCode,
        String name,
        String category,
        String unit,
        boolean hasExpiry,
        int expiryAlertDays,
        BigDecimal minStock,
        String storageCondition,
        boolean active) {}
