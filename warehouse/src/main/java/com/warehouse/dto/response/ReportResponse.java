package com.warehouse.dto.response;

import java.math.BigDecimal;

/** Dữ liệu trả về của phân hệ báo cáo kho. */
public record ReportResponse(
        Long productId,
        String skuCode,
        BigDecimal openingQty,
        BigDecimal inboundQty,
        BigDecimal outboundQty,
        BigDecimal netAdjustment,
        BigDecimal closingQty) {}
