package com.warehouse.dto.response;

import com.warehouse.entity.enums.Role;

/** Dữ liệu trả về của phân hệ người dùng. */
public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String addr,
        Role role,
        boolean active,
        Long supplierId,
        String carrierName) {}
