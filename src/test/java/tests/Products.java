package tests;

import utils.Base;
import utils.Variables;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.testng.Tag;

import java.util.*;

public class Products {

    @Test
    @Description("Verify retrieval of all products")
    @Tag("Get All Products")
    @Severity(SeverityLevel.NORMAL)
    public void getAllProducts() {
        Allure.step("Send GET /products request to retrieve all products", () -> {
            Response response = RestAssured
                    .given()
                    .when()
                    .get(Base.URL + "/products")
                    .then()
                    .statusCode(200)
                    .body("", Matchers.not(Matchers.empty()))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test
    @Description("Verify retrieval of available products")
    @Tag("Filtering Products")
    @Severity(SeverityLevel.CRITICAL)
    public void getAvailableProducts() {
        Allure.step("Send GET /products with available=true query param", () -> {
            Response response = RestAssured
                    .given()
                    .queryParam("available", true)
                    .when()
                    .get(Base.URL + "/products")
                    .then()
                    .statusCode(200)
                    .body("", Matchers.not(Matchers.empty()))
                    .body("inStock", Matchers.everyItem(Matchers.equalTo(true)))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());

            // pick a random productId
            List<Integer> ids = response.jsonPath().getList("id");
            int randomId = new Random().nextInt(ids.size());
            int productId = ids.get(randomId);
            Variables.setProductId(productId);
        });
    }

    @Test
    @Description("Verify retrieval of unavailable products")
    @Tag("Filtering Products")
    @Severity(SeverityLevel.CRITICAL)
    public void getUnavailableProducts() {
        Allure.step("Send GET /products with available=false query param", () -> {
            Response response = RestAssured
                    .given()
                    .queryParam("available", false)
                    .when()
                    .get(Base.URL + "/products")
                    .then()
                    .statusCode(200)
                    .body("", Matchers.not(Matchers.empty()))
                    .body("inStock", Matchers.everyItem(Matchers.equalTo(false)))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"getAvailableProducts"})
    @Description("Verify getting product by valid Id")
    @Tag("Valid getting product")
    @Severity(SeverityLevel.CRITICAL)
    public void getProductById() {
        Allure.step("Send GET /products/{productId} with valid productId", () -> {
            Response response = RestAssured
                    .given()
                    .pathParam("productId", Variables.getProductId())
                    .when()
                    .get(Base.URL + "/products/{productId}")
                    .then()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(Variables.getProductId()))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"getAvailableProducts"})
    @Description("Verify getting product by invalid Id")
    @Tag("Invalid getting product")
    @Severity(SeverityLevel.CRITICAL)
    public void getProductbyInvalidId() {
        Allure.step("Send GET /products/999999 with invalid productId", () -> {
            Response response = RestAssured
                    .given()
                    .when()
                    .get(Base.URL + "/products/999999")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test
    @Description("Check getting Products With Invalid QueryParam")
    @Tag("Invalid Query Params")
    @Severity(SeverityLevel.NORMAL)
    public void getProductsWithInvalidQueryParams() {
        Allure.step("Send GET /products with invalid query param 'available=notaboolean'", () -> {
            Response response = RestAssured
                    .given()
                    .queryParam("available", "notaboolean")
                    .when()
                    .get(Base.URL + "/products")
                    .then()
                    .statusCode(400)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }
}
