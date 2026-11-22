package com.warehouse.dto.request;

import com.warehouse.entity.enums.ItemCondition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class InboundOrderRequest {
    private InboundOrderRequest() {}

    public record Write(@Positive Long supplierId, LocalDateTime expectedAt,
                        @NotEmpty List<@NotNull @Valid Line> items,
                        @Size(max = 255) String externalReference) {}

    public record Line(@NotNull @Positive Long productId, @NotNull @Positive BigDecimal qtyOrdered,
                       @PositiveOrZero BigDecimal unitCost) {}

    public record Receive(@NotEmpty List<@NotNull @Valid ReceivedLine> items) {}

    public record ReceivedLine(@NotNull @Positive Long inboundItemId, @Positive Long locationId,
                               @NotNull @PositiveOrZero BigDecimal qtyReceived, ItemCondition itemCondition,
                               @Size(max = 255) String batchNo, LocalDate mfgDate, LocalDate expiryDate,
                               @Size(max = 255) String discrepancyReason, @PositiveOrZero BigDecimal qtyAccepted,
                               @PositiveOrZero BigDecimal unitCost, @Size(max = 255) String storageCondition) {}

    public record Placements(@NotNull List<@NotNull @Valid Placement> items) {}
    public record Placement(@NotNull @Positive Long inboundItemId, @NotNull @Positive Long locationId,
                            @Size(max = 255) String batchNo) {}

    public record Return(@NotNull @Positive Long outboundOrderId, @Size(max = 255) String reason,
                         @NotEmpty List<@NotNull @Valid ReturnLine> items) {}
    public record ReturnLine(@NotNull @Positive Long outboundItemId, @NotNull @Positive Long locationId,
                             @NotNull @Positive BigDecimal quantity, @NotNull ItemCondition itemCondition) {}
}
