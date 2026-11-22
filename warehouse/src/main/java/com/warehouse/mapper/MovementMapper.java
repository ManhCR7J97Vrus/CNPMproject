package com.warehouse.mapper;

import com.warehouse.dto.response.MovementResponse;
import com.warehouse.entity.InventoryMovement;
import com.warehouse.entity.Location;

public interface MovementMapper {
    static MovementResponse toResponse(InventoryMovement movement) {
        Location from = movement.getFromLocation();
        Location to = movement.getToLocation();
        return new MovementResponse(movement.getId(), movement.getBatch().getId(),
                from == null ? null : from.getId(), to == null ? null : to.getId(),
                movement.getMovementType(), movement.getReferenceNo(), movement.getChangeQty(),
                movement.getCreatedAt(), movement.getBatch().getBatchNo(), path(from), path(to),
                movement.getCreatedBy().getUsername(), movement.getReason());
    }

    private static String path(Location location) {
        return location == null ? null : location.getWarehouseName() + " / " + location.getCode();
    }
}
