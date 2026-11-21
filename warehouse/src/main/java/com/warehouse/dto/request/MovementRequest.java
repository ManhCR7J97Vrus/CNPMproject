package com.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** Dữ liệu đầu vào của phân hệ biến động kho. */
public final class MovementRequest {
    private MovementRequest() {}

    public record Transfer(
            @NotNull @Positive Long batchId,
            @NotNull @Positive Long fromLocationId,
            @NotNull @Positive Long toLocationId,
            @NotNull @Positive BigDecimal quantity,
            String reason) {}
}
