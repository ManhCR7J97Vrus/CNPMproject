package com.warehouse.service.impl;

import com.warehouse.dto.request.InventoryRequest;
import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.entity.Inventory;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.InventoryMapper;
import com.warehouse.repository.InventoryRepository;
import com.warehouse.service.InventoryService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventory;
    private final StockLedger ledger;

    @Override
    public List<InventoryResponse> listInventory(InventoryRequest.Search search) {
        return inventory.findAll().stream().filter(row -> row.getQtyOnHand().signum() > 0)
                .filter(row -> matches(row, search)).map(this::response)
                .sorted(Comparator.comparing(InventoryResponse::skuCode).thenComparing(InventoryResponse::batchNo)
                        .thenComparing(InventoryResponse::warehouseName).thenComparing(InventoryResponse::locationCode))
                .toList();
    }

    @Override
    public InventoryResponse.Summary summarize(InventoryRequest.Search search) {
        List<InventoryResponse> rows = listInventory(search);
        return new InventoryResponse.Summary(rows,
                rows.stream().map(InventoryResponse::qtyOnHand).reduce(BigDecimal.ZERO, BigDecimal::add),
                rows.stream().map(InventoryResponse::qtyReserved).reduce(BigDecimal.ZERO, BigDecimal::add),
                rows.stream().map(InventoryResponse::qtyAvailable).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Override
    public InventoryResponse getInventory(Long id) {
        return response(inventory.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));
    }

    private InventoryResponse response(Inventory row) {
        return InventoryMapper.toResponse(row, ledger.eligible(row));
    }

    private static boolean matches(Inventory row, InventoryRequest.Search filter) {
        var batch = row.getBatch();
        var location = row.getLocation();
        var product = batch.getProduct();
        return (filter.batchId() == null || filter.batchId().equals(batch.getId()))
                && (filter.locationId() == null || filter.locationId().equals(location.getId()))
                && (filter.productId() == null || filter.productId().equals(product.getId()))
                && matchesText(filter.sku(), product.getSkuCode())
                && matchesText(filter.batchNo(), batch.getBatchNo())
                && matchesText(filter.locationCode(), location.getCode())
                && matchesText(filter.warehouseName(), location.getWarehouseName());
    }

    private static boolean matchesText(String filter, String value) {
        String text = optional(filter);
        return text == null || text.equalsIgnoreCase(value);
    }
}
