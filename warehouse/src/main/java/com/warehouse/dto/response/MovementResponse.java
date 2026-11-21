package com.warehouse.dto.response;

import com.warehouse.entity.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ biến động kho. */
public record MovementResponse(
        Long id,
        Long batchId,
        Long fromLocationId,
        Long toLocationId,
        MovementType movementType,
        String referenceNo,
        BigDecimal changeQty,
        LocalDateTime createdAt) {}
