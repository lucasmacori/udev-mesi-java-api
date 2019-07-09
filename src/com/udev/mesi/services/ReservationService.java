package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.config.APIFormat;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetReservations;
import com.udev.mesi.messages.WsGetSingleReservation;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.FlightDetails;
import main.java.com.udev.mesi.entities.Passenger;
import main.java.com.udev.mesi.entities.Reservation;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Date;
import java.util.List;

public class ReservationService {

    public static WsGetReservations read() throws JSONException {

        // Initialisation de la réponse
        WsGetReservations response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Reservation> reservations = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des reservations depuis la base de données
            Query query = em.createQuery("FROM Reservation WHERE isActive = true");
            reservations = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetReservations(status, message, code, reservations);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetReservations(status, message, code, null);
        }

        return response;
    }

    public static WsGetSingleReservation readOne(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsGetSingleReservation response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Reservation reservation = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des constructeurs depuis la base de données
            reservation = em.find(Reservation.class, id);

            // Vérification de l'existence du constructeur
            if (reservation == null || !reservation.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("reservation_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSingleReservation(status, null, code, reservation);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSingleReservation(status, message, code, null);
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

        Reservation reservation;

        try {
            // Vérification des paramètres
            if (!isValidReservation(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_reservation", languageCode).text + " 'passenger', 'flightDetails', 'reservationClass'");
            }

            Long passenger_id = Long.parseLong(formParams.get("passenger").get(0));
            Long flightDetails_id = Long.parseLong(formParams.get("flightDetails").get(0));
            char reservationClass = formParams.get("reservationClass").get(0).charAt(0);

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence de la réservation
            Query query = em.createQuery("FROM Reservation r, FlightDetails fd, Passenger p WHERE fd.id = r.flightDetails AND p.id = r.passenger AND fd.id = :fd_id AND p.id = :p_id");
            query.setParameter("fd_id", flightDetails_id);
            query.setParameter("p_id", passenger_id);
            List<Reservation> reservations = query.getResultList();

            em.getTransaction().begin();

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
            em.persist(reservation);
            em.flush();
            em.getTransaction().commit();

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();

            status = "OK";
            code = 201;
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
            if (!isValidReservation(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_reservation", languageCode).text + " 'id', 'reservationClass'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));
            char reservationClass = formParams.get("reservationClass").get(0).charAt(0);

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération de la réservation
            Reservation reservation = em.find(Reservation.class, id);

            if (reservation == null || !reservation.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("reservation_does_not_exist", languageCode).text);
            }

            em.getTransaction().begin();

            // Modification de la réservation
            reservation.reservationClass = reservationClass;

            // Persistence de la réservation
            em.persist(reservation);
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

    public static WsResponse delete(final String acceptLanguage, final Long id) throws JSONException {

        // Initialisation de la réponse
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération de la réservation depuis la base de données
            Reservation reservation = em.find(Reservation.class, id);

            // Suppression de la réservation
            reservation.isActive = false;

            // Persistence de la reservation
            em.getTransaction().begin();
            em.persist(reservation);
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

    private static boolean isValidReservation(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        if (!formParams.containsKey("reservationClass") || !APIFormat.isValidString(formParams.get("reservationClass").get(0), 1, 1))
            return false;
        if (!isUpdate && (!formParams.containsKey("flightDetails") || !APIFormat.isValidLong(formParams.get("flightDetails").get(0))))
            return false;
        return isUpdate || (formParams.containsKey("passenger") && APIFormat.isValidLong(formParams.get("passenger").get(0)));
    }
}
