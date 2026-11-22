package com.warehouse.dto.response;

/** Dữ liệu trả về của phân hệ nhà cung cấp. */
public record SupplierResponse(
        Long id,
        String name,
        String phone,
        String address,
        String licenseNo,
        boolean active,
        String code) {
    public record Option(Long id, String code, String name, boolean active) {}
}
