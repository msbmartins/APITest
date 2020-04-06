package restassuredtest;

/*   @author maramartins   */

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

public class GetValidationsUsingParams {

    @Test
    public void getUsersViaParams() {
        given()
                .log().all()
                .queryParam("fomart", "xml")
                .queryParam("another", "thing")
        .when()
                .get("http://restapi.wcaquino.me/v2/users")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML)
                .contentType(Matchers.containsString("UTF-a"))
        ;
    }


    @Test
    public void getUsersViaHeader() {
        given()
                .log().all()
                .accept(ContentType.XML)

        .when()
                .get("http://restapi.wcaquino.me/v2/users")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML)
        ;
    }
}
