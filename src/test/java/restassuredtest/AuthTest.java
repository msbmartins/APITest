package restassuredtest;

/*   @author maramartins   */

import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;

public class AuthTest {

    @Test
    public void getWeather() {
        given()
                .log().all()
                .queryParam("q", "Fortaleza, BR")
                .queryParam("appid", "6bf522accf878970c70a961aa5c2f061")
                .queryParam("units", "metric")
        .when()
                .get("http://api.openweathermap.org/data/2.5/weather")
        .then()
                .log().all()
                .statusCode(200)
                .body("name", is("Fortaleza"))
                .body("main.temp", greaterThan(24f))
        ;
    }

    @Test
    public void unauthorizedAccess() {
        given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(401)
        ;
    }

    @Test
    public void authorizedAccessBasicAuth1() {
        given()
                .log().all()
                .when()
                .get("https://admin:senha@restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void authorizedAccessBasicAuth2() {
        given()
                .log().all()
                .auth().basic("admin", "senha")
        .when()
                .get("https://restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void authorizedAccessBasicAuthChallenge() {
        given()
                .log().all()
                .auth().preemptive().basic("admin", "senha")
                .when()
                .get("https://restapi.wcaquino.me/basicauth2")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void authorizedAccessTokenJWT() {
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "marasbm@gmail.com");
        login.put("senha", "123456");

        //api login and get token
        String token = given()
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
        .when()
                .post("http://barrigarest.wcaquino.me/signin")
        .then()
                .log().all()
                .statusCode(200)
        .extract().path("token");

        ;

        //get accounts
        given()
                .log().all()
                .header("Authorization", "JWT " + token)
        .when()
                .get("http://barrigarest.wcaquino.me/contas")
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", hasItem("Conta de teste"))
        ;
    }

    @Test
    public void accessFromWebApplication() {
        //api login
        String cookie = given()
               .log().all()
                .formParam("email", "marasbm@gmail.com")
                .formParam("senha", "123456")
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
        .when()
                .post("http://seubarriga.wcaquino.me/logar")
        .then()
                .log().all()
                .statusCode(200)
        .extract().header("set-cookie")
        ;
        cookie = cookie.split("=")[1].split(";")[0];

        //get accounts
        String body = given()
                .log().all()
                .cookie("connect.sid", cookie)
        .when()
                .get("http://seubarriga.wcaquino.me/contas")
        .then()
                .log().all()
                .statusCode(200)
                .body("html.body.table.tbody.tr[0].td[0]", is("Conta de teste"))
                .extract().body().asString();
        ;
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
    }
}
