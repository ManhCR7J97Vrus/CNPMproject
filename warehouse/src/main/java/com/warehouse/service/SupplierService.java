package com.warehouse.service;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ nhà cung cấp. */
public interface SupplierService {
    List<SupplierResponse> listSuppliers();

    SupplierResponse getSupplier(Long id);

    SupplierResponse createSupplier(SupplierRequest.Write request);

    SupplierResponse updateSupplier(Long id, SupplierRequest.Write request);
}
