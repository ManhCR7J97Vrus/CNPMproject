package com.warehouse.dto.response;

import com.warehouse.entity.enums.ShipmentStatus;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ chuyến giao hàng. */
public record ShipmentResponse(
        Long id,
        Long outboundOrderId,
        String carrierName,
        String trackingNo,
        ShipmentStatus status,
        LocalDateTime dispatchedAt,
        LocalDateTime deliveredAt) {}
