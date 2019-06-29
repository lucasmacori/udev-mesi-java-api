package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.messages.WsGetFlightDetails;
import main.java.com.udev.mesi.entities.FlightDetails;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class FlightDetailsService {
    public static WsGetFlightDetails read() throws JSONException {

        // Initialisation de la réponse
        WsGetFlightDetails response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<FlightDetails> flightDetails = null;

        //
        // FIXME: L'entité FlightDetails à tout pété
        //

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des modèles depuis la base de données
            Query query = em.createQuery("FROM FlightDetails WHERE isActive = true");
            flightDetails = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetFlightDetails(status, message, code, flightDetails);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetFlightDetails(status, message, code, null);
        }

        return response;
    }
}
