package restassuredtest;

/*   @author maramartins   */

import io.restassured.http.ContentType;
import org.junit.Test;
import restassuredtest.User;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;

public class SerializationObjectTest {

    @Test
    public void createUserViaObject() {
       User user = new User("User via Object", 30);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
        .when()
                .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("User via Object"))
                .body("age", is(30))
        ;
    }

    @Test
    public void createUserXmlSerialization() {
        User user = new User(" XML User",33);
        given()
                .log().all()
                .contentType(ContentType.XML)
                .body(user)
                .when()
                .post("https://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("XML User"))
                .body("user.age", is("33"))
        ;
    }
}
