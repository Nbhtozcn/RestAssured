import POJO.Location;
import POJO.Place;
import POJO.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ZippoTest {
    @Test
    public void test() {
        given() //preparation (token, request body, parameters, cookies)
                .when() //for url and request method(get,post,put,delete)
                .then(); //response body, assertion, extract data from response
    }

    @Test
    public void statusCodeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .log().status() //prints the response body
                .statusCode(200);
    }

    @Test
    public void contentTypeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    public void checkCountryFromResponseBody() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .body("places[0].'longitude'", equalTo("-118.4065"))
                .body("places[0].'state'", equalTo("California"))
                .body("country", equalTo("United States"));

    }
    // pm                                               // Rest Assured
    // pm.response.json().'post code'                   // body("'post code'", ...)
    // pm.response.json().places[0].'place name'        // body("places[0].'place name'", ...)
    // body("places.'place name'") gives a list of place names in the places list


    @Test
    public void checkStateFromResponse() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].'state'", equalTo("California"));
    }

    @Test
    public void bodyArrayHasItem() {
        given().when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                //.log().body()
                .body("places.'place name'", hasItem("Dervişler Köyü"))
                .body("places.'place name'", hasSize(71));
    }

    @Test
    public void bodyArraySizeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places", hasSize(1));
    }

    @Test
    public void multipleTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places[2].'place name'", equalTo("Dörtağaç Köyü"));

    }

    @Test
    public void pathParamTest() {
        given()
                .pathParams("Country", "us")
                .pathParams("ZipCode", "90210")
                .log().uri()
                .when()
                .get("http://api.zippopotam.us/{Country}/{90210}")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    public void pathParamTest1() {
        //we are sending parameters from url because of that they call it pathParam
        // there is other option to send parameters from request body, what is called query parameters or query

        //send get request for zipcodes between 90210 and 90213 and verify that in all response the size of the places array is 1
        for (int i = 90210; i < 90213; i++) {
            given()
                    .pathParams("Country", "us")
                    .pathParams("ZipCode", i)
                    .log().uri()
                    .when()
                    .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("places", hasSize(1));
        }

    }

    @Test
    public void queryParamTest() {
        given()
                .param("page", 2) //check if the page number we send and receive are the same
                .when()
                .get("https://gorest.co.in/public/v1/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(2));
    }

    @Test
    public void queryParamTest1() {
        // send the same request for the pages between 1-10 and check if
        // the page number we send from request and page number we get from response are the same
        for (int i = 1; i < 10; i++) {
            given()
                    .param("page", i) //check if the page number we send and receive are the same
                    .when()
                    .get("https://gorest.co.in/public/v1/users")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }

    }

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void setup() {
        baseURI = "https://gorest.co.in/public/v1";

        requestSpec = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();


    }

    @Test
    public void baseURITest() {
        given()
                .param("page", 2) //check if the page number we send and receive are the same
                .log().uri()
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(2));

    }

    @Test
    public void requestResponseSpecsTest() {
        given()
                .param("page", 2) //check if the page number we send and receive are the same'
                .spec(requestSpec)
                .when()
                .get("/users")
                .then()
                .body("meta.pagination.page", equalTo(2))
                .spec(responseSpec);
    }

    //JSON DATA EXTRACT

    @Test
    public void extractData() {
        String placeName = given()
                .pathParams("Country", "us")
                .pathParams("ZipCode", "90210")
                .log().uri()
                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                .then()
                //.log().body()
                .statusCode(200)
                .extract().path("places[0].'place name'");
        System.out.println(placeName);
    }

    @Test
    public void extractData1() {
        int limit = given()
                .param("page", 2) //check if the page number we send and receive are the same\
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("meta.pagination.limit");

        System.out.println(limit);
        Assert.assertEquals(limit, 10);
    }

    //get all IDs from data

    @Test
    public void allIDTest() {
        List<Integer> IDs=given()
                .param("page",2)
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("data.id");
        System.out.println(IDs.get(1));
        Assert.assertTrue(IDs.contains(1078200));

    }
    @Test
    public void extractData2(){
        // get all ids from the response and verify that 1060492 is among them separately from the request

        List<Integer> listOfIds = given()
                .param("page",2)

                .when()
                .get("/users")  //
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("data.id");

        System.out.println(listOfIds.get(1));
        Assert.assertTrue(listOfIds.contains(1060492));
    }
    @Test
    public void extractData3(){

        // send get request to https://gorest.co.in/public/v1/users.
        // extract all names from data to a list

        List<String> namesList = given()
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("data.name");

        System.out.println(namesList.get(5));

        Assert.assertEquals(namesList.get(5),"Ranjit Devar");

    }
    @Test
    public void extractData4(){

        Response response = given()
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().response();

        List<Integer> listOfIds = response.path("data.id");
        List<String> listOfNames = response.path("data.name");
        int limit = response.path("meta.pagination.limit");
        String currentLink = response.path("meta.pagination.links.current");

        System.out.println("listOfIds = " + listOfIds);
        System.out.println("listOfNames = " + listOfNames);
        System.out.println("limit = " + limit);
        System.out.println("currentLink = " + currentLink);

        Assert.assertTrue(listOfNames.contains("Rev. Bhadraksh Gill"));
        Assert.assertTrue(listOfIds.contains(1078203));
        Assert.assertEquals(limit, 10);

    }
    @Test
    public void extractJsonPOJO(){
        // Location                                     // PLace
        // String post code;                            String place name;
        // String country;                              String longitude;
        // String country abbreviation;                 String state;
        // List<Place> places;                          String state abbreviation;
        //String latitude;

        Location location = given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .extract().as(Location.class);

        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPostCode() = " + location.getPostCode());
        System.out.println("location.getPlaces().get(0).getPlaceName() = " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getState() = " + location.getPlaces().get(0).getState());
    }
    // extract.path()   => We can get only one value. Doesn't allow us to assign an int to a String variable and extract classes.
    // extract.as(Location.class) => Allows us to get the entire response body as an object. Doesn't let us to separate any part of the body
    // extract.jsonPath. => Lets us to set an int to a String, extract the entire body and extract any part of the body we want. So we don't have to
    // create classes for the entire body

    @Test
    public void extractWithJsonPath(){
        Place place=given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .extract().jsonPath().getObject("places[0]", Place.class);


        System.out.println("place.getPlaceName()="+place.getPlaceName());
        System.out.println("place.getStateAbbreviation="+place.getStateAbbreviation());

    }

    @Test
    public void extractWithJsonPath2(){
        User user=given()
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().jsonPath().getObject("data[0]", User.class);
        System.out.println(user.getName());

    }







}
