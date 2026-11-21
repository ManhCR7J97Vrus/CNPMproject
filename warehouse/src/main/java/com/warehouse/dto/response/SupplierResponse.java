package com.warehouse.dto.response;

/** Dữ liệu trả về của phân hệ nhà cung cấp. */
public record SupplierResponse(
        Long id,
        String name,
        String phone,
        String address,
        String licenseNo,
        boolean active) {}
