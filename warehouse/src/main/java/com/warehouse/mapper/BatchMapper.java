package com.warehouse.mapper;

import com.warehouse.dto.response.BatchResponse;
import com.warehouse.entity.Batch;

/** Hợp đồng chuyển lô hàng sang DTO công khai. */
public interface BatchMapper {
    BatchResponse toResponse(Batch entity);
}
