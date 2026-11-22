package com.warehouse.dto.request;

import com.warehouse.entity.enums.SupportStatus;
import com.warehouse.entity.enums.SupportType;
import jakarta.validation.constraints.*;

public final class SupportRequest {
    private SupportRequest() {}

    public record Create(@NotNull @Positive Long outboundOrderId, @NotNull SupportType type,
                         @NotBlank @Size(max = 2000) String content) {}

    public record Review(@NotNull SupportStatus status, @Size(max = 2000) String response) {}
}
