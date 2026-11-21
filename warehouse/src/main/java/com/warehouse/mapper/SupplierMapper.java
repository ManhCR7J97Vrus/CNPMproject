package com.warehouse.mapper;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.entity.Supplier;

/** Hợp đồng chuyển nhà cung cấp sang DTO công khai. */
public interface SupplierMapper {
    SupplierResponse toResponse(Supplier entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Supplier toEntity(SupplierRequest.Write request);
}
