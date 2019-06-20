import config.APIConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class ConstructorTest {

    private final String ROUTE = APIConfig.PATH + "constructor/";

    @Test
    public void create() {
        given()
                .urlEncodingEnabled(true)
                .param("name", "TestConstructor")
                .post(ROUTE)
                .then()
                .assertThat().body("status", Matchers.equalTo("OK"));
    }

    @Test
    public void read() {
        when()
                .get(ROUTE)
                .then()
                .assertThat().body("status", Matchers.equalTo("OK"))
                .assertThat().body("constructors", Matchers.notNullValue());
    }
}
