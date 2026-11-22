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
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'SUPPLIER')")
    public List<InboundOrderResponse> listInboundOrders(@RequestParam(required = false) String reference) {
        return service.listInboundOrders(reference);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'SUPPLIER')")
    public InboundOrderResponse createInbound(@Valid @RequestBody InboundOrderRequest.Write request) {
        return service.createInbound(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'SUPPLIER')")
    public InboundOrderResponse getInbound(@PathVariable("id") Long id) {
        return service.getInbound(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'SUPPLIER')")
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
    public InboundOrderResponse confirmInbound(@PathVariable("id") Long id,
            @Valid @RequestBody(required = false) InboundOrderRequest.Placements placements) {
        return service.confirmInbound(id, placements);
    }

    @PostMapping("/{id}/post")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse postInbound(@PathVariable Long id, @Valid @RequestBody InboundOrderRequest.Placements actual) {
        return service.postInbound(id, actual);
    }

    @PostMapping("/returns")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse createReturn(@Valid @RequestBody InboundOrderRequest.Return request) {
        return service.createReturn(request);
    }
}
