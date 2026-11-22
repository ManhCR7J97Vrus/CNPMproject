package com.warehouse.controller;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.service.OutboundOrderService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ đơn xuất. */
@RestController
@RequestMapping("/api/v1/outbound-orders")
@RequiredArgsConstructor
public class OutboundOrderController {
    private final OutboundOrderService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'MANAGER')")
    public List<OutboundOrderResponse> listOrders(@Valid @ModelAttribute OutboundOrderRequest.Search search, Principal actor) {
        return service.listOrders(search, actor);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public OutboundOrderResponse createOrder(@Valid @RequestBody OutboundOrderRequest.Create request, Principal actor) {
        return service.createOrder(request, actor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'MANAGER')")
    public OutboundOrderResponse getOrder(@PathVariable("id") Long id, Principal actor) {
        return service.getOrder(id, actor);
    }

    @GetMapping("/{id}/suggestions")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public OutboundOrderResponse.Plan suggest(@PathVariable Long id) {
        return service.suggest(id);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public OutboundOrderResponse approveOrder(@PathVariable("id") Long id) {
        return service.approveOrder(id);
    }

    @PostMapping("/{id}/reallocate")
    @PreAuthorize("hasRole('MANAGER')")
    public OutboundOrderResponse reallocate(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean returnedToStorage) {
        return service.reallocate(id, returnedToStorage);
    }

    @PostMapping("/{id}/pick")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public OutboundOrderResponse pickOrder(@PathVariable("id") Long id, @Valid @RequestBody OutboundOrderRequest.Pick request) {
        return service.pickOrder(id, request);
    }

    @PostMapping("/{id}/pack")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public OutboundOrderResponse packOrder(@PathVariable("id") Long id, @Valid @RequestBody OutboundOrderRequest.Pack request) {
        return service.packOrder(id, request);
    }

    @PostMapping("/{id}/issue-dispatch")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public OutboundOrderResponse issueDispatch(@PathVariable Long id) {
        return service.issueDispatch(id);
    }

    @PostMapping("/{id}/cancel-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public OutboundOrderResponse requestCancel(@PathVariable Long id,
            @Valid @RequestBody OutboundOrderRequest.CancelRequest request, Principal actor) {
        return service.requestCancel(id, request, actor);
    }

    @PostMapping("/{id}/handover")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public OutboundOrderResponse handoverOrder(@PathVariable("id") Long id, @Valid @RequestBody OutboundOrderRequest.Handover request) {
        return service.handoverOrder(id, request);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public OutboundOrderResponse cancelOrder(@PathVariable("id") Long id, @Valid @RequestBody OutboundOrderRequest.Cancel request) {
        return service.cancelOrder(id, request);
    }
}
