package tests;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.testng.Tag;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.hamcrest.Matchers;
import utils.Base;


public class Status {

    @Test
    @Description("Verify that the status is UP ")
    @Tag("Checking Status")
    @Severity(SeverityLevel.NORMAL)
    public void checkingStatus() {

        Response response = RestAssured
                        .given()
                        .when()
                        .get(Base.URL + "/status")
                        .then().statusCode(200)
                        .body("status", Matchers.equalTo("UP"))
                        .extract().response();
                         response.prettyPrint();
    }
}
