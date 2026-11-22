package com.warehouse.service;

import com.warehouse.dto.request.SupportRequest;
import com.warehouse.dto.response.SupportResponse;
import java.security.Principal;
import java.util.List;

public interface SupportService {
    List<SupportResponse> list(Principal actor);
    SupportResponse get(Long id, Principal actor);
    SupportResponse create(SupportRequest.Create request, Principal actor);
    SupportResponse review(Long id, SupportRequest.Review request, Principal actor);
}
