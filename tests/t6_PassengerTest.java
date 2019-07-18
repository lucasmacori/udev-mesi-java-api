import com.udev.mesi.config.Database;
import config.APIConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import main.java.com.udev.mesi.entities.Passenger;
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
public class t6_PassengerTest {

    private static final String ROUTE = APIConfig.PATH + "passenger/";
    private static Passenger passenger;

    @AfterClass
    public static void clean() {
        if (passenger != null) {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            Query query = em.createQuery("FROM Passenger WHERE id = :id");
            query.setParameter("id", passenger.id);
            List<Passenger> passengers = query.getResultList();

            if (passengers.size() == 1) {
                em.getTransaction().begin();
                em.flush();
                em.remove(passengers.get(0));
                em.getTransaction().commit();
            }

            em.close();
            emf.close();
        }
    }

    @Test
    public void t1_create() {
        Response response = given()
                .urlEncodingEnabled(true)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", "test.passenger@test.net")
                .formParam("password", "password")
                .formParam("firstName", "Jean-Claude")
                .formParam("lastName", "Van Damme")
                .formParam("gender", "m")
                .formParam("birthday", "1960-10-18")
                .formParam("phoneNumber", "0666666666")
                .formParam("IDNumber", "0123456789")
                .post(ROUTE);

        assertEquals(201, response.getStatusCode());
        response
                .then()
                .assertThat().body("status", CoreMatchers.equalTo("OK"));

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du constructeur depuis la base de données
        Query query = em.createQuery("FROM Passenger WHERE isActive = true AND email = 'test.passenger@test.net' AND firstName = 'Jean-Claude' AND lastName = 'Van Damme' AND gender = 'M' AND phoneNumber = '0666666666' AND IDNumber = '0123456789'");
        List<Passenger> passengers = query.getResultList();

        assertEquals(1, passengers.size());

        if (passengers.size() == 1) {
            passenger = passengers.get(0);
        }

        em.close();
        emf.close();
    }

    @Test
    public void t2_readOne() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        if (passenger != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .get(ROUTE + passenger.id);
            assertEquals(200, response.getStatusCode());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse
                    .assertThat().body("status", Matchers.equalTo("OK"))
                    .assertThat().body("passenger", Matchers.notNullValue());

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
        Query query = em.createQuery("SELECT COUNT(p.id) FROM Passenger p WHERE isActive = true");
        List<Long> passengers = query.getResultList();
        Long size = passengers.get(0);

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
                    .assertThat().body("passengers", Matchers.notNullValue());
        }
        if (size > 1) {
            validatableResponse
                    .assertThat().body("passengers", Matchers.hasSize(size.intValue()));
        } else if (size == 0) {
            validatableResponse.assertThat().body("passengers", Matchers.nullValue());
        }

        em.close();
        emf.close();
    }

    @Test
    public void t4_update() {
        if (passenger != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", passenger.id)
                    .formParam("email", "test.passenger@test.com")
                    .formParam("password", "password1")
                    .formParam("firstName", "Bonnie")
                    .formParam("lastName", "Tyler")
                    .formParam("gender", "f")
                    .formParam("birthday", "1951-06-08")
                    .formParam("phoneNumber", "0777777777")
                    .formParam("IDNumber", "9876543210")
                    .put(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Passenger WHERE isActive = true AND email = 'test.passenger@test.com' AND firstName = 'Bonnie' AND lastName = 'Tyler' AND gender = 'F' AND phoneNumber = '0777777777' AND IDNumber = '9876543210'");
            List<Passenger> passengers = query.getResultList();

            assertEquals(1, passengers.size());

            if (passengers.size() == 1) {
                assertEquals(passenger.id, passengers.get(0).id);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t5_delete() {
        if (passenger != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .delete(ROUTE + passenger.id);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Passenger WHERE isActive = false AND id = :id");
            query.setParameter("id", passenger.id);
            List<Passenger> passengers = query.getResultList();

            assertEquals(1, passengers.size());

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }

}
