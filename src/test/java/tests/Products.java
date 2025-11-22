package tests;
import utils.Base;
import utils.Variables;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
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
        Response response = RestAssured
                .given()
                .when()
                .get(Base.URL + "/products")
                .then()
                .statusCode(200)
                .body("" , Matchers.not(Matchers.empty()))
                .extract().response();
        response.prettyPrint();


    }


    @Test
    @Description("Verify retrieval of available products")
    @Tag("Get Available Products")
    @Severity(SeverityLevel.CRITICAL)
    public void getAvailableProducts() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .queryParam("available", true)
                .when()
                .get(Base.URL + "/products")
                .then()
                .statusCode(200)
                .body("" , Matchers.not(Matchers.empty()))
                .body("inStock", Matchers.everyItem(Matchers.equalTo(true)))
                .extract().response();

        response.prettyPrint();


        List<Integer> ids = response.jsonPath().getList("id");
        int randomId = new Random().nextInt(ids.size());
        int productId = ids.get(randomId);
        Variables.setProductId(productId);

    }


    @Test
    @Description("Verify retrieval of unavailable products")
    @Tag("Get Unavailable Products")
    @Severity(SeverityLevel.CRITICAL)
    public void getUnavailableProducts() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .queryParam("available", false)
                .when()
                .get(Base.URL + "/products");
        response.then()
                .statusCode(200)
                .body("", Matchers.not(Matchers.empty()))
                .body("inStock", Matchers.everyItem(Matchers.equalTo(false)))
                .extract().response();
        response.prettyPrint();

    }

    @Test( dependsOnMethods = {"getAvailableProducts"})
    @Description("Verify retrieval of product by ID")
    @Tag("Get Product By ID")
    @Severity(SeverityLevel.CRITICAL)
    public void getProductById() {

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("productId" , Variables.getProductId())
                .when()
                .get(Base.URL + "/products/{productId}" );
        response.then()
                .statusCode(200)
                .body("id", Matchers.equalTo(Variables.getProductId()))
                .extract().response();
        response.prettyPrint();

      }


    @Test( dependsOnMethods = {"getAvailableProducts"})
    @Description("Verify getting product by invalid ID")
    @Tag("Invalid Product ID")
    @Severity(SeverityLevel.CRITICAL)
    public void getProductbyInvalidId() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .when()
                .get(Base.URL + "/products/999999");
        response.then()
                .statusCode(404)
                .extract().response();
        response.prettyPrint();

    }

    @Test
    @Description("Check getting Products With Invalid QueryParam")
    @Tag("Invalid Query Params")
    @Severity(SeverityLevel.NORMAL)
    public void getProductsWithInvalidQueryParams() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .queryParam("available", "notaboolean")
                .when()
                .get(Base.URL + "/products")
                .then()
                .statusCode(400)
                .extract().response();
        response.prettyPrint();

    }




}

