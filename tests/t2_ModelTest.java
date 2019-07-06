import com.udev.mesi.Database;
import config.APIConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import main.java.com.udev.mesi.entities.Constructor;
import main.java.com.udev.mesi.entities.Model;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class t2_ModelTest {

    private static final String ROUTE = APIConfig.PATH + "model/";
    static Model model;


    @BeforeClass
    public static void prepare() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT COUNT(c.id) FROM Constructor c WHERE isActive = true");
        int count = Integer.parseInt(query.getResultList().get(0).toString());

        if (count == 0) {
            Constructor constructor = new Constructor();
            constructor.name = "TestConstructor";
            constructor.isActive = true;
            em.getTransaction().begin();
            em.flush();
            em.persist(constructor);
            em.getTransaction().commit();
        }
    }

    @AfterClass
    public static void clean() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Suppression du modèle
        Query query = em.createQuery("FROM Model WHERE id = ( SELECT MAX(c.id) FROM Model c)");
        List<Model> models = query.getResultList();

        if (models.size() == 1) {
            em.remove(models.get(0));
            em.flush();

            // Suppression du constructeur
            query = em.createQuery("DELETE FROM Constructor WHERE name = 'TestConstructor' AND id = (SELECT MAX(c.id) FROM Constructor c)");
            query.executeUpdate();
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

        Query query = em.createQuery("FROM Constructor WHERE id = ( SELECT MAX(c.id) FROM Constructor c WHERE isActive = true)");
        List<Constructor> constructors = query.getResultList();

        if (constructors.size() == 1) {
            Constructor constructor = constructors.get(0);

            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("name", "TestModel")
                    .formParam("constructor", constructor.id)
                    .formParam("countEcoSlots", 300)
                    .formParam("countBusinessSlots", 100)
                    .post(ROUTE);

            assertEquals(201, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", CoreMatchers.equalTo("OK"));

            // Récupération du constructeur depuis la base de données
            query = em.createQuery("FROM Model WHERE isActive = true AND name = 'TestModel' AND countEcoSlots = 300 AND countBusinessSlots = 100");
            List<Model> models = query.getResultList();

            assertEquals(1, models.size());

            if (models.size() == 1) {
                model = models.get(0);
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

        if (model != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .get(ROUTE + model.id);
            assertEquals(200, response.getStatusCode());

            ValidatableResponse validatableResponse = response.then();

            validatableResponse
                    .assertThat().body("status", Matchers.equalTo("OK"))
                    .assertThat().body("model", Matchers.notNullValue());

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
        Query query = em.createQuery("SELECT COUNT(m.id) FROM Model m WHERE isActive = true");
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
            validatableResponse.assertThat().body("models", Matchers.nullValue());
        } else if (size == 1) {
            validatableResponse.assertThat().body("models", Matchers.notNullValue());
        } else if (size > 1) {
            validatableResponse.assertThat().body("models", Matchers.hasSize(size.intValue()));
        }

        em.close();
        emf.close();
    }

    @Test
    public void t4_update() {
        if (model != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", model.id)
                    .formParam("name", "TestModelNew")
                    .formParam("countEcoSlots", 400)
                    .formParam("countBusinessSlots", 150)
                    .put(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Model WHERE isActive = true AND name = 'TestModelNew'");
            List<Model> models = query.getResultList();

            assertEquals(1, models.size());

            if (models.size() == 1) {
                assertEquals(model.id, models.get(0).id);
                assertEquals("TestModelNew", models.get(0).name);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t5_delete() {
        if (model != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", model.id)
                    .delete(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Model WHERE isActive = false AND id = :id");
            query.setParameter("id", model.id);
            List<Model> models = query.getResultList();

            assertEquals(1, models.size());

            if (models.size() == 1) {
                assertEquals(model.id, models.get(0).id);
                assertEquals("TestModelNew", models.get(0).name);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }
}
