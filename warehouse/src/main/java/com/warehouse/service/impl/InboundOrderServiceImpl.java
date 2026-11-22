package com.warehouse.service.impl;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.entity.*;
import com.warehouse.entity.enums.*;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.InboundOrderMapper;
import com.warehouse.repository.*;
import com.warehouse.service.InboundOrderService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class InboundOrderServiceImpl implements InboundOrderService {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final InboundOrderRepository orders;
    private final InboundItemRepository items;
    private final SupplierRepository suppliers;
    private final ProductRepository products;
    private final BatchRepository batches;
    private final LocationRepository locations;
    private final OutboundOrderRepository outboundOrders;
    private final OutboundItemRepository outboundItems;
    private final StockWriteLock lock;
    private final AccountActor actor;
    private final StockLedger ledger;
    private final StorageRules storage;

    private record StorageZone(String warehouse, String zone) {}

    @Override
    @Transactional(readOnly = true)
    public List<InboundOrderResponse> listInboundOrders(String reference) {
        User user = actor.require();
        String filter = optional(reference);
        var list = user.getRole() == Role.SUPPLIER
                ? orders.findBySupplierIdAndSupplierNoticeTrueOrderByCreatedAtDescIdDesc(user.getSupplier().getId())
                : orders.findAll(Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return list.stream().filter(o -> filter == null || filter.equalsIgnoreCase(o.getExternalReference())
                || filter.equalsIgnoreCase(o.getOrderNo())).map(o -> response(o, user)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InboundOrderResponse getInbound(Long id) {
        User user = actor.require();
        return response(find(id, user), user);
    }

    @Override
    public InboundOrderResponse createInbound(InboundOrderRequest.Write request) {
        lock.acquire();
        User user = actor.require();
        boolean notice = user.getRole() == Role.SUPPLIER;
        InboundOrder order = newOrder(notice ? "GD" : "PN", notice ? InboundStatus.EXPECTED : InboundStatus.DRAFT, user);
        order.setSupplierNotice(notice);
        return write(order, request, user);
    }

    @Override
    public InboundOrderResponse updateInbound(Long id, InboundOrderRequest.Write request) {
        lock.acquire();
        User user = actor.require();
        InboundOrder order = find(id, user);
        state(order.getStatus() == InboundStatus.DRAFT || order.getStatus() == InboundStatus.EXPECTED,
                "Phiếu đã được đối chiếu");
        return write(order, request, user);
    }

    private InboundOrderResponse write(InboundOrder order, InboundOrderRequest.Write request, User user) {
        boolean supplierActor = user.getRole() == Role.SUPPLIER;
        Long supplierId = order.isSupplierNotice() && order.getSupplier() != null
                ? order.getSupplier().getId() : request.supplierId();
        if (supplierActor) {
            supplierId = user.getSupplier().getId();
            require(request.supplierId() == null || supplierId.equals(request.supplierId()), "supplierId",
                    "Thông báo phải mang mã nhà cung cấp của tài khoản");
        }
        require(supplierId != null, "supplierId", "Cần chọn nhà cung cấp");
        Supplier supplier = suppliers.findById(supplierId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        state(supplier.isActive(), "Nhà cung cấp đã ngừng hoạt động");
        String reference = optional(request.externalReference());
        if (order.isSupplierNotice()) {
            require(request.expectedAt() != null, "expectedAt", "Cần ngày dự kiến giao");
            require(reference != null, "externalReference", "Cần mã tham chiếu giao hàng");
        }
        if (reference != null) {
            orders.findBySupplierIdAndExternalReferenceIgnoreCase(supplierId, reference)
                    .filter(other -> !other.getId().equals(order.getId())).ifPresent(other -> {
                        throw new AppException(ErrorCode.DUPLICATE, "externalReference",
                                "Chứng từ đã liên kết " + other.getOrderNo());
                    });
        }
        order.setSupplier(supplier);
        order.setExpectedAt(request.expectedAt());
        order.setExternalReference(reference);
        boolean existing = order.getId() != null;
        orders.save(order);
        if (existing) {
            items.deleteByInboundOrderId(order.getId());
        }
        List<InboundItem> lines = request.items().stream().map(line -> {
            Product product = products.findById(line.productId()).filter(Product::isActive)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "SKU không tồn tại hoặc đã ngừng hoạt động"));
            BigDecimal cost = supplierActor || line.unitCost() == null ? ZERO : amount(line.unitCost(), "unitCost");
            return newLine(order, product, positive(line.qtyOrdered(), "qtyOrdered"), cost);
        }).toList();
        return InboundOrderMapper.toResponse(order, items.saveAll(lines), supplierActor);
    }

    @Override
    public InboundOrderResponse receiveInbound(Long id, InboundOrderRequest.Receive request) {
        lock.acquire();
        User user = actor.require();
        InboundOrder order = find(id, user);
        state(order.getStatus() == InboundStatus.DRAFT || order.getStatus() == InboundStatus.EXPECTED,
                "Chỉ đối chiếu thông báo dự kiến hoặc phiếu nháp");
        Map<Long, InboundOrderRequest.ReceivedLine> submitted = new HashMap<>();
        for (var input : request.items()) {
            require(submitted.putIfAbsent(input.inboundItemId(), input) == null, "items", "Dòng nhận hàng bị lặp");
        }
        List<InboundItem> lines = lines(order);
        requireLineIds(submitted.keySet(), lines);
        for (InboundItem line : lines) {
            reconcile(line, submitted.get(line.getId()));
        }
        order.setStatus(InboundStatus.PENDING_CONFIRMATION);
        return InboundOrderMapper.toResponse(order, lines, false);
    }

    private void reconcile(InboundItem line, InboundOrderRequest.ReceivedLine input) {
        BigDecimal received = amount(input.qtyReceived(), "qtyReceived");
        ItemCondition condition = input.itemCondition() == null ? ItemCondition.GOOD : input.itemCondition();
        BigDecimal accepted = input.qtyAccepted() == null ? (condition == ItemCondition.GOOD ? received : ZERO)
                : amount(input.qtyAccepted(), "qtyAccepted");
        require(accepted.compareTo(received) <= 0, "qtyAccepted", "Số đạt vượt số thực giao của " + line.getProduct().getSkuCode());
        if (accepted.signum() > 0) {
            checkDates(line.getProduct(), input.mfgDate(), input.expiryDate());
        }
        String suggestedProfile = line.getProduct().getStorageCondition();
        String profile = optional(input.storageCondition()) == null ? suggestedProfile : storage.profileCode(input.storageCondition());
        String reason = optional(input.discrepancyReason());
        require(Objects.equals(profile, suggestedProfile) || reason != null, "discrepancyReason",
                "Cần lý do khi sửa phân loại bảo quản");
        line.setQtyReceived(received);
        line.setQtyAccepted(accepted);
        line.setItemCondition(condition);
        line.setProposedBatchNo(normalizeBatch(input.batchNo()));
        line.setLocation(input.locationId() == null ? null : location(input.locationId()));
        line.setMfgDate(input.mfgDate());
        line.setExpiryDate(input.expiryDate());
        line.setStorageCondition(profile);
        line.setDiscrepancyReason(reason);
        if (input.unitCost() != null) {
            line.setUnitCost(amount(input.unitCost(), "unitCost"));
        }
    }

    @Override
    public InboundOrderResponse confirmInbound(Long id, InboundOrderRequest.Placements request) {
        lock.acquire();
        User user = actor.require();
        InboundOrder order = find(id, user);
        state(order.getStatus() == InboundStatus.PENDING_CONFIRMATION
                || (order.getStatus() == InboundStatus.CONFIRMED && request != null),
                "Phiếu phải chờ xác nhận hoặc đang chờ duyệt lại ô");
        List<InboundItem> lines = lines(order);
        List<InboundItem> accepted = accepted(lines);
        Map<Long, InboundOrderRequest.Placement> placements = placements(request);
        if (request != null) {
            requireLineIds(placements.keySet(), accepted);
        }
        Map<StorageZone, List<Batch>> planned = new HashMap<>();
        for (InboundItem line : accepted) {
            approveLine(order, line, placements.get(line.getId()), planned);
        }
        order.setStatus(InboundStatus.CONFIRMED);
        return InboundOrderMapper.toResponse(order, lines, false);
    }

    private void approveLine(InboundOrder order, InboundItem line, InboundOrderRequest.Placement input,
                             Map<StorageZone, List<Batch>> planned) {
        if (input != null) {
            line.setLocation(location(input.locationId()));
            if (input.batchNo() != null && line.getReturnItem() == null) {
                line.setProposedBatchNo(normalizeBatch(input.batchNo()));
            }
        }
        require(line.getProposedBatchNo() != null, "batchNo", "Thiếu mã lô của " + line.getProduct().getSkuCode());
        require(line.getLocation() != null, "locationId", "Cần ô cho " + line.getProduct().getSkuCode());
        Batch batch = line.getReturnItem() == null ? resolveBatch(order.getSupplier(), line) : line.getBatch();
        boolean quarantine = isQuarantine(line);
        ledger.checkDestination(batch, line.getLocation(), quarantine);
        if (!quarantine) {
            Location location = line.getLocation();
            StorageZone zone = new StorageZone(location.getWarehouseName(), location.getZoneName());
            List<Batch> atZone = planned.computeIfAbsent(zone, ignored -> new ArrayList<>());
            state(atZone.stream().allMatch(other -> storage.compatible(batch.getProduct(), storage.condition(batch), other)),
                    "Hai lô trong phiếu có điều kiện xung khắc trong khu " + location.getZoneName());
            atZone.add(batch);
        }
        line.setBatch(batch);
    }

    private Batch resolveBatch(Supplier supplier, InboundItem line) {
        Product product = line.getProduct();
        String number = line.getProposedBatchNo();
        Batch existing = batches.findByProductIdAndBatchNoIgnoreCase(product.getId(), number).orElse(null);
        if (existing != null) {
            require(existing.getSupplier().getId().equals(supplier.getId()) && Objects.equals(existing.getMfgDate(), line.getMfgDate())
                            && Objects.equals(existing.getExpiryDate(), line.getExpiryDate())
                            && Objects.equals(storage.condition(existing), line.getStorageCondition()),
                    "batchNo", "Mã lô đã có với ngày, nguồn hoặc hồ sơ bảo quản khác");
            return existing;
        }
        Batch batch = new Batch();
        batch.setProduct(product);
        batch.setSupplier(supplier);
        batch.setBatchNo(number);
        batch.setMfgDate(line.getMfgDate());
        batch.setExpiryDate(line.getExpiryDate());
        batch.setStorageCondition(line.getStorageCondition());
        batch.setStatus(BatchStatus.AVAILABLE);
        return batches.save(batch);
    }

    @Override
    public InboundOrderResponse postInbound(Long id, InboundOrderRequest.Placements request) {
        lock.acquire();
        User user = actor.require();
        InboundOrder order = find(id, user);
        List<InboundItem> lines = lines(order);
        if (order.getStatus() == InboundStatus.RECEIVED) {
            return InboundOrderMapper.toResponse(order, lines, false);
        }
        state(order.getStatus() == InboundStatus.CONFIRMED, "Phiếu chưa được quản lý xác nhận");
        List<InboundItem> accepted = accepted(lines);
        Map<Long, InboundOrderRequest.Placement> actual = placements(request);
        requireLineIds(actual.keySet(), accepted);
        for (InboundItem line : accepted) {
            var input = actual.get(line.getId());
            state(input.locationId().equals(line.getLocation().getId())
                            && line.getBatch().getBatchNo().equals(normalizeBatch(input.batchNo())),
                    "Lô hoặc ô thực tế khác phiếu. Cần đối chiếu và duyệt lại trước khi ghi nhập");
            ledger.checkDestination(line.getBatch(), line.getLocation(), isQuarantine(line));
        }
        for (InboundItem line : accepted) {
            MovementType type = order.getReturnOf() == null ? MovementType.INBOUND : MovementType.RETURN;
            ledger.putAway(line.getBatch(), line.getLocation(), isQuarantine(line), line.getQtyAccepted(), type,
                    order.getOrderNo(), user, line.getDiscrepancyReason());
        }
        order.setStatus(InboundStatus.RECEIVED);
        return InboundOrderMapper.toResponse(order, lines, false);
    }

    @Override
    public InboundOrderResponse createReturn(InboundOrderRequest.Return request) {
        lock.acquire();
        User user = actor.require();
        OutboundOrder source = outboundOrders.findById(request.outboundOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy đơn xuất"));
        state(source.getStatus() == OutboundStatus.HANDED_OVER || source.getStatus() == OutboundStatus.DELIVERED
                || source.getStatus() == OutboundStatus.FAILED, "Chỉ lập phiếu hoàn từ hàng đã ghi xuất");
        InboundOrder order = newOrder("PH", InboundStatus.PENDING_CONFIRMATION, user);
        order.setReturnOf(source);
        Map<Long, BigDecimal> requested = new HashMap<>();
        List<InboundItem> lines = new ArrayList<>();
        for (var input : request.items()) {
            OutboundItem origin = outboundItems.findById(input.outboundItemId())
                    .filter(item -> item.getOutboundOrder().getId().equals(source.getId()) && item.getInventory() != null)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Dòng xuất không thuộc đơn gốc"));
            BigDecimal qty = positive(input.quantity(), "quantity");
            BigDecimal used = items.findByReturnItemId(origin.getId()).stream()
                    .map(InboundItem::getQtyAccepted).reduce(ZERO, BigDecimal::add);
            BigDecimal remaining = origin.getQtyPicked().subtract(used).subtract(requested.getOrDefault(origin.getId(), ZERO));
            require(qty.compareTo(remaining) <= 0, "quantity",
                    "Còn được hoàn " + remaining.max(ZERO).stripTrailingZeros().toPlainString() + " của dòng " + origin.getId());
            requested.merge(origin.getId(), qty, BigDecimal::add);
            lines.add(returnLine(order, origin, input, qty, optional(request.reason())));
        }
        orders.save(order);
        return InboundOrderMapper.toResponse(order, items.saveAll(lines), false);
    }

    private InboundItem returnLine(InboundOrder order, OutboundItem origin, InboundOrderRequest.ReturnLine request,
                                   BigDecimal qty, String reason) {
        Batch batch = origin.getInventory().getBatch();
        InboundItem line = newLine(order, origin.getProduct(), qty, ZERO);
        line.setReturnItem(origin);
        line.setBatch(batch);
        line.setProposedBatchNo(batch.getBatchNo());
        line.setMfgDate(batch.getMfgDate());
        line.setExpiryDate(batch.getExpiryDate());
        line.setStorageCondition(storage.condition(batch));
        line.setQtyReceived(qty);
        line.setQtyAccepted(qty);
        line.setItemCondition(request.itemCondition());
        line.setLocation(location(request.locationId()));
        line.setDiscrepancyReason(reason);
        ledger.checkDestination(batch, line.getLocation(), isQuarantine(line));
        return line;
    }

    private InboundOrder newOrder(String prefix, InboundStatus status, User user) {
        InboundOrder order = new InboundOrder();
        order.setOrderNo(documentNo(prefix));
        order.setCreatedBy(user);
        order.setCreatedAt(ledger.now());
        order.setStatus(status);
        return order;
    }

    private static InboundItem newLine(InboundOrder order, Product product, BigDecimal qty, BigDecimal cost) {
        InboundItem line = new InboundItem();
        line.setInboundOrder(order);
        line.setProduct(product);
        line.setQtyOrdered(qty);
        line.setQtyReceived(ZERO);
        line.setQtyAccepted(ZERO);
        line.setUnitCost(cost);
        line.setStorageCondition(product.getStorageCondition());
        return line;
    }

    private static boolean isQuarantine(InboundItem line) {
        return line.getReturnItem() != null && line.getItemCondition() != ItemCondition.GOOD;
    }

    private static void checkDates(Product product, LocalDate mfg, LocalDate expiry) {
        require(!product.isHasExpiry() || expiry != null, "expiryDate", "Cần hạn dùng của " + product.getSkuCode());
        require(mfg == null || expiry == null || !expiry.isBefore(mfg), "expiryDate",
                "Hạn dùng phải từ ngày sản xuất trở đi");
    }

    private static String normalizeBatch(String batch) {
        return optional(batch) == null ? null : code(batch, "batchNo");
    }

    private static Map<Long, InboundOrderRequest.Placement> placements(InboundOrderRequest.Placements request) {
        Map<Long, InboundOrderRequest.Placement> result = new HashMap<>();
        if (request != null) {
            for (var item : request.items()) {
                require(result.putIfAbsent(item.inboundItemId(), item) == null, "items", "Dòng đối chiếu bị lặp");
            }
        }
        return result;
    }

    private static void requireLineIds(Set<Long> ids, List<InboundItem> lines) {
        require(ids.size() == lines.size() && lines.stream().allMatch(line -> ids.contains(line.getId())),
                "items", "Cần đối chiếu đủ từng dòng của phiếu");
    }

    private Location location(Long id) {
        return locations.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ô kho"));
    }

    private InboundOrder find(Long id, User user) {
        return orders.findById(id).filter(order -> user.getRole() != Role.SUPPLIER || (order.isSupplierNotice()
                        && order.getSupplier() != null && order.getSupplier().getId().equals(user.getSupplier().getId())))
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy phiếu nhập"));
    }

    private List<InboundItem> lines(InboundOrder order) {
        return items.findByInboundOrderIdOrderById(order.getId());
    }

    private static List<InboundItem> accepted(List<InboundItem> lines) {
        return lines.stream().filter(line -> line.getQtyAccepted().signum() > 0).toList();
    }

    private InboundOrderResponse response(InboundOrder order, User user) {
        return InboundOrderMapper.toResponse(order, lines(order), user.getRole() == Role.SUPPLIER);
    }
}
