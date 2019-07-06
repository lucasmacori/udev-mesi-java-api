import com.udev.mesi.Database;
import com.udev.mesi.config.APIFormat;
import config.APIConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import main.java.com.udev.mesi.entities.Flight;
import main.java.com.udev.mesi.entities.FlightDetails;
import main.java.com.udev.mesi.entities.Model;
import main.java.com.udev.mesi.entities.Plane;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.text.ParseException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class t5_FlightDetailsTest {

    private static final String ROUTE = APIConfig.PATH + "flightDetails/";
    private static FlightDetails flightDetails;
    private static int flightCount = 0;
    private static int planeCount = 0;

    @BeforeClass
    public static void prepare() {
        t3_PlaneTest.prepare();

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        Query query;
        int count;

        em.getTransaction().begin();

        // Création d'un vol si aucun n'existe
        query = em.createQuery("SELECT COUNT(f.id) FROM Flight f WHERE f.isActive = true");
        count = Integer.parseInt(query.getResultList().get(0).toString());

        while (count < 2) {
            // Création d'un vol
            Flight flight = new Flight();
            flight.departureCity = "Westeros";
            flight.arrivalCity = "Gotham";
            flight.isActive = true;

            em.persist(flight);
            em.flush();

            count++;
            flightCount++;
        }

        // Création d'un avion si aucun n'existe
        query = em.createQuery("SELECT COUNT(p.id) FROM Plane p WHERE p.isActive = true");
        count = Integer.parseInt(query.getResultList().get(0).toString());

        while (count < 2) {

            // Récupération d'un modèle
            query = em.createQuery("FROM Model WHERE id = ( SELECT MAX(id) FROM Model m )");
            List<Model> models = query.getResultList();

            if (models.size() != 1) {
                fail("Aucun modèle n'existe, le test n'a pas pu s'effectuer");
            }

            // Création d'un avion
            Plane plane = new Plane();
            plane.ARN = "ARN-9991";
            plane.model = models.get(0);
            plane.isUnderMaintenance = false;
            plane.isActive = true;

            em.persist(plane);
            em.flush();

            count++;
            flightCount++;
        }

        em.getTransaction().commit();
    }

    @AfterClass
    public static void clean() {
        t3_PlaneTest.clean();

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Query query;

        // Suppression du vol
        while (flightCount > 0) {
            query = em.createQuery("DELETE FROM Flight WHERE departureCity = 'Westeros' AND arrivalCity = 'Gotham' AND id = (SELECT MAX(f.id) FROM Flight f)");
            query.executeUpdate();
            em.flush();
            flightCount--;
        }

        // Suppression de l'avion
        while (planeCount > 0) {
            query = em.createQuery("DELETE FROM Plane WHERE ARN = 'ARN-9991' AND id = (SELECT MAX(p.id) FROM Plane p)");
            query.executeUpdate();
            em.flush();
            planeCount--;
        }

        // Suppression du details de vol si ce n'est pas déjà fait
        if (flightDetails != null) {
            query = em.createQuery("DELETE FROM FlightDetails fd WHERE fd.id = :id");
            query.setParameter("id", flightDetails.id);
            query.executeUpdate();
            em.flush();
        }

        em.getTransaction().commit();
    }

    @Test
    public void t1_create() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("FROM Flight WHERE id = ( SELECT MAX(f.id) FROM Flight f WHERE f.isActive = true)");
        List<Flight> flights = query.getResultList();

        query = em.createQuery("FROM Plane WHERE id = ( SELECT MAX(p.id) FROM Plane p WHERE p.isActive = true)");
        List<Plane> planes = query.getResultList();

        if (flights.size() == 1 && planes.size() == 1) {
            Flight flight = flights.get(0);
            Plane plane = planes.get(0);

            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("plane", plane.ARN)
                    .formParam("flight", flight.id)
                    .formParam("departureDateTime", "2019-06-29 06:30:00")
                    .formParam("arrivalDateTime", "2019-06-30 04:15:00")
                    .post(ROUTE);

            assertEquals(201, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", CoreMatchers.equalTo("OK"));

            // Récupération du détail de vol depuis la base de données
            query = em.createQuery("SELECT fd FROM FlightDetails fd, Plane p, Flight f WHERE p.ARN = fd.plane AND f.id = fd.flight AND fd.isActive = true AND fd.departureDateTime = '2019-06-29 06:30:00' AND fd.arrivaleDateTime = '2019-06-30 04:15:00'");
            List<FlightDetails> flightDetailss = query.getResultList();

            assertEquals(1, flightDetailss.size());

            if (flightDetailss.size() == 1) {
                flightDetails = flightDetailss.get(0);
            }
        }

        em.close();
        emf.close();
    }

    @Test
    public void t2_readOne() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        if (flightDetails != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .get(ROUTE + flightDetails.id);
            assertEquals(200, response.getStatusCode());

            ValidatableResponse validatableResponse = response.then();

            validatableResponse
                    .assertThat().body("status", Matchers.equalTo("OK"))
                    .assertThat().body("flightDetails", Matchers.notNullValue());

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

        // Récupération du nombre de détails de vol dans la base de données
        Query query = em.createQuery("SELECT COUNT(fd.id) FROM FlightDetails fd WHERE fd.isActive = true");
        List<Long> flightDetails = query.getResultList();
        Long size = flightDetails.get(0);

        Response response = given()
                .urlEncodingEnabled(true)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .get(ROUTE);
        assertEquals(200, response.getStatusCode());
        ValidatableResponse validatableResponse = response.then();

        validatableResponse
                .assertThat().body("status", Matchers.equalTo("OK"));

        if (size == 0) {
            validatableResponse.assertThat().body("flightDetails", Matchers.nullValue());
        } else if (size == 1) {
            validatableResponse.assertThat().body("flightDetails", Matchers.notNullValue());
        } else if (size > 1) {
            validatableResponse.assertThat().body("flightDetails", Matchers.hasSize(size.intValue()));
        }

        em.close();
        emf.close();
    }

    @Test
    public void t4_update() {
        if (flightDetails != null) {

            Query query;

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération de l'autre vol
            query = em.createQuery("FROM Flight WHERE id < ( SELECT MAX(f.id) FROM Flight f) ORDER BY id DESC");
            query.setMaxResults(1);
            List<Flight> flights = query.getResultList();
            if (flights.size() != 1) {
                fail("Il n'y a pas assez de vols dans la base de données pour le test");
            }

            // Récupération de l'autre avion
            query = em.createQuery("FROM Plane WHERE id < ( SELECT MAX(p.id) FROM Plane p) ORDER BY id DESC");
            query.setMaxResults(1);
            List<Plane> planes = query.getResultList();
            if (planes.size() != 1) {
                fail("Il n'y a pas assez d'avions dans la base de données pour le test");
            }

            Flight flight = flights.get(0);
            Plane plane = planes.get(0);

            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", flightDetails.id)
                    .formParam("flight", flight.id)
                    .formParam("plane", plane.ARN)
                    .formParam("departureDateTime", "2019-06-30 06:30:00")
                    .formParam("arrivalDateTime", "2019-07-01 06:30:00")
                    .put(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Vérification de la modification
            query = em.createQuery("SELECT fd FROM FlightDetails fd, Flight f, Plane p WHERE f.id = fd.flight AND p.ARN = fd.plane AND fd.isActive = true AND fd.departureDateTime = '2019-06-30 06:30:00' AND fd.arrivaleDateTime = '2019-07-01 06:30:00'");
            List<FlightDetails> flightDetails = query.getResultList();

            assertEquals(1, flightDetails.size());

            try {
                if (flightDetails.size() == 1) {
                    assertEquals(t5_FlightDetailsTest.flightDetails.id, flightDetails.get(0).id);
                    assertEquals(flight.id, flightDetails.get(0).flight.id);
                    assertEquals(plane.ARN, flightDetails.get(0).plane.ARN);
                    assertEquals(APIFormat.DATETIME_FORMAT.parse("2019-06-30 06:30:00"), APIFormat.DATETIME_FORMAT.parse(flightDetails.get(0).departureDateTime.toString()));
                    assertEquals(APIFormat.DATETIME_FORMAT.parse("2019-07-01 06:30:00"), APIFormat.DATETIME_FORMAT.parse(flightDetails.get(0).arrivaleDateTime.toString()));

                    t5_FlightDetailsTest.flightDetails.departureDateTime = APIFormat.DATETIME_FORMAT.parse("2019-06-30 06:30:00");
                    t5_FlightDetailsTest.flightDetails.arrivaleDateTime = APIFormat.DATETIME_FORMAT.parse("2019-07-01 06:30:00");
                }
            } catch (ParseException e) {
                fail("Impossible de convertir la date de sortie");
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t5_delete() {
        if (flightDetails != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", flightDetails.id)
                    .delete(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM FlightDetails WHERE isActive = false AND id = :id");
            query.setParameter("id", flightDetails.id);
            List<FlightDetails> flightDetails = query.getResultList();

            assertEquals(1, flightDetails.size());

            if (flightDetails.size() == 1) {
                assertEquals(t5_FlightDetailsTest.flightDetails.id, flightDetails.get(0).id);
                assertEquals(t5_FlightDetailsTest.flightDetails.departureDateTime, flightDetails.get(0).departureDateTime);
                assertEquals(t5_FlightDetailsTest.flightDetails.arrivaleDateTime, flightDetails.get(0).arrivaleDateTime);
                assertFalse(flightDetails.get(0).isActive);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }
}
