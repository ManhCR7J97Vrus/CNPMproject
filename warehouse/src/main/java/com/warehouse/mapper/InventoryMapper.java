package com.warehouse.mapper;

import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.entity.Inventory;

/** Hợp đồng chuyển tồn kho sang DTO công khai. */
public interface InventoryMapper {
    InventoryResponse toResponse(Inventory entity);
}
