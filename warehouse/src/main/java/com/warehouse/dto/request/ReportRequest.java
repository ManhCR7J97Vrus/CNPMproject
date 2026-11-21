package com.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Dữ liệu đầu vào của phân hệ báo cáo kho. */
public final class ReportRequest {
    private ReportRequest() {}

    public record Period(
            @NotNull LocalDate from,
            @NotNull LocalDate to) {}
}
