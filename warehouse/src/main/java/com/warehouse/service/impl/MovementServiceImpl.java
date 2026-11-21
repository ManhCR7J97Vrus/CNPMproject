package com.warehouse.service.impl;

import com.warehouse.dto.request.MovementRequest;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.MovementService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ biến động kho. */
@Service
@Transactional
public class MovementServiceImpl implements MovementService {
    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> listMovements() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public MovementResponse transferStock(MovementRequest.Transfer request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
