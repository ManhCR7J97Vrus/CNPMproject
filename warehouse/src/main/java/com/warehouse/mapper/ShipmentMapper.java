package com.warehouse.mapper;

import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.entity.Shipment;

/** Hợp đồng chuyển chuyến giao hàng sang DTO công khai. */
public interface ShipmentMapper {
    ShipmentResponse toResponse(Shipment entity);
}
