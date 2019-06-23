package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetModels;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Constructor;
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
            Query query = em.createQuery("FROM Model WHERE isActive = true");
            models = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetModels(status, message, code, models);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetModels(status, message, code, null);
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
                        "'constructor', 'name', 'countEcoSlots', 'countBusinessSlots'");
            }

            long constructor_id = Long.parseLong(formParams.get("constructor").get(0));
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
            query = em.createQuery("FROM Constructor WHERE id = :constructor_id");
            query.setParameter("constructor_id", constructor_id);
            List<Constructor> constructors = query.getResultList();

            if (constructors.size() == 0 || !constructors.get(0).isActive) {
                throw new Exception(MessageService.getMessageFromCode("model_does_not_exist", languageCode).text);
            }

            // Création du modèle
            model = new Model();
            model.name = name;
            model.constructor = constructors.get(0);
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
            long constructor_id = -1;
            String name = null;
            int countEcoSlots = -1;
            int countBusinessSlots = -1; // = Integer.parseInt(formParams.get("countBusinessSlots").get(0));

            // Récupération des paramètres
            if (formParams.containsKey("constructor"))
                constructor_id = Long.parseLong(formParams.get("constructor").get(0));
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
                throw new Exception(MessageService.getMessageFromCode("model_does_not_exist", languageCode).text);
            }

            // Modification du modèle
            if (constructor_id > 0) {
                // Récupération du constructeur
                Query query = em.createQuery("FROM Constructor WHERE id = :constructor_id");
                query.setParameter("constructor_id", constructor_id);
                List<Constructor> constructors = query.getResultList();

                if (constructors.size() == 0 || !constructors.get(0).isActive) {
                    throw new Exception(MessageService.getMessageFromCode("constructor_does_not_exist", languageCode).text);
                }
                model.constructor = constructors.get(0);
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

    public static WsResponse delete(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

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

            // Vérification des paramètres
            if (!formParams.containsKey("id")) {
                throw new Exception(MessageService.getMessageFromCode("invalid_model", languageCode).text + " 'id'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));

            // Récupération des modèles depuis la base de données
            Query query = em.createQuery("FROM Model WHERE isActive = true AND id = :id");
            query.setParameter("id", id);
            models = query.getResultList();

            // Vérification de l'existence du modèle
            if (models.size() == 0) {
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
        } catch (Exception e) {
            message = e.getMessage();
        }

        return new WsResponse(status, message, code);
    }

    private static boolean isValidModel(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        return formParams.containsKey("name") && formParams.containsKey("constructor")
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
}
