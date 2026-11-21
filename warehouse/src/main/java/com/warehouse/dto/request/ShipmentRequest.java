package com.warehouse.dto.request;

/** Dữ liệu đầu vào của phân hệ chuyến giao hàng. */
public final class ShipmentRequest {
    private ShipmentRequest() {}

    public record Receipt(
            String note) {}
}
