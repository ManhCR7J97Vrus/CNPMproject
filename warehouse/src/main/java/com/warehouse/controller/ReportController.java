package com.warehouse.controller;

import com.warehouse.dto.request.ReportRequest;
import com.warehouse.dto.response.ReportResponse;
import com.warehouse.service.ReportService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ báo cáo kho. */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @GetMapping("/stock-summary")
    @PreAuthorize("hasRole('MANAGER')")
    public List<ReportResponse> stockSummary(@Valid @ModelAttribute ReportRequest.Period request) {
        return service.stockSummary(request);
    }
}
