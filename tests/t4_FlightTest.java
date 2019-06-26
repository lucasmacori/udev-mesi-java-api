import com.udev.mesi.Database;
import config.APIConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import main.java.com.udev.mesi.entities.Flight;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class t4_FlightTest {

    private static final String ROUTE = APIConfig.PATH + "flight/";
    private static Flight flight;

    @AfterClass
    public static void clean() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("FROM Flight WHERE id = ( SELECT MAX(f.id) FROM Flight f)");
        List<Flight> flights = query.getResultList();

        if (flights.size() == 1) {
            em.getTransaction().begin();
            em.flush();
            em.remove(flights.get(0));
            em.getTransaction().commit();
        }

        em.close();
        emf.close();
    }

    @Test
    public void t1_create() {
        Response response = given()
                .urlEncodingEnabled(true)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .formParam("departureCity", "Westeros")
                .formParam("arrivalCity", "Gotham")
                .post(ROUTE);

        assertEquals(200, response.getStatusCode());
        response
                .then()
                .assertThat().body("status", CoreMatchers.equalTo("OK"));

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du constructeur depuis la base de données
        Query query = em.createQuery("FROM Flight WHERE isActive = true AND departureCity = 'Westeros' AND arrivalCity = 'Gotham'");
        List<Flight> flights = query.getResultList();

        assertEquals(1, flights.size());

        if (flights.size() == 1) {
            flight = flights.get(0);
        }

        em.close();
        emf.close();
    }

    @Test
    public void t2_read() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du nombre de constructors dans la base de données
        Query query = em.createQuery("SELECT COUNT(c.id) FROM Flight c WHERE isActive = true");
        List<Long> constructors = query.getResultList();
        Long size = constructors.get(0);

        Response response = given()
                .urlEncodingEnabled(true)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .get(ROUTE);
        assertEquals(200, response.getStatusCode());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.assertThat().body("status", Matchers.equalTo("OK"));
        if (size > 0) {
            validatableResponse
                    .assertThat().body("flights", Matchers.notNullValue())
                    .assertThat().body("flights", Matchers.hasSize(size.intValue()));
        } else {
            validatableResponse
                    .assertThat().body("flights", Matchers.nullValue());
        }

        em.close();
        emf.close();
    }
}
