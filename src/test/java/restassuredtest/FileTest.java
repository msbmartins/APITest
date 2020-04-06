package restassuredtest;

/*   @author maramartins   */

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.lessThan;

public class FileTest {

    @Test
    public void uploadFileWithoutFile(){
        given()
                .log().all()
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .statusCode(404)
                .body("error", is("Arquivo não enviado"))
        ;
    }

    @Test
    public void uploadFile(){
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/users.pdf") )
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is("users.pdf"))
        ;
    }

    @Test
    public void uploadFileSizeOverLimit(){
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/ECR2018_C-0251.pdf") )
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .time(lessThan(1700l))
                .statusCode(413)
        ;
    }

    @Test
    public void downloadFile() throws IOException {
        byte[] image = given()
                .log().all()
        .when()
                .get("http://restapi.wcaquino.me/download")
        .then()
                .log().all()
                .statusCode(200)
                .extract().asByteArray()
        ;
        File images = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(images);
        out.write(image);
        out.close();
        Assert.assertThat(images.length(), lessThan(100000L));
    }
}
