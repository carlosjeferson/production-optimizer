package com.factory.api;

import com.factory.api.model.Product;
import com.factory.api.model.ProductComposition;
import com.factory.api.model.RawMaterial;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class ProductResourceTest {

    private Long materialId;

    @BeforeEach
    @Transactional
    void setup() {
        ProductComposition.deleteAll();
        Product.deleteAll();
        RawMaterial.deleteAll();

        RawMaterial rm = new RawMaterial();
        rm.code = "BASE-01";
        rm.name = "Base de Ferro";
        rm.quantity = 100.0;
        rm.persist();
        materialId = rm.id;
    }

    @Test
    public void testFullProductLifecycle() {
        Product product = new Product();
        product.code = "PROD-01";
        product.name = "Mesa Industrial";
        product.price = 500.0;

        ProductComposition comp = new ProductComposition();
        RawMaterial ref = new RawMaterial();
        ref.id = materialId;
        comp.rawMaterial = ref;
        comp.requiredQuantity = 2.0;

        product.composition = List.of(comp);

        Long productId = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .body(product)
                .when().post("/products")
                .then()
                .statusCode(201)
                .body("name", is("Mesa Industrial"))
                .body("composition.size()", is(1))
                .extract().path("id").toString());

        product.name = "Mesa Industrial Premium";
        product.price = 750.0;

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when().put("/products/" + productId)
                .then()
                .statusCode(200)
                .body("name", is("Mesa Industrial Premium"))
                .body("price", is(750.0f));

        given()
                .when().delete("/products/" + productId)
                .then()
                .statusCode(200);

        given()
                .when().get("/products")
                .then()
                .body("size()", is(0));
    }
}