package com.warehouse.service.impl;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import com.warehouse.service.LocationService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ vị trí lưu trữ. */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {
    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> listLocations() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public LocationResponse createLocation(LocationRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public LocationResponse updateLocation(Long id, LocationRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
