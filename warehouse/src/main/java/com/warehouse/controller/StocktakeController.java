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
    @PreAuthorize("hasRole('MANAGER')")
    public StocktakeResponse createStocktake(@Valid @RequestBody StocktakeRequest.Create request) {
        return service.createStocktake(request);
    }

    @GetMapping("/counters")
    @PreAuthorize("hasRole('MANAGER')")
    public List<StocktakeResponse.Counter> counters() {
        return service.counters();
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
    public StocktakeResponse approveAdjustment(@PathVariable("id") Long id,
            @Valid @RequestBody(required = false) StocktakeRequest.Review request) {
        return service.approveAdjustment(id, request);
    }

    @PostMapping("/{id}/recount")
    @PreAuthorize("hasRole('MANAGER')")
    public StocktakeResponse returnForRecount(@PathVariable Long id, @Valid @RequestBody StocktakeRequest.Review request) {
        return service.returnForRecount(id, request);
    }

    @PostMapping("/{id}/refresh")
    @PreAuthorize("hasRole('MANAGER')")
    public StocktakeResponse refreshSnapshot(@PathVariable Long id) {
        return service.refreshSnapshot(id);
    }

    @GetMapping("/quarantine")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<StocktakeResponse.Quarantine> quarantineRequests() {
        return service.quarantineRequests();
    }

    @PostMapping("/quarantine")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse.Quarantine requestQuarantine(@Valid @RequestBody StocktakeRequest.Quarantine request) {
        return service.requestQuarantine(request);
    }

    @PostMapping("/quarantine/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public StocktakeResponse.Quarantine approveQuarantine(@PathVariable Long id,
            @Valid @RequestBody StocktakeRequest.QuarantineApproval request) {
        return service.approveQuarantine(id, request);
    }

    @PostMapping("/quarantine/{id}/post")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse.Quarantine postQuarantine(@PathVariable Long id) {
        return service.postQuarantine(id);
    }
}
