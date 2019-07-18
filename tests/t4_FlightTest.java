import com.udev.mesi.config.Database;
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
import static org.junit.Assert.fail;

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

        assertEquals(201, response.getStatusCode());
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
    public void t2_readOne() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        if (flight != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .get(ROUTE + flight.id);
            assertEquals(200, response.getStatusCode());

            ValidatableResponse validatableResponse = response.then();

            validatableResponse
                    .assertThat().body("status", Matchers.equalTo("OK"))
                    .assertThat().body("flight", Matchers.notNullValue());

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête GET " + ROUTE + "/pk");
        }
    }

    @Test
    public void t3_read() {
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
            validatableResponse.assertThat().body("flights", Matchers.notNullValue());
        }
        if (size > 1) {
            validatableResponse.assertThat().body("flights", Matchers.hasSize(size.intValue()));
        } else if (size == 0) {
            validatableResponse.assertThat().body("flights", Matchers.nullValue());
        }

        em.close();
        emf.close();
    }

    @Test
    public void t4_update() {
        if (flight != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", flight.id)
                    .formParam("departureCity", "Westeros1")
                    .formParam("arrivalCity", "Gotham1")
                    .put(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Flight WHERE isActive = true AND id = :id");
            query.setParameter("id", flight.id);
            List<Flight> flights = query.getResultList();

            assertEquals(1, flights.size());

            if (flights.size() == 1) {
                assertEquals(flight.id, flights.get(0).id);
                assertEquals("Westeros1", flights.get(0).departureCity);
                assertEquals("Gotham1", flights.get(0).arrivalCity);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t5_delete() {
        if (flight != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .delete(ROUTE + flight.id);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Flight WHERE isActive = false AND id = :id");
            query.setParameter("id", flight.id);
            List<Flight> flights = query.getResultList();

            assertEquals(1, flights.size());

            if (flights.size() == 1) {
                assertEquals(flight.id, flights.get(0).id);
                assertEquals("Westeros1", flights.get(0).departureCity);
                assertEquals("Gotham1", flights.get(0).arrivalCity);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }
}
