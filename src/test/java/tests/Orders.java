package tests;
import utils.Base;
import utils.Variables;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
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

        Map<String, Object> body = new HashMap<>();
        body.put("cartId", Variables.getCartId());
        body.put("customerName", Variables.getClientName());

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " +  Variables.getAccessToken())
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(Base.URL + "/orders")
                .then()
                .statusCode(201)
                .body("orderId", Matchers.notNullValue())
                .body("created", Matchers.equalTo(true))
                .extract().response();
        response.prettyPrint();

        Variables.setOrderId( response.jsonPath().getString("orderId"));
    }


    @Test( dependsOnMethods = {"tests.Cart.addItem"})
    @Description("Verify creating an order with an invalid cart Id")
    @Tag("Invalid Creating order")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithinvalidCartId() {

        Map<String, Object> body = new HashMap<>();
        body.put("cartId", 11);
        body.put("customerName",  Variables.getClientName());

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
        response.prettyPrint();

    }


    @Test(dependsOnMethods = {"tests.Cart.addItem"})
    @Description("Verify creating an order without authorization")
    @Tag("Invalid Creating order")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithoutAuth() {

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
        response.prettyPrint();


    }

    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieval of all orders")
    @Tag("Valid Getting All Orders")
    @Severity(SeverityLevel.NORMAL)
    public void getAllOrders() {

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + Variables.getAccessToken())
                .when()
                .get(Base.URL + "/orders")
                .then()
                .statusCode(200)
                .body("", Matchers.not(Matchers.empty()))
                .extract().response();
        response.prettyPrint();

    }

    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieving orders without authorization ")
    @Tag("Invalid Getting All Orders")
    @Severity(SeverityLevel.CRITICAL)
    public void getOrdersWithoutAuth() {
        Response response = RestAssured
                .given()
                .when()
                .get(Base.URL + "/orders")
                .then()
                .statusCode(401)
                .extract().response();

        response.prettyPrint();

    }


    @Test( dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieval of a single order")
    @Tag("Valid Getting Single Order")
    @Severity(SeverityLevel.CRITICAL)
    public void getSingleOrder() {

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

        response.prettyPrint();

        }



    @Test( dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieval of a single order without authorization ")
    @Tag("Invalid Getting Single Order")
    @Severity(SeverityLevel.CRITICAL)
    public void getSingleOrderwithoutAuth() {

        Response response = RestAssured
                .given()
                .pathParam("orderId", Variables.getOrderId())
                .when()
                .get(Base.URL + "/orders/{orderId}")
                .then()
                .statusCode(401)
                .extract().response();

        response.prettyPrint();

    }



    @Test(dependsOnMethods = {"createNewOrder"})
    @Description("Verify retrieving a non-existent order ")
    @Tag("Invalid Getting Single Order")
    @Severity(SeverityLevel.CRITICAL)
    public void getNonExistentOrder() {

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + Variables.getAccessToken())
                .pathParam("invalidorderId","non-existent-id")
                .when()
                .get(Base.URL + "/orders/{invalidorderId}")
                .then()
                .statusCode(404)
                .extract().response();

        response.prettyPrint();
    }



    @Test(dependsOnMethods = {"getSingleOrder"})
    @Description("Verify updating order with updated customer name")
    @Tag("Valid updating Order")
    @Severity(SeverityLevel.CRITICAL)
    public void updateOrder() {

        Map<String, Object> body = new HashMap<>();
        body.put("customerName",Variables.getUpdatedCustomerName());
        System.out.println(Variables.getUpdatedCustomerName());
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

        response.prettyPrint();

    }




    @Test(dependsOnMethods = {"getSingleOrder"})
    @Description("Verify updating an order without authorization ")
    @Tag("Invalid updating Order")
    @Severity(SeverityLevel.CRITICAL)
    public void updateOrderWithoutAuth() {
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

        response.prettyPrint();
    }


    @Test(dependsOnMethods = {"updateOrder"})
    @Description("Verify deleting an order")
    @Tag("Valid Deleting Order")
    @Severity(SeverityLevel.CRITICAL)

    public void deleteOrder() {
      Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " +  Variables.getAccessToken())
                .pathParam("orderId", Variables.getOrderId())
                .when()
                .delete(Base.URL + "/orders/{orderId}")
                .then()
                .statusCode(204)
                .extract().response();

        response.prettyPrint();

    }


    @Test( dependsOnMethods = {"deleteOrder"})
    @Description("Verify data existence after deleting order")
    @Tag("Verify Deleted Order" )
    @Severity(SeverityLevel.CRITICAL)
    public void getOrderafterDeletion() {

      Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + Variables.getAccessToken())
                .pathParam("orderId", Variables.getOrderId())
                .when()
                .get(Base.URL + "/orders/{orderId}")
                .then()
                .statusCode(404)
                .extract().response();

        response.prettyPrint();
    }


    @Test( dependsOnMethods = {"updateOrder"})
    @Description("Verify deleting a non-existent order ")
    @Tag("Invalid Deleting Order")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteNonExistentOrder() {

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + Variables.getAccessToken())
                .pathParam("invalidorderId" ,"non-existent-id")
                .when()
                .delete(Base.URL + "/orders/{invalidorderId}")
                .then()
                .statusCode(404)
                .extract().response();

        response.prettyPrint();
    }


}