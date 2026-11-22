package com.warehouse.service;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.ShipmentResponse;
import java.security.Principal;
import java.util.List;

public interface ShipmentService {
    List<ShipmentResponse> listShipments(Principal actor);
    ShipmentResponse getShipment(Long id, Principal actor);
    ShipmentResponse create(ShipmentRequest.Create request, Principal actor);
    ShipmentResponse assign(Long id, ShipmentRequest.Assign request, Principal actor);
    ShipmentResponse acceptShipment(Long id, ShipmentRequest.Receipt request, Principal actor);
    ShipmentResponse deliver(Long id, ShipmentRequest.Delivery request, Principal actor);
    ShipmentResponse reschedule(Long id, ShipmentRequest.Reschedule request, Principal actor);
    ShipmentResponse requestReturn(Long id, ShipmentRequest.ReturnRequest request, Principal actor);
    List<ShipmentResponse.Driver> drivers(String carrier, boolean availableOnly, Principal actor);
    ShipmentResponse.Driver updateDriver(Long id, ShipmentRequest.Driver request, Principal actor);
    List<ShipmentResponse.Vehicle> vehicles(String carrier, boolean activeOnly, Principal actor);
    ShipmentResponse.Vehicle createVehicle(ShipmentRequest.Vehicle request, Principal actor);
    ShipmentResponse.Vehicle updateVehicle(Long id, ShipmentRequest.Vehicle request, Principal actor);
}
