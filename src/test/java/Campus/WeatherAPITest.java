package Campus;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class WeatherAPITest {
    //url:http://api.weatherapi.com/v1/current.json
    // key: key
    //value:cc0f3ab150044944ae4213458231407
    @Test
    public void apiWeatherTest(){
        Response response=given()
                .pathParam("q","Indianapolis")
                .pathParam("aqi","yes")
                .pathParams("key","cc0f3ab150044944ae4213458231407")
                .log().uri()
                .when()
                .get("http://api.weatherapi.com/v1/current.json?key={key}&q={q}&aqi={aqi}")
                .then()
                .statusCode(200)
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        double tempC = jsonPath.getDouble("current.temp_c");
        double tempF = jsonPath.getDouble("current.temp_f");

        System.out.println("Temperature in Celsius: " + tempC);
        System.out.println("Temperature in Fahrenheit: " + tempF);
    }
}
