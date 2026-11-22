package com.warehouse.service.impl;

import com.warehouse.config.JwtConfig;
import com.warehouse.dto.request.AuthRequest;
import com.warehouse.dto.response.AuthResponse;
import com.warehouse.entity.User;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.repository.UserRepository;
import com.warehouse.security.JwtService;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements com.warehouse.service.AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final JwtService tokens;
    private final JwtConfig config;

    @Override
    public AuthResponse login(AuthRequest.Login request) {
        User user = users.findByUsernameIgnoreCase(request.username().strip()).orElse(null);
        if (user == null || !user.isActive()
                || request.password().getBytes(StandardCharsets.UTF_8).length > 72
                || !passwords.matches(request.password(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không hợp lệ");
        }
        return new AuthResponse(tokens.issue(user), "Bearer", config.expirationSeconds(),
                user.getUsername(), user.getFullName(), user.getRole(), user.getCarrierName());
    }
}
