package com.warehouse.service.impl;

import com.warehouse.dto.request.BatchRequest;
import com.warehouse.dto.response.BatchResponse;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.BatchService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ lô hàng. */
@Service
@Transactional
public class BatchServiceImpl implements BatchService {
    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> listBatches(BatchRequest.Search request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public BatchResponse getBatch(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> traceBatch(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
