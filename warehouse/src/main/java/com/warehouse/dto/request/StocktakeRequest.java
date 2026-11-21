package com.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Dữ liệu đầu vào của phân hệ kiểm kê. */
public final class StocktakeRequest {
    private StocktakeRequest() {}

    public record Create(
            @NotNull @Positive Long locationId,
            @NotNull @Positive Long batchId) {}

    public record Count(
            @NotNull @PositiveOrZero BigDecimal countedQty,
            String reason) {}
}
