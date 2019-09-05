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
            Query query = session.createQuery("SELECT u FROM AppUser u WHERE u.username = :username");
            query.setParameter("username", username);
            List<AppUser> appUsers = query.getResultList();
            if (appUsers.size() != 1) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_username_password", languageCode).text);
            }
            AppUser appUser = appUsers.get(0);

            // Vérification du mot de passe saisi
            if (verify(password, appUser.hash)) {

                session.getTransaction().begin();

                // Suppression des anciens tokens de l'utilisateur
                query = session.createQuery("DELETE FROM Token root WHERE root.token IN ( SELECT t.token FROM Token t, AppUser u WHERE t.appUser = u AND u.username = :username)");
                query.setParameter("username", username);
                query.executeUpdate();

                // Création d'un nouveau token
                Token token = new Token();
                token.appUser = appUser;
                token.token = generateToken();

                // Sauvegarde du token
                session.persist(token);
                session.getTransaction().commit();

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
}
