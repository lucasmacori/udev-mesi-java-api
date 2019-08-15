package com.udev.mesi.services;

import com.udev.mesi.config.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetFlightDetails;
import com.udev.mesi.messages.WsGetFlights;
import com.udev.mesi.messages.WsGetSingleFlight;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Flight;
import main.java.com.udev.mesi.entities.FlightDetails;
import org.json.JSONException;

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
            // Récupération des constructeurs depuis la base de données
            Query query = Database.em.createQuery("FROM Flight WHERE isActive = true ORDER BY departureCity, arrivalCity");
            flights = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetFlights(status, message, code, flights);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetFlights(status, message, code, null);
        }

        return response;
    }

    public static WsGetSingleFlight readOne(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsGetSingleFlight response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Flight flight = null;

        try {
            // Récupération des constructeurs depuis la base de données
            flight = Database.em.find(Flight.class, id);

            // Vérification de l'existence du constructeur
            if (flight == null || !flight.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSingleFlight(status, null, code, flight);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSingleFlight(status, message, code, null);
        }

        return response;
    }

    public static WsGetFlightDetails readFlightDetails(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsGetFlightDetails response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<FlightDetails> flightDetails = null;

        try {
            // Récupération des constructeurs depuis la base de données
            Query query = Database.em.createQuery("SELECT fd FROM FlightDetails fd, Flight f WHERE f.isActive = true AND fd.isActive = true AND fd.flight = f AND f.id = :flightId ORDER BY fd.departureDateTime, fd.arrivaleDateTime");
            query.setParameter("flightId", id);
            flightDetails = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetFlightDetails(status, message, code, flightDetails);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetFlightDetails(status, message, code, null);
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

            // Vérification de l'existence du constructeur
            Query query = Database.em.createQuery("FROM Flight WHERE departureCity = :departureCity AND arrivalCity = :arrivalCity");
            query.setParameter("departureCity", departureCity);
            query.setParameter("arrivalCity", arrivalCity);
            List<Flight> flights = query.getResultList();

            Database.em.getTransaction().begin();

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
            Database.em.persist(flight);
            Database.em.flush();
            Database.em.getTransaction().commit();

            status = "OK";
            code = 201;
        } catch (Exception e) {
            message = e.getMessage();
            Database.em.getTransaction().rollback();
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

            // Récupération du vol
            Flight flight = Database.em.find(Flight.class, id);

            if (flight == null || !flight.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_does_not_exist", languageCode).text);
            }
            Database.em.getTransaction().begin();

            // Modification du vol
            if (departureCity != null) {
                flight.departureCity = departureCity;
            }
            if (arrivalCity != null) {
                flight.arrivalCity = arrivalCity;
            }

            // Persistence du flight
            Database.em.persist(flight);
            Database.em.flush();
            Database.em.getTransaction().commit();

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
            Database.em.getTransaction().rollback();
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

        List<Flight> flights = null;
        Flight flight = null;

        try {
            // Récupération des constructeurs depuis la base de données
            Query query = Database.em.createQuery("FROM Flight WHERE isActive = true AND id = :id");
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
            Database.em.getTransaction().begin();
            Database.em.persist(flight);
            Database.em.flush();
            Database.em.getTransaction().commit();

            // Création de la réponse JSON
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
            Database.em.getTransaction().rollback();
        }

        return new WsResponse(status, message, code);
    }

    private static boolean isValidFlight(MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) {
            return false;
        }
        return isUpdate || (formParams.containsKey("departureCity") && formParams.containsKey("arrivalCity"));
    }

    public static Flight exists(long pk) {
        // Récupération du vol
        Flight flight = Database.em.find(Flight.class, pk);
        if (flight == null || !flight.isActive) {
            return null;
        }
        return flight;
    }
}
