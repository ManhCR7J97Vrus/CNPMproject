package com.warehouse.controller;

import com.warehouse.dto.request.BatchRequest;
import com.warehouse.dto.response.BatchResponse;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.BatchService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ lô hàng. */
@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<BatchResponse> listBatches(@Valid @ModelAttribute BatchRequest.Search request) {
        return service.listBatches(request);
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasRole('MANAGER')")
    public BatchResponse.Alerts alerts() {
        return service.alerts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public BatchResponse getBatch(@PathVariable("id") Long id) {
        return service.getBatch(id);
    }

    @GetMapping("/{id}/trace")
    @PreAuthorize("hasRole('MANAGER')")
    public List<MovementResponse> traceBatch(@PathVariable("id") Long id) {
        return service.traceBatch(id);
    }
}
