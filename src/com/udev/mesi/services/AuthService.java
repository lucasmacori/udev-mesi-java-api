package com.udev.mesi.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.udev.mesi.config.Database;
import com.udev.mesi.messages.WsGetToken;
import com.udev.mesi.messages.WsResponse;
import main.java.com.udev.mesi.entities.AppUser;
import main.java.com.udev.mesi.entities.Token;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MultivaluedMap;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class AuthService {

    public static WsResponse login(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) {

        Session session = null;

        WsResponse response;
        int code = 500;
        String message;
        String status = "KO";

        String username;
        String password;

        try {
            // Récupération de la langue de l'utilisateur
            String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

            // Récupération des paramètres
            if (!formParams.containsKey("username") || !formParams.containsKey("password")) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_auth", languageCode).text +
                        "'username', 'password'");
            }
            username = formParams.get("username").get(0);
            password = formParams.get("password").get(0);

            session = Database.sessionFactory.openSession();

            // Récupération de l'utilisateur
            AppUser appUser = appUserExists(username);
            if (appUser == null) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_username_password", languageCode).text);
            }

            // Vérification du mot de passe saisi
            if (verify(password, appUser.hash)) {

                session.getTransaction().begin();

                // Suppression des anciens tokens de l'utilisateur
                Query query = session.createQuery("DELETE FROM Token root WHERE root.token IN ( SELECT t.token FROM Token t, AppUser u WHERE t.appUser = u AND u.username = :username)");
                query.setParameter("username", username);
                query.executeUpdate();

                // Création d'un nouveau token
                Token token = new Token();
                token.appUser = appUser;
                token.token = generateToken();

                // Sauvegarde du token
                session.persist(token);
                session.getTransaction().commit();

                code = 200;
                status = "OK";
                return new WsGetToken(status, null, code, token.token);
            }
            code = 400;
            throw new Exception(MessageService.getMessageFromCode("invalid_username_password", languageCode).text);
        } catch (Exception e) {
            status = "KO";
            message = e.getMessage();
            return new WsResponse(status, message, code);
        } finally {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
                session.close();
            }
        }
    }

    public static WsResponse create(final String acceptLanguage, final MultivaluedMap<String, String> formParams, final String username, final String token) {
        Session session = null;

        WsResponse response;
        int code = 500;
        String message;
        String status = "KO";

        String created_username;
        String created_password;

        try {
            // Récupération de la langue de l'utilisateur
            String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

            // Vérification du token
            if (!AuthService.verifyToken(username, token)) {
                code = 401;
                throw new Exception(MessageService.getMessageFromCode("user_not_authentified", languageCode).text);
            }

            // Récupération des paramètres
            if (!formParams.containsKey("username") || !formParams.containsKey("password")) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_auth", languageCode).text +
                        "'username', 'password'");
            }
            created_username = formParams.get("username").get(0);
            created_password = formParams.get("password").get(0);

            session = Database.sessionFactory.openSession();

            // Vérification de l'existence de l'utilisateur
            if (appUserExists(created_username) != null) {
                throw new Exception(MessageService.getMessageFromCode("user_already_exists", languageCode).text);
            }

            AppUser appUser = new AppUser();
            appUser.username = created_username;
            appUser.hash = hash(created_password);

            session.getTransaction().begin();
            session.persist(appUser);
            session.getTransaction().commit();

            code = 201;
            status = "OK";
            response = new WsResponse(status, null, code);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsResponse(status, message, code);
        } finally {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
                session.close();
            }
        }
        return response;
    }

    public static WsResponse update(final String acceptLanguage, final MultivaluedMap<String, String> formParams, final String username, final String token) {
        Session session = null;

        WsResponse response;
        int code = 500;
        String message;
        String status = "KO";

        String created_username = null;
        String created_password = null;

        try {
            // Récupération de la langue de l'utilisateur
            String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

            // Vérification du token
            if (!AuthService.verifyToken(username, token)) {
                code = 401;
                throw new Exception(MessageService.getMessageFromCode("user_not_authentified", languageCode).text);
            }

            // Récupération des paramètres
            if (formParams.containsKey("username")) {
                created_username = formParams.get("username").get(0);
            }
            if (formParams.containsKey("password")) {
                created_password = formParams.get("password").get(0);
            }

            session = Database.sessionFactory.openSession();

            // Vérification de l'existence de l'utilisateur
            AppUser appUser = appUserExists(created_username);
            if (appUser == null) {
                throw new Exception(MessageService.getMessageFromCode("user_does_not_exist", languageCode).text);
            }

            if (created_username != null) {
                appUser.username = created_username;
            }
            if (created_password != null) {
                appUser.hash = hash(created_password);
            }

            session.getTransaction().begin();
            session.persist(appUser);
            session.getTransaction().commit();

            code = 200;
            status = "OK";
            response = new WsResponse(status, null, code);
        } catch (Exception e) {
            message = e.getMessage();
            session.getTransaction().rollback();
            response = new WsResponse(status, message, code);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return response;
    }

    public static String hash(String password) {
        // String hash
        // hash = new String(BCrypt.with(new SecureRandom()).hash(6, password.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verify(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

    private static String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String token = encoder.encodeToString(bytes);
        return token;
    }

    public static WsResponse verifyTokenService(final String acceptLanguage, final MultivaluedMap<String, String> formParams) {

        int code = 500;

        try {
            // Récupération de la langue de l'utilisateur
            String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

            // Récupération des paramètres d'entrée
            if (!formParams.containsKey("username") || !formParams.containsKey("token")) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_auth", languageCode).text +
                        "'username', 'token'");
            }
            String username = formParams.get("username").get(0);
            String token = formParams.get("token").get(0);

            // Vérification du token
            if (verifyToken(username, token)) {
                return new WsResponse("OK", null, 200);
            } else {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_username_token", languageCode).text);
            }
        } catch (Exception e) {
            return new WsResponse("KO", e.getMessage(), code);
        }
    }

    public static boolean verifyToken(String username, String token) {
        Session session = null;

        try {
            session = Database.sessionFactory.openSession();

            Query query = session.createQuery("SELECT COUNT(*) FROM Token t, AppUser u WHERE t.appUser = u AND t.token = :token AND u.username = :username");
            query.setParameter("username", username);
            query.setParameter("token", token);
            int count = Integer.parseInt(query.getResultList().get(0).toString());

            if (count > 0) {
                return true;
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    public static AppUser appUserExists(String username) {
        Session session = null;

        try {
            session = Database.sessionFactory.openSession();

            Query query = session.createQuery("SELECT u FROM AppUser u WHERE u.username = :username");
            query.setParameter("username", username);
            List<AppUser> appUsers = query.getResultList();
            if (appUsers.size() != 1) {
                throw new Exception();
            }
            return appUsers.get(0);
        } catch (Exception e) {
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
