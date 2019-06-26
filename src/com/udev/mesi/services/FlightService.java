package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetFlights;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Flight;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class FlightService {
    public static WsGetFlights read() throws JSONException {

        // Initialisation de la réponse
        WsGetFlights response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Flight> flights = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des constructeurs depuis la base de données
            Query query = em.createQuery("FROM Flight WHERE isActive = true");
            flights = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetFlights(status, message, code, flights);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetFlights(status, message, code, null);
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

        Flight flight;

        try {
            // Vérification des paramètres
            if (!isValidFlight(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_flight", languageCode).text + " 'name'");
            }

            String departureCity = formParams.get("departureCity").get(0);
            String arrivalCity = formParams.get("arrivalCity").get(0);

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence du constructeur
            Query query = em.createQuery("FROM Flight WHERE departureCity = :departureCity AND arrivalCity = :arrivalCity");
            query.setParameter("departureCity", departureCity);
            query.setParameter("arrivalCity", arrivalCity);
            List<Flight> flights = query.getResultList();

            em.getTransaction().begin();

            if (flights.size() == 1) {
                flight = flights.get(0);
                if (flight.isActive) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("flight_already_exists", languageCode).text);
                } else {
                    // Création du vol
                    flight = new Flight();
                    flight.departureCity = departureCity;
                    flight.arrivalCity = arrivalCity;
                }
            } else {
                // Création du vol
                flight = new Flight();
                flight.departureCity = departureCity;
                flight.arrivalCity = arrivalCity;
            }
            flight.isActive = true;

            // Validation des changements
            em.persist(flight);
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

    public static WsResponse update(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            // Vérification des paramètres
            if (!isValidFlight(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_flight", languageCode).text + " 'id', 'name'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));
            String arrivalCity = null;
            String departureCity = null;
            if (formParams.containsKey("arrivalCity")) {
                arrivalCity = formParams.get("arrivalCity").get(0);
            }
            if (formParams.containsKey("departureCity")) {
                departureCity = formParams.get("departureCity").get(0);
            }

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération du vol
            Flight flight = em.find(Flight.class, id);

            if (flight == null || !flight.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_does_not_exist", languageCode).text);
            }
            em.getTransaction().begin();

            // Modification du vol
            if (departureCity != null) {
                flight.departureCity = departureCity;
            }
            if (arrivalCity != null) {
                flight.arrivalCity = arrivalCity;
            }

            // Persistence du flight
            em.persist(flight);
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

    public static WsResponse delete(final String acceptLanguage, MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        List<Flight> flights = null;
        Flight flight = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification des paramètres
            if (!formParams.containsKey("id")) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_flight", languageCode).text + " 'id'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));

            // Récupération des constructeurs depuis la base de données
            Query query = em.createQuery("FROM Flight WHERE isActive = true AND id = :id");
            query.setParameter("id", id);
            flights = query.getResultList();

            // Vérification de l'existence du constructeur
            if (flights.size() == 0) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_does_not_exist", languageCode).text);
            }

            flight = flights.get(0);
            flight.isActive = false;

            // Persistence du constructeur
            em.getTransaction().begin();
            em.persist(flight);
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

    private static boolean isValidFlight(MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) {
            return false;
        }
        return isUpdate || (formParams.containsKey("departureCity") && formParams.containsKey("arrivalCity"));
    }
}
