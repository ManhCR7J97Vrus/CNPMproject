package com.warehouse.dto.request;

import com.warehouse.entity.enums.ItemCondition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu đầu vào của phân hệ phiếu nhập. */
public final class InboundOrderRequest {
    private InboundOrderRequest() {}

    public record Write(
            @NotNull @Positive Long supplierId,
            LocalDateTime expectedAt,
            @NotEmpty List<@NotNull @Valid Line> items) {}

    public record Line(
            @NotNull @Positive Long productId,
            @NotNull @Positive BigDecimal qtyOrdered,
            @NotNull @PositiveOrZero BigDecimal unitCost) {}

    public record Receive(
            @NotEmpty List<@NotNull @Valid ReceivedLine> items) {}

    public record ReceivedLine(
            @NotNull @Positive Long inboundItemId,
            @NotNull @Positive Long locationId,
            @NotNull @PositiveOrZero BigDecimal qtyReceived,
            @NotNull ItemCondition itemCondition,
            String batchNo,
            LocalDate mfgDate,
            LocalDate expiryDate,
            String discrepancyReason) {}
}
