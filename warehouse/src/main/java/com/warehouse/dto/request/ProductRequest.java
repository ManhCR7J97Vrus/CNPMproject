package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Dữ liệu đầu vào của phân hệ sản phẩm. */
public final class ProductRequest {
    private ProductRequest() {}

    public record Write(
            @NotBlank String skuCode,
            @NotBlank String name,
            @NotBlank String category,
            @NotBlank String unit,
            boolean hasExpiry,
            @PositiveOrZero int expiryAlertDays,
            String storageCondition,
            @NotNull @PositiveOrZero BigDecimal minStock,
            boolean active) {}
}
