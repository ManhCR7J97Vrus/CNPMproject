package com.warehouse.dto.request;

import com.warehouse.entity.enums.BatchStatus;
import jakarta.validation.constraints.Positive;

/** Dữ liệu đầu vào của phân hệ lô hàng. */
public final class BatchRequest {
    private BatchRequest() {}

    public record Search(
            @Positive Long productId,
            BatchStatus status) {}
}
