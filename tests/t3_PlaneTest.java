import com.udev.mesi.Database;
import config.APIConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import main.java.com.udev.mesi.entities.Constructor;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class t3_PlaneTest {

    private static final String ROUTE = APIConfig.PATH + "plane/";
    static Plane plane;
    static int modelCount = 0;
    static int constructorCount = 0;

    @BeforeClass
    public static void prepare() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Création d'un constructeur si aucun n'existe
        Query query = em.createQuery("SELECT COUNT(c.id) FROM Constructor c WHERE isActive = true");
        int count = Integer.parseInt(query.getResultList().get(0).toString());

        if (count == 0) {
            Constructor constructor = new Constructor();
            constructor.name = "TestConstructor";
            constructor.isActive = true;
            em.flush();
            em.persist(constructor);
            constructorCount++;
        }

        // Création d'un model si aucun n'existe
        query = em.createQuery("SELECT COUNT(m.id) FROM Model m WHERE isActive = true");
        count = Integer.parseInt(query.getResultList().get(0).toString());

        while (count < 2) {

            // Récupération du dernier constructeur
            query = em.createQuery("FROM Constructor WHERE id = ( SELECT MAX(c.id) FROM Constructor c WHERE isActive = true)");
            List<Constructor> constructors = query.getResultList();

            if (constructors.size() == 1) {
                Model model = new Model();
                model.name = "TestModel" + count;
                model.countBusinessSlots = 300;
                model.countEcoSlots = 150;
                model.constructor = constructors.get(0);
                model.isActive = true;
                em.flush();
                em.persist(model);
                modelCount++;
            } else {
                fail("Aucun constructeur n'a pu être créé");
            }

            count++;
        }

        em.getTransaction().commit();
    }

    @AfterClass
    public static void clean() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Query query;

        // Suppression de l'avion
        query = em.createQuery("DELETE FROM Plane WHERE ARN = 'ARN-999'");
        query.executeUpdate();
        em.flush();

        // Suppression des modèles
        while (modelCount > 0) {
            // Récupération du modèle
            query = em.createQuery("DELETE FROM Model WHERE id = ( SELECT MAX(m.id) FROM Model m )");
            query.executeUpdate();
            em.flush();
            modelCount--;
        }

        // Suppression du constructeur
        while (constructorCount > 0) {
            query = em.createQuery("DELETE FROM Constructor WHERE name = 'TestConstructor' AND id = (SELECT MAX(c.id) FROM Constructor c)");
            query.executeUpdate();
            em.flush();
            constructorCount--;
        }

        em.getTransaction().commit();

        em.close();
        emf.close();
    }

    @Test
    public void t1_create() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("FROM Model WHERE id = ( SELECT MAX(m.id) FROM Model m WHERE isActive = true)");
        List<Model> models = query.getResultList();

        if (models.size() == 1) {
            Model model = models.get(0);

            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("ARN", "ARN-999")
                    .formParam("model", model.id)
                    .post(ROUTE);

            assertEquals(201, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", CoreMatchers.equalTo("OK"));

            // Récupération de l'avion depuis la base de données
            query = em.createQuery("FROM Plane WHERE isActive = true AND ARN = 'ARN-999'");
            List<Plane> planes = query.getResultList();

            assertEquals(1, models.size());

            if (planes.size() == 1) {
                plane = planes.get(0);
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

        if (plane != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .get(ROUTE + plane.ARN);
            assertEquals(200, response.getStatusCode());

            ValidatableResponse validatableResponse = response.then();

            validatableResponse
                    .assertThat().body("status", Matchers.equalTo("OK"))
                    .assertThat().body("plane", Matchers.notNullValue());

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
        Query query = em.createQuery("SELECT COUNT(p.id) FROM Plane p WHERE isActive = true");
        List<Long> models = query.getResultList();
        Long size = models.get(0);

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
            validatableResponse.assertThat().body("planes", Matchers.nullValue());
        } else if (size == 1) {
            validatableResponse.assertThat().body("planes", Matchers.notNullValue());
        } else if (size > 1) {
            validatableResponse.assertThat().body("planes", Matchers.hasSize(size.intValue()));
        }

        em.close();
        emf.close();
    }

    @Test
    public void t4_update() {
        if (plane != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("ARN", plane.ARN)
                    .formParam("isUnderMaintenance", "true")
                    .put(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Plane WHERE isActive = true AND isUnderMaintenance = true");
            List<Plane> planes = query.getResultList();

            assertEquals(1, planes.size());

            if (planes.size() == 1) {
                assertEquals(plane.ARN, planes.get(0).ARN);
                assertTrue(planes.get(0).isUnderMaintenance);
                assertTrue(planes.get(0).isActive);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t5_delete() {
        if (plane != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("ARN", plane.ARN)
                    .delete(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Plane WHERE isActive = false AND ARN = :ARN");
            query.setParameter("ARN", plane.ARN);
            List<Plane> planes = query.getResultList();

            assertEquals(1, planes.size());

            if (planes.size() == 1) {
                assertEquals(plane.ARN, planes.get(0).ARN);
                assertFalse(planes.get(0).isActive);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }
}
