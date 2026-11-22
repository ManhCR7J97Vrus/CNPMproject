package com.warehouse.dto.request;

import com.warehouse.entity.enums.OutboundStatus;
import com.warehouse.entity.enums.Priority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public final class OutboundOrderRequest {
    private OutboundOrderRequest() {}

    public record Search(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, OutboundStatus status, String reference) {}

    public record Create(@NotNull Priority priority, LocalDateTime deliveryDeadline,
                         @NotBlank @Size(max = 255) String shippingAddress,
                         @NotEmpty List<@NotNull @Valid Line> items,
                         @Size(max = 255) String receiverName, @Size(max = 255) String receiverPhone) {}

    public record Line(@NotNull @Positive Long productId, @NotNull @Positive BigDecimal qtyRequested) {}

    public record Pick(@NotEmpty List<@NotNull @Valid PickedLine> items) {}
    public record PickedLine(@NotNull @Positive Long outboundItemId, @NotNull @Positive Long inventoryId,
                             @NotNull @Positive BigDecimal qtyPicked, @Positive Long batchId,
                             @Positive Long locationId) {}

    public record Pack(@NotNull @Positive Integer packageCount) {}
    public record Handover(@NotNull @PositiveOrZero Integer packageCount) {}
    public record Cancel(@NotBlank @Size(max = 255) String reason, boolean returnedToStorage) {}
    public record CancelRequest(@NotBlank @Size(max = 255) String reason) {}
}
