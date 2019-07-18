import com.udev.mesi.config.Database;
import config.APIConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import main.java.com.udev.mesi.entities.Manufacturer;
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
public class t1_ManufacturerTest {

    private static final String ROUTE = APIConfig.PATH + "manufacturer/";
    private static Manufacturer manufacturer;

    @AfterClass
    public static void clean() {
        if (manufacturer != null) {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            Query query = em.createQuery("FROM Manufacturer WHERE id = :id");
            query.setParameter("id", manufacturer.id);
            List<Manufacturer> manufacturers = query.getResultList();

            if (manufacturers.size() == 1) {
                em.getTransaction().begin();
                em.flush();
                em.remove(manufacturers.get(0));
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
                .formParam("name", "TestConstructor")
                .post(ROUTE);

        assertEquals(201, response.getStatusCode());
        response
                .then()
                .assertThat().body("status", CoreMatchers.equalTo("OK"));

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du constructeur depuis la base de données
        Query query = em.createQuery("FROM Manufacturer WHERE isActive = true AND name = 'TestConstructor'");
        List<Manufacturer> manufacturers = query.getResultList();

        assertEquals(1, manufacturers.size());

        if (manufacturers.size() == 1) {
            manufacturer = manufacturers.get(0);
        }

        em.close();
        emf.close();
    }

    @Test
    public void t2_readOne() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        if (manufacturer != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .get(ROUTE + manufacturer.id);
            assertEquals(200, response.getStatusCode());

            ValidatableResponse validatableResponse = response.then();

            validatableResponse
                    .assertThat().body("status", Matchers.equalTo("OK"))
                    .assertThat().body("manufacturer", Matchers.notNullValue());

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
        Query query = em.createQuery("SELECT COUNT(c.id) FROM Manufacturer c WHERE isActive = true");
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
                    .assertThat().body("manufacturers", Matchers.notNullValue());
        }
        if (size > 1) {
            validatableResponse
                    .assertThat().body("manufacturers", Matchers.hasSize(size.intValue()));
        } else if (size == 0) {
            validatableResponse.assertThat().body("manufacturers", Matchers.nullValue());
        }

        em.close();
        emf.close();
    }

    @Test
    public void t4_update() {
        if (manufacturer != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", manufacturer.id)
                    .formParam("name", "TestConstructorNew")
                    .put(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Manufacturer WHERE isActive = true AND name = 'TestConstructorNew'");
            List<Manufacturer> manufacturers = query.getResultList();

            assertEquals(1, manufacturers.size());

            if (manufacturers.size() == 1) {
                assertEquals(manufacturer.id, manufacturers.get(0).id);
                assertEquals("TestConstructorNew", manufacturers.get(0).name);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t5_delete() {
        if (manufacturer != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .delete(ROUTE + manufacturer.id);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Manufacturer WHERE isActive = false AND id = :id");
            query.setParameter("id", manufacturer.id);
            List<Manufacturer> manufacturers = query.getResultList();

            assertEquals(1, manufacturers.size());

            if (manufacturers.size() == 1) {
                assertEquals(manufacturer.id, manufacturers.get(0).id);
                assertEquals("TestConstructorNew", manufacturers.get(0).name);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }
}
