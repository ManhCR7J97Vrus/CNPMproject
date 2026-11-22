package com.warehouse.service;

import com.warehouse.dto.request.MovementRequest;
import com.warehouse.dto.response.MovementResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ biến động kho. */
public interface MovementService {
    List<MovementResponse> listMovements(String referenceNo);

    MovementResponse transferStock(MovementRequest.Transfer request);
}
