package com.warehouse.controller;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.service.StocktakeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ kiểm kê. */
@RestController
@RequestMapping("/api/v1/stocktakes")
@RequiredArgsConstructor
public class StocktakeController {
    private final StocktakeService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<StocktakeResponse> listStocktakes() {
        return service.listStocktakes();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse createStocktake(@Valid @RequestBody StocktakeRequest.Create request) {
        return service.createStocktake(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse getStocktake(@PathVariable("id") Long id) {
        return service.getStocktake(id);
    }

    @PostMapping("/{id}/count")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse recordCount(@PathVariable("id") Long id, @Valid @RequestBody StocktakeRequest.Count request) {
        return service.recordCount(id, request);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public StocktakeResponse approveAdjustment(@PathVariable("id") Long id) {
        return service.approveAdjustment(id);
    }
}
