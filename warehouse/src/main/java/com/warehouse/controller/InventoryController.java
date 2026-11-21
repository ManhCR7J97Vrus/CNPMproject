package com.warehouse.controller;

import com.warehouse.dto.request.InventoryRequest;
import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ tồn kho. */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<InventoryResponse> listInventory(@Valid @ModelAttribute InventoryRequest.Search request) {
        return service.listInventory(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InventoryResponse getInventory(@PathVariable("id") Long id) {
        return service.getInventory(id);
    }
}
