package com.warehouse.dto.response;

import com.warehouse.entity.enums.StocktakeStatus;
import java.math.BigDecimal;

/** Dữ liệu trả về của phân hệ kiểm kê. */
public record StocktakeResponse(
        Long id,
        Long locationId,
        Long batchId,
        BigDecimal expectedQty,
        BigDecimal countedQty,
        StocktakeStatus status,
        String reason) {}
