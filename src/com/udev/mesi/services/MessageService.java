package com.udev.mesi.services;

import com.udev.mesi.config.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.exceptions.MessageNotFoundException;
import com.udev.mesi.models.WsMessage;
import main.java.com.udev.mesi.entities.Language;
import main.java.com.udev.mesi.entities.Message;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.List;

public class MessageService {

    private static final String DEFAULT_LANGUAGE_CODE = "fr";

    public static WsMessage getMessageFromCode(String messageCode, String languageCode) throws MessageException {
        Session session = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération du message
            Query query = session.createQuery("SELECT m FROM Message m, Language l WHERE l.id = m.language AND m.code = :messageCode AND l.code = :languageCode");
            query.setParameter("messageCode", messageCode);
            query.setParameter("languageCode", languageCode);
            List<Message> messages = query.getResultList();
            if (messages.size() > 0) {
                return messages.get(0).toWs();
            } else {
                throw new MessageNotFoundException("Le message '" + messageCode + "' pour le langage '" + languageCode + "' n'existe pas");
            }
        } catch (ClassCastException e) {
            throw new MessageException("Une erreur inconnue est survenue lors de la lecture du message '" + messageCode + "' pour le langage '" + languageCode + "'");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public static String processAcceptLanguage(String acceptLanguage) {
        Session session = null;

        try {
            session = Database.sessionFactory.openSession();

            // Vérification de l'existence de la language
            Query query = session.createQuery("FROM Language WHERE code = :code");
            query.setParameter("code", acceptLanguage);
            List<Language> languages = query.getResultList();

            if (languages.size() > 0) {
                return languages.get(0).code;
            }
            return DEFAULT_LANGUAGE_CODE;
        } catch (ClassCastException e) {
            return DEFAULT_LANGUAGE_CODE;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
