package com.warehouse.service;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ kiểm kê. */
public interface StocktakeService {
    List<StocktakeResponse> listStocktakes();

    StocktakeResponse createStocktake(StocktakeRequest.Create request);

    StocktakeResponse getStocktake(Long id);

    StocktakeResponse recordCount(Long id, StocktakeRequest.Count request);

    StocktakeResponse approveAdjustment(Long id);
}
