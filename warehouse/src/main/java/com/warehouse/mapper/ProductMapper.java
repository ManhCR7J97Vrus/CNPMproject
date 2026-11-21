package com.warehouse.mapper;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.entity.Product;

/** Hợp đồng chuyển sản phẩm sang DTO công khai. */
public interface ProductMapper {
    ProductResponse toResponse(Product entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Product toEntity(ProductRequest.Write request);
}
