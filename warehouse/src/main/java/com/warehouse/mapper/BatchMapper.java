package com.warehouse.mapper;

import com.warehouse.dto.response.BatchResponse;
import com.warehouse.entity.Batch;
import com.warehouse.entity.enums.BatchStatus;

public interface BatchMapper {
    static BatchResponse toResponse(Batch batch, BatchStatus status) {
        var product = batch.getProduct();
        return new BatchResponse(batch.getId(), product.getId(), batch.getSupplier().getId(), batch.getBatchNo(),
                batch.getMfgDate(), batch.getExpiryDate(), batch.getReceivedAt(), status, product.getSkuCode(), product.getName(),
                batch.getStorageCondition() == null ? product.getStorageCondition() : batch.getStorageCondition());
    }
}
