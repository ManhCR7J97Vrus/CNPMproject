package com.warehouse.service;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ vị trí lưu trữ. */
public interface LocationService {
    List<LocationResponse> listLocations();

    LocationResponse getLocation(Long id);

    LocationResponse createLocation(LocationRequest.Write request);

    LocationResponse updateLocation(Long id, LocationRequest.Write request);
}
