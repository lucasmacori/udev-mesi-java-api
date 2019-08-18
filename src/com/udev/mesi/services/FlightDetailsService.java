package com.udev.mesi.services;

import com.udev.mesi.config.APIFormat;
import com.udev.mesi.config.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetFlightDetails;
import com.udev.mesi.messages.WsGetSingleFlightDetails;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Flight;
import main.java.com.udev.mesi.entities.FlightDetails;
import main.java.com.udev.mesi.entities.Plane;
import org.json.JSONException;

import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class FlightDetailsService {
    public static WsGetFlightDetails read() throws JSONException {

        // Initialisation de la réponse
        WsGetFlightDetails response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<FlightDetails> flightDetails = null;

        try {
            // Récupération des modèles depuis la base de données
            Query query = Database.em.createQuery("SELECT fd FROM FlightDetails fd, Flight f, Plane p WHERE f.id = fd.flight AND p.id = fd.plane AND fd.isActive = true AND f.isActive = true AND p.isActive = true ORDER BY fd.departureDateTime DESC, fd.arrivaleDateTime DESC, f.departureCity, f.arrivalCity, p.ARN");
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

    public static WsGetSingleFlightDetails readOne(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsGetSingleFlightDetails response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        FlightDetails flightDetails = null;

        try {
            // Récupération des constructeurs depuis la base de données
            flightDetails = Database.em.find(FlightDetails.class, id);

            // Vérification de l'existence du constructeur
            if (flightDetails == null || !flightDetails.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_details_do_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSingleFlightDetails(status, null, code, flightDetails);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSingleFlightDetails(status, message, code, null);
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

        FlightDetails flightDetails;

        Query query;

        int conversion_step = 0;

        try {
            // Vérification des paramètres
            if (!areValidFlightDetails(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_flight_details", languageCode).text +
                        "'flight', 'plane', 'departureDateTime', 'arrivalDateTime'");
            }

            // Récupération des paramètres
            long flight_id = Long.parseLong(formParams.get("flight").get(0));
            String plane_arn = formParams.get("plane").get(0);
            Date departureDateTime = APIFormat.DATETIME_FORMAT.parse(formParams.get("departureDateTime").get(0));
            conversion_step++;
            Date arrivalDateTime = APIFormat.DATETIME_FORMAT.parse(formParams.get("arrivalDateTime").get(0));

            Database.em.getTransaction().begin();

            // Récupération du vol
            Flight flight = null;
            if (flight_id > 0) {
                flight = FlightService.exists(flight_id);
            }
            if (flight == null) {
                throw new Exception(MessageService.getMessageFromCode("flight_does_not_exist", languageCode).text);
            }

            // Récupération de l'avion
            Plane plane = null;
            if (plane_arn != null) {
                plane = PlaneService.exists(plane_arn);
            }
            if (plane == null) {
                throw new Exception(MessageService.getMessageFromCode("plane_does_not_exist", languageCode).text);
            }

            // Création du détail du vol
            flightDetails = new FlightDetails();
            flightDetails.flight = flight;
            flightDetails.plane = plane;
            flightDetails.departureDateTime = departureDateTime;
            flightDetails.arrivaleDateTime = arrivalDateTime;
            flightDetails.isActive = true;

            // Validation des changements
            Database.em.persist(flightDetails);
            Database.em.flush();
            Database.em.getTransaction().commit();

            status = "OK";
            code = 201;
        } catch (NumberFormatException e) {
            try {
                message = "'flight_id' " + MessageService.getMessageFromCode("is_not_an_integer", languageCode).text;
                code = 400;
            } catch (MessageException me) {
            }
        } catch (ParseException e) {
            message = getMessageFromConversionStep(conversion_step, languageCode);
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
        int conversion_step = 0;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            // Vérification des paramètres
            if (!areValidFlightDetails(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_flight_details", languageCode).text + " 'id'");
            }

            long flight_id = 0;
            String plane_arn = null;
            Date departureDateTime = null;
            Date arrivalDateTime = null;

            // Récupération des paramètres
            long id = Long.parseLong(formParams.get("id").get(0));
            if (formParams.containsKey("flight")) {
                flight_id = Long.parseLong(formParams.get("flight").get(0));
            }
            if (formParams.containsKey("plane")) {
                plane_arn = formParams.get("plane").get(0);
            }
            if (formParams.containsKey("departureDateTime")) {
                departureDateTime = APIFormat.DATETIME_FORMAT.parse(formParams.get("departureDateTime").get(0));
            }
            conversion_step++;
            if (formParams.containsKey("arrivalDateTime")) {
                arrivalDateTime = APIFormat.DATETIME_FORMAT.parse(formParams.get("arrivalDateTime").get(0));
            }

            // Récupération du détail du vol
            FlightDetails flightDetails = Database.em.find(FlightDetails.class, id);
            if (flightDetails == null || !flightDetails.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_details_do_not_exist", languageCode).text);
            }

            // Récupération du vol
            Flight flight = null;
            if (flight_id > 0) {
                flight = FlightService.exists(flight_id);
                if (flight == null) {
                    throw new Exception(MessageService.getMessageFromCode("flight_does_not_exist", languageCode).text);
                }
            }

            // Récupération de l'avion
            Plane plane = null;
            if (plane_arn != null) {
                plane = PlaneService.exists(plane_arn);
                if (plane == null) {
                    throw new Exception(MessageService.getMessageFromCode("plane_does_not_exist", languageCode).text);
                }
            }

            // Persistence du constructeur
            Database.em.getTransaction().begin();
            if (flight != null) {
                flightDetails.flight = flight;
            }
            if (plane != null) {
                flightDetails.plane = plane;
            }
            if (departureDateTime != null) {
                flightDetails.departureDateTime = departureDateTime;
            }
            if (arrivalDateTime != null) {
                flightDetails.arrivaleDateTime = arrivalDateTime;
            }
            Database.em.persist(flightDetails);
            Database.em.flush();
            Database.em.getTransaction().commit();

            status = "OK";
            code = 200;
        } catch (NumberFormatException e) {
            code = 400;
            try {
                message = "'id' " + MessageService.getMessageFromCode("is_not_an_integer", languageCode).text;
            } catch (MessageException me) {
            }
        } catch (ParseException e) {
            message = getMessageFromConversionStep(conversion_step, languageCode);
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

        FlightDetails flightDetails = null;

        try {
            // Récupération des constructeurs depuis la base de données
            flightDetails = Database.em.find(FlightDetails.class, id);

            // Vérification de l'existence du constructeur
            if (flightDetails == null || !flightDetails.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_details_do_not_exist", languageCode).text);
            }

            // Suppression du détail du vol
            Database.em.getTransaction().begin();
            flightDetails.isActive = false;
            Database.em.flush();
            Database.em.getTransaction().commit();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
        } catch (NumberFormatException e) {
            code = 400;
            try {
                message = "'id' " + MessageService.getMessageFromCode("is_not_an_integer", languageCode).text;
            } catch (MessageException me) {
            }
        } catch (Exception e) {
            message = e.getMessage();
            Database.em.getTransaction().rollback();
        }

        return new WsResponse(status, message, code);
    }

    private static String getMessageFromConversionStep(int conversion_step, String languageCode) {
        String message;
        try {
            if (conversion_step == 0) {
                message = "'departureDateTime' ";
            } else {
                message = "'arrivalDateTime' ";
            }
            message += MessageService.getMessageFromCode("is_not_a_date", languageCode).text;
        } catch (MessageException me) {
            message = null;
        }
        return message;
    }

    private static boolean areValidFlightDetails(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        return isUpdate || (formParams.containsKey("flight") && formParams.containsKey("plane")
                && formParams.containsKey("departureDateTime") && formParams.containsKey("arrivalDateTime"));
    }

    public static FlightDetails exists(long pk) {
        // Récupération du constructeur
        FlightDetails flightDetails = Database.em.find(FlightDetails.class, pk);
        if (flightDetails == null || !flightDetails.isActive) {
            return null;
        }
        return flightDetails;
    }
}
