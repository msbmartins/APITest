package restassuredtest;

/*   @author maramartins   */

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class DeserializingObjectTest {

    @Test
    public void createUserDeserializedObject() {
       User user = new User("User Deserialized", 30);

        User insertedUser = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
        .when()
                .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(201)
                .extract().body().as(User.class)
        ;
        assertEquals("User Deserialized", insertedUser.getName());
        Assert.assertThat(insertedUser.getAge(), is(30));
        Assert.assertThat(insertedUser.getId(), notNullValue());

    }
}
