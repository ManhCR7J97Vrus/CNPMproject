package com.warehouse.service.impl;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.service.StocktakeService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ kiểm kê. */
@Service
@Transactional
public class StocktakeServiceImpl implements StocktakeService {
    @Override
    @Transactional(readOnly = true)
    public List<StocktakeResponse> listStocktakes() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public StocktakeResponse createStocktake(StocktakeRequest.Create request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public StocktakeResponse getStocktake(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public StocktakeResponse recordCount(Long id, StocktakeRequest.Count request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public StocktakeResponse approveAdjustment(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
