package tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Map;

public class PetStoreApiTest {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static final String USER = "/user";
    private static final String USER_USERNAME = USER + "/{username}";
    private static final String USER_LOGIN = USER + "/login";
    private static final String USER_LOGOUT = USER + "/logout";

    private String username;
    private String firstName;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    @Test
    public void verifyLoginAction() {
        username = "MelnykVitalii_122225"; // имя + номер группы
        Map<String, String> body = Map.of(
                "username", username,
                "password", "122-22-5.12"
        );

        Response response = RestAssured.given().body(body).get(USER_LOGIN);
        response.then().statusCode(200);

        // Сохраняем sessionId для последующих запросов
        RestAssured.requestSpecification.sessionId(
                response.jsonPath().get("message").toString().replaceAll("-1", "")
        );

        System.out.println("Login successful. Session ID: " + response.jsonPath().get("message"));
    }

    @Test(dependsOnMethods = "verifyLoginAction")
    public void verifyCreateAction() {
        firstName = "Vitalii";
        String lastName = "Melnyk";
        String email = "melnyk122225@test.com";
        String password = "password122225";
        String phone = "1234567890";
        int userStatus = 1;

        Map<String, Object> body = Map.of(
                "username", username,
                "firstName", firstName,
                "lastName", lastName,
                "email", email,
                "password", password,
                "phone", phone,
                "userStatus", userStatus
        );

        Response response = RestAssured.given().body(body).post(USER);
        response.then().statusCode(200);

        System.out.println("Created user: " + username);
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("User Status: " + userStatus);
    }

    @Test(dependsOnMethods = "verifyCreateAction")
    public void verifyGetUserAction() {
        Response response = RestAssured.given().get(USER_USERNAME, username);
        response.then().statusCode(200);

        System.out.println("Get user data:");
        System.out.println(response.getBody().asPrettyString());
    }

    @Test(dependsOnMethods = "verifyGetUserAction", priority = 1)
    public void verifyDeleteAction() {
        RestAssured.given().pathParam("username", username)
                .delete(USER_USERNAME)
                .then().statusCode(200);

        System.out.println("Deleted user: " + username);
    }

    @Test(dependsOnMethods = "verifyLoginAction", priority = 2)
    public void verifyLogoutAction() {
        RestAssured.given().get(USER_LOGOUT)
                .then().statusCode(200);

        System.out.println("User logged out successfully.");
    }
}
