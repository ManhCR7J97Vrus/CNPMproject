package com.warehouse.service.impl;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.service.SupplierService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ nhà cung cấp. */
@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {
    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> listSuppliers() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplier(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
