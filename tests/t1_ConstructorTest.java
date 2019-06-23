import com.udev.mesi.Database;
import config.APIConfig;
import io.restassured.response.Response;
import main.java.com.udev.mesi.entities.Constructor;
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
public class t1_ConstructorTest {

    private static final String ROUTE = APIConfig.PATH + "constructor/";
    private static Constructor constructor;

    @AfterClass
    public static void clean() {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("FROM Constructor WHERE id = ( SELECT MAX(c.id) FROM Constructor c)");
        List<Constructor> constructors = query.getResultList();

        if (constructors.size() == 1) {
            em.getTransaction().begin();
            em.flush();
            em.remove(constructors.get(0));
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
                .formParam("name", "TestConstructor")
                .post(ROUTE);

        assertEquals(200, response.getStatusCode());
        response
                .then()
                .assertThat().body("status", CoreMatchers.equalTo("OK"));

        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du constructeur depuis la base de données
        Query query = em.createQuery("FROM Constructor WHERE isActive = true AND name = 'TestConstructor'");
        List<Constructor> constructors = query.getResultList();

        assertEquals(1, constructors.size());

        if (constructors.size() == 1) {
            constructor = constructors.get(0);
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
        Query query = em.createQuery("SELECT COUNT(c.id) FROM Constructor c WHERE isActive = true");
        List<Long> constructors = query.getResultList();
        Long size = constructors.get(0);

        Response response = given()
                .urlEncodingEnabled(true)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .contentType("application/x-www-form-urlencoded")
                .get(ROUTE);
        assertEquals(200, response.getStatusCode());
        response.then()
                .assertThat().body("status", Matchers.equalTo("OK"))
                .assertThat().body("constructors", Matchers.notNullValue())
                .assertThat().body("constructors", Matchers.hasSize(size.intValue()));

        em.close();
        emf.close();
    }

    @Test
    public void t3_update() {
        if (constructor != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", constructor.id)
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
            Query query = em.createQuery("FROM Constructor WHERE isActive = true AND name = 'TestConstructorNew'");
            List<Constructor> constructors = query.getResultList();

            assertEquals(1, constructors.size());

            if (constructors.size() == 1) {
                assertEquals(constructor.id, constructors.get(0).id);
                assertEquals("TestConstructorNew", constructors.get(0).name);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête PUT");
        }
    }

    @Test
    public void t4_delete() {
        if (constructor != null) {
            Response response = given()
                    .urlEncodingEnabled(true)
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("id", constructor.id)
                    .delete(ROUTE);

            assertEquals(200, response.getStatusCode());
            response
                    .then()
                    .assertThat().body("status", Matchers.equalTo("OK"));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de la modification
            Query query = em.createQuery("FROM Constructor WHERE isActive = false AND id = :id");
            query.setParameter("id", constructor.id);
            List<Constructor> constructors = query.getResultList();

            assertEquals(1, constructors.size());

            if (constructors.size() == 1) {
                assertEquals(constructor.id, constructors.get(0).id);
                assertEquals("TestConstructorNew", constructors.get(0).name);
            }

            em.close();
            emf.close();
        } else {
            fail("La requête POST ne s'étant pas exécutée, il est impossible de tester la requête DELETE");
        }
    }
}
