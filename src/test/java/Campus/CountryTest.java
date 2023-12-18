package Campus;

import Campus.Models.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class CountryTest {
    Cookies cookies;
    Response responseCreateCountry;
    String name;
    String code;

    
    public String randomCountryName(){
        return RandomStringUtils.randomAlphabetic(8);
    }
    public String randomCountryCode(){
        return RandomStringUtils.randomAlphabetic(3);
    }

    @BeforeClass
    public void login(){
        //{"username": "turkeyts", "password": "TechnoStudy123", "rememberMe": true}
        baseURI="https://test.mersys.io";
        Map<String,String> credentials=new HashMap<>();
        credentials.put("username","turkeyts");
        credentials.put("password","TechnoStudy123");
        credentials.put("rememberMe","true");


      cookies= given()
                .body(credentials)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .log().body()
                .extract().response().getDetailedCookies();
    }
    Country country;

    @Test()
    public void createCountry(){
        country=new Country();
        country.setName(randomCountryName());
        country.setCode(randomCountryCode());
        responseCreateCountry=given()
                .body(country)
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when()
                .post("/school-service/api/countries")
                .then()
                .statusCode(201)
                .log().body()
                .extract().response();
        name=responseCreateCountry.jsonPath().getString("name");
        code=responseCreateCountry.jsonPath().getString("code");
    }

    /**Write create country negative test*/

    @Test
    public void createCountryNegative(){
        Country country=new Country();
        country.setName(name);
        country.setCode(code);
        given()
                .body(country)
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when()
                .post("/school-service/api/countries")
                .then()
                .statusCode(400)
                .log().body();
    }

    /**update the country we created*/

    @Test(dependsOnMethods = "createCountry")
    public void updateTheCountry(){
       country.setId(responseCreateCountry.jsonPath().getString("id"));
       country.setName(randomCountryName());
       country.setCode(randomCountryCode());
        given()
                .body(country)
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when()
                .put("/school-service/api/countries")
                .then()
                .statusCode(200)
                .log().body();

    }

    @Test(dependsOnMethods = "createCountry")
    public void deleteCountry(){
        given()
                .pathParam("countryId",responseCreateCountry.jsonPath().getString("id"))
                .cookies(cookies)
                .when()
                .delete("/school-service/api/countries/{countryId}")
                .then()
                .statusCode(200)
                .log().body();
    }
    @Test(dependsOnMethods = {"createCountry","deleteCountry"})
    public void deleteCountryNegative(){
        given()
                .pathParam("countryId",responseCreateCountry.jsonPath().getString("id"))
                .cookies(cookies)
                .when()
                .delete("/school-service/api/countries/{countryId}")
                .then()
                .statusCode(400)
                .log().body();
    }


}
