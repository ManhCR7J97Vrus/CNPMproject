package com.warehouse.service.impl;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.entity.*;
import com.warehouse.entity.enums.OutboundStatus;
import com.warehouse.entity.enums.Role;
import com.warehouse.entity.enums.ShipmentStatus;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.OutboundOrderMapper;
import com.warehouse.repository.*;
import com.warehouse.service.OutboundOrderService;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OutboundOrderServiceImpl implements OutboundOrderService {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final OutboundOrderRepository orders;
    private final OutboundItemRepository items;
    private final ProductRepository products;
    private final ServiceLevelRepository levels;
    private final ShipmentRepository shipments;
    private final AccountActor account;
    private final PickingPlanner planner;
    private final StockWriteLock lock;
    private final StockLedger ledger;
    private final OrderHistory history;

    @Override
    @Transactional(readOnly = true)
    public List<OutboundOrderResponse> listOrders(OutboundOrderRequest.Search search, Principal principal) {
        User user = account.require(principal);
        boolean customer = user.getRole() == Role.CUSTOMER;
        require(search.from() == null || search.to() == null || !search.from().isAfter(search.to()),
                "from", "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        String reference = optional(search.reference());
        var candidates = customer ? orders.findByCustomerId(user.getId()) : orders.findAll();
        Comparator<OutboundOrder> sort = customer
                ? Comparator.comparing(OutboundOrder::getCreatedAt).reversed().thenComparing(OutboundOrder::getOrderNo)
                : workQueue();
        return candidates.stream().filter(order -> matches(order, search, reference)).sorted(sort)
                .map(order -> response(order, customer, false)).toList();
    }

    private Comparator<OutboundOrder> workQueue() {
        var ranks = levels.findAll().stream().collect(Collectors.toMap(ServiceLevel::getPriority, ServiceLevel::getRank));
        return Comparator.comparing((OutboundOrder order) -> order.getStatus() != OutboundStatus.REQUESTED)
                .thenComparing(OutboundOrder::getDeliveryDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(order -> ranks.getOrDefault(order.getPriority(), Integer.MAX_VALUE))
                .thenComparing(OutboundOrder::getCreatedAt)
                .thenComparing(OutboundOrder::getOrderNo);
    }

    private static boolean matches(OutboundOrder order, OutboundOrderRequest.Search search, String reference) {
        LocalDate date = order.getCreatedAt().toLocalDate();
        return (search.from() == null || !date.isBefore(search.from()))
                && (search.to() == null || !date.isAfter(search.to()))
                && (search.status() == null || order.getStatus() == search.status())
                && (reference == null || reference.equalsIgnoreCase(order.getOrderNo())
                    || reference.equalsIgnoreCase(order.getDispatchNo()));
    }

    @Override
    public OutboundOrderResponse createOrder(OutboundOrderRequest.Create request, Principal principal) {
        lock.acquire();
        User customer = account.require(principal);
        require(request.deliveryDeadline() == null || request.deliveryDeadline().isAfter(ledger.now()),
                "deliveryDeadline", "Hạn giao đã qua");
        OutboundOrder order = new OutboundOrder();
        order.setCustomer(customer);
        order.setCreatedBy(customer);
        order.setCreatedAt(ledger.now());
        order.setOrderNo(documentNo("DX"));
        order.setShippingAddress(text(request.shippingAddress(), "shippingAddress"));
        order.setReceiverName(optional(request.receiverName()) == null ? customer.getFullName() : optional(request.receiverName()));
        order.setReceiverPhone(optional(request.receiverPhone()) == null ? customer.getPhone() : optional(request.receiverPhone()));
        order.setPriority(request.priority());
        order.setStatus(OutboundStatus.REQUESTED);
        order.setDeliveryDeadline(request.deliveryDeadline());

        Map<Long, BigDecimal> demand = new LinkedHashMap<>();
        for (var line : request.items()) {
            demand.merge(line.productId(), positive(line.qtyRequested(), "qtyRequested"), BigDecimal::add);
        }
        List<OutboundItem> lines = new ArrayList<>();
        demand.forEach((id, qty) -> {
            Product product = products.findById(id).filter(Product::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "SKU không tồn tại hoặc đã ngừng hoạt động"));
            lines.add(newItem(order, product, positive(qty, "qtyRequested")));
        });
        orders.save(order);
        items.saveAll(lines);
        history.add(order, customer, "REQUESTED", null);
        return response(order, true, true);
    }

    @Override
    @Transactional(readOnly = true)
    public OutboundOrderResponse getOrder(Long id, Principal principal) {
        User user = account.require(principal);
        return response(visibleOrder(id, user), user.getRole() == Role.CUSTOMER, true);
    }

    @Override
    @Transactional(readOnly = true)
    public OutboundOrderResponse.Plan suggest(Long id) {
        OutboundOrder order = find(id);
        state(order.getStatus() == OutboundStatus.REQUESTED, "Đơn không còn chờ duyệt");
        return planner.plan(lines(order)).response();
    }

    @Override
    public OutboundOrderResponse approveOrder(Long id) {
        lock.acquire();
        OutboundOrder order = find(id);
        state(order.getStatus() == OutboundStatus.REQUESTED, "Đơn không còn chờ duyệt");
        applyPlan(order, lines(order));
        order.setStatus(OutboundStatus.APPROVED);
        history.add(order, account.require(), "APPROVED", null);
        return response(order, false, true);
    }

    @Override
    public OutboundOrderResponse reallocate(Long id, boolean returnedToStorage) {
        lock.acquire();
        OutboundOrder order = find(id);
        state((order.getStatus() == OutboundStatus.APPROVED || order.getStatus() == OutboundStatus.PICKED)
                        && order.getDispatchNo() == null, "Chỉ đổi lô trước khi đóng gói");
        List<OutboundItem> lines = lines(order);
        state(returnedToStorage || lines.stream().noneMatch(line -> line.getQtyPicked().signum() > 0),
                "Cần xác nhận đã trả hàng vừa lấy về đúng ô trước khi đổi lô");
        Map<Long, OutboundItem> demands = new LinkedHashMap<>();
        for (OutboundItem line : lines) {
            ledger.release(line.getInventory(), line.getQtyRequested());
            OutboundItem first = demands.putIfAbsent(line.getProduct().getId(), line);
            if (first != null) {
                first.setQtyRequested(first.getQtyRequested().add(line.getQtyRequested()));
                items.delete(line);
            } else {
                line.setInventory(null);
                line.setQtyPicked(ZERO);
            }
        }
        applyPlan(order, List.copyOf(demands.values()));
        order.setStatus(OutboundStatus.APPROVED);
        history.add(order, account.require(), "REALLOCATED", null);
        return response(order, false, true);
    }

    private void applyPlan(OutboundOrder order, List<OutboundItem> demands) {
        PickingPlanner.Plan plan = planner.plan(demands);
        if (!plan.shortages().isEmpty()) {
            String missing = plan.shortages().stream()
                    .map(row -> row.skuCode() + " thiếu " + row.missing().stripTrailingZeros().toPlainString())
                    .collect(Collectors.joining(", "));
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, missing);
        }
        Map<Long, List<PickingPlanner.Reservation>> byLine = plan.reservations().stream()
                .collect(Collectors.groupingBy(PickingPlanner.Reservation::itemId, LinkedHashMap::new, Collectors.toList()));
        for (OutboundItem demand : demands) {
            boolean first = true;
            for (var reservation : byLine.get(demand.getId())) {
                Inventory row = ledger.rowForUpdate(reservation.row().getId());
                ledger.reserve(row, reservation.quantity());
                OutboundItem line = first ? demand : newItem(order, demand.getProduct(), reservation.quantity());
                line.setStorageCondition(demand.getStorageCondition());
                line.setQtyRequested(reservation.quantity());
                line.setInventory(row);
                items.save(line);
                first = false;
            }
        }
    }

    @Override
    public OutboundOrderResponse pickOrder(Long id, OutboundOrderRequest.Pick request) {
        lock.acquire();
        OutboundOrder order = find(id);
        state(order.getStatus() == OutboundStatus.APPROVED, "Đơn phải đang soạn hàng sau khi duyệt");
        List<OutboundItem> lines = lines(order);
        Map<Long, OutboundItem> byId = lines.stream().collect(Collectors.toMap(OutboundItem::getId, line -> line));
        for (var input : request.items()) {
            OutboundItem line = byId.get(input.outboundItemId());
            require(line != null, "outboundItemId", "Dòng lấy không thuộc đơn");
            Inventory row = line.getInventory();
            require(row != null && row.getId().equals(input.inventoryId())
                            && (input.batchId() == null || row.getBatch().getId().equals(input.batchId()))
                            && (input.locationId() == null || row.getLocation().getId().equals(input.locationId())),
                    "inventoryId", "Lô hoặc ô thực lấy không khớp dòng được phân bổ");
            checkEligible(row);
            BigDecimal picked = line.getQtyPicked().add(positive(input.qtyPicked(), "qtyPicked"));
            require(picked.compareTo(line.getQtyRequested()) <= 0, "qtyPicked",
                    "Vượt số phải lấy " + picked.subtract(line.getQtyRequested()).max(ZERO).stripTrailingZeros().toPlainString());
            line.setQtyPicked(picked);
        }
        if (lines.stream().allMatch(line -> line.getQtyPicked().compareTo(line.getQtyRequested()) == 0)) {
            order.setStatus(OutboundStatus.PICKED);
            history.add(order, account.require(), "PICKED", null);
        }
        return response(order, false, true);
    }

    private void checkEligible(Inventory row) {
        if (!ledger.eligible(row)) {
            var batch = row.getBatch();
            boolean expired = batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(ledger.today());
            throw new AppException(ErrorCode.INVALID_STATE, expired
                    ? "Lô " + batch.getBatchNo() + " đã hết hạn. Cần quản lý phân bổ lô khác"
                    : "Lô " + batch.getBatchNo() + " không còn đủ điều kiện. Cần quản lý kiểm tra");
        }
    }

    @Override
    public OutboundOrderResponse packOrder(Long id, OutboundOrderRequest.Pack request) {
        lock.acquire();
        OutboundOrder order = find(id);
        state(order.getStatus() == OutboundStatus.PICKED, "Đơn phải được lấy đủ trước khi đóng gói");
        lines(order).forEach(line -> checkEligible(line.getInventory()));
        order.setPackageCount(request.packageCount());
        order.setStatus(OutboundStatus.PACKED);
        history.add(order, account.require(), "PACKED", null);
        return response(order, false, true);
    }

    @Override
    public OutboundOrderResponse issueDispatch(Long id) {
        lock.acquire();
        OutboundOrder order = find(id);
        if (order.getDispatchNo() != null) {
            return response(order, false, true);
        }
        state(order.getStatus() == OutboundStatus.PACKED, "Cần đóng gói trước khi lập phiếu xuất");
        order.setDispatchNo(documentNo("PX"));
        order.setIssuedAt(ledger.now());
        history.add(order, account.require(), "DISPATCH_ISSUED", null);
        return response(order, false, true);
    }

    @Override
    public OutboundOrderResponse handoverOrder(Long id, OutboundOrderRequest.Handover request) {
        lock.acquire();
        OutboundOrder order = find(id);
        state(order.getStatus() == OutboundStatus.PACKED && order.getDispatchNo() != null, "Cần phiếu xuất trước khi bàn giao");
        Shipment shipment = shipments.findByOutboundOrderId(order.getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STATE, "Cần phân công chuyến trước khi bàn giao"));
        state(shipment.getStatus() == ShipmentStatus.ASSIGNED || shipment.getStatus() == ShipmentStatus.AWAITING_DRIVER,
                "Chuyến chưa có lịch nhận hoặc đã bàn giao");
        require(order.getPackageCount().equals(request.packageCount()), "packageCount",
                "Số kiện bàn giao phải bằng " + order.getPackageCount());
        if (shipment.getStatus() == ShipmentStatus.ASSIGNED) {
            shipment.setCheckedPackages(request.packageCount());
            shipment.setCheckedAt(ledger.now());
            shipment.setStatus(ShipmentStatus.AWAITING_DRIVER);
            history.add(order, account.require(), "WAREHOUSE_CHECKED", null);
        }
        return response(order, false, true);
    }

    @Override
    public OutboundOrderResponse requestCancel(Long id, OutboundOrderRequest.CancelRequest request, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        OutboundOrder order = visibleOrder(id, user);
        state(order.getStatus() == OutboundStatus.REQUESTED || order.getStatus() == OutboundStatus.APPROVED
                || order.getStatus() == OutboundStatus.PICKED || order.getStatus() == OutboundStatus.PACKED,
                "Đơn không còn ở bước có thể yêu cầu hủy");
        if (order.getCancelRequestedAt() == null) {
            order.setCancelRequestReason(text(request.reason(), "reason"));
            order.setCancelRequestedAt(ledger.now());
            history.add(order, user, "CANCEL_REQUESTED", order.getCancelRequestReason());
        }
        return response(order, true, true);
    }

    @Override
    public OutboundOrderResponse cancelOrder(Long id, OutboundOrderRequest.Cancel request) {
        lock.acquire();
        OutboundOrder order = find(id);
        if (order.getStatus() == OutboundStatus.CANCELLED) {
            return response(order, false, true);
        }
        state(order.getStatus() != OutboundStatus.HANDED_OVER && order.getStatus() != OutboundStatus.DELIVERED
                && order.getStatus() != OutboundStatus.FAILED, "Hàng đã bàn giao. Cần lập phiếu hoàn từ phiếu xuất gốc");
        List<OutboundItem> lines = lines(order);
        String reason = text(request.reason(), "reason");
        User user = account.require();
        boolean alreadyPicked = lines.stream().anyMatch(line -> line.getQtyPicked().signum() > 0);
        if (alreadyPicked && (order.getStatus() != OutboundStatus.CANCEL_PENDING_RETURN || !request.returnedToStorage())) {
            if (order.getStatus() != OutboundStatus.CANCEL_PENDING_RETURN) {
                order.setStatus(OutboundStatus.CANCEL_PENDING_RETURN);
                order.setCancelReason(reason);
                history.add(order, user, "CANCEL_PENDING_RETURN", reason);
            }
            return response(order, false, true);
        }
        for (OutboundItem line : lines) {
            if (line.getInventory() != null) {
                ledger.release(line.getInventory(), line.getQtyRequested());
            }
        }
        shipments.findByOutboundOrderId(id).ifPresent(shipment -> shipment.setStatus(ShipmentStatus.CANCELLED));
        order.setStatus(OutboundStatus.CANCELLED);
        order.setCancelReason(reason);
        history.add(order, user, "CANCELLED", reason);
        return response(order, false, true);
    }

    private OutboundOrder find(Long id) {
        return orders.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy đơn"));
    }

    private OutboundOrder visibleOrder(Long id, User user) {
        OutboundOrder order = find(id);
        if (user.getRole() == Role.CUSTOMER && !order.getCustomer().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy đơn");
        }
        return order;
    }

    private List<OutboundItem> lines(OutboundOrder order) {
        return items.findByOutboundOrderIdOrderById(order.getId());
    }

    private OutboundOrderResponse response(OutboundOrder order, boolean customer, boolean detail) {
        var events = detail ? history.read(order.getId(), customer) : null;
        Long shipmentId = detail && !customer ? shipments.findByOutboundOrderId(order.getId()).map(Shipment::getId).orElse(null) : null;
        return OutboundOrderMapper.toResponse(order, lines(order), customer, events, shipmentId);
    }

    private static OutboundItem newItem(OutboundOrder order, Product product, BigDecimal quantity) {
        OutboundItem line = new OutboundItem();
        line.setOutboundOrder(order);
        line.setProduct(product);
        line.setQtyRequested(quantity);
        line.setQtyPicked(ZERO);
        line.setStorageCondition(product.getStorageCondition());
        line.setUnitPrice(ZERO);
        return line;
    }
}
