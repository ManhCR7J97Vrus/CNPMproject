package com.warehouse.service.impl;

import com.warehouse.dto.request.ReportRequest;
import com.warehouse.dto.response.ReportResponse;
import com.warehouse.service.ReportService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ báo cáo kho. */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {
    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> stockSummary(ReportRequest.Period request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
