package com.warehouse.controller;

import com.warehouse.dto.request.SupportRequest;
import com.warehouse.dto.response.SupportResponse;
import com.warehouse.service.SupportService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class SupportController {
    private final SupportService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public List<SupportResponse> list(Principal actor) {
        return service.list(actor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public SupportResponse detail(@PathVariable Long id, Principal actor) {
        return service.get(id, actor);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public SupportResponse create(@Valid @RequestBody SupportRequest.Create request, Principal actor) {
        return service.create(request, actor);
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('MANAGER')")
    public SupportResponse review(@PathVariable Long id, @Valid @RequestBody SupportRequest.Review request,
                                  Principal actor) {
        return service.review(id, request, actor);
    }
}
