package com.warehouse.service;

import com.warehouse.dto.request.InventoryRequest;
import com.warehouse.dto.response.InventoryResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ tồn kho. */
public interface InventoryService {
    InventoryResponse.Summary summarize(InventoryRequest.Search request);
    List<InventoryResponse> listInventory(InventoryRequest.Search request);

    InventoryResponse getInventory(Long id);
}
