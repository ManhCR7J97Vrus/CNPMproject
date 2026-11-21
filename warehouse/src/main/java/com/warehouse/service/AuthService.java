package com.warehouse.service;

import com.warehouse.dto.request.AuthRequest;
import com.warehouse.dto.response.AuthResponse;

/** Hợp đồng nghiệp vụ cho phân hệ xác thực. */
public interface AuthService {
    AuthResponse login(AuthRequest.Login request);
}
