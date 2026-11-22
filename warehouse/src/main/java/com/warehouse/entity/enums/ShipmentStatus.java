package com.warehouse.entity.enums;

/** Trạng thái chuyến giao hàng. */
public enum ShipmentStatus {
    PENDING_ASSIGNMENT,
    ASSIGNED,
    AWAITING_DRIVER,
    ACCEPTED,
    DISPATCHED,
    DELIVERED,
    LATE,
    REFUSED,
    FAILED,
    CANCELLED
}
