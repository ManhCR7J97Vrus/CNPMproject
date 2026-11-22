package com.warehouse.mapper;

import com.warehouse.dto.response.UserResponse;
import com.warehouse.entity.User;

public interface UserMapper {
    static UserResponse toResponse(User user) {
        Long supplierId = user.getSupplier() == null ? null : user.getSupplier().getId();
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(),
                user.getPhone(), user.getAddr(), user.getRole(), user.isActive(), supplierId, user.getCarrierName());
    }
}
