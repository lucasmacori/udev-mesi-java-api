package com.udev.mesi.services;

import com.udev.mesi.config.APIFormat;
import com.udev.mesi.config.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetReservations;
import com.udev.mesi.messages.WsGetSingleReservation;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.FlightDetails;
import main.java.com.udev.mesi.entities.Passenger;
import main.java.com.udev.mesi.entities.Reservation;
import org.hibernate.Session;
import org.json.JSONException;

import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Date;
import java.util.List;

public class ReservationService {

    public static WsGetReservations read(final String acceptLanguage, final String username, final String token) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetReservations response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Reservation> reservations = null;

        try {
            // Récupération de la langue de l'utilisateur
            String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

            // Vérification du token
            if (!AuthService.verifyToken(username, token)) {
                code = 401;
                throw new Exception(MessageService.getMessageFromCode("user_not_authentified", languageCode).text);
            }

            session = Database.sessionFactory.openSession();

            // Récupération des reservations depuis la base de données
            Query query = session.createQuery("SELECT r FROM Reservation r, Passenger p WHERE p.id = r.passenger AND r.isActive = true AND p.isActive = true ORDER BY r.reservationDate DESC, r.reservationClass");
            reservations = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetReservations(status, message, code, reservations);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetReservations(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsGetSingleReservation readOne(final long id, final String acceptLanguage, final String username, final String token) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetSingleReservation response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Reservation reservation = null;

        try {
            // Vérification du token
            if (!AuthService.verifyToken(username, token)) {
                code = 401;
                throw new Exception(MessageService.getMessageFromCode("user_not_authentified", languageCode).text);
            }

            session = Database.sessionFactory.openSession();

            // Récupération des constructeurs depuis la base de données
            reservation = session.find(Reservation.class, id);

            // Vérification de l'existence du constructeur
            if (reservation == null || !reservation.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("reservation_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSingleReservation(status, null, code, reservation);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSingleReservation(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsResponse create(final String acceptLanguage, final MultivaluedMap<String, String> formParams, final String username, final String token) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Reservation reservation;

        try {
            // Vérification du token
            if (!AuthService.verifyToken(username, token)) {
                code = 401;
                throw new Exception(MessageService.getMessageFromCode("user_not_authentified", languageCode).text);
            }

            session = Database.sessionFactory.openSession();

            // Vérification des paramètres
            if (!isValidReservation(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_reservation", languageCode).text + " 'passenger', 'flightDetails', 'reservationClass'");
            }

            Long passenger_id = Long.parseLong(formParams.get("passenger").get(0));
            Long flightDetails_id = Long.parseLong(formParams.get("flightDetails").get(0));
            char reservationClass = formParams.get("reservationClass").get(0).charAt(0);

            // Vérification de l'existence de la réservation
            Query query = session.createQuery("SELECT r FROM Reservation r, FlightDetails fd, Passenger p WHERE fd.id = r.flightDetails AND p.id = r.passenger AND fd.id = :fd_id AND p.id = :p_id");
            query.setParameter("fd_id", flightDetails_id);
            query.setParameter("p_id", passenger_id);
            List<Reservation> reservations = query.getResultList();

            session.getTransaction().begin();

            if (reservations.size() == 1) {
                reservation = reservations.get(0);
                if (reservation.isActive) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("reservation_already_exists", languageCode).text);
                }
            }

            // Récupération du détail de vol
            FlightDetails flightDetails = FlightDetailsService.exists(flightDetails_id);
            if (flightDetails == null) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("flight_details_do_not_exist", languageCode).text);
            }

            // Récupération du passager
            Passenger passenger = PassengerService.exists(passenger_id);
            if (passenger == null) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("passenger_does_not_exist", languageCode).text);
            }

            // Création de la réservation
            reservation = new Reservation();
            reservation.reservationDate = new Date();
            reservation.reservationClass = reservationClass;
            reservation.flightDetails = flightDetails;
            reservation.passenger = passenger;
            reservation.isActive = true;

            // Validation des changements
            session.persist(reservation);
            session.flush();
            session.getTransaction().commit();

            status = "OK";
            code = 201;
        } catch (Exception e) {
            message = e.getMessage();
            session.getTransaction().rollback();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return new WsResponse(status, message, code);
    }

    public static WsResponse update(final String acceptLanguage, final MultivaluedMap<String, String> formParams, final String username, final String token) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            // Vérification du token
            if (!AuthService.verifyToken(username, token)) {
                code = 401;
                throw new Exception(MessageService.getMessageFromCode("user_not_authentified", languageCode).text);
            }

            session = Database.sessionFactory.openSession();

            // Vérification des paramètres
            if (!isValidReservation(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_reservation", languageCode).text + " 'id', 'reservationClass'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));
            char reservationClass = formParams.get("reservationClass").get(0).charAt(0);

            // Récupération de la réservation
            Reservation reservation = session.find(Reservation.class, id);

            if (reservation == null || !reservation.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("reservation_does_not_exist", languageCode).text);
            }

            session.getTransaction().begin();

            // Modification de la réservation
            reservation.reservationClass = reservationClass;

            // Persistence de la réservation
            session.persist(reservation);
            session.flush();
            session.getTransaction().commit();

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
            session.getTransaction().rollback();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return new WsResponse(status, message, code);
    }

    public static WsResponse delete(final String acceptLanguage, final Long id, final String username, final String token) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            // Vérification du token
            if (!AuthService.verifyToken(username, token)) {
                code = 401;
                throw new Exception(MessageService.getMessageFromCode("user_not_authentified", languageCode).text);
            }

            session = Database.sessionFactory.openSession();

            // Récupération de la réservation depuis la base de données
            Reservation reservation = session.find(Reservation.class, id);

            // Suppression de la réservation
            reservation.isActive = false;

            // Persistence de la reservation
            session.getTransaction().begin();
            session.persist(reservation);
            session.flush();
            session.getTransaction().commit();

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
            session.getTransaction().rollback();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return new WsResponse(status, message, code);
    }

    private static boolean isValidReservation(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        if (!formParams.containsKey("reservationClass") || !APIFormat.isValidString(formParams.get("reservationClass").get(0), 1, 1))
            return false;
        if (!isUpdate && (!formParams.containsKey("flightDetails") || !APIFormat.isValidLong(formParams.get("flightDetails").get(0))))
            return false;
        return isUpdate || (formParams.containsKey("passenger") && APIFormat.isValidLong(formParams.get("passenger").get(0)));
    }
}
