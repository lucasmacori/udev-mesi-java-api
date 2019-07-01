package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetPlanes;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Model;
import main.java.com.udev.mesi.entities.Plane;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class PlaneService implements IWebService {
    public static WsGetPlanes read() throws JSONException {

        // Initialisation de la réponse
        WsGetPlanes response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Plane> planes = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des constructeurs depuis la base de données
            Query query = em.createQuery("FROM Plane WHERE isActive = true");
            planes = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetPlanes(status, message, code, planes, true);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetPlanes(status, message, code, null, false);
        }

        return response;
    }

    public static WsResponse create(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Plane plane;

        try {
            // Vérification des paramètres
            if (!isValidPlane(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_plane", languageCode).text + " 'ARN', 'model'");
            }

            String ARN = formParams.get("ARN").get(0);
            long model_id = Long.parseLong(formParams.get("model").get(0));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence du modèle
            Model model = em.find(Model.class, model_id);
            if (model == null || !model.isActive) {
                throw new Exception(MessageService.getMessageFromCode("model_does_not_exist", languageCode).text);
            }

            // Vérification de l'existence de l'avion
            Query query = em.createQuery("FROM Plane WHERE ARN = :arn");
            query.setParameter("arn", ARN);
            List<Plane> planes = query.getResultList();

            em.getTransaction().begin();

            if (planes.size() == 1) {
                plane = planes.get(0);
                if (plane.isActive) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("plane_already_exists", languageCode).text);
                } else {
                    plane.model = model;
                    plane.isUnderMaintenance = false;
                }
            } else {
                // Création de l'avion
                plane = new Plane();
                plane.ARN = ARN;
                plane.model = model;
            }
            plane.isActive = true;

            // Validation des changements
            em.persist(plane);
            em.flush();
            em.getTransaction().commit();

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();

            status = "OK";
            code = 200;
        } catch (NumberFormatException e) {
            try {
                message = "'model' " + MessageService.getMessageFromCode("is_not_an_integer", languageCode).text;
                code = 400;
                if (message == null) {
                    code = 500;
                }
            } catch (MessageException me) {
                message = null;
            }

        } catch (Exception e) {
            message = e.getMessage();
        }

        return new WsResponse(status, message, code);
    }

    public static WsResponse update(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;
        int conversion_step = 0;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            // Vérification des paramètres
            if (!isValidPlane(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_plane", languageCode).text + " 'ARN'");
            }

            // Récupération des paramètres
            String ARN = formParams.get("ARN").get(0);
            long model_id = -1;
            if (formParams.containsKey("model")) {
                model_id = Long.parseLong(formParams.get("model").get(0));
            }
            Boolean isUnderMaintenance = null;
            if (formParams.containsKey("isUnderMaintenance")) {
                String isUnderMaintenanceStr = formParams.get("isUnderMaintenance").get(0).trim().toLowerCase();
                if (isUnderMaintenanceStr.equals("true") || isUnderMaintenanceStr.equals("false")) {
                    isUnderMaintenance = Boolean.parseBoolean(formParams.get("isUnderMaintenance").get(0));
                } else {
                    code = 400;
                    throw new Exception("'isUnderMaintenance' " + MessageService.getMessageFromCode("is_not_a_boolean", languageCode).text);
                }
            }

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération de l'avion
            Plane plane = em.find(Plane.class, ARN);

            if (plane == null || !plane.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("plane_does_not_exist", languageCode).text);
            }

            // Modification de l'avion
            if (model_id > 0) {
                // Vérification de l'existence du modèle
                Model model = em.find(Model.class, model_id);
                if (!model.equals(plane.model) && (model == null || !model.isActive)) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("model_does_not_exist", languageCode).text);
                }
                plane.model = model;
            }
            if (isUnderMaintenance != null) plane.isUnderMaintenance = isUnderMaintenance;

            // Persistence du constructeur
            em.getTransaction().begin();
            em.persist(plane);
            em.flush();
            em.getTransaction().commit();

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();

            status = "OK";
            code = 200;
        } catch (NumberFormatException e) {
            try {
                message = "'id': " + MessageService.getMessageFromCode("is_not_an_integer", languageCode).text;
                code = 400;
            } catch (MessageException me) {
                message = me.getMessage();
            }
        } catch (Exception e) {
            message = e.getMessage();
        }

        return new WsResponse(status, message, code);
    }

    public static WsResponse delete(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Plane plane = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification des paramètres
            if (!isValidPlane(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_plane", languageCode).text + " 'ARN'");
            }

            String ARN = formParams.get("ARN").get(0);

            plane = em.find(Plane.class, ARN);

            // Vérification de l'existence du modèle
            if (plane == null || !plane.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("plane_does_not_exist", languageCode).text);
            }
            plane.isActive = false;

            // Persistence du model
            em.getTransaction().begin();
            em.persist(plane);
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

    private static boolean isValidPlane(final MultivaluedMap<String, String> formParams, boolean isUpdateOrDelete) {
        if (!isUpdateOrDelete && !formParams.containsKey("model")) {
            return false;
        }
        return formParams.containsKey("ARN");
    }

    public static Plane exists(String pk) {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du vol
        Query query = em.createQuery("FROM Plane WHERE ARN = :ARN");
        query.setParameter("ARN", pk);
        List<Plane> planes = query.getResultList();
        if (planes.size() != 1 || !planes.get(0).isActive) {
            return null;
        }
        return planes.get(0);
    }
}
