package tests;

import utils.Base;
import utils.Variables;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.testng.Tag;

public class Orders {

    @Test(dependsOnMethods = {"tests.Cart.addItem"})
    @Description("Verify creation of a new order")
    @Tag("Valid Creating Order")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewOrder() {
        Allure.step("Create a new order using cartId and clientName", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("cartId", Variables.getCartId());
            body.put("customerName", Variables.getClientName());

            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .when()
                    .post(Base.URL + "/orders")
                    .then()
                    .statusCode(201)
                    .body("orderId", Matchers.notNullValue())
                    .body("created", Matchers.equalTo(true))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
            Variables.setOrderId(response.jsonPath().getString("orderId"));
        });
    }

    @Test(dependsOnMethods = {"tests.Cart.addItem"})
    @Description("Verify creating an order with an invalid cart Id")
    @Tag("Invalid Creating order")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithinvalidCartId() {
        Allure.step("Attempt to create order with invalid cartId", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("cartId", 11);
            body.put("customerName", Variables.getClientName());

            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .when()
                    .post(Base.URL + "/orders")
                    .then()
                    .statusCode(400)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"tests.Cart.addItem"})
    @Description("Verify creating an order without authorization")
    @Tag("Invalid Creating order")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithoutAuth() {
        Allure.step("Attempt to create order without authorization", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("cartId", Variables.getCartId());
            body.put("customerName", Variables.getClientName());

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .body(body)
                    .when()
                    .post(Base.URL + "/orders")
                    .then()
                    .statusCode(401)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieval of all orders")
    @Tag("Valid Getting All Orders")
    @Severity(SeverityLevel.NORMAL)
    public void getAllOrders() {
        Allure.step("Retrieve all orders with valid authorization", () -> {
            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .when()
                    .get(Base.URL + "/orders")
                    .then()
                    .statusCode(200)
                    .body("", Matchers.not(Matchers.empty()))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieving orders without authorization")
    @Tag("Invalid Getting All Orders")
    @Severity(SeverityLevel.CRITICAL)
    public void getOrdersWithoutAuth() {
        Allure.step("Attempt to retrieve all orders without authorization", () -> {
            Response response = RestAssured
                    .given()
                    .when()
                    .get(Base.URL + "/orders")
                    .then()
                    .statusCode(401)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieval of a single order")
    @Tag("Valid Getting Single Order")
    @Severity(SeverityLevel.CRITICAL)
    public void getSingleOrder() {
        Allure.step("Retrieve a single order by orderId with valid authorization", () -> {
            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .pathParam("orderId", Variables.getOrderId())
                    .when()
                    .get(Base.URL + "/orders/{orderId}")
                    .then()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(Variables.getOrderId()))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieval of a single order without authorization")
    @Tag("Invalid Getting Single Order")
    @Severity(SeverityLevel.CRITICAL)
    public void getSingleOrderwithoutAuth() {
        Allure.step("Attempt to retrieve a single order without authorization", () -> {
            Response response = RestAssured
                    .given()
                    .pathParam("orderId", Variables.getOrderId())
                    .when()
                    .get(Base.URL + "/orders/{orderId}")
                    .then()
                    .statusCode(401)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieving a non-existent order")
    @Tag("Invalid Getting Single Order")
    @Severity(SeverityLevel.CRITICAL)
    public void getNonExistentOrder() {
        Allure.step("Attempt to retrieve a non-existent order", () -> {
            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .pathParam("invalidorderId", "non-existent-id")
                    .when()
                    .get(Base.URL + "/orders/{invalidorderId}")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"getSingleOrder"})
    @Description("Verify updating order with updated customer name")
    @Tag("Update Order")
    @Severity(SeverityLevel.CRITICAL)
    public void updateOrder() {
        Allure.step("Update order's customer name", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("customerName", Variables.getUpdatedCustomerName());

            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .header("Content-Type", "application/json")
                    .pathParam("orderId", Variables.getOrderId())
                    .body(body)
                    .when()
                    .patch(Base.URL + "/orders/{orderId}")
                    .then()
                    .statusCode(204)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"getSingleOrder"})
    @Description("Verify updating an order without authorization")
    @Tag("Update Order With Invalid Token")
    @Severity(SeverityLevel.CRITICAL)
    public void updateOrderWithoutAuth() {
        Allure.step("Attempt to update order without authorization", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("customerName", Variables.getClientName());

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("orderId", Variables.getOrderId())
                    .body(body)
                    .when()
                    .patch(Base.URL + "/orders/{orderId}")
                    .then()
                    .statusCode(401)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"updateOrder"})
    @Description("Verify deleting of an order")
    @Tag("Delete Order")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteOrder() {
        Allure.step("Delete the created order", () -> {
            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .pathParam("orderId", Variables.getOrderId())
                    .when()
                    .delete(Base.URL + "/orders/{orderId}")
                    .then()
                    .statusCode(204)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"deleteOrder"})
    @Description("Verify data after deleting order")
    @Tag("Verify Deleted Order")
    @Severity(SeverityLevel.CRITICAL)
    public void getOrderafterDeletion() {
        Allure.step("Verify order is deleted", () -> {
            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .pathParam("orderId", Variables.getOrderId())
                    .when()
                    .get(Base.URL + "/orders/{orderId}")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"updateOrder"})
    @Description("Verify deleting a non-existent order")
    @Tag("Delete Non-Existent Order")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteNonExistentOrder() {
        Allure.step("Attempt to delete a non-existent order", () -> {
            Response response = RestAssured
                    .given()
                    .header("Authorization", "Bearer " + Variables.getAccessToken())
                    .pathParam("invalidorderId", "non-existent-id")
                    .when()
                    .delete(Base.URL + "/orders/{invalidorderId}")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }
}
