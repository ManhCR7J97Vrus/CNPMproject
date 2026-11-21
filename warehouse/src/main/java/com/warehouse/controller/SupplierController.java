package com.warehouse.controller;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.service.SupplierService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ nhà cung cấp. */
@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<SupplierResponse> listSuppliers() {
        return service.listSuppliers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public SupplierResponse getSupplier(@PathVariable("id") Long id) {
        return service.getSupplier(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public SupplierResponse createSupplier(@Valid @RequestBody SupplierRequest.Write request) {
        return service.createSupplier(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public SupplierResponse updateSupplier(@PathVariable("id") Long id, @Valid @RequestBody SupplierRequest.Write request) {
        return service.updateSupplier(id, request);
    }
}
