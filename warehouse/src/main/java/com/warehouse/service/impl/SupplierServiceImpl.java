package com.warehouse.service.impl;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.entity.Supplier;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.SupplierMapper;
import com.warehouse.repository.SupplierRepository;
import com.warehouse.service.SupplierService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository suppliers;
    private final StockWriteLock lock;

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> listSuppliers() {
        return suppliers.findAll(Sort.by("code")).stream().map(SupplierMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplier(Long id) {
        return SupplierMapper.toResponse(find(id));
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest.Write request) {
        lock.acquire();
        return write(new Supplier(), request);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest.Write request) {
        lock.acquire();
        return write(find(id), request);
    }

    private SupplierResponse write(Supplier supplier, SupplierRequest.Write request) {
        String code = code(request.code(), "code");
        if (suppliers.existsByCodeIgnoreCaseAndIdNot(code, supplier.getId() == null ? 0L : supplier.getId())) {
            throw new AppException(ErrorCode.DUPLICATE, "code", "Mã nhà cung cấp đã dùng");
        }
        supplier.setCode(code);
        supplier.setName(text(request.name(), "name"));
        supplier.setPhone(optional(request.phone()));
        supplier.setAddress(optional(request.address()));
        supplier.setLicenseNo(optional(request.licenseNo()));
        supplier.setActive(request.active());
        return SupplierMapper.toResponse(suppliers.save(supplier));
    }

    private Supplier find(Long id) {
        return suppliers.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy nhà cung cấp"));
    }
}
