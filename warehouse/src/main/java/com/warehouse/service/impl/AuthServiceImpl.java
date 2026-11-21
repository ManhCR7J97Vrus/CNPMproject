package com.warehouse.service.impl;

import com.warehouse.dto.request.AuthRequest;
import com.warehouse.dto.response.AuthResponse;
import com.warehouse.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ xác thực. */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse login(AuthRequest.Login request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
