package com.warehouse.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public final class StocktakeRequest {
    private StocktakeRequest() {}

    public record Create(@Positive Long locationId, @Positive Long batchId,
                         List<@NotNull @Positive Long> locationIds, @Positive Long counterId) {}

    public record Count(@PositiveOrZero BigDecimal countedQty, @Size(max = 255) String reason,
                        List<@NotNull @Valid CountedLine> items, Boolean submit) {}

    public record CountedLine(@NotNull @Positive Long itemId, @PositiveOrZero BigDecimal countedQty,
                              @Size(max = 255) String reason) {}

    public record Review(@Size(max = 255) String reason) {}

    public record Quarantine(@NotNull @Positive Long batchId, @NotNull @Positive Long fromLocationId,
                             @NotNull @Positive BigDecimal quantity, @NotBlank @Size(max = 255) String reason) {}

    public record QuarantineApproval(@NotNull @Positive Long toLocationId) {}
}
