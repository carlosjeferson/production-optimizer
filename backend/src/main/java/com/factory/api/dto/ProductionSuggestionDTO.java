package com.factory.api.dto;

import java.util.Map;

public class ProductionSuggestionDTO {
    public Map<String, Integer> productsToProduce;
    public Double totalEstimatedValue;

    public ProductionSuggestionDTO(Map<String, Integer> productsToProduce, Double totalEstimatedValue) {
        this.productsToProduce = productsToProduce;
        this.totalEstimatedValue = totalEstimatedValue;
    }
}