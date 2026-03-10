package com.factory.api;

import com.factory.api.dto.ProductionSuggestionDTO;
import com.factory.api.model.Product;
import com.factory.api.model.ProductComposition;
import com.factory.api.model.RawMaterial;
import com.factory.api.service.ProductionService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testCalculateOptimalProductionWithMultipleProducts() {
        RawMaterial wood = new RawMaterial();
        wood.setName("Wood");
        wood.setCode("RM-101");
        wood.setQuantity(100.0);
        wood.persist();

        RawMaterial steel = new RawMaterial();
        steel.setName("Steel");
        steel.setCode("RM-102");
        steel.setQuantity(100.0);
        steel.persist();

        Product p1 = new Product();
        p1.setName("Car 1");
        p1.setCode("CAR01");
        p1.setPrice(1000.0);

        ProductComposition c1 = new ProductComposition();
        c1.setRawMaterial(steel);
        c1.setRequiredQuantity(10.0);
        p1.getComposition().add(c1);
        p1.persist();

        ProductionSuggestionDTO result = productionService.calculateOptimalProduction();

        assertNotNull(result);
        assertTrue(result.totalEstimatedValue > 0);
        assertFalse(result.productsToProduce.isEmpty(), "Deve haver uma sugestão de produção");
    }

    @Test
    @Transactional
    public void testOptimalDecisionBetweenProducts() {
        RawMaterial wood = new RawMaterial();
        wood.setName("Wood");
        wood.setCode("WOOD-01");
        wood.setQuantity(10.0);
        wood.persist();

        Product prodA = new Product();
        prodA.setName("Product A");
        prodA.setCode("A");
        prodA.setPrice(100.0);
        ProductComposition compA = new ProductComposition();
        compA.setRawMaterial(wood);
        compA.setRequiredQuantity(10.0);
        prodA.getComposition().add(compA);
        prodA.persist();

        Product prodB = new Product();
        prodB.setName("Product B");
        prodB.setCode("B");
        prodB.setPrice(60.0);
        ProductComposition compB = new ProductComposition();
        compB.setRawMaterial(wood);
        compB.setRequiredQuantity(5.0);
        prodB.getComposition().add(compB);
        prodB.persist();

        ProductionSuggestionDTO result = productionService.calculateOptimalProduction();

        assertEquals(120.0, result.totalEstimatedValue, "O Solver deveria ter escolhido o Produto B (2x 60) em vez do A (1x 100).");
        assertEquals(2, result.productsToProduce.get("Product B"), "Deveria sugerir 2 unidades do Produto B.");
        assertNull(result.productsToProduce.get("Product A"), "Não deveria sugerir o Produto A.");
    }
}