package com.factory.api;

import com.factory.api.dto.ProductionSuggestionDTO;
import com.factory.api.model.Product;
import com.factory.api.model.ProductComposition;
import com.factory.api.model.RawMaterial;
import com.factory.api.service.ProductionService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductionServiceTest {

    @Inject
    ProductionService productionService;

    @BeforeEach
    @Transactional
    void setup() {
        ProductComposition.deleteAll();
        Product.deleteAll();
        RawMaterial.deleteAll();
    }

    @Test
    @Transactional
    void shouldPrioritizeMostProfitableProduct() {
        RawMaterial wood = new RawMaterial();
        wood.code = "RM001";
        wood.name = "Test Wood";
        wood.quantity = 10.0;
        wood.persist();

        Product cheap = new Product();
        cheap.code = "P001";
        cheap.name = "Cheap Item";
        cheap.price = 10.0;
        cheap.persist();

        ProductComposition comp1 = new ProductComposition();
        comp1.rawMaterial = wood;
        comp1.requiredQuantity = 1.0;
        comp1.persist();
        cheap.composition = java.util.List.of(comp1);

        Product expensive = new Product();
        expensive.code = "P002";
        expensive.name = "Expensive Item";
        expensive.price = 100.0;
        expensive.persist();

        ProductComposition comp2 = new ProductComposition();
        comp2.rawMaterial = wood;
        comp2.requiredQuantity = 10.0;
        comp2.persist();
        expensive.composition = java.util.List.of(comp2);

        ProductionSuggestionDTO suggestion = productionService.calculateOptimalProduction();

        Assertions.assertEquals(100.0, suggestion.totalEstimatedValue);
        Assertions.assertTrue(suggestion.productsToProduce.containsKey("Expensive Item"));
        Assertions.assertEquals(1, suggestion.productsToProduce.get("Expensive Item"));
    }
}