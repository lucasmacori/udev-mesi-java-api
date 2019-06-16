package com.udev.mesi.services;

import com.udev.mesi.Database;
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

    public static WsResponse create(final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        Model model;

        Query query;

        int conversion_step = 0;

        try {
            // Vérification des paramètres
            if (!isValidModel(formParams, false)) {
                code = 400;
                throw new Exception("Le modèle n'est pas correct. Veuillez renseigner les valeurs suivantes: " +
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

            // Vérification de l'existence du modèle
            // TODO: Fix la vérification (seul le nom est vérifié pour l'unicité du model)
            query = em.createQuery("FROM Model WHERE name = :name");
            // query.setParameter("constructor_id", constructor_id);
            query.setParameter("name", name);
            List<Model> models = query.getResultList();

            em.getTransaction().begin();

            if (models.size() > 0) {
                model = models.get(0);
                if (model.isActive) {
                    throw new Exception("Un modèle avec le constructor " + constructor_id + " et le nom '" + name + "' existe déjà");
                }
            } else {
                // Récupération du constructeur
                query = em.createQuery("FROM Constructor WHERE id = :constructor_id");
                query.setParameter("constructor_id", constructor_id);
                List<Constructor> constructors = query.getResultList();

                if (constructors.size() == 0 || !constructors.get(0).isActive) {
                    throw new Exception("Le constructeur avec l'id '" + constructor_id + "' n'existe pas");
                }
                // Création du modèle
                model = new Model();
                model.name = name;
                model.constructor = constructors.get(0);
                model.countEcoSlots = countEcoSlots;
                model.countBusinessSlots = countBusinessSlots;
            }
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
            code = 400;
            if (conversion_step == 0) {
                message = "L'id du constructeur entré n'est pas un nombre entier";
            } else if (conversion_step == 1) {
                message = "Le nombre de places eco entré n'est pas un nombre entier";
            } else {
                message = "Le nombre de places affaire entré n'est pas un nombre entier";
            }
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
        int conversion_step = 0;

        try {
            // Vérification des paramètres
            if (!formParams.containsKey("id")) {
                code = 400;
                throw new Exception("Le modèle n'est pas correct. Veuillez renseigner au moins les valeurs suivantes: 'id'");
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
                throw new Exception("Le modèle avec l'id '" + id + "' n'existe pas");
            }

            // TODO: Vérifier que le modèle n'existe pas déjà avec le même nom et le même constructeur

            // Modification du modèle
            if (constructor_id > 0) {
                // Récupération du constructeur
                Query query = em.createQuery("FROM Constructor WHERE id = :constructor_id");
                query.setParameter("constructor_id", constructor_id);
                List<Constructor> constructors = query.getResultList();

                if (constructors.size() == 0 || !constructors.get(0).isActive) {
                    throw new Exception("Le constructeur avec l'id '" + constructor_id + "' n'existe pas");
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
            code = 400;
            if (conversion_step == 0) {
                message = "L'id du constructeur entré n'est pas un nombre entier";
            } else if (conversion_step == 1) {
                message = "Le nombre de places eco entré n'est pas un nombre entier";
            } else {
                message = "Le nombre de places affaire entré n'est pas un nombre entier";
            }
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

        List<Model> models = null;
        Model model = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification des paramètres
            if (!formParams.containsKey("id")) {
                throw new Exception("Le modèle n'est pas correct. Veuillez renseigner les valeurs suivantes: 'id'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));

            // Récupération des modèles depuis la base de données
            Query query = em.createQuery("FROM Model WHERE isActive = true AND id = :id");
            query.setParameter("id", id);
            models = query.getResultList();

            // Vérification de l'existence du modèle
            if (models.size() == 0) {
                throw new Exception("Le modèle avec l'id '" + id + "' n'existe pas");
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
}
