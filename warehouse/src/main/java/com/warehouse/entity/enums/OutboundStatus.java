package com.warehouse.entity.enums;

/** Trạng thái đơn xuất kho. */
public enum OutboundStatus {
    REQUESTED,
    APPROVED,
    PICKED,
    PACKED,
    HANDED_OVER,
    CANCELLED,
    LATE,
    REFUSED
}
