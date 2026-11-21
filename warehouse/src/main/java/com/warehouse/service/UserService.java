package com.warehouse.service;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ người dùng. */
public interface UserService {
    List<UserResponse> listUsers();

    UserResponse getUser(Long id);

    UserResponse createUser(UserRequest.Create request);

    UserResponse updateUser(Long id, UserRequest.Update request);
}
