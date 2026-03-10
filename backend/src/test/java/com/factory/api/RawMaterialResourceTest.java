package com.factory.api;

import com.factory.api.model.Product;
import com.factory.api.model.ProductComposition;
import com.factory.api.model.RawMaterial;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class RawMaterialResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        ProductComposition.deleteAll();
        Product.deleteAll();
        RawMaterial.deleteAll();
    }

    @Test
    public void testFullRawMaterialLifecycle() {
        RawMaterial material = new RawMaterial();
        material.setCode("RM-99");
        material.setName("Aço Inox");
        material.setQuantity(50.0);

        Long id = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .body(material)
                .when().post("/raw-materials")
                .then()
                .statusCode(201)
                .body("code", is("RM-99"))
                .extract().path("id").toString());

        given()
                .when().get("/raw-materials")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is("Aço Inox"));

        material.setName("Aço Inox Escovado");
        given()
                .contentType(ContentType.JSON)
                .body(material)
                .when().put("/raw-materials/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Aço Inox Escovado"));

        given()
                .when().delete("/raw-materials/" + id)
                .then()
                .statusCode(expectedDeleteStatus());

        given()
                .when().get("/raw-materials")
                .then()
                .body("size()", is(0));
    }

    private int expectedDeleteStatus() {
        return 204;
    }
}