package com.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public final class ReportRequest {
    private ReportRequest() {}

    public record Period(@NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                         @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                         String warehouseName, String sku) {}
}
