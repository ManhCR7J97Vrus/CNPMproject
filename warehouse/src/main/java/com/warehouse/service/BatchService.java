package com.warehouse.service;

import com.warehouse.dto.request.BatchRequest;
import com.warehouse.dto.response.BatchResponse;
import com.warehouse.dto.response.MovementResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ lô hàng. */
public interface BatchService {
    BatchResponse.Alerts alerts();
    List<BatchResponse> listBatches(BatchRequest.Search request);

    BatchResponse getBatch(Long id);

    List<MovementResponse> traceBatch(Long id);
}
