package com.warehouse.controller;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.service.InboundOrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ phiếu nhập. */
@RestController
@RequestMapping("/api/v1/inbound-orders")
@RequiredArgsConstructor
public class InboundOrderController {
    private final InboundOrderService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<InboundOrderResponse> listInboundOrders() {
        return service.listInboundOrders();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse createInbound(@Valid @RequestBody InboundOrderRequest.Write request) {
        return service.createInbound(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse getInbound(@PathVariable("id") Long id) {
        return service.getInbound(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse updateInbound(@PathVariable("id") Long id, @Valid @RequestBody InboundOrderRequest.Write request) {
        return service.updateInbound(id, request);
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse receiveInbound(@PathVariable("id") Long id, @Valid @RequestBody InboundOrderRequest.Receive request) {
        return service.receiveInbound(id, request);
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('MANAGER')")
    public InboundOrderResponse confirmInbound(@PathVariable("id") Long id) {
        return service.confirmInbound(id);
    }
}
