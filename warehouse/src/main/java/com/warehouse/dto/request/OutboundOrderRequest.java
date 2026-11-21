package com.warehouse.dto.request;

import com.warehouse.entity.enums.Priority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu đầu vào của phân hệ đơn xuất. */
public final class OutboundOrderRequest {
    private OutboundOrderRequest() {}

    public record Create(
            @NotNull Priority priority,
            LocalDateTime deliveryDeadline,
            @NotBlank String shippingAddress,
            @NotEmpty List<@NotNull @Valid Line> items) {}

    public record Line(
            @NotNull @Positive Long productId,
            @NotNull @Positive BigDecimal qtyRequested) {}

    public record Pick(
            @NotEmpty List<@NotNull @Valid PickedLine> items) {}

    public record PickedLine(
            @NotNull @Positive Long outboundItemId,
            @NotNull @Positive Long inventoryId,
            @NotNull @Positive BigDecimal qtyPicked,
            String overrideReason) {}

    public record Handover(
            @NotNull @Positive Long driverId,
            @NotBlank String receiverName,
            @NotBlank String carrierName,
            String trackingNo) {}

    public record Cancel(
            @NotBlank String reason) {}
}
