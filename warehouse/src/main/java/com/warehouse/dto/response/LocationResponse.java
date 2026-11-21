package com.warehouse.dto.response;

/** Dữ liệu trả về của phân hệ vị trí lưu trữ. */
public record LocationResponse(
        Long id,
        String code,
        String warehouseName,
        String zoneName,
        String storageCondition,
        boolean locked,
        boolean active) {}
