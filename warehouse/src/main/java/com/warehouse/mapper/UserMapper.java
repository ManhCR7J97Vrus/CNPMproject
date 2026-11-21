package com.warehouse.mapper;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import com.warehouse.entity.User;

/** Hợp đồng chuyển người dùng sang DTO công khai. */
public interface UserMapper {
    UserResponse toResponse(User entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    User toEntity(UserRequest.Create request);
}
