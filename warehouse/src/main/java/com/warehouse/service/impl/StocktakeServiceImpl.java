package com.warehouse.service.impl;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.entity.*;
import com.warehouse.entity.enums.QuarantineStatus;
import com.warehouse.entity.enums.Role;
import com.warehouse.entity.enums.StocktakeStatus;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.StocktakeMapper;
import com.warehouse.repository.*;
import com.warehouse.service.StocktakeService;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class StocktakeServiceImpl implements StocktakeService {
    private final StocktakeRepository counts;
    private final StocktakeLineRepository countLines;
    private final InventoryRepository inventory;
    private final BatchRepository batches;
    private final LocationRepository locations;
    private final UserRepository users;
    private final QuarantineRequestRepository quarantine;
    private final StockLedger ledger;
    private final AccountActor actor;
    private final StockWriteLock lock;

    @Override
    @Transactional(readOnly = true)
    public List<StocktakeResponse> listStocktakes() {
        return counts.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(count -> StocktakeMapper.toResponse(count, List.of())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StocktakeResponse.Counter> counters() {
        return users.findAll(Sort.by("username")).stream().filter(User::isActive)
                .filter(user -> user.getRole() == Role.STAFF || user.getRole() == Role.MANAGER)
                .map(user -> new StocktakeResponse.Counter(user.getId(), user.getUsername(), user.getFullName())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StocktakeResponse getStocktake(Long id) {
        return response(find(id));
    }

    @Override
    public StocktakeResponse createStocktake(StocktakeRequest.Create request) {
        lock.acquire();
        User creator = actor.require();
        Set<Long> locationIds = new LinkedHashSet<>();
        if (request.locationIds() != null) {
            locationIds.addAll(request.locationIds());
        }
        if (request.locationId() != null) {
            locationIds.add(request.locationId());
        }
        require(!locationIds.isEmpty(), "locationIds", "Chọn ít nhất một ô cần đếm");
        List<Location> scope = locations.findAllById(locationIds);
        require(scope.size() == locationIds.size(), "locationIds", "Có ô không tồn tại");
        User counter = request.counterId() == null ? creator : users.findById(request.counterId()).filter(User::isActive)
                .filter(user -> user.getRole() == Role.STAFF || user.getRole() == Role.MANAGER)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy người đếm đang hoạt động"));
        Stocktake count = new Stocktake();
        count.setReferenceNo(documentNo("KK"));
        count.setLocations(scope);
        count.setScopeBatch(request.batchId() == null ? null : batch(request.batchId()));
        count.setCreatedBy(creator);
        count.setCounter(counter);
        count.setStatus(StocktakeStatus.DRAFT);
        count.setSnapshotAt(ledger.now());
        counts.save(count);
        return snapshot(count);
    }

    private List<Inventory> scopedInventory(Stocktake count) {
        List<Long> ids = count.getLocations().stream().map(Location::getId).toList();
        return inventory.findByLocationIdInOrderById(ids).stream()
                .filter(row -> count.getScopeBatch() == null || count.getScopeBatch().getId().equals(row.getBatch().getId()))
                .toList();
    }

    private StocktakeResponse snapshot(Stocktake count) {
        List<StocktakeLine> lines = scopedInventory(count).stream().map(stock -> {
            StocktakeLine line = new StocktakeLine();
            line.setStocktake(count);
            line.setInventory(stock);
            line.setExpectedQty(stock.getQtyOnHand());
            line.setStockRevision(stock.getStockRevision());
            return line;
        }).toList();
        require(!lines.isEmpty(), "locationIds", "Chưa có dòng tồn trong phạm vi đếm");
        return StocktakeMapper.toResponse(count, countLines.saveAll(lines));
    }

    @Override
    public StocktakeResponse recordCount(Long id, StocktakeRequest.Count request) {
        lock.acquire();
        Stocktake count = find(id);
        User user = actor.require();
        if (user.getRole() != Role.MANAGER && !count.getCounter().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Phiếu thuộc người đếm được phân công");
        }
        state(count.getStatus() == StocktakeStatus.DRAFT, "Phiếu phải đang mở để ghi số đếm");
        List<StocktakeLine> lines = lines(count);
        Map<Long, StocktakeLine> byId = lines.stream().collect(Collectors.toMap(StocktakeLine::getId, line -> line));
        if (request.items() == null) {
            require(lines.size() == 1, "items", "Nhập số đếm cho từng dòng");
            record(lines.get(0), request.countedQty(), request.reason());
        } else {
            Set<Long> seen = new HashSet<>();
            for (var entry : request.items()) {
                require(byId.containsKey(entry.itemId()) && seen.add(entry.itemId()), "items", "Dòng đếm lặp hoặc ngoài phạm vi");
                record(byId.get(entry.itemId()), entry.countedQty(), entry.reason());
            }
        }
        if (!Boolean.FALSE.equals(request.submit())) {
            for (StocktakeLine line : lines) {
                require(line.getCountedQty() != null, "countedQty", "Chưa đếm lô " + line.getInventory().getBatch().getBatchNo()
                        + " tại " + line.getInventory().getLocation().getCode());
            }
            count.setStatus(StocktakeStatus.COUNTED);
        }
        if (optional(request.reason()) != null) {
            count.setReason(request.reason().strip());
        }
        return StocktakeMapper.toResponse(count, lines);
    }

    private static void record(StocktakeLine line, BigDecimal qty, String reason) {
        if (qty != null) {
            amount(qty, "countedQty");
            require(qty.stripTrailingZeros().scale() <= 0, "countedQty", "Số thực đếm phải là số nguyên không âm");
        }
        line.setCountedQty(qty);
        line.setReason(optional(reason));
    }

    @Override
    public StocktakeResponse approveAdjustment(Long id, StocktakeRequest.Review request) {
        lock.acquire();
        Stocktake count = find(id);
        if (count.getStatus() == StocktakeStatus.APPROVED) {
            return response(count);
        }
        state(count.getStatus() == StocktakeStatus.COUNTED, "Phiếu chưa chờ duyệt");
        List<StocktakeLine> lines = lines(count);
        state(scopedInventory(count).size() == lines.size(), "Phạm vi đã có thêm dòng tồn, cần lấy mốc mới");
        for (StocktakeLine line : lines) {
            Inventory stock = line.getInventory();
            String label = stock.getBatch().getBatchNo() + " tại " + stock.getLocation().getCode();
            state(stock.getStockRevision() == line.getStockRevision()
                    && stock.getQtyOnHand().compareTo(line.getExpectedQty()) == 0,
                    "Tồn lô " + label + " đã biến động. Cần lấy mốc mới và đếm lại");
            state(line.getCountedQty().compareTo(stock.getQtyReserved()) >= 0,
                    "Số đếm lô " + label + " thấp hơn số đang giữ " + stock.getQtyReserved().stripTrailingZeros().toPlainString());
        }
        User manager = actor.require();
        String review = request == null ? null : optional(request.reason());
        if (review != null) {
            count.setReason(review);
        }
        for (StocktakeLine line : lines) {
            String reason = line.getReason() != null ? line.getReason()
                    : count.getReason() != null ? count.getReason() : "Điều chỉnh theo số thực đếm";
            ledger.adjust(line.getInventory(), line.getCountedQty(), count.getReferenceNo(), manager, reason);
        }
        count.setStatus(StocktakeStatus.APPROVED);
        count.setApprovedBy(manager);
        count.setApprovedAt(ledger.now());
        return StocktakeMapper.toResponse(count, lines);
    }

    @Override
    public StocktakeResponse returnForRecount(Long id, StocktakeRequest.Review request) {
        lock.acquire();
        Stocktake count = find(id);
        state(count.getStatus() == StocktakeStatus.COUNTED, "Chỉ trả phiếu đang chờ duyệt");
        count.setReason(text(request.reason(), "reason"));
        count.setStatus(StocktakeStatus.NEEDS_RECOUNT);
        return response(count);
    }

    @Override
    public StocktakeResponse refreshSnapshot(Long id) {
        lock.acquire();
        Stocktake count = find(id);
        state(count.getStatus() == StocktakeStatus.NEEDS_RECOUNT, "Phiếu chưa được trả để đếm lại");
        countLines.deleteAll(lines(count));
        countLines.flush();
        count.setSnapshotAt(ledger.now());
        count.setStatus(StocktakeStatus.DRAFT);
        return snapshot(count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StocktakeResponse.Quarantine> quarantineRequests() {
        return quarantine.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(StocktakeMapper::quarantine).toList();
    }

    @Override
    public StocktakeResponse.Quarantine requestQuarantine(StocktakeRequest.Quarantine input) {
        lock.acquire();
        Inventory stock = ledger.existing(batch(input.batchId()), location(input.fromLocationId()));
        state(!stock.getLocation().isQuarantine() && stock.getQtyOnHand().signum() > 0, "Ô nguồn phải còn hàng ngoài khu cách ly");
        QuarantineRequest request = new QuarantineRequest();
        request.setReferenceNo(documentNo("CL"));
        request.setInventory(stock);
        request.setQuantity(positive(input.quantity(), "quantity"));
        request.setReason(text(input.reason(), "reason"));
        request.setCreatedBy(actor.require());
        request.setCreatedAt(ledger.now());
        request.setStatus(QuarantineStatus.REQUESTED);
        return StocktakeMapper.quarantine(quarantine.save(request));
    }

    @Override
    public StocktakeResponse.Quarantine approveQuarantine(Long id, StocktakeRequest.QuarantineApproval input) {
        lock.acquire();
        QuarantineRequest request = quarantine(id);
        state(request.getStatus() != QuarantineStatus.COMPLETED, "Đề nghị đã ghi sổ");
        Inventory stock = request.getInventory();
        if (request.getQuantity().compareTo(ledger.unreserved(stock)) > 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, "Số chưa giữ còn "
                    + ledger.unreserved(stock).stripTrailingZeros().toPlainString() + ". Cần xử lý đơn giữ trước khi duyệt");
        }
        Location destination = location(input.toLocationId());
        state(stock.getLocation().getWarehouseName().equals(destination.getWarehouseName()),
                "Chọn ô cách ly trong cùng kho");
        ledger.checkDestination(stock.getBatch(), destination, true);
        request.setDestination(destination);
        request.setApprovedBy(actor.require());
        request.setApprovedAt(ledger.now());
        request.setStatus(QuarantineStatus.APPROVED);
        return StocktakeMapper.quarantine(request);
    }

    @Override
    public StocktakeResponse.Quarantine postQuarantine(Long id) {
        lock.acquire();
        QuarantineRequest request = quarantine(id);
        if (request.getStatus() == QuarantineStatus.COMPLETED) {
            return StocktakeMapper.quarantine(request);
        }
        state(request.getStatus() == QuarantineStatus.APPROVED, "Đề nghị chưa được duyệt cách ly");
        ledger.move(request.getInventory(), request.getDestination(), request.getQuantity(), true, request.getReferenceNo(),
                actor.require(), request.getReason());
        request.setStatus(QuarantineStatus.COMPLETED);
        request.setCompletedAt(ledger.now());
        return StocktakeMapper.quarantine(request);
    }

    private Stocktake find(Long id) {
        return counts.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy phiếu kiểm kê"));
    }

    private QuarantineRequest quarantine(Long id) {
        return quarantine.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy đề nghị cách ly"));
    }

    private Batch batch(Long id) {
        return batches.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy lô"));
    }

    private Location location(Long id) {
        return locations.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ô kho"));
    }

    private List<StocktakeLine> lines(Stocktake count) {
        return countLines.findByStocktakeIdOrderById(count.getId());
    }

    private StocktakeResponse response(Stocktake count) {
        return StocktakeMapper.toResponse(count, lines(count));
    }
}
