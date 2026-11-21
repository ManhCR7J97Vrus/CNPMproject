package com.warehouse.mapper;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import com.warehouse.entity.Location;

/** Hợp đồng chuyển vị trí lưu trữ sang DTO công khai. */
public interface LocationMapper {
    LocationResponse toResponse(Location entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Location toEntity(LocationRequest.Write request);
}
