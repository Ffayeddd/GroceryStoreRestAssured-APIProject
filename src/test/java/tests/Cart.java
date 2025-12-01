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

public class Cart {

    @Test(dependsOnMethods = {"tests.Products.getAvailableProducts"})
    @Description("Verify creation of new cart")
    @Tag("Create Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void createNewCart() {
        Allure.step("Create a new cart", () -> {
            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .when()
                    .post(Base.URL + "/carts")
                    .then()
                    .statusCode(201)
                    .body("cartId", Matchers.notNullValue())
                    .body("created", Matchers.equalTo(true))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
            Variables.setCartId(response.jsonPath().getString("cartId"));
        });
    }

    @Test(dependsOnMethods = {"createNewCart"})
    @Description("Verify retrieval of cart by valid Id")
    @Tag("Valid getting cart")
    @Severity(SeverityLevel.CRITICAL)
    public void getCartById() {
        Allure.step("Retrieve cart by valid cartId", () -> {
            Response response = RestAssured
                    .given()
                    .pathParam("cartId", Variables.getCartId())
                    .when()
                    .get(Base.URL + "/carts/{cartId}")
                    .then()
                    .statusCode(200)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"createNewCart"})
    @Description("Verify retrieving a non-existing cart")
    @Tag("Invalid getting cart")
    @Severity(SeverityLevel.NORMAL)
    public void getCartByNonexistentId() {
        Allure.step("Retrieve cart with invalid cartId", () -> {
            String invalidCartId = "non-existing-cart-123456";
            Response response = RestAssured
                    .given()
                    .pathParam("cartId", invalidCartId)
                    .when()
                    .get(Base.URL + "/carts/{cartId}")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"createNewCart"})
    @Description("Verify adding an item to the cart")
    @Tag("Valid Adding Item")
    @Severity(SeverityLevel.CRITICAL)
    public void addItem() {
        Allure.step("Add an item to the cart", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("productId", Variables.getProductId());
            body.put("quantity", 1);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("cartId", Variables.getCartId())
                    .body(body)
                    .when()
                    .post(Base.URL + "/carts/{cartId}/items")
                    .then()
                    .statusCode(201)
                    .body("itemId", Matchers.notNullValue())
                    .body("created", Matchers.equalTo(true))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
            Variables.setItemId(response.jsonPath().getString("itemId"));
        });
    }

    @Test(dependsOnMethods = {"createNewCart"})
    @Description("Verify adding an item with invalid product Id")
    @Tag("Invalid Adding Item")
    @Severity(SeverityLevel.CRITICAL)
    public void addItemWithInvalidProductId() {
        Allure.step("Attempt to add an item with invalid productId", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("productId", "hhhh");
            body.put("quantity", 7);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("cartId", Variables.getCartId())
                    .body(body)
                    .when()
                    .post(Base.URL + "/carts/{cartId}/items")
                    .then()
                    .statusCode(400)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"addItem"})
    @Description("Verify deletion of an item from the cart")
    @Tag("Valid Deleting Item")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteItem() {
        Allure.step("Delete an existing item from the cart", () -> {
            Response response = RestAssured
                    .given()
                    .pathParam("cartId", Variables.getCartId())
                    .pathParam("itemId", Variables.getItemId())
                    .when()
                    .delete(Base.URL + "/carts/{cartId}/items/{itemId}")
                    .then()
                    .statusCode(204)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"addItem"})
    @Description("Verify deleting a non-existing item")
    @Tag("Invalid Deleting Item")
    @Severity(SeverityLevel.NORMAL)
    public void deleteNonExistingItem() {
        Allure.step("Attempt to delete a non-existing item", () -> {
            String invalidItemId = "9999999999999";

            Response response = RestAssured
                    .given()
                    .pathParam("cartId", Variables.getCartId())
                    .pathParam("itemId", invalidItemId)
                    .when()
                    .delete(Base.URL + "/carts/{cartId}/items/{itemId}")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"deleteItem"})
    @Description("Verify the cart is empty after item deletion")
    @Tag("Verify Empty Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void getItemAfterDeletion() {
        Allure.step("Check cart is empty after item deletion", () -> {
            Response response = RestAssured
                    .given()
                    .pathParam("cartId", Variables.getCartId())
                    .when()
                    .get(Base.URL + "/carts/{cartId}/items")
                    .then()
                    .statusCode(200)
                    .body("", Matchers.empty())
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"getItemAfterDeletion"})
    @Description("Verify adding an item again to the cart after deleting")
    @Tag("Re-Add Item to Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addItemAgain() {
        Allure.step("Add an item again after deletion", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("productId", Variables.getProductId());
            body.put("quantity", 2);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("cartId", Variables.getCartId())
                    .body(body)
                    .when()
                    .post(Base.URL + "/carts/{cartId}/items")
                    .then()
                    .statusCode(201)
                    .body("itemId", Matchers.notNullValue())
                    .body("created", Matchers.equalTo(true))
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
            Variables.setItemId(response.jsonPath().getString("itemId"));
        });
    }

    @Test(dependsOnMethods = {"addItemAgain"})
    @Description("Verify modifying the quantity with missing cartId")
    @Tag("Invalid Modifying Item Quantity")
    @Severity(SeverityLevel.CRITICAL)
    public void modifyItemWithMissingCartId() {
        Allure.step("Attempt to modify an item with missing cartId", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("quantity", 3);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("itemId", Variables.getItemId())
                    .body(body)
                    .when()
                    .patch(Base.URL + "/carts/items/{itemId}")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"addItemAgain"})
    @Description("Verify modifying item with invalid quantity")
    @Tag("Invalid Modifying Item Quantity")
    @Severity(SeverityLevel.NORMAL)
    public void modifyItemWithInvalidQuantity() {
        Allure.step("Attempt to modify an item with invalid quantity", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("quantity", "invalid");

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("cartId", Variables.getCartId())
                    .pathParam("itemId", Variables.getItemId())
                    .body(body)
                    .when()
                    .patch(Base.URL + "/carts/{cartId}/items/{itemId}")
                    .then()
                    .statusCode(400)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"addItemAgain"})
    @Description("Verify replacing an item in the cart")
    @Tag("Valid Replacing Item in Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void replaceItemInCart() {
        Allure.step("Replace an item in the cart with valid productId", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("productId", 1709);
            body.put("quantity", 5);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("cartId", Variables.getCartId())
                    .pathParam("itemId", Variables.getItemId())
                    .body(body)
                    .when()
                    .put(Base.URL + "/carts/{cartId}/items/{itemId}")
                    .then()
                    .statusCode(204)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"addItemAgain"})
    @Description("Verify replacing an item with invalid product Id")
    @Tag("Invalid Replacing Item in Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void replaceItemInCartWithInvalidProductId() {
        Allure.step("Attempt to replace an item with invalid productId", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("productId", "hhhh");
            body.put("quantity", 5);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("cartId", Variables.getCartId())
                    .pathParam("itemId", Variables.getItemId())
                    .body(body)
                    .when()
                    .put(Base.URL + "/carts/{cartId}/items/{itemId}")
                    .then()
                    .statusCode(400)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }

    @Test(dependsOnMethods = {"addItemAgain"})
    @Description("Verify replacing an item with missing cart Id")
    @Tag("Invalid Replacing Item in Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void replaceItemInCartWithMissingCartId() {
        Allure.step("Attempt to replace an item without cartId", () -> {
            Map<String, Object> body = new HashMap<>();
            body.put("productId", 1225);
            body.put("quantity", 5);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .pathParam("itemId", Variables.getItemId())
                    .body(body)
                    .when()
                    .put(Base.URL + "/carts/items/{itemId}")
                    .then()
                    .statusCode(404)
                    .extract().response();

            Allure.addAttachment("Response Body", response.getBody().asPrettyString());
        });
    }
}
