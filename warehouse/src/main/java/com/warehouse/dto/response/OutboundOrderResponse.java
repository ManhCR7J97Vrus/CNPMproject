package com.warehouse.dto.response;

import com.warehouse.entity.enums.OutboundStatus;
import com.warehouse.entity.enums.Priority;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu trả về của phân hệ đơn xuất. */
public record OutboundOrderResponse(
        Long id,
        String orderNo,
        Priority priority,
        OutboundStatus status,
        String shippingAddress,
        LocalDateTime deliveryDeadline,
        List<Line> items) {
    public record Line(
            Long id,
            Long productId,
            BigDecimal qtyRequested,
            BigDecimal qtyPicked) {}
}
