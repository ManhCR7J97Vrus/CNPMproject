package com.warehouse.controller;

import com.warehouse.dto.request.AuthRequest;
import com.warehouse.dto.response.AuthResponse;
import com.warehouse.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ xác thực. */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    /** Kiểm tra tiến trình HTTP của ứng dụng. */
    @GetMapping("/health")
    @PreAuthorize("permitAll()")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @PostMapping("/auth/login")
    @PreAuthorize("permitAll()")
    public AuthResponse login(@Valid @RequestBody AuthRequest.Login request) {
        return service.login(request);
    }
}
