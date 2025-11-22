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

public class Cart {

  @Test(dependsOnMethods={"tests.Products.getAvailableProducts"})
  @Description("Verify creation of new cart")
  @Tag("Create Cart")
  @Severity(SeverityLevel.CRITICAL)
  public void createNewCart() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .post(Base.URL + "/carts")
                .then()
                .statusCode(201)
                .body("cartId", Matchers.notNullValue())
                .body("created", Matchers.equalTo(true))
                .extract().response();

        response.prettyPrint();
        Variables.setCartId(response.jsonPath().getString("cartId"));
    }


    @Test( dependsOnMethods = {"createNewCart"})
    @Description("Verify retrieval of a cart by ID")
    @Tag("Get Cart By ID")
    @Severity(SeverityLevel.CRITICAL)
    public void getCartById() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .when()
                .get(Base.URL + "/carts/{cartId}")
                .then()
                .statusCode(200)
                .extract().response();

        response.prettyPrint();
    }


    @Test( dependsOnMethods = {"createNewCart"})
    @Description("Verify retrieving a non-existing cart ")
    @Tag("Get cart by nonexistent id")
    @Severity(SeverityLevel.NORMAL)
    public void getCartbynonexistentId() {
        String invalidCartId = "non-existing-cart-123456";
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", invalidCartId)
                .when()
                .get(Base.URL + "/carts/{cartId}")
                .then()
                .statusCode(404)
                .extract().response();

        response.prettyPrint();

    }



   @Test(dependsOnMethods = {"createNewCart"})
   @Description("Verify adding an item to the cart")
   @Tag("Add Item to Cart")
   @Severity(SeverityLevel.CRITICAL)
    public void addItem() {

        Map<String, Object> body = new HashMap<>();
        body.put("productId",Variables.getProductId());
        body.put("quantity" ,1);

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
        response.prettyPrint();
        Variables.setItemId(response.jsonPath().getString("itemId"));
    }


    @Test( dependsOnMethods = {"addItem"})
    @Description("Verify deletion of an item from the cart")
    @Tag("Delete Item from Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteItem() {

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .pathParam("itemId", Variables.getItemId())
                .when()
                .delete(Base.URL + "/carts/{cartId}/items/{itemId}")
                .then()
                .statusCode(204)
                .extract().response();
        response.prettyPrint();

    }

    @Test(dependsOnMethods = {"addItemAgain"})
    @Description("Verify deleting a non-existing item ")
    @Tag("Delete Non-existing Item")
    @Severity(SeverityLevel.NORMAL)
    public void deleteNonExistingItem() {
        String invalidItemId = "9999999999999";

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .pathParam("itemId", invalidItemId)
                .when()
                .delete(Base.URL + "/carts/{cartId}/items/{itemId}")
                .then()
                .statusCode(404)
                .extract().response();

        response.prettyPrint();
    }



    @Test( dependsOnMethods = {"deleteItem"})
    @Description("Verify the cart is empty after item deletion")
    @Tag("Verify Empty Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void getItemAfterDeletion() {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId" , Variables.getCartId())
                .when()
                .get(Base.URL + "/carts/{cartId}/items")
                .then()
                .statusCode(200)
                .body("", Matchers.empty())
                .extract().response();
        response.prettyPrint();

    }


    @Test(dependsOnMethods = {"getItemAfterDeletion"})
    @Description("Verify adding an item again to the cart after deleting")
    @Tag("Re-Add Item to Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addItemAgain() {

        Map<String, Object> body = new HashMap<>();
        body.put("productId", Variables.getProductId());
        body.put("quantity" ,1);


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
        response.prettyPrint();

        Variables.setItemId(response.jsonPath().getString("itemId"));
    }


    @Test( dependsOnMethods = {"addItem"})
    @Description("Verify modifying the quantity of an item in the cart")
    @Tag("Modify Item Quantity")
    @Severity(SeverityLevel.CRITICAL)
    public void modifyItemQuantity() {

        Map<String, Object> body = new HashMap<>();
        body.put("quantity", 2);
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .pathParam("itemId", Variables.getItemId())
                .body(body)
                .patch(Base.URL + "/carts/{cartId}/items/{itemId}")
                .then()
                .statusCode(204)
                .extract().response();
        response.prettyPrint();

    }

    @Test(dependsOnMethods = {"addItem"})
    @Description("Verify modifying the quantity with missing cartId")
    @Tag("Modify Item Quantity")
    @Severity(SeverityLevel.CRITICAL)
    public void modifyItemwithmissingcartid() {

        Map<String, Object> body = new HashMap<>();
        body.put("quantity", 2);

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("itemId", Variables.getItemId())
                .body(body)
                .patch(Base.URL + "/carts/items/{itemId}")
                .then()
                .statusCode(404)
                .extract().response();
        response.prettyPrint();
  }


    @Test(dependsOnMethods = {"addItem"})
    @Description("Verify replacing an item in the cart with a new product")
    @Tag("Replace Item in Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void replaceItemInCart() {

        Map<String, Object> body = new HashMap<>();
        body.put("productId",1709);
        body.put("quantity", 5);

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .pathParam("itemId", Variables.getItemId())
                .body(body)
                .put(Base.URL + "/carts/{cartId}/items/{itemId}")
                .then()
                .statusCode(204)
                .extract().response();
        response.prettyPrint();

    }

    @Test( dependsOnMethods = {"addItem"})
    @Description("Verify replacing an item in the cart with a new product with invalid productId")
    @Tag("Replace Item in Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void replaceItemInCartwithinvalidproductId() {

        Map<String, Object> body = new HashMap<>();
        body.put("productId","hhhh");
        body.put("quantity", 5);

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .pathParam("itemId", Variables.getItemId())
                .body(body)
                .put(Base.URL + "/carts/{cartId}/items/{itemId}")
                .then()
                .statusCode(400)
                .extract().response();
        response.prettyPrint();

    }



    @Test( dependsOnMethods = {"addItem"})
    @Description("Verify replacing an item in the cart with a new product with missing cartId")
    @Tag("Replace Item in Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void replaceItemInCartwithmissingCartId() {

        Map<String, Object> body = new HashMap<>();
        body.put("productId",1225);
        body.put("quantity", 5);

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("itemId", Variables.getItemId())
                .body(body)
                .put(Base.URL + "/carts/items/{itemId}")
                .then()
                .statusCode(404)
                .extract().response();
        response.prettyPrint();

    }


    @Test( dependsOnMethods = {"createNewCart"})
    @Description("Verify adding an item with invalid product ID ")
    @Tag("Add Item with Invalid Product ID")
    @Severity(SeverityLevel.CRITICAL)
    public void addItemWithInvalidProductId() {
       Map<String, Object> body = new HashMap<>();
        body.put("productId", "hhhh");

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .when()
                .body(body)
                .post(Base.URL + "/carts/{cartId}/items")
                .then()
                .statusCode(400)
                .extract().response();
        response.prettyPrint();

    }


    @Test(dependsOnMethods = {"addItem"})
    @Description("Verify modifying item with invalid quantity ")
    @Tag("invalid scenario")
    @Severity(SeverityLevel.NORMAL)
    public void modifyItemWithInvalidQuantity() {

     Map<String, Object> body = new HashMap<>();
        body.put("quantity", "invalid");

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .pathParam("cartId", Variables.getCartId())
                .pathParam("itemId", Variables.getItemId())
                .body(body)
                .patch(Base.URL + "/carts/{cartId}/items/{itemId}")
                .then()
                .statusCode(400)
                .extract().response();
        response.prettyPrint();

    }






}
