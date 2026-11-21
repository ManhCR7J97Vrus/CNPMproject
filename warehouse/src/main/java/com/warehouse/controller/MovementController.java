package com.warehouse.controller;

import com.warehouse.dto.request.MovementRequest;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.MovementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ biến động kho. */
@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {
    private final MovementService service;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public List<MovementResponse> listMovements() {
        return service.listMovements();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public MovementResponse transferStock(@Valid @RequestBody MovementRequest.Transfer request) {
        return service.transferStock(request);
    }
}
