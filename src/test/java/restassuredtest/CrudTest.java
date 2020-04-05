package restassuredtest;

/*   @author maramartins   */

import io.restassured.http.ContentType;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class CrudTest {

    @Test
    public void validCreateUser() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"João da Silva\",\"age\": 30,\"salary\": 1234.5678}")
        .when()
                .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Jose"))
                .body("age", is(50))
        ;
    }

    @Test
    public void invalidCreateUser() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"age\": 30,\"salary\": 1234.5678}")
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(400)
                .body("id", is(nullValue()))
                .body("error", is("Name é um atributo obrigatório"))
        ;
    }

    @Test
    public void validCreateUserXml() {
        given()
                .log().all()
                .contentType(ContentType.XML)
                .body("<user><name>Yves da Silva</name><age>2</age><salary>1234.5678</salary></user>")
                .when()
                .post("https://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Yves da Silva"))
                .body("user.age", is("2"))
        ;
    }

    @Test
    public void updateUser() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Yves Saint Laurent\",\"age\": 30}")
        .when()
                .put("https://restapi.wcaquino.me/users/1")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Yves Saint Laurent"))
        ;
    }

    @Test
    public void updateUserParameterizableUrl_1() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Yves Saint Laurent\",\"age\": 30}")
        .when()
                .put("https://restapi.wcaquino.me/{entity}/{userId}", "users", "1")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Yves Saint Laurent"))
        ;
    }


    @Test
    public void updateUserParameterizableUrl_2() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Yves Saint Laurent\",\"age\": 30}")
                .pathParam("entity", "users")
                .pathParam("userid", "1")
        .when()
                .put("https://restapi.wcaquino.me/{entity}/{userid}")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Yves Saint Laurent"))
        ;
    }

    @Test
    public void deleteValidUser() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .pathParam("entity", "users")
                .pathParam("userid", "1")
        .when()
                .delete("https://restapi.wcaquino.me/{entity}/{userid}")
        .then()
                .log().all()
                .statusCode(204)
        ;
    }

    @Test
    public void deleteNonExistentUser() {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .pathParam("entity", "users")
                .pathParam("userid", "2")
                .when()
                .delete("https://restapi.wcaquino.me/{entity}/{userid}")
                .then()
                .log().all()
                .statusCode(204)
        ;
    }
}

