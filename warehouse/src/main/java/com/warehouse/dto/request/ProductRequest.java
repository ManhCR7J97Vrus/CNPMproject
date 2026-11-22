package com.warehouse.dto.request;

import com.warehouse.entity.enums.Priority;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class ProductRequest {
    private ProductRequest() {}

    public record Write(@NotBlank @Size(max = 255) String skuCode, @NotBlank @Size(max = 255) String name,
                        @NotBlank @Size(max = 255) String category, @NotBlank @Size(max = 255) String unit,
                        boolean hasExpiry, @PositiveOrZero int expiryAlertDays,
                        @Size(max = 255) String storageCondition, @NotNull @PositiveOrZero BigDecimal minStock,
                        boolean active, boolean perishable, @Size(max = 255) String isolationGroup) {}

    public record Profile(
            @Digits(integer = 4, fraction = 2) BigDecimal temperatureMin,
            @Digits(integer = 4, fraction = 2) BigDecimal temperatureMax,
            @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 2) BigDecimal humidityMin,
            @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 2) BigDecimal humidityMax,
            boolean avoidLight, boolean fireProtection,
            List<@NotBlank @Pattern(regexp = "[^,]+") String> incompatibleGroups) {}

    public record Priorities(@NotEmpty Map<Priority, @NotNull @Positive Integer> ranks) {}
}
