import com.udev.mesi.Database;
import com.udev.mesi.config.APIFormat;
import config.APIConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import main.java.com.udev.mesi.entities.*;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class t7_ReservationTest {

    private static final String ROUTE = APIConfig.PATH + "reservation/";
    private static Reservation reservation;
    private static Long flightDetails_id;
    private static Long passenger_id;

    @BeforeClass
    public static void prepare() throws ParseException {
        t5_FlightDetailsTest.prepare();

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du vol et de l'avion
        Query query = em.createQuery("FROM Flight WHERE id = ( SELECT MAX(f.id) FROM Flight f WHERE f.isActive = true)");
        List<Flight> flights = query.getResultList();

        query = em.createQuery("FROM Plane WHERE id = ( SELECT MAX(p.id) FROM Plane p WHERE p.isActive = true)");
        List<Plane> planes = query.getResultList();

        if (flights.size() == 1 && planes.size() == 1) {

            em.getTransaction().begin();

            // Création d'un détail de vol
            Flight flight = flights.get(0);
            Plane plane = planes.get(0);
            FlightDetails flightDetails = new FlightDetails();
            flightDetails.departureDateTime = APIFormat.DATETIME_FORMAT.parse("2019-07-07 09:00:00");
            flightDetails.arrivaleDateTime = APIFormat.DATETIME_FORMAT.parse("2019-07-08 11:00:00");
            flightDetails.plane = plane;
            flightDetails.flight = flight;
            flightDetails.isActive = true;

            // Persistence du détail de vol
            em.persist(flightDetails);
            em.flush();

            // Récupération de l'id du détail de vol
            query = em.createQuery("SELECT MAX(id) FROM FlightDetails");
            flightDetails_id = (Long) query.getResultList().get(0);

            // Création d'un passager
            Passenger passenger = new Passenger();
            passenger.firstName = "Serge";
            passenger.lastName = "Karamazov";
            passenger.email = "s.karamazov@security.com";
            passenger.birthday = APIFormat.DATE_FORMAT.parse("1958-11-24");
            passenger.gender = 'M';
            passenger.hash = "hash";
            passenger.phoneNumber = "0000000000";
            passenger.IDNumber = "8888888888888";
            passenger.isActive = true;

            // Persistence du passager
            em.persist(passenger);
            em.flush();

            // Récupération de l'id du passager
            query = em.createQuery("SELECT id FROM Passenger WHERE email = 's.karamazov@security.com'");
            passenger_id = (Long) query.getResultList().get(0);

            em.getTransaction().commit();
        }
    }

    @AfterClass
    public static void clean() {
        t5_FlightDetailsTest.clean();

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Suppression de la réservation si existante
        if (reservation != null) {
            Reservation reservation1 = em.find(Reservation.class, reservation.id);
            em.remove(reservation1);
        }

        em.flush();

        // Suppression du passager
        Passenger passenger = em.find(Passenger.class, passenger_id);
        em.remove(passenger);

        em.flush();
        em.getTransaction().commit();
    }

    @Test
    public void t1_create() {
        Response response = given()
                .urlEncodingEnabled(true)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .formParam("reservationDate", "2019-07-07")
                .formParam("reservationClass", "B")
                .formParam("flightDetails", flightDetails_id)
                .formParam("passenger", passenger_id)
                .post(ROUTE);

        assertEquals(201, response.getStatusCode());
        response
                .then()
                .assertThat().body("status", CoreMatchers.equalTo("OK"));

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération de la réservation depuis la base de données
        Query query = em.createQuery("SELECT r FROM Reservation r, FlightDetails fd, Passenger p WHERE r.isActive = true AND fd.id = r.flightDetails AND p.id = r.passenger AND fd.id = :fd_id AND p.id = :p_id");
        query.setParameter("fd_id", flightDetails_id);
        query.setParameter("p_id", passenger_id);
        List<Reservation> reservations = query.getResultList();

        assertEquals(1, reservations.size());

        if (reservations.size() == 1) {
            reservation = reservations.get(0);
        }

        em.close();
        emf.close();
    }

    @Test
    public void t2_readOne() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        if (reservation != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .get(ROUTE + reservation.id);
            assertEquals(200, response.getStatusCode());

            ValidatableResponse validatableResponse = response.then();

            validatableResponse
                    .assertThat().body("status", Matchers.equalTo("OK"))
                    .assertThat().body("reservation", Matchers.notNullValue());

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

        // Récupération du nombre de réservations dans la base de données
        Query query = em.createQuery("SELECT COUNT(r.id) FROM Reservation r WHERE r.isActive = true");
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
            validatableResponse.assertThat().body("reservations", Matchers.nullValue());
        } else if (size == 1) {
            validatableResponse.assertThat().body("reservations", Matchers.notNullValue());
        } else if (size > 1) {
            validatableResponse.assertThat().body("reservations", Matchers.hasSize(size.intValue()));
        }

        em.close();
        emf.close();
    }

    @Test
    public void t4_update() {
        if (reservation != null) {

            Query query;

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", reservation.id)
                    .formParam("reservationClass", 'E')
                    .put(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Vérification de la modification
            query = em.createQuery("SELECT r FROM Reservation r, FlightDetails fd, Passenger p WHERE r.isActive = true AND r.id = :id AND fd.id = r.flightDetails AND p.id = r.passenger AND fd.id = :fd_id AND p.id = :p_id");
            query.setParameter("id", reservation.id);
            query.setParameter("fd_id", flightDetails_id);
            query.setParameter("p_id", passenger_id);
            List<FlightDetails> flightDetails = query.getResultList();

            assertEquals(1, flightDetails.size());

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t5_delete() {
        if (reservation != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", reservation.id)
                    .delete(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Reservation WHERE isActive = false AND id = :id");
            query.setParameter("id", reservation.id);
            List<Reservation> reservations = query.getResultList();

            assertEquals(1, reservations.size());

            if (reservations.size() == 1) {
                assertEquals(reservation.id, reservations.get(0).id);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }
}
