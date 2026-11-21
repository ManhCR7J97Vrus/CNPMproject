package com.warehouse.controller;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.service.ShipmentService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ chuyến giao hàng. */
@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService service;

    @GetMapping("/mine")
    @PreAuthorize("hasRole('DRIVER')")
    public List<ShipmentResponse> myShipments(Principal actor) {
        return service.myShipments(actor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ShipmentResponse getShipment(@PathVariable("id") Long id, Principal actor) {
        return service.getShipment(id, actor);
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ShipmentResponse acceptShipment(@PathVariable("id") Long id, @Valid @RequestBody ShipmentRequest.Receipt request, Principal actor) {
        return service.acceptShipment(id, request, actor);
    }
}
