import POJO.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class GoRestUsersTest {
    public String createRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String createRandomEmail() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@techmobile.com";
    }

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;
    Response response;

    @BeforeClass
    public void setup() {
        baseURI = "https://gorest.co.in/public/v2/users";
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer 4eb93a5735fbe49c723fb33a697cc0cce17da406adce3231b5e0b094e912bd90")
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }

    @Test(enabled = false)
    public void createAUser() {
        given()
                .spec(requestSpec)
                .body("{\"name\":\"" + createRandomName() + "\",\"email\":\"" + createRandomEmail() + "\",\"gender\": \"male\",\"status\": \"active\"}")
                .log().uri()
                .log().body()
                .when()
                .post("")
                .then()
                .spec(responseSpec)
                .statusCode(201);
    }

    @Test(enabled = false)
    public void createAUserWithMaps() {

        Map<String, String> user = new HashMap<>();
        user.put("name", createRandomName());
        user.put("email", createRandomEmail());
        user.put("gender", "female");
        user.put("status", "active");
        given()
                .spec(requestSpec)
                .body(user)
                .log().uri()
                .log().body()
                .when()
                .post("")
                .then()
                .spec(responseSpec)
                .statusCode(201);

    }

    User user;

    @Test
    public void createAUserWithObjects() {
        user = new User();
        user.setName(createRandomName());
        user.setEmail(createRandomEmail());
        user.setGender("male");
        user.setStatus("active");

        response =
                given()
                        .spec(requestSpec)
                        .body(user)
                        .log().uri()
                        .log().body()
                        .when()
                        .post("https://gorest.co.in/public/v2/users")
                        .then()
                        .spec(responseSpec)
                        .statusCode(201)
                        .extract().response();
    }

    /**
     * create user negative test
     */

    @Test(dependsOnMethods = "createAUserWithObjects", priority = 1)
    public void createUserNegativeTest() {
        User user = new User();
        user.setName(createRandomName());
        user.setEmail(response.path("email"));
        user.setGender("male");
        user.setStatus("active");

        given()
                .spec(requestSpec)
                .body(user)
                .log().uri()
                .log().body()
                .when()
                .post("")
                .then()
                .spec(responseSpec)
                .statusCode(422)
                .body("[0].message", equalTo("has already been taken"));
    }
    /**get the user you created in createAUserWithObjects test**/

    @Test(dependsOnMethods = "createAUserWithObjects",priority = 2)
    public void getUserById(){
        given()
                .spec(requestSpec)
                .pathParam("userId",response.path("id")) // we get the parameter id from the response of createAUserWithObjects
                .when()
                .get("/{userId}")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("email",equalTo(response.path("email")))
                .body("id",equalTo(response.path("id")))
                .body("name",equalTo(response.path("name")));
    }

    /** Update the user you created in createAUserWithObjects **/
    @Test(dependsOnMethods = "createAUserWithObjects",priority = 3)
    public void updateUser(){
        user.setName("Ragip Bey");
        given()
                .spec(requestSpec)
                .body(user)
                .pathParam("userId",response.path("id"))
                .with()
                .put("/{userId}")
                .then()
                .spec(responseSpec)
                .statusCode(200);
    }
    /** Delete the user we created in createAUserWithObjects **/

    @Test(dependsOnMethods = "createAUserWithObjects", priority = 4)
    public void deleteUser(){
        given()
                .spec(requestSpec)
                .pathParam("userId",response.path("id"))

                .when()
                .delete("/{userId}")

                .then()
                .statusCode(204);
    }

    /** create delete user negative test **/

    @Test(dependsOnMethods = {"createAUserWithObjects","deleteUser"}, priority = 5)
    public void deleteUserNegativeTest(){
        given()
                .spec(requestSpec)
                .pathParam("userId",response.path("id"))

                .when()
                .delete("/{userId}")

                .then()
                .statusCode(404);
    }
    @Test
    public void getUsers(){
       Response response1= given()
                .spec(requestSpec)
                .when()
                .get()
                .then()
               .spec(responseSpec)
               .statusCode(200)
               .extract().response();
       int userId0=response1.jsonPath().getInt("[0].id");
       int userId1=response1.jsonPath().getInt("[1].id");
       List<User> usersList=response1.jsonPath().getList("",User.class);
        System.out.println("userId0="+ userId0);
        System.out.println("userId1="+ userId1);
        System.out.println(usersList);

    }


}
