package com.warehouse.dto.response;

import com.warehouse.entity.enums.InboundStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu trả về của phân hệ phiếu nhập. */
public record InboundOrderResponse(
        Long id,
        String orderNo,
        Long supplierId,
        InboundStatus status,
        LocalDateTime expectedAt,
        List<Line> items) {
    public record Line(
            Long id,
            Long productId,
            Long batchId,
            Long locationId,
            BigDecimal qtyOrdered,
            BigDecimal qtyReceived,
            BigDecimal unitCost) {}
}
