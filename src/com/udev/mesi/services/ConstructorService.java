package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.messages.WsGetConstructors;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Constructor;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class ConstructorService {

    public static WsGetConstructors read() throws JSONException {

        // Initialisation de la réponse
        WsGetConstructors response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Constructor> constructors = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des constructeurs depuis la base de données
            Query query = em.createQuery("FROM Constructor WHERE isActive = true");
            constructors = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetConstructors(status, message, code, constructors);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetConstructors(status, message, code, null);
        }

        return response;
    }

    public static WsResponse create(final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        Constructor constructor;

        try {
            // Vérification des paramètres
            if (!isValidConstructor(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_constructor", "fr").text + " 'name'");
            }

            String name = formParams.get("name").get(0);

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence du constructeur
            Query query = em.createQuery("FROM Constructor WHERE name = :name");
            query.setParameter("name", name);
            List<Constructor> constructors = query.getResultList();

            em.getTransaction().begin();

            if (constructors.size() > 0) {
                constructor = constructors.get(0);
                if (constructor.isActive) {
                    throw new Exception(MessageService.getMessageFromCode("constructor_already_exists", "en").text);
                }
            } else {
                // Création du constructeur
                constructor = new Constructor();
                constructor.name = name;
                constructor.models = null;
            }
            constructor.isActive = true;

            // Validation des changements
            em.persist(constructor);
            em.flush();
            em.getTransaction().commit();

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();

            status = "OK";
            code = 200;
        } catch (Exception e) {
            message = e.getMessage();
        }

        return new WsResponse(status, message, code);
    }

    public static WsResponse update(final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        try {
            // Vérification des paramètres
            if (!isValidConstructor(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_constructor", "fr").text + " 'id', 'name'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));
            String name = formParams.get("name").get(0);

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération du constructeur
            Constructor constructor = em.find(Constructor.class, id);

            if (constructor == null || !constructor.isActive) {
                throw new Exception(MessageService.getMessageFromCode("constructor_does_not_exist", "fr").text);
            }

            // Récupération des constructeurs depuis la base de données
            Query query = em.createQuery("FROM Constructor WHERE name = :name");
            query.setParameter("name", name);
            List<Constructor> constructors = query.getResultList();

            // Vérification de l'existence du constructeur
            if (constructors.size() > 0) {
                throw new Exception(MessageService.getMessageFromCode("constructor_already_exists", "fr").text);
            }

            // Modification du constructeur
            constructor.name = name;

            // Persistence du constructeur
            em.getTransaction().begin();
            em.persist(constructor);
            em.flush();
            em.getTransaction().commit();

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();

            status = "OK";
            code = 200;
        } catch (NumberFormatException e) {
            code = 400;
            message = "L'id entré n'est pas un nombre entier";
        } catch (Exception e) {
            message = e.getMessage();
        }

        return new WsResponse(status, message, code);
    }

    public static WsResponse delete(final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        List<Constructor> constructors = null;
        Constructor constructor = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification des paramètres
            if (!formParams.containsKey("id")) {
                throw new Exception(MessageService.getMessageFromCode("invalid_constructor", "fr").text + " 'id'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));

            // Récupération des constructeurs depuis la base de données
            Query query = em.createQuery("FROM Constructor WHERE isActive = true AND id = :id");
            query.setParameter("id", id);
            constructors = query.getResultList();

            // Vérification de l'existence du constructeur
            if (constructors.size() == 0) {
                throw new Exception(MessageService.getMessageFromCode("constructor_does_not_exist", "fr").text);
            }

            constructor = constructors.get(0);
            constructor.isActive = false;

            // Persistence du constructeur
            em.getTransaction().begin();
            em.persist(constructor);
            em.flush();
            em.getTransaction().commit();

            // Création de la réponse JSON
            status = "OK";
            code = 200;

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
        }

        return new WsResponse(status, message, code);
    }

    private static boolean isValidConstructor(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        return formParams.containsKey("name");
    }
}
