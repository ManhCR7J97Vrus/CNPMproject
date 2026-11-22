package com.warehouse.service.impl;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.entity.*;
import com.warehouse.entity.enums.OutboundStatus;
import com.warehouse.entity.enums.Role;
import com.warehouse.entity.enums.ShipmentStatus;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.ShipmentMapper;
import com.warehouse.repository.*;
import com.warehouse.service.ShipmentService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private static final Set<ShipmentStatus> BOOKED = EnumSet.of(
            ShipmentStatus.ASSIGNED, ShipmentStatus.AWAITING_DRIVER, ShipmentStatus.DISPATCHED);
    private static final Set<String> SHIPMENT_EVENTS = Set.of("SHIPMENT_REQUESTED", "ASSIGNED", "WAREHOUSE_CHECKED",
            "HANDED_OVER", "LATE_PICKUP", "NO_SHOW", "REDELIVERY", "DELIVERED", "FAILED", "RETURN_REQUESTED", "CANCELLED");
    private final ShipmentRepository shipments;
    private final OutboundOrderRepository orders;
    private final OutboundItemRepository items;
    private final InboundOrderRepository inbound;
    private final UserRepository users;
    private final VehicleRepository vehicles;
    private final StockLedger ledger;
    private final StockWriteLock lock;
    private final AccountActor account;
    private final OrderHistory history;

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> listShipments(Principal principal) {
        User user = account.require(principal);
        List<Shipment> list = switch (user.getRole()) {
            case DRIVER -> shipments.findByDriverIdOrderByIdDesc(user.getId());
            case DISPATCHER -> shipments.findByCarrierNameIgnoreCaseOrderByIdDesc(user.getCarrierName());
            default -> shipments.findAll(Sort.by(Sort.Direction.DESC, "id"));
        };
        return list.stream().map(shipment -> ShipmentMapper.toResponse(shipment, List.of())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(Long id, Principal principal) {
        User user = account.require(principal);
        return detail(visible(id, user), user);
    }

    @Override
    public ShipmentResponse create(ShipmentRequest.Create input, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        OutboundOrder order = orders.findById(input.outboundOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy phiếu xuất"));
        state(order.getStatus() == OutboundStatus.PACKED && order.getDispatchNo() != null,
                "Cần có phiếu xuất chờ bàn giao để gửi yêu cầu vận chuyển");
        String carrier = code(input.carrierName(), "carrierName");
        Shipment existing = shipments.findByOutboundOrderId(order.getId()).orElse(null);
        if (existing != null) {
            state(existing.getCarrierName().equalsIgnoreCase(carrier), "Phiếu đã có yêu cầu tại đơn vị khác");
            return detail(existing, user);
        }
        Shipment shipment = new Shipment();
        shipment.setOutboundOrder(order);
        shipment.setCarrierName(carrier);
        shipment.setPickupLocation(text(input.pickupLocation(), "pickupLocation"));
        shipment.setDropoffLocation(valueOr(input.dropoffLocation(), order.getShippingAddress()));
        shipment.setContactName(valueOr(input.contactName(), order.getReceiverName()));
        shipment.setContactPhone(valueOr(input.contactPhone(), order.getReceiverPhone()));
        shipment.setStatus(ShipmentStatus.PENDING_ASSIGNMENT);
        shipments.save(shipment);
        history.add(order, user, "SHIPMENT_REQUESTED", null);
        return detail(shipment, user);
    }

    @Override
    public ShipmentResponse assign(Long id, ShipmentRequest.Assign input, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        Shipment shipment = visible(id, user);
        state(shipment.getStatus() == ShipmentStatus.PENDING_ASSIGNMENT || shipment.getStatus() == ShipmentStatus.ASSIGNED
                        || shipment.getStatus() == ShipmentStatus.AWAITING_DRIVER, "Chuyến không còn chờ phân công");
        state(shipment.getOutboundOrder().getStatus() == OutboundStatus.PACKED, "Phiếu xuất không còn chờ bàn giao");
        User driver = driver(input.driverId());
        Vehicle vehicle = findVehicle(input.vehicleId());
        state(driver.isActive() && driver.isDriverAvailable() && sameCarrier(shipment.getCarrierName(), driver.getCarrierName()),
                "Tài xế ngừng hoạt động hoặc ngoài đơn vị");
        state(vehicle.isActive() && sameCarrier(shipment.getCarrierName(), vehicle.getCarrierName()),
                "Xe ngừng hoạt động hoặc ngoài đơn vị");
        checkSlot(shipment.getId(), input.startAt(), input.endAt(), driver, vehicle);
        LocalDateTime previous = shipment.getStartAt();
        shipment.setDriver(driver);
        shipment.setDriverName(driver.getFullName());
        shipment.setDriverPhone(driver.getPhone());
        shipment.setVehicle(vehicle);
        shipment.setStartAt(input.startAt());
        shipment.setEndAt(input.endAt());
        shipment.setTrackingNo(optional(input.trackingNo()));
        resetDock(shipment);
        history.add(shipment.getOutboundOrder(), user, "ASSIGNED", null, previous, shipment.getStartAt());
        return detail(shipment, user);
    }

    @Override
    public ShipmentResponse acceptShipment(Long id, ShipmentRequest.Receipt input, Principal principal) {
        lock.acquire();
        User driver = account.require(principal);
        Shipment shipment = visible(id, driver);
        if (shipment.getDispatchedAt() != null) {
            return detail(shipment, driver);
        }
        OutboundOrder order = shipment.getOutboundOrder();
        state(shipment.getStatus() == ShipmentStatus.AWAITING_DRIVER && shipment.getCheckedPackages() != null
                && order.getStatus() == OutboundStatus.PACKED, "Chờ kho xác nhận đủ kiện");
        Integer packages = input.packageCount() == null ? shipment.getCheckedPackages() : input.packageCount();
        require(packages.equals(shipment.getCheckedPackages()), "packageCount", "Số kiện tài xế nhận khác số kho đối chiếu");
        List<OutboundItem> lines = items.findByOutboundOrderIdOrderById(order.getId());
        state(!lines.isEmpty(), "Phiếu xuất chưa có dòng hàng");
        for (OutboundItem line : lines) {
            state(line.getInventory() != null && line.getQtyPicked().compareTo(line.getQtyRequested()) == 0,
                    "Dòng phiếu chưa được lấy đủ");
            Inventory stock = ledger.rowForUpdate(line.getInventory().getId());
            state(ledger.eligible(stock), "Lô " + stock.getBatch().getBatchNo() + " không còn đủ điều kiện ghi xuất");
            ledger.dispatch(stock, line.getQtyPicked(), order.getDispatchNo(), driver);
        }
        shipment.setReceivedPackages(packages);
        shipment.setDispatchedAt(ledger.now());
        shipment.setStatus(ShipmentStatus.DISPATCHED);
        order.setStatus(OutboundStatus.HANDED_OVER);
        history.add(order, driver, "HANDED_OVER", optional(input.note()));
        return detail(shipment, driver);
    }

    @Override
    public ShipmentResponse deliver(Long id, ShipmentRequest.Delivery input, Principal principal) {
        lock.acquire();
        User driver = account.require(principal);
        Shipment shipment = visible(id, driver);
        state(shipment.getStatus() == ShipmentStatus.DISPATCHED, "Chuyến phải đang giao");
        boolean delivered = input.delivered();
        String note = delivered ? optional(input.note()) : text(input.note(), "note");
        require(!delivered || !input.requestReturn(), "requestReturn", "Chỉ yêu cầu hoàn khi giao thất bại");
        shipment.setDeliveryNote(note);
        shipment.setStatus(delivered ? ShipmentStatus.DELIVERED : ShipmentStatus.FAILED);
        shipment.setDeliveredAt(delivered ? ledger.now() : null);
        OutboundOrder order = shipment.getOutboundOrder();
        order.setStatus(delivered ? OutboundStatus.DELIVERED : OutboundStatus.FAILED);
        history.add(order, driver, delivered ? "DELIVERED" : "FAILED", note);
        if (input.requestReturn()) {
            markReturn(shipment, driver, note);
        }
        return detail(shipment, driver);
    }

    @Override
    public ShipmentResponse reschedule(Long id, ShipmentRequest.Reschedule input, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        Shipment shipment = visible(id, user);
        boolean redelivery = input.kind() == ShipmentRequest.ChangeKind.REDELIVERY;
        if (user.getRole() == Role.DRIVER && input.kind() != ShipmentRequest.ChangeKind.LATE_PICKUP) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if (redelivery) {
            state(shipment.getStatus() == ShipmentStatus.FAILED && !shipment.isReturnRequested()
                    && !inbound.existsByReturnOfId(shipment.getOutboundOrder().getId()),
                    "Chuyến phải giao thất bại và hàng còn do đơn vị vận chuyển giữ");
        } else {
            state((shipment.getStatus() == ShipmentStatus.ASSIGNED || shipment.getStatus() == ShipmentStatus.AWAITING_DRIVER)
                            && !ledger.now().isBefore(shipment.getStartAt()),
                    "Chỉ đổi lịch nhận sau giờ hẹn khi chuyến chưa bàn giao");
        }
        require(input.startAt().isAfter(ledger.now()), "startAt", "Giờ hẹn mới phải ở tương lai");
        checkSlot(id, input.startAt(), input.endAt(), shipment.getDriver(), shipment.getVehicle());
        LocalDateTime previous = shipment.getStartAt();
        shipment.setStartAt(input.startAt());
        shipment.setEndAt(input.endAt());
        if (redelivery) {
            shipment.setStatus(ShipmentStatus.DISPATCHED);
        } else {
            resetDock(shipment);
        }
        history.add(shipment.getOutboundOrder(), user, input.kind().name(), text(input.reason(), "reason"),
                previous, shipment.getStartAt());
        return detail(shipment, user);
    }

    @Override
    public ShipmentResponse requestReturn(Long id, ShipmentRequest.ReturnRequest input, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        Shipment shipment = visible(id, user);
        state(shipment.getStatus() == ShipmentStatus.FAILED, "Chỉ yêu cầu hoàn từ chuyến giao thất bại");
        markReturn(shipment, user, text(input.reason(), "reason"));
        return detail(shipment, user);
    }

    private void markReturn(Shipment shipment, User user, String reason) {
        if (!shipment.isReturnRequested()) {
            shipment.setReturnRequested(true);
            history.add(shipment.getOutboundOrder(), user, "RETURN_REQUESTED", reason);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse.Driver> drivers(String carrier, boolean availableOnly, Principal principal) {
        User user = account.require(principal);
        String scope = carrierScope(user, carrier);
        return users.findAllByRoleOrderByUsername(Role.DRIVER).stream()
                .filter(driver -> scope == null || sameCarrier(scope, driver.getCarrierName()))
                .map(ShipmentMapper::driver).filter(response -> !availableOnly || response.available()).toList();
    }

    @Override
    public ShipmentResponse.Driver updateDriver(Long id, ShipmentRequest.Driver input, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        User driver = driver(id);
        String carrier = carrierScope(user, code(input.carrierName(), "carrierName"));
        if (user.getRole() == Role.DISPATCHER && !sameCarrier(carrier, driver.getCarrierName())) {
            throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy tài xế trong đơn vị");
        }
        driver.setCarrierName(carrier);
        driver.setPhone(optional(input.phone()));
        driver.setDriverAvailable(input.available());
        return ShipmentMapper.driver(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse.Vehicle> vehicles(String carrier, boolean activeOnly, Principal principal) {
        String scope = carrierScope(account.require(principal), carrier);
        List<Vehicle> list = scope == null ? vehicles.findAll(Sort.by("carrierName", "code"))
                : vehicles.findByCarrierNameIgnoreCaseOrderByCode(scope);
        return list.stream().filter(vehicle -> !activeOnly || vehicle.isActive()).map(ShipmentMapper::vehicle).toList();
    }

    @Override
    public ShipmentResponse.Vehicle createVehicle(ShipmentRequest.Vehicle input, Principal principal) {
        lock.acquire();
        return writeVehicle(new Vehicle(), input, account.require(principal));
    }

    @Override
    public ShipmentResponse.Vehicle updateVehicle(Long id, ShipmentRequest.Vehicle input, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        Vehicle vehicle = findVehicle(id);
        if (user.getRole() == Role.DISPATCHER && !sameCarrier(user.getCarrierName(), vehicle.getCarrierName())) {
            throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy xe trong đơn vị");
        }
        return writeVehicle(vehicle, input, user);
    }

    private ShipmentResponse.Vehicle writeVehicle(Vehicle vehicle, ShipmentRequest.Vehicle input, User user) {
        String carrier = carrierScope(user, code(input.carrierName(), "carrierName"));
        String code = code(input.code(), "code");
        if (vehicles.existsByCarrierNameIgnoreCaseAndCodeIgnoreCaseAndIdNot(carrier, code,
                vehicle.getId() == null ? 0L : vehicle.getId())) {
            throw new AppException(ErrorCode.DUPLICATE, "code", "Mã xe đã dùng trong đơn vị");
        }
        vehicle.setCarrierName(carrier);
        vehicle.setCode(code);
        vehicle.setPlateNumber(code(input.plateNumber(), "plateNumber"));
        vehicle.setActive(input.active());
        return ShipmentMapper.vehicle(vehicles.save(vehicle));
    }

    private void checkSlot(Long id, LocalDateTime start, LocalDateTime end, User driver, Vehicle vehicle) {
        require(end.isAfter(start), "endAt", "Giờ kết thúc phải sau giờ bắt đầu");
        List<Shipment> overlaps = shipments.findScheduleConflicts(id, BOOKED, start, end, driver.getId(), vehicle.getId());
        state(overlaps.stream().noneMatch(other -> other.getDriver().getId().equals(driver.getId())),
                "Tài xế " + driver.getUsername() + " trùng lịch");
        state(overlaps.stream().noneMatch(other -> other.getVehicle().getId().equals(vehicle.getId())),
                "Xe " + vehicle.getCode() + " trùng lịch");
    }

    private Shipment visible(Long id, User user) {
        return shipments.findById(id).filter(shipment -> switch (user.getRole()) {
            case DRIVER -> shipment.getDriver() != null && shipment.getDriver().getId().equals(user.getId());
            case DISPATCHER -> sameCarrier(user.getCarrierName(), shipment.getCarrierName());
            default -> true;
        }).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy chuyến"));
    }

    private ShipmentResponse detail(Shipment shipment, User user) {
        boolean driver = user.getRole() == Role.DRIVER;
        List<OutboundOrderResponse.Event> events = history.read(shipment.getOutboundOrder().getId(), driver).stream()
                .filter(event -> SHIPMENT_EVENTS.contains(event.type())).toList();
        return ShipmentMapper.toResponse(shipment, events);
    }

    private User driver(Long id) {
        return users.findById(id).filter(user -> user.getRole() == Role.DRIVER)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy tài xế"));
    }

    private Vehicle findVehicle(Long id) {
        return vehicles.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy xe"));
    }

    private static String carrierScope(User user, String requested) {
        if (user.getRole() != Role.DISPATCHER) {
            return optional(requested);
        }
        if (optional(requested) != null && !sameCarrier(user.getCarrierName(), requested)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return user.getCarrierName();
    }

    private static boolean sameCarrier(String one, String other) {
        return one != null && one.equalsIgnoreCase(other);
    }

    private static String valueOr(String value, String fallback) {
        return optional(value) == null ? fallback : optional(value);
    }

    private static void resetDock(Shipment shipment) {
        shipment.setCheckedPackages(null);
        shipment.setCheckedAt(null);
        shipment.setStatus(ShipmentStatus.ASSIGNED);
    }
}
