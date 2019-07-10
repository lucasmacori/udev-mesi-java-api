package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetModels;
import com.udev.mesi.messages.WsGetSingleModel;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Manufacturer;
import main.java.com.udev.mesi.entities.Model;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class ModelService {

    public static WsGetModels read() throws JSONException {

        // Initialisation de la réponse
        WsGetModels response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Model> models = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des modèles depuis la base de données
            Query query = em.createQuery("SELECT m FROM Model m, Manufacturer c WHERE c.id = m.manufacturer AND m.isActive = true AND c.isActive = true ORDER BY m.name, c.name");
            models = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetModels(status, message, code, models, true);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetModels(status, message, code, null, true);
        }

        return response;
    }

    public static WsGetSingleModel readOne(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsGetSingleModel response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Model model = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération du modèle depuis la base de données
            model = em.find(Model.class, id);

            // Vérification de l'existence du modèle
            if (model == null || !model.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("model_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSingleModel(status, null, code, model);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSingleModel(status, message, code, null);
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

        Model model;

        Query query;

        int conversion_step = 0;

        try {
            // Vérification des paramètres
            if (!isValidModel(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_model", languageCode).text +
                        "'manufacturer', 'name', 'countEcoSlots', 'countBusinessSlots'");
            }

            long manufacturer_id = Long.parseLong(formParams.get("manufacturer").get(0));
            conversion_step++;
            String name = formParams.get("name").get(0);
            int countEcoSlots = Integer.parseInt(formParams.get("countEcoSlots").get(0));
            conversion_step++;
            int countBusinessSlots = Integer.parseInt(formParams.get("countBusinessSlots").get(0));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            em.getTransaction().begin();

            // Récupération du constructeur
            Manufacturer manufacturer = ManufacturerService.exists(manufacturer_id);
            if (manufacturer == null) {
                throw new Exception(MessageService.getMessageFromCode("manufacturer_does_not_exist", languageCode).text + " 'id'");
            }

            // Création du modèle
            model = new Model();
            model.name = name;
            model.manufacturer = manufacturer;
            model.countEcoSlots = countEcoSlots;
            model.countBusinessSlots = countBusinessSlots;
            model.isActive = true;

            // Validation des changements
            em.persist(model);
            em.flush();
            em.getTransaction().commit();

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();

            status = "OK";
            code = 201;
        } catch (NumberFormatException e) {
            message = getMessageFromConversionStep(conversion_step, languageCode);
            code = 400;
            if (message == null) {
                code = 500;
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
            if (!formParams.containsKey("id")) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_model", languageCode).text + " 'id'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));
            long manufacturer_id = -1;
            String name = null;
            int countEcoSlots = -1;
            int countBusinessSlots = -1; // = Integer.parseInt(formParams.get("countBusinessSlots").get(0));

            // Récupération des paramètres
            if (formParams.containsKey("manufacturer"))
                manufacturer_id = Long.parseLong(formParams.get("manufacturer").get(0));
            conversion_step++;
            if (formParams.containsKey("name")) name = formParams.get("name").get(0);
            if (formParams.containsKey("countEcoSlots"))
                countEcoSlots = Integer.parseInt(formParams.get("countEcoSlots").get(0));
            conversion_step++;
            if (formParams.containsKey("countBusinessSlots"))
                countBusinessSlots = Integer.parseInt(formParams.get("countBusinessSlots").get(0));

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération du modèle
            Model model = em.find(Model.class, id);

            if (model == null || !model.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("model_does_not_exist", languageCode).text);
            }

            // Modification du modèle
            if (manufacturer_id > 0) {
                // Récupération du constructeur
                model.manufacturer = ManufacturerService.exists(id);
                if (model.manufacturer == null) {
                    throw new Exception(MessageService.getMessageFromCode("manufacturer_does_not_exist", languageCode).text + " 'id'");
                }
            }
            if (name != null && name.trim() != "") model.name = name;
            if (countEcoSlots > -1) model.countEcoSlots = countEcoSlots;
            if (countBusinessSlots > -1) model.countBusinessSlots = countBusinessSlots;

            // Persistence du constructeur
            em.getTransaction().begin();
            em.persist(model);
            em.flush();
            em.getTransaction().commit();

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();

            status = "OK";
            code = 200;
        } catch (NumberFormatException e) {
            message = getMessageFromConversionStep(conversion_step, languageCode);
            code = 400;
            if (message == null) {
                code = 500;
            }
        } catch (Exception e) {
            message = e.getMessage();
        }

        return new WsResponse(status, message, code);
    }

    public static WsResponse delete(final String acceptLanguage, final Long id) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        List<Model> models = null;
        Model model = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des modèles depuis la base de données
            Query query = em.createQuery("FROM Model WHERE isActive = true AND id = :id");
            query.setParameter("id", id);
            models = query.getResultList();

            // Vérification de l'existence du modèle
            if (models.size() == 0) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("model_does_not_exist", languageCode).text);
            }

            model = models.get(0);
            model.isActive = false;

            // Persistence du model
            em.getTransaction().begin();
            em.persist(model);
            em.flush();
            em.getTransaction().commit();

            // Création de la réponse JSON
            status = "OK";
            code = 200;

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
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

    private static boolean isValidModel(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        return formParams.containsKey("name") && formParams.containsKey("manufacturer")
                && formParams.containsKey("countEcoSlots") && formParams.containsKey("countBusinessSlots");
    }

    private static String getMessageFromConversionStep(int conversion_step, String languageCode) {
        String message = null;
        try {
            if (conversion_step == 0) {
                message = "'id' ";
            } else if (conversion_step == 1) {
                message = "'countEcoSlots': ";
            } else {
                message = "'countBusinessSlots': ";
            }
            message += MessageService.getMessageFromCode("is_not_an_integer", languageCode).text;
        } catch (MessageException me) {
            message = null;
        }
        return message;
    }

    public static Model exists(long pk) {
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du constructeur
        Model model = em.find(Model.class, pk);
        if (model == null || !model.isActive) {
            return null;
        }
        return model;
    }
}
