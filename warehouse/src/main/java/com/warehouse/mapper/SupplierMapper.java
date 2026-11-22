package com.warehouse.mapper;

import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.entity.Supplier;

public interface SupplierMapper {
    static SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(supplier.getId(), supplier.getName(), supplier.getPhone(), supplier.getAddress(),
                supplier.getLicenseNo(), supplier.isActive(), supplier.getCode());
    }
}
