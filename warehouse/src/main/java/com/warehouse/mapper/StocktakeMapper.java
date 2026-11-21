package com.warehouse.mapper;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.entity.Stocktake;

/** Hợp đồng chuyển kiểm kê sang DTO công khai. */
public interface StocktakeMapper {
    StocktakeResponse toResponse(Stocktake entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Stocktake toEntity(StocktakeRequest.Create request);
}
