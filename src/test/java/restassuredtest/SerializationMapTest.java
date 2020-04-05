package restassuredtest;

/*   @author maramartins   */

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;

public class SerializationMapTest {

    @Test
    public void createUserWithMap() {
        Map<String, Object> params = new HashMap<String, Object>(); //list to store pairs
        params.put("name", "User Via MAP");
        params.put("age", 25);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("User Via MAP"))
                .body("age", is(25))
        ;
    }
}
