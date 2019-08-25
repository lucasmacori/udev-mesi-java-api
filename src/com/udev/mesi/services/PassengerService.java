package com.udev.mesi.services;

import com.udev.mesi.config.APIFormat;
import com.udev.mesi.config.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.*;
import main.java.com.udev.mesi.entities.Passenger;
import main.java.com.udev.mesi.entities.Reservation;
import org.hibernate.Session;
import org.json.JSONException;

import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class PassengerService {

    public static WsGetPassengers read() throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetPassengers response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Passenger> passengers = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des constructeurs depuis la base de données
            Query query = session.createQuery("FROM Passenger WHERE isActive = true ORDER BY lastName, firstName");
            passengers = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetPassengers(status, message, code, passengers);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetPassengers(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsExists emailExists(final String email, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsExists response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence de l'email
            Query query = session.createQuery("SELECT COUNT(p.id) FROM Passenger p WHERE p.email = :email");
            query.setParameter("email", email);
            int count = Integer.parseInt(query.getResultList().get(0).toString());

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsExists(status, null, code, (count == 1));
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsExists(status, message, code, false);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsExists phoneNumberExists(final String phoneNumber, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsExists response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence du numéro de téléphone
            Query query = session.createQuery("SELECT COUNT(p.id) FROM Passenger p WHERE p.phoneNumber = :phoneNumber");
            query.setParameter("phoneNumber", phoneNumber);
            int count = Integer.parseInt(query.getResultList().get(0).toString());

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsExists(status, null, code, (count == 1));
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsExists(status, message, code, false);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsExists IDNumberExists(final String IDNumber, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsExists response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence du numéro de téléphone
            Query query = session.createQuery("SELECT COUNT(p.id) FROM Passenger p WHERE p.IDNumber = :IDNumber");
            query.setParameter("IDNumber", IDNumber);
            int count = Integer.parseInt(query.getResultList().get(0).toString());

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsExists(status, null, code, (count == 1));
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsExists(status, message, code, false);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsGetSinglePassenger readOne(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetSinglePassenger response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des passagers depuis la base de données
            passenger = session.find(Passenger.class, id);

            // Vérification de l'existence du passager
            if (passenger == null || !passenger.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("passenger_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSinglePassenger(status, null, code, passenger);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSinglePassenger(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsGetReservations readReservations(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetReservations response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Reservation> reservations = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des constructeurs depuis la base de données
            Query query = session.createQuery("SELECT r FROM Reservation r, Passenger p, FlightDetails fd, Flight f WHERE r.isActive = true AND p.isActive = true AND fd.isActive = true AND f.isActive = true AND r.passenger = p AND r.flightDetails = fd AND fd.flight = f AND p.id = :passengerId ORDER BY fd.departureDateTime, fd.arrivaleDateTime, f.departureCity, f.arrivalCity");
            query.setParameter("passengerId", id);
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

    public static WsResponse create(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger;

        try {
            session = Database.sessionFactory.openSession();

            // Vérification des paramètres
            if (!isValidPassenger(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_passenger", languageCode).text + " 'email', 'firstName', 'lastName', 'gender', 'birthday', 'password', 'phoneNumber', 'IDNumber'");
            }

            // Récupération des paramètres
            String email = formParams.get("email").get(0);
            String password = formParams.get("password").get(0);
            String firstName = formParams.get("firstName").get(0);
            String lastName = formParams.get("lastName").get(0);
            char gender = formParams.get("gender").get(0).toUpperCase().toCharArray()[0];
            Date birthday = APIFormat.DATE_FORMAT.parse(formParams.get("birthday").get(0));
            String phoneNumber = formParams.get("phoneNumber").get(0);
            String IDNumber = formParams.get("IDNumber").get(0);

            // Vérification de l'existence du passager
            Query query = session.createQuery("FROM Passenger WHERE email = :email OR phoneNumber = :phoneNumber OR IDNumber = :IDNumber");
            query.setParameter("email", email);
            query.setParameter("phoneNumber", phoneNumber);
            query.setParameter("IDNumber", IDNumber);
            List<Passenger> passengers = query.getResultList();

            session.getTransaction().begin();

            if (passengers.size() == 1) {
                if (passengers.get(0).isActive) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("passenger_already_exists", languageCode).text);
                } else {
                    // Archivage du passager
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    passengers.get(0).email = timestamp.toString() + "";
                    passengers.get(0).phoneNumber = timestamp.getTime() + "";
                    passengers.get(0).IDNumber = timestamp.getTime() + "";

                    // Sauvegarde du passager
                    session.persist(passengers.get(0));
                    session.flush();
                }
            }
            // Création du passager
            passenger = new Passenger();
            passenger.email = email;
            passenger.hash = AuthService.hash(password);
            passenger.firstName = firstName;
            passenger.lastName = lastName;
            passenger.gender = gender;
            passenger.birthday = birthday;
            passenger.phoneNumber = phoneNumber;
            passenger.IDNumber = IDNumber;
            passenger.isActive = true;

            // Validation des changements
            session.persist(passenger);
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

    public static WsResponse update(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            session = Database.sessionFactory.openSession();

            // Vérification des paramètres
            if (!isValidPassenger(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_passenger", languageCode).text + " 'id'");
            }

            // Récupération des paramètres
            long id = Long.parseLong(formParams.get("id").get(0));
            String email = null;
            String password = null;
            String firstName = null;
            String lastName = null;
            char gender = ' ';
            Date birthday = null;
            String phoneNumber = null;
            String IDNumber = null;
            if (formParams.containsKey("email")) {
                email = formParams.get("email").get(0);
            }
            if (formParams.containsKey("password")) {
                password = formParams.get("password").get(0);
            }
            if (formParams.containsKey("firstName")) {
                firstName = formParams.get("firstName").get(0);
            }
            if (formParams.containsKey("lastName")) {
                lastName = formParams.get("lastName").get(0);
            }
            if (formParams.containsKey("gender")) {
                gender = formParams.get("gender").get(0).toUpperCase().toCharArray()[0];
            }
            if (formParams.containsKey("birthday")) {
                birthday = APIFormat.DATE_FORMAT.parse(formParams.get("birthday").get(0));
            }
            if (formParams.containsKey("phoneNumber")) {
                phoneNumber = formParams.get("phoneNumber").get(0);
            }
            if (formParams.containsKey("IDNumber")) {
                IDNumber = formParams.get("IDNumber").get(0);
            }

            // Récupération du passager
            Passenger passenger = session.find(Passenger.class, id);

            if (passenger == null || !passenger.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("passenger_does_not_exist", languageCode).text);
            }

            // Récupération des passagers depuis la base de données
            Query query = session.createQuery("FROM Passenger WHERE (email = :email OR phoneNumber = :phoneNumber OR IDNumber = :IDNumber) AND id <> :id");
            query.setParameter("email", email);
            query.setParameter("phoneNumber", phoneNumber);
            query.setParameter("IDNumber", IDNumber);
            query.setParameter("id", id);
            List<Passenger> passengers = query.getResultList();

            session.getTransaction().begin();

            // Vérification de l'existence du passager
            if (passengers.size() == 1) {
                throw new Exception(MessageService.getMessageFromCode("passenger_already_exists", languageCode).text);
            }

            // Modification du passager
            if (email != null) passenger.email = email;
            if (password != null) passenger.hash = AuthService.hash(password);
            if (firstName != null) passenger.firstName = firstName;
            if (lastName != null) passenger.lastName = lastName;
            if (gender != ' ') passenger.gender = gender;
            if (birthday != null) passenger.birthday = birthday;
            if (phoneNumber != null) passenger.phoneNumber = phoneNumber;
            if (IDNumber != null) passenger.IDNumber = IDNumber;

            // Persistence du passager
            session.persist(passenger);
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

    public static WsResponse delete(final String acceptLanguage, final Long id) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        String status = "KO";
        String message = null;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        List<Passenger> passengers = null;
        Passenger passager = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des passagers depuis la base de données
            Query query = session.createQuery("FROM Passenger WHERE isActive = true AND id = :id ORDER BY lastName, firstName, email");
            query.setParameter("id", id);
            passengers = query.getResultList();

            // Vérification de l'existence du passager
            if (passengers.size() == 0) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("passenger_does_not_exist", languageCode).text);
            }

            passager = passengers.get(0);
            passager.isActive = false;

            // Persistence du passager
            session.getTransaction().begin();
            session.persist(passager);
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

    private static boolean isValidPassenger(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        if ((formParams.containsKey("email") || !isUpdate) && (!formParams.containsKey("email") || !APIFormat.isValidEmail(formParams.get("email").get(0))))
            return false;
        if ((formParams.containsKey("password") || !isUpdate) && (!formParams.containsKey("password") && !APIFormat.isValidString(formParams.get("password").get(0), 8, 50)))
            return false;
        if ((formParams.containsKey("firstName") || !isUpdate) && (!formParams.containsKey("firstName") && !APIFormat.isValidString(formParams.get("firstName").get(0), 2, 35)))
            return false;
        if ((formParams.containsKey("lastName") || !isUpdate) && (!formParams.containsKey("lastName") && !APIFormat.isValidString(formParams.get("firstName").get(0), 2, 35)))
            return false;
        if ((formParams.containsKey("gender") || !isUpdate) && (!formParams.containsKey("gender") && APIFormat.isValidGender(formParams.get("gender").get(0))))
            return false;
        if ((formParams.containsKey("birthday") || !isUpdate) && (!formParams.containsKey("birthday") && APIFormat.isValidDate(formParams.get("birthday").get(0))))
            return false;
        if ((formParams.containsKey("phoneNumber") || !isUpdate) && (!formParams.containsKey("phoneNumber") && APIFormat.isValidString(formParams.get("phoneNumber").get(0), 6, 15)))
            return false;
        return (!formParams.containsKey("IDNumber") && isUpdate) || (formParams.containsKey("IDNumber") || !APIFormat.isValidString(formParams.get("IDNumber").get(0), 8, 20));
    }

    public static Passenger exists(long pk) {
        Session session = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération du constructeur
            Passenger passenger = session.find(Passenger.class, pk);
            if (passenger == null || !passenger.isActive) {
                return null;
            }
            return passenger;
        } catch (Exception e) {
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
