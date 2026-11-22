package com.warehouse.entity.enums;

/** Trạng thái đơn xuất kho. */
public enum OutboundStatus {
    REQUESTED,
    APPROVED,
    PICKED,
    PACKED,
    HANDED_OVER,
    DELIVERED,
    FAILED,
    CANCEL_PENDING_RETURN,
    CANCELLED,
    LATE,
    REFUSED
}
