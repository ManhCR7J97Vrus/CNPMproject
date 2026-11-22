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

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'DISPATCHER')")
    public List<ShipmentResponse> list(Principal actor) {
        return service.listShipments(actor);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('DRIVER')")
    public List<ShipmentResponse> myShipments(Principal actor) {
        return service.listShipments(actor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DRIVER', 'STAFF', 'MANAGER', 'DISPATCHER')")
    public ShipmentResponse getShipment(@PathVariable Long id, Principal actor) {
        return service.getShipment(id, actor);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public ShipmentResponse create(@Valid @RequestBody ShipmentRequest.Create request, Principal actor) {
        return service.create(request, actor);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ShipmentResponse assign(@PathVariable Long id, @Valid @RequestBody ShipmentRequest.Assign request, Principal actor) {
        return service.assign(id, request, actor);
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ShipmentResponse acceptShipment(@PathVariable Long id, @Valid @RequestBody ShipmentRequest.Receipt request,
                                           Principal actor) {
        return service.acceptShipment(id, request, actor);
    }

    @PostMapping("/{id}/delivery")
    @PreAuthorize("hasRole('DRIVER')")
    public ShipmentResponse deliver(@PathVariable Long id, @Valid @RequestBody ShipmentRequest.Delivery request, Principal actor) {
        return service.deliver(id, request, actor);
    }

    @PostMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER', 'DRIVER')")
    public ShipmentResponse reschedule(@PathVariable Long id, @Valid @RequestBody ShipmentRequest.Reschedule request, Principal actor) {
        return service.reschedule(id, request, actor);
    }

    @PostMapping("/{id}/request-return")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ShipmentResponse requestReturn(@PathVariable Long id, @Valid @RequestBody ShipmentRequest.ReturnRequest request,
                                          Principal actor) {
        return service.requestReturn(id, request, actor);
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public List<ShipmentResponse.Driver> drivers(@RequestParam(required = false) String carrier,
            @RequestParam(defaultValue = "false") boolean availableOnly, Principal actor) {
        return service.drivers(carrier, availableOnly, actor);
    }

    @PutMapping("/drivers/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ShipmentResponse.Driver updateDriver(@PathVariable Long id,
            @Valid @RequestBody ShipmentRequest.Driver request, Principal actor) {
        return service.updateDriver(id, request, actor);
    }

    @GetMapping("/vehicles")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public List<ShipmentResponse.Vehicle> vehicles(@RequestParam(required = false) String carrier,
            @RequestParam(defaultValue = "false") boolean activeOnly, Principal actor) {
        return service.vehicles(carrier, activeOnly, actor);
    }

    @PostMapping("/vehicles")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ShipmentResponse.Vehicle createVehicle(@Valid @RequestBody ShipmentRequest.Vehicle request, Principal actor) {
        return service.createVehicle(request, actor);
    }

    @PutMapping("/vehicles/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ShipmentResponse.Vehicle updateVehicle(@PathVariable Long id,
            @Valid @RequestBody ShipmentRequest.Vehicle request, Principal actor) {
        return service.updateVehicle(id, request, actor);
    }
}
