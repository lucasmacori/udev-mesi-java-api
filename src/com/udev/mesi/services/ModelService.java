package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.messages.WsGetModels;
import main.java.com.udev.mesi.entities.Model;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
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
            // TODO: Fix la requête (la requête générée par Hibernate n'est pas correcte) -> Dialect ?
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
}
