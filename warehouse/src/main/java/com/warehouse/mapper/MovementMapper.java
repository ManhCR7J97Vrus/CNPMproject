package com.warehouse.mapper;

import com.warehouse.dto.response.MovementResponse;
import com.warehouse.entity.InventoryMovement;

/** Hợp đồng chuyển biến động kho sang DTO công khai. */
public interface MovementMapper {
    MovementResponse toResponse(InventoryMovement entity);
}
