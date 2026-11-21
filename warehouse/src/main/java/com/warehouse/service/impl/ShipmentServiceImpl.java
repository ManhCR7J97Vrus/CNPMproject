package com.warehouse.service.impl;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.service.ShipmentService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ chuyến giao hàng. */
@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {
    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> myShipments(Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(Long id, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public ShipmentResponse acceptShipment(Long id, ShipmentRequest.Receipt request, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
