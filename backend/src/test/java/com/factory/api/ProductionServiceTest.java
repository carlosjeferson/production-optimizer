package com.factory.api;

import com.factory.api.dto.ProductionSuggestionDTO;
import com.factory.api.model.Product;
import com.factory.api.model.ProductComposition;
import com.factory.api.model.RawMaterial;
import com.factory.api.service.ProductionService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ProductionServiceTest {

    @Inject
    ProductionService productionService;

    @Test
    public void testCalculateOptimalProduction() {

        RawMaterial wood = new RawMaterial();
        wood.name = "Wood";
        wood.code = "RM-101";
        wood.quantity = 100.0;

        RawMaterial steel = new RawMaterial();
        steel.name = "Steel";
        steel.code = "RM-102";
        steel.quantity = 100.0;

        Product product = new Product();
        product.name = "Car";
        product.code = "CAR01";
        product.price = 1000.0;

        Product product2 = new Product();
        product2.name = "Car 2";
        product2.code = "CAR02";
        product2.price = 1400.0;

        Product product3 = new Product();
        product3.name = "Car 3";
        product3.code = "CAR02";
        product3.price = 1100.0;

        ProductComposition comp = new ProductComposition();
        comp.rawMaterial = steel;
        comp.requiredQuantity = 10.0;

        ProductComposition comp2 = new ProductComposition();
        comp2.rawMaterial = steel;
        comp2.requiredQuantity = 8.0;

        ProductComposition comp3 = new ProductComposition();
        comp3.rawMaterial = wood;
        comp3.requiredQuantity = 10.0;

        ProductComposition comp4 = new ProductComposition();
        comp4.rawMaterial = wood;
        comp4.requiredQuantity = 8.0;

        product.composition.add(comp);
        product2.composition.add(comp2);
        product2.composition.add(comp3);
        product3.composition.add(comp4);

        ProductionSuggestionDTO result = productionService.calculateOptimalProduction();

        assertNotNull(result);
        assertTrue(result.totalEstimatedValue  >= 0);
    }
}