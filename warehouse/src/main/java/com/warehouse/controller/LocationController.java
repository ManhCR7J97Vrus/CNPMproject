package com.warehouse.controller;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import com.warehouse.service.LocationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ vị trí lưu trữ. */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<LocationResponse> listLocations() {
        return service.listLocations();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public LocationResponse getLocation(@PathVariable("id") Long id) {
        return service.getLocation(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public LocationResponse createLocation(@Valid @RequestBody LocationRequest.Write request) {
        return service.createLocation(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public LocationResponse updateLocation(@PathVariable("id") Long id, @Valid @RequestBody LocationRequest.Write request) {
        return service.updateLocation(id, request);
    }
}
