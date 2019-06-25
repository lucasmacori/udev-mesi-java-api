package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.messages.WsGetPlanes;
import main.java.com.udev.mesi.entities.Plane;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class PlaneService {
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
}
