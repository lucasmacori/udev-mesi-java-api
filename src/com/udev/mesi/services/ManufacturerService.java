package com.udev.mesi.services;

import com.udev.mesi.config.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetManufacturers;
import com.udev.mesi.messages.WsGetSingleManufacturer;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.Manufacturer;
import org.hibernate.Session;
import org.json.JSONException;

import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.sql.Timestamp;
import java.util.List;

public class ManufacturerService {

    public static WsGetManufacturers read() throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetManufacturers response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Manufacturer> manufacturers = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des constructeurs depuis la base de données
            Query query = session.createQuery("FROM Manufacturer WHERE isActive = true ORDER BY name");
            manufacturers = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetManufacturers(status, message, code, manufacturers);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetManufacturers(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsGetSingleManufacturer readOne(final long id, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetSingleManufacturer response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        Manufacturer manufacturer = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des constructeurs depuis la base de données
            manufacturer = session.find(Manufacturer.class, id);

            // Vérification de l'existence du constructeur
            if (manufacturer == null || !manufacturer.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("manufacturer_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSingleManufacturer(status, null, code, manufacturer);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSingleManufacturer(status, message, code, null);
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

        Manufacturer manufacturer;

        try {
            session = Database.sessionFactory.openSession();

            // Vérification des paramètres
            if (!isValidManufacturer(formParams, false)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_manufacturer", languageCode).text + " 'name'");
            }

            String name = formParams.get("name").get(0);

            // Vérification de l'existence du constructeur
            Query query = session.createQuery("FROM Manufacturer WHERE name = :name");
            query.setParameter("name", name);
            List<Manufacturer> manufacturers = query.getResultList();

            session.getTransaction().begin();

            if (manufacturers.size() == 1) {
                manufacturer = manufacturers.get(0);
                if (manufacturer.isActive) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("manufacturer_already_exists", languageCode).text);
                } else {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    manufacturer.name += "_old_" + timestamp.getTime();
                    session.persist(manufacturer);
                    session.flush();

                    // Création du constructeur
                    manufacturer = new Manufacturer();
                    manufacturer.name = name;
                }
            } else {
                // Création du constructeur
                manufacturer = new Manufacturer();
                manufacturer.name = name;
            }
            manufacturer.isActive = true;

            // Validation des changements
            session.persist(manufacturer);
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
            if (!isValidManufacturer(formParams, true)) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_manufacturer", languageCode).text + " 'id', 'name'");
            }

            long id = Long.parseLong(formParams.get("id").get(0));
            String name = formParams.get("name").get(0);

            // Récupération du constructeur
            Manufacturer manufacturer = session.find(Manufacturer.class, id);

            if (manufacturer == null || !manufacturer.isActive) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("manufacturer_does_not_exist", languageCode).text);
            }

            // Récupération des constructeurs depuis la base de données
            Query query = session.createQuery("FROM Manufacturer WHERE name = :name");
            query.setParameter("name", name);
            List<Manufacturer> manufacturers = query.getResultList();

            session.getTransaction().begin();

            // Vérification de l'existence du constructeur
            if (manufacturers.size() == 1) {
                Manufacturer oldManufacturer = manufacturers.get(0);
                if (oldManufacturer.isActive) {
                    code = 400;
                    throw new Exception(MessageService.getMessageFromCode("manufacturer_already_exists", languageCode).text);
                } else {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    oldManufacturer.name += "_old_" + timestamp.getTime();
                    session.persist(oldManufacturer);
                    session.flush();
                }
            }

            // Modification du constructeur
            manufacturer.name = name;

            // Persistence du constructeur
            session.persist(manufacturer);
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

        List<Manufacturer> manufacturers = null;
        Manufacturer manufacturer = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des constructeurs depuis la base de données
            Query query = session.createQuery("FROM Manufacturer WHERE isActive = true AND id = :id");
            query.setParameter("id", id);
            manufacturers = query.getResultList();

            // Vérification de l'existence du constructeur
            if (manufacturers.size() == 0) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("manufacturer_does_not_exist", languageCode).text);
            }

            manufacturer = manufacturers.get(0);
            manufacturer.isActive = false;

            // Persistence du constructeur
            session.getTransaction().begin();
            session.persist(manufacturer);
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

    private static boolean isValidManufacturer(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
        if (isUpdate && !formParams.containsKey("id")) return false;
        return formParams.containsKey("name");
    }

    public static Manufacturer exists(long pk) {
        Session session = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération du constructeur
            Manufacturer manufacturer = session.find(Manufacturer.class, pk);
            if (manufacturer == null || !manufacturer.isActive) {
                return null;
            }
            return manufacturer;
        } catch (Exception e) {
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
