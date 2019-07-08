package com.udev.mesi.services;

import com.udev.mesi.Database;
import com.udev.mesi.config.APIFormat;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsExists;
import com.udev.mesi.messages.WsGetPassengers;
import com.udev.mesi.messages.WsGetSinglePassenger;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Passenger;
import org.json.JSONException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Date;
import java.util.List;

public class PassengerService {

    public static WsGetPassengers read() throws JSONException {

        // Initialisation de la réponse
        WsGetPassengers response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Passenger> passengers = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des constructeurs depuis la base de données
            Query query = em.createQuery("FROM Passenger WHERE isActive = true");
            passengers = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetPassengers(status, message, code, passengers);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetPassengers(status, message, code, null);
        }

        return response;
    }

    public static WsExists emailExists(final String email, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsExists response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence de l'email
            Query query = em.createQuery("SELECT COUNT(p.id) FROM Passenger p WHERE p.email = :email");
            query.setParameter("email", email);
            int count = Integer.parseInt(query.getResultList().get(0).toString());

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsExists(status, null, code, (count == 1));

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsExists(status, message, code, false);
        }

        return response;
    }

    public static WsExists phoneNumberExists(final String phoneNumber, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsExists response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence du numéro de téléphone
            Query query = em.createQuery("SELECT COUNT(p.id) FROM Passenger p WHERE p.phoneNumber = :phoneNumber");
            query.setParameter("phoneNumber", phoneNumber);
            int count = Integer.parseInt(query.getResultList().get(0).toString());

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsExists(status, null, code, (count == 1));

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsExists(status, message, code, false);
        }

        return response;
    }

    public static WsExists IDNumberExists(final String IDNumber, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsExists response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence du numéro de téléphone
            Query query = em.createQuery("SELECT COUNT(p.id) FROM Passenger p WHERE p.IDNumber = :IDNumber");
            query.setParameter("IDNumber", IDNumber);
            int count = Integer.parseInt(query.getResultList().get(0).toString());

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsExists(status, null, code, (count == 1));

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsExists(status, message, code, false);
        }

        return response;
    }

    public static WsGetSinglePassenger readOne(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        WsGetSinglePassenger response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Passenger passenger = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération des passagers depuis la base de données
            passenger = em.find(Passenger.class, id);

            // Vérification de l'existence du passager
            if (passenger == null || !passenger.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("passenger_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSinglePassenger(status, null, code, passenger);

            // Fermeture du gestionnaire d'entités
            em.close();
            emf.close();
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSinglePassenger(status, message, code, null);
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

        Passenger passenger;

        try {
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

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification de l'existence du passager
            Query query = em.createQuery("FROM Passenger WHERE email = :email OR phoneNumber = :phoneNumber OR IDNumber = :IDNumber");
            query.setParameter("email", email);
            query.setParameter("phoneNumber", phoneNumber);
            query.setParameter("IDNumber", IDNumber);
            List<Passenger> passengers = query.getResultList();

            em.getTransaction().begin();

            if (passengers.size() == 1) {
                if (passengers.get(0).isActive) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("passenger_already_exists", languageCode).text);
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
            em.persist(passenger);
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

            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Récupération du passager
            Passenger passenger = em.find(Passenger.class, id);

            if (passenger == null || !passenger.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("passenger_does_not_exist", languageCode).text);
            }

            // Récupération des passagers depuis la base de données
            Query query = em.createQuery("FROM Passenger WHERE email = :email OR phoneNumber = :phoneNumber OR IDNumber = :IDNumber");
            query.setParameter("email", email);
            query.setParameter("phoneNumber", phoneNumber);
            query.setParameter("IDNumber", IDNumber);
            List<Passenger> passengers = query.getResultList();

            em.getTransaction().begin();

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
            em.persist(passenger);
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

        List<Passenger> passengers = null;
        Passenger passager = null;

        try {
            // Création du gestionnaire d'entités
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
            EntityManager em = emf.createEntityManager();

            // Vérification des paramètres
            if (!formParams.containsKey("id")) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_passenger", languageCode).text + " 'id'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));

            // Récupération des passagers depuis la base de données
            Query query = em.createQuery("FROM Passenger WHERE isActive = true AND id = :id");
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
            em.getTransaction().begin();
            em.persist(passager);
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
        // Création du gestionnaire d'entités
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Database.UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Récupération du constructeur
        Passenger passenger = em.find(Passenger.class, pk);
        if (passenger == null || !passenger.isActive) {
            return null;
        }
        return passenger;
    }
}
