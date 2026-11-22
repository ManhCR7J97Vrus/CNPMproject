package com.warehouse.mapper;

import com.warehouse.dto.response.LocationResponse;
import com.warehouse.entity.Location;

public interface LocationMapper {
    static LocationResponse toResponse(Location location) {
        return new LocationResponse(location.getId(), location.getCode(), location.getWarehouseName(), location.getZoneName(),
                location.getStorageCondition(), location.isLocked(), location.isActive(), location.getRackCode(),
                location.isQuarantine());
    }
}
