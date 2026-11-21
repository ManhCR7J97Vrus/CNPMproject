package com.warehouse.service;

import com.warehouse.dto.request.ReportRequest;
import com.warehouse.dto.response.ReportResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ báo cáo kho. */
public interface ReportService {
    List<ReportResponse> stockSummary(ReportRequest.Period request);
}
