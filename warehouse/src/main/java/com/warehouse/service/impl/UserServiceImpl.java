package com.warehouse.service.impl;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import com.warehouse.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ người dùng. */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public UserResponse createUser(UserRequest.Create request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest.Update request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
