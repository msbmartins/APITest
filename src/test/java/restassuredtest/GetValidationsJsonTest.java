package restassuredtest;

/*   @author maramartins   */

import static io.restassured.RestAssured.*;

import static java.lang.Integer.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetValidationsJsonTest {

    @Test
    public void getRestApiAquino() {
        Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
        response.then().statusCode(200);
    }

    @Test
    public void getRestApiAquino2() {
        get("http://restapi.wcaquino.me/ola").then().statusCode(200);
    }

    @Test
    public void getRestApiAquino3() {
        given().
                when().
                get("http://restapi.wcaquino.me/ola").
                then().
                statusCode(200);
    }

    @Test
    public void knowingMatcheersHamcrest() {
        assertThat("Maria", Matchers.is("Maria"));
        assertThat(128, Matchers.is(128));
        assertThat(128, Matchers.isA(Integer.class));
        assertThat(128d, Matchers.isA(Double.class));
        assertThat(128d, Matchers.greaterThan(120d));
        assertThat(128d, Matchers.lessThan(130d));

        List<Integer> oddNumbers = Arrays.asList(1, 3, 5, 7, 9);
        assertThat(oddNumbers, hasSize(5));
        assertThat(oddNumbers, contains(1, 3, 5, 7, 9));
        assertThat(oddNumbers, containsInAnyOrder(7, 3, 5, 1, 9));
        assertThat(oddNumbers, hasItem(9));
        assertThat(oddNumbers, hasItems(9, 3));

        assertThat("Maria", is(not("Joao")));
        assertThat("Maria", anyOf(is("Maria"), is("Catarina")));
        assertThat("Catarina", allOf(startsWith("Cat"), endsWith("ina"), containsString("ar")));
    }

    @Test
    public void bodyValidation() {
        given()
        .when()
                .get("http://restapi.wcaquino.me/ola")
        .then()
                .statusCode(200)
                .body(is("Ola Mundo!"))
                .body(containsString("Mundo"))
                .body(is(not(nullValue())));
    }

    @Test
    public void jsonValidationLevelOne_1(){
        given()
        .when()
                .get("http://restapi.wcaquino.me/users/1")
        .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", containsString("Silva"))
                .body("age", greaterThan(18));
    }

    @Test
    public void jsonValidationLevelOne_2(){
        Response response = RestAssured.request(Method.GET,"http://restapi.wcaquino.me/users/1");

        //path (Json or Xml)
        assertEquals(valueOf(1), response.path("id"));
        assertEquals(valueOf(1), response.path("%s", "id"));

        //JsonPath
        JsonPath jsonPath = new JsonPath(response.asString());
        assertEquals(1, jsonPath.getInt("id"));

        //From
        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);

    }

    @Test
    public void jsonValidationLevelTwo_1(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
                .body("name", containsString("Maria"))
                .body("endereco.rua", is("Rua dos bobos"))
                .body("endereco.numero", is(0));

    }

    @Test
    public void jsonValidationList(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/3")
                .then()
                .statusCode(200)
                .body("name", containsString("Ana"))
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name", is("Luizinho"))
                .body("filhos.name", hasItem("Luizinho"))
                .body("filhos.name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void jsonValidationNonExistentUser(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/4")
                .then()
                .statusCode(404)
                .body("error", is("Usuário inexistente"));
    }

    @Test
    public void jsonValidationListAtRoot(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
                .body("age[1]", is(25))
                .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
                .body("salary", contains(1234.5677f, 2500, null));
    }

    @Test
    public void jsonValidationAdvanced(){
        given()
        .when()
                .get("http://restapi.wcaquino.me/users")
        .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("age.findAll{it <= 25}.size()", is(2))
                .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
                .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))     //last record
                .body("find{it.age <= 25}.name", is("Maria Joaquina"))        // returns the first
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina","Ana Júlia"))
                .body("findAll{it.name.length() > 10}.name",  hasItems("João da Silva", "Maria Joaquina"))
                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
        ;
    }

    @Test
    public void jsonValidationWithJsonPath(){
        ArrayList<String> names =
        given()
        .when()
                .get("http://restapi.wcaquino.me/users")
        .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}")
        ;
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina"));
        Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina.toUpperCase()");
    }
}

