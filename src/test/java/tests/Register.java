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

public class Register {
    
    @Test
    @Description("Verify that a client can successfully register with valid name and email")
    @Tag("Client Registration")
    @Severity(SeverityLevel.CRITICAL)
    public void registerClientSuccess() {

        Map<String, String> body = new HashMap<>();
        body.put("clientName", Variables.getClientName());
        body.put("clientEmail", Variables.getClientEmail());

        Response response =RestAssured
                        .given()
                        .header("Content-Type", "application/json")
                        .body(body)
                        .when()
                        .post(Base.URL+ "/api-clients")
                        .then()
                        .statusCode(201)
                        .body("accessToken", Matchers.notNullValue())
                        .extract().response();

        response.prettyPrint();
        Variables.setAccessToken(response.jsonPath().getString("accessToken"));

    }


    @Test
    @Description("Check if client can register with invalid data types")
    @Tag("Client Registration")
    @Severity(SeverityLevel.CRITICAL)
    public void registerClientBadRequest() {

        Map<String, Object> body = new HashMap<>();
        body.put("clientName", 12345);
        body.put("clientEmail", 67890);

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(Base.URL+ "/api-clients")
                .then()
                .statusCode(400)
                .body("error", Matchers.equalTo("Invalid or missing client email."))
                .extract().response();
        response.prettyPrint();


    }

    @Test
    @Description("Check if client can register with duplicated email ")
    @Tag("Client Registration")
    @Severity(SeverityLevel.CRITICAL)
    public void registerClientDuplicateEmail() {

        Map<String, String> body = new HashMap<>();
        body.put("clientName", "fatma");
        body.put("clientEmail", "fatma19@gmail.com");

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(Base.URL+ "/api-clients")
                .then()
                .statusCode(409)
                .body("error", Matchers.equalTo("API client already registered. Try a different email."))
                .extract().response();
                 response.prettyPrint();

    }


    @Test
    @Description("Check if client can register with missing email ")
    @Tag("Client Registration")
    @Severity(SeverityLevel.CRITICAL)
    public void registerClientMissingEmail() {
        Map<String, String> body = new HashMap<>();

        body.put("clientEmail", "");
        body.put("clientName", Variables.getClientName());

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(Base.URL+ "/api-clients")
                .then()
                .statusCode(400)
                .extract().response();
        response.prettyPrint();
    }


    @Test
    @Description("Check if client can register with missing name ")
    @Tag("Client Registration")
    @Severity(SeverityLevel.CRITICAL)
    public void registerClientMissingName() {
        Map<String, String> body = new HashMap<>();

        body.put("clientEmail", Variables.getClientEmail());
        body.put("clientName", "");
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(Base.URL + "/api-clients")
                .then()
                .statusCode(400)
                .extract().response();
        response.prettyPrint();

    }
}
