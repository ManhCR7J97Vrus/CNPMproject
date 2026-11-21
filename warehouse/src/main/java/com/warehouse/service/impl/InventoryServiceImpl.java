package com.warehouse.service.impl;

import com.warehouse.dto.request.InventoryRequest;
import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.service.InventoryService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ tồn kho. */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {
    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> listInventory(InventoryRequest.Search request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
