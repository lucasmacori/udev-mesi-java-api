package com.udev.mesi.config;

import main.java.com.udev.mesi.entities.AppUser;
import main.java.com.udev.mesi.entities.Language;
import main.java.com.udev.mesi.entities.Message;
import main.java.com.udev.mesi.entities.Report;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public class ApplicationServletContextListener implements ServletContextListener {
    private static Language insertIfNotExistsLanguage(Language language) {
        Session session = null;
        Query query;
        Language returnedLanguage = null;

        try {
            // Connexion à la base de données
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence de la langue
            query = session.createQuery("FROM Language WHERE code = :code");
            query.setParameter("code", language.code);
            List<Language> languages = query.getResultList();

            // Création de la langue si inexistante
            if (languages.size() == 0) {
                session.getTransaction().begin();
                session.persist(language);

                query = session.createQuery("FROM Language WHERE code = :code");
                query.setParameter("code", language.code);
                languages = query.getResultList();

                session.getTransaction().commit();
            }

            returnedLanguage = languages.get(0);

        } catch (Exception e) {
            if (session != null && session.isOpen() && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return returnedLanguage;
    }

    private static void insertIfNotExistsMessage(Message message) {
        Session session = null;
        Query query;

        try {
            // Connexion à la base de données
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence du message
            query = session.createQuery("SELECT m FROM Message m, Language l WHERE m.language = l AND m.code = :code AND l.code = :language");
            query.setParameter("code", message.code);
            query.setParameter("language", message.language.code);
            List<Message> messages = query.getResultList();

            // Création du message si inexistant
            if (messages.size() == 0) {
                session.getTransaction().begin();
                session.persist(message);
                session.flush();
                session.getTransaction().commit();
            }

        } catch (Exception e) {
            if (session != null && session.isOpen() && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private static void insertIfNotExistsReport(Report report) {
        Session session = null;
        Query query;

        try {
            // Connexion à la base de données
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence du rapport
            query = session.createQuery("FROM Report WHERE code = :code");
            query.setParameter("code", report.code);
            List<Report> reports = query.getResultList();

            // Création du rapport si inexistant
            if (reports.size() == 0) {
                session.getTransaction().begin();
                report.isActive = true;
                session.persist(report);
                session.flush();
                session.getTransaction().commit();
            }

        } catch (Exception e) {
            if (session != null && session.isOpen() && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {

        // Languages
        Language french = insertIfNotExistsLanguage(new Language("fr", "Français"));
        Language english = insertIfNotExistsLanguage(new Language("en", "English"));
        Language italian = insertIfNotExistsLanguage(new Language("it", "Italiano"));

        // Messages
        /// Génériques
        insertIfNotExistsMessage(new Message("is_not_an_integer", french, "n'est pas un nombre entier"));
        insertIfNotExistsMessage(new Message("is_not_an_integer", english, "is not an integer"));
        insertIfNotExistsMessage(new Message("is_not_an_integer", italian, "non é un numero intero"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", french, "n'est pas un booléen"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", english, "is not a boolean"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", italian, "non é un booleano"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", french, "n'est pas une date"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", english, "is not a date"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", italian, "non é una data"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", french, "n'est pas valide"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", english, "is not valid"));
        insertIfNotExistsMessage(new Message("is_not_a_boolean", italian, "non é valido"));

        /// Constructeurs
        insertIfNotExistsMessage(new Message("invalid_manufacturer", french, "Le constructeur est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_manufacturer", english, "The manufacturer is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_manufacturer", italian, "Il costruttore non è valido, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("manufacturer_already_exists", french, "Le constructeur existe déjà"));
        insertIfNotExistsMessage(new Message("manufacturer_already_exists", english, "The manufacturer already exists"));
        insertIfNotExistsMessage(new Message("manufacturer_already_exists", italian, "Il costruttore esiste già"));
        insertIfNotExistsMessage(new Message("manufacturer_does_not_exist", french, "Le constructeur n'existe pas"));
        insertIfNotExistsMessage(new Message("manufacturer_does_not_exist", english, "The manufacturer does not exist"));
        insertIfNotExistsMessage(new Message("manufacturer_does_not_exist", italian, "Il costruttore non esiste"));

        /// Modèles
        insertIfNotExistsMessage(new Message("invalid_model", french, "Le modèle est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_model", english, "The model is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_model", italian, "Il modello non è valido, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("model_already_exists", french, "Le modèle existe déjà"));
        insertIfNotExistsMessage(new Message("model_already_exists", english, "The model already exists"));
        insertIfNotExistsMessage(new Message("model_already_exists", italian, "Il modello esiste già"));
        insertIfNotExistsMessage(new Message("model_does_not_exist", french, "Le modèle n'existe pas"));
        insertIfNotExistsMessage(new Message("model_does_not_exist", english, "The model does not exist"));
        insertIfNotExistsMessage(new Message("model_does_not_exist", italian, "Il modello non esiste"));

        /// Avions
        insertIfNotExistsMessage(new Message("invalid_plane", french, "L'avion est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_plane", english, "The plane is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_plane", italian, "L'aeroplano non è valido, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("plane_already_exists", french, "L'avion existe déjà"));
        insertIfNotExistsMessage(new Message("plane_already_exists", english, "The plane already exists"));
        insertIfNotExistsMessage(new Message("plane_already_exists", italian, "L'aeroplano esiste già"));
        insertIfNotExistsMessage(new Message("plane_does_not_exist", french, "L'avion n'existe pas"));
        insertIfNotExistsMessage(new Message("plane_does_not_exist", english, "The plane does not exist"));
        insertIfNotExistsMessage(new Message("plane_does_not_exist", italian, "L'aeroplano non esiste"));

        /// Vols
        insertIfNotExistsMessage(new Message("invalid_flight", french, "Le vol est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_flight", english, "The flight is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_flight", italian, "Il volo non è valido, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("flight_already_exists", french, "Le vol existe déjà"));
        insertIfNotExistsMessage(new Message("flight_already_exists", english, "The flight already exists"));
        insertIfNotExistsMessage(new Message("flight_already_exists", italian, "Il volo esiste già"));
        insertIfNotExistsMessage(new Message("flight_does_not_exist", french, "Le vol n'existe pas"));
        insertIfNotExistsMessage(new Message("flight_does_not_exist", english, "The flight does not exist"));
        insertIfNotExistsMessage(new Message("flight_does_not_exist", italian, "Il volo non esiste"));

        /// Détail de vols
        insertIfNotExistsMessage(new Message("invalid_flight_details", french, "Le détail du vol est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_flight_details", english, "The flight details are invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_flight_details", italian, "I dettagli del volo non sono validi, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("flight_details_already_exist", french, "Le détail du vol existe déjà"));
        insertIfNotExistsMessage(new Message("flight_details_already_exist", english, "The flight details already exist"));
        insertIfNotExistsMessage(new Message("flight_details_already_exist", italian, "I dettagli del volo esistono già"));
        insertIfNotExistsMessage(new Message("flight_details_do_not_exist", french, "Le détail du vol n'existe pas"));
        insertIfNotExistsMessage(new Message("flight_details_do_not_exist", english, "The flight details do not exist"));
        insertIfNotExistsMessage(new Message("flight_details_do_not_exist", italian, "I dettagli del volo non esistono"));

        /// Passagers
        insertIfNotExistsMessage(new Message("invalid_passenger", french, "Le passager est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_passenger", english, "The passenger is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_passenger", italian, "Il passeggero non è valido, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("passenger_already_exists", french, "Le passager existe déjà"));
        insertIfNotExistsMessage(new Message("passenger_already_exists", english, "The passenger already exists"));
        insertIfNotExistsMessage(new Message("passenger_already_exists", italian, "Il passeggero esiste già"));
        insertIfNotExistsMessage(new Message("passenger_does_not_exist", french, "Le passager n'existe pas"));
        insertIfNotExistsMessage(new Message("passenger_does_not_exist", english, "The passenger does not exist"));
        insertIfNotExistsMessage(new Message("passenger_does_not_exist", italian, "Il passeggero non esiste"));

        /// Réservations
        insertIfNotExistsMessage(new Message("invalid_reservation", french, "La réservation est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_reservation", english, "The reservation is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_reservation", italian, "La prenotazione non é valida, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("reservation_already_exists", french, "La réservation existe déjà"));
        insertIfNotExistsMessage(new Message("reservation_already_exists", english, "The reservation already exists"));
        insertIfNotExistsMessage(new Message("reservation_already_exists", italian, "La prenotazione esiste già"));
        insertIfNotExistsMessage(new Message("reservation_does_not_exist", french, "La réservation n'existe pas"));
        insertIfNotExistsMessage(new Message("reservation_does_not_exist", english, "The reservation does not exist"));
        insertIfNotExistsMessage(new Message("reservation_does_not_exist", italian, "La prenotazione non esiste"));

        /// Reports
        insertIfNotExistsMessage(new Message("invalid_report", french, "Le rapport est invalide, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_report", english, "The report is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_report", italian, "Il relazione non è valido, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("report_already_exists", french, "Le rapport existe déjà"));
        insertIfNotExistsMessage(new Message("report_already_exists", english, "The report already exists"));
        insertIfNotExistsMessage(new Message("report_already_exists", italian, "Il relazione esiste già"));
        insertIfNotExistsMessage(new Message("report_does_not_exist", french, "Le rapport n'existe pas"));
        insertIfNotExistsMessage(new Message("report_does_not_exist", english, "The report does not exist"));
        insertIfNotExistsMessage(new Message("report_does_not_exist", italian, "Il relazione non esiste"));

        /// Authentification
        insertIfNotExistsMessage(new Message("invalid_auth", french, "Les données de connexion sont invalides, veuillez renseigner les valeurs suivantes:"));
        insertIfNotExistsMessage(new Message("invalid_auth", english, "The authentication data is invalid, please provide the following values:"));
        insertIfNotExistsMessage(new Message("invalid_auth", italian, "I dati della connessione non sono validi, inserire i seguenti valori:"));
        insertIfNotExistsMessage(new Message("invalid_username_password", french, "Mauvais nom d'utilisateur ou mot de passe"));
        insertIfNotExistsMessage(new Message("invalid_username_password", english, "Wrong username or password"));
        insertIfNotExistsMessage(new Message("invalid_username_password", italian, "Nome utente o password sbagliati"));
        insertIfNotExistsMessage(new Message("invalid_username_token", french, "Mauvais nom d'utilisateur ou token"));
        insertIfNotExistsMessage(new Message("invalid_username_token", english, "Wrong username or token"));
        insertIfNotExistsMessage(new Message("invalid_username_token", italian, "Nome utente o token sbagliati"));
        insertIfNotExistsMessage(new Message("user_already_exists", french, "L'utilisateur existe déjà"));
        insertIfNotExistsMessage(new Message("user_already_exists", english, "The user already exists"));
        insertIfNotExistsMessage(new Message("user_already_exists", italian, "L'utente esiste già"));
        insertIfNotExistsMessage(new Message("user_does_not_exist", french, "L'utilisateur n'existe pas"));
        insertIfNotExistsMessage(new Message("user_does_not_exist", english, "The user does not exist"));
        insertIfNotExistsMessage(new Message("user_does_not_exist", italian, "L'utente non esiste"));
        insertIfNotExistsMessage(new Message("user_not_authentified", french, "L'utilisateur n'est pas identifié"));
        insertIfNotExistsMessage(new Message("user_not_authentified", english, "The user is not authentified"));
        insertIfNotExistsMessage(new Message("user_not_authentified", italian, "L'utente non é autenticata"));

        // Rapports
        insertIfNotExistsReport(new Report("reservations_periode", "Récupère les réservations sur une période donnée",
                "SELECT COUNT(*) AS Nombre FROM Reservation WHERE reservationDate >= :minDate AND reservationDate <= :maxDate GROUP BY reservationDate ORDER BY reservationDate"));
        insertIfNotExistsReport(new Report("nombre_passagers_enregistres", "Récupère le nombre de passagers enregistrés",
                "SELECT COUNT(*) AS Nombre FROM Passenger"));
        insertIfNotExistsReport(new Report("nombre_passagers_reservations_periode", "Récupère le nombre de passagers ayant effectué au moins une réservation dans une période donnée",
                "SELECT SUM(Compteur) AS Nombre FROM (SELECT COUNT(DISTINCT passenger_id) AS Compteur FROM Reservation WHERE reservationDate >= :minDate AND reservationDate <= :maxDate ) AS requete"));
        insertIfNotExistsReport(new Report("nombre_annulations_periode", "Récupère le nombre d'annulations sur une période donnée",
                "SELECT COUNT(*) AS Nombre FROM Reservation WHERE isActive = false AND reservationDate >= :minDate AND reservationdate <= :maxDate "));
        insertIfNotExistsReport(new Report("nombre_avions_maintenance", "Récupère le nombre d'avions actuellement en maintance",
                "SELECT COUNT(*) AS Nombre FROM Plane WHERE isUnderMaintenance = true"));
        insertIfNotExistsReport(new Report("avion_plus_vols_periode", "Récupère l'avion ayant effectué le plus de vols sur une période donnée",
                "SELECT COUNT(fd.id) AS Nombre, p.ARN AS ARN, m.name AS Modele, c.name AS Constructeur FROM Plane p, Model m, Manufacturer c, FlightDetails fd WHERE p.isActive = true AND m.isActive = true AND c.isActive = true AND m.id = p.model_id AND c.id = m.manufacturer_id AND fd.plane_arn = p.ARN AND fd.departuredatetime >= :minDate AND fd.arrivaledatetime <= :maxDate GROUP BY p.ARN, m.name, c.name HAVING COUNT(fd.id) = (SELECT MAX((SELECT DISTINCT COUNT(DISTINCT a1.ARN) FROM Plane a1, FlightDetails fd1 WHERE fd1.plane_arn = a1.ARN)))"));

        // Compte admin
        Session session = null;
        Query query;

        try {
            // Connexion à la base de données
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence du rapport
            query = session.createQuery("FROM AppUser WHERE username = :username");
            query.setParameter("username", "admin");
            List<AppUser> appUsers = query.getResultList();

            // Création du rapport si inexistant
            if (appUsers.size() == 0) {
                session.getTransaction().begin();
                session.persist(new AppUser("admin", "$2a$12$GSWojTSkm0ZROczOm5brGOuHkL.xYUzo2k2HsvnS3C/h8zXHBVKPO"));
                session.flush();
                session.clear();
                session.getTransaction().commit();
            }

        } catch (Exception e) {
            if (session != null && session.isOpen() && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }
}
