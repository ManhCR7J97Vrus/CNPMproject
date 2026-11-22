package com.warehouse.mapper;

import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.entity.Shipment;
import com.warehouse.entity.User;
import com.warehouse.entity.Vehicle;
import java.util.List;

public interface ShipmentMapper {
    static ShipmentResponse toResponse(Shipment shipment, List<OutboundOrderResponse.Event> history) {
        Vehicle vehicle = shipment.getVehicle();
        User driver = shipment.getDriver();
        var order = shipment.getOutboundOrder();
        return new ShipmentResponse(shipment.getId(), order.getId(), shipment.getCarrierName(), shipment.getTrackingNo(),
                shipment.getStatus(), shipment.getDispatchedAt(), shipment.getDeliveredAt(), order.getDispatchNo(),
                driver == null ? null : driver.getId(), shipment.getDriverName(), shipment.getDriverPhone(),
                vehicle == null ? null : vehicle.getId(), vehicle == null ? null : vehicle.getCode(),
                vehicle == null ? null : vehicle.getPlateNumber(), shipment.getPickupLocation(), shipment.getDropoffLocation(),
                shipment.getContactName(), shipment.getContactPhone(), shipment.getStartAt(), shipment.getEndAt(),
                order.getPackageCount(), shipment.getCheckedPackages(), shipment.getCheckedAt(), shipment.getReceivedPackages(),
                shipment.getDeliveryNote(), shipment.isReturnRequested(), history);
    }

    static ShipmentResponse.Driver driver(User user) {
        return new ShipmentResponse.Driver(user.getId(), user.getUsername(), user.getFullName(), user.getPhone(),
                user.getCarrierName(), user.isActive() && user.isDriverAvailable());
    }

    static ShipmentResponse.Vehicle vehicle(Vehicle vehicle) {
        return new ShipmentResponse.Vehicle(vehicle.getId(), vehicle.getCode(), vehicle.getPlateNumber(),
                vehicle.getCarrierName(), vehicle.isActive());
    }
}
