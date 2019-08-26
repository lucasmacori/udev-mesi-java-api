package com.udev.mesi.services;

import com.udev.mesi.config.APIFormat;
import com.udev.mesi.config.Database;
import com.udev.mesi.exceptions.MessageException;
import com.udev.mesi.messages.WsGetReportResults;
import com.udev.mesi.messages.WsGetReports;
import com.udev.mesi.messages.WsGetSingleReport;
import com.udev.mesi.models.WsReportResults;
import main.java.com.udev.mesi.entities.Report;
import org.hibernate.Session;
import org.json.JSONException;

import javax.persistence.Query;
import javax.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import java.util.List;

public class ReportService {

    public static WsGetReports read() throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetReports response;
        String status = "KO";
        String message = null;
        int code = 500;

        List<Report> reports = null;

        try {
            session = Database.sessionFactory.openSession();

            // Récupération des constructeurs depuis la base de données
            Query query = session.createQuery("FROM Report WHERE isActive = true ORDER BY description");
            reports = query.getResultList();

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetReports(status, message, code, reports);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetReports(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsGetSingleReport readOne(final String reportCode, final String acceptLanguage) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetSingleReport response;
        String status = "KO";
        String message;
        int code = 500;

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            session = Database.sessionFactory.openSession();

            // Récupération du rapport depuis la base de données
            Query query = session.createQuery("From Report WHERE isActive = true AND code = :code");
            query.setParameter("code", reportCode);
            List<Report> reports = query.getResultList();

            // Vérification de l'existence du rapport
            if (reports.size() != 1) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("report_does_not_exist", languageCode).text);
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            response = new WsGetSingleReport(status, null, code, reports.get(0));
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetSingleReport(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }

    public static WsGetReportResults executeReport(final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {

        // Initialisation de la réponse
        Session session = null;
        WsGetReportResults response;
        String status = "KO";
        String message = null;
        int code = 500;
        String lastParameter = "";

        // Récupération de la langue de l'utilisateur
        String languageCode = MessageService.processAcceptLanguage(acceptLanguage);

        try {
            // Vérification des paramètres
            if (!formParams.containsKey("code")) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("invalid_report", languageCode).text + " 'code'");
            }
            String reportCode = formParams.get("code").get(0);

            session = Database.sessionFactory.openSession();

            // Récupération du rapport depuis la base de données
            Query query = session.createQuery("From Report WHERE isActive = true AND code = :code");
            query.setParameter("code", reportCode);
            List<Report> reports = query.getResultList();

            // Vérification de l'existence du rapport
            if (reports.size() != 1) {
                code = 400;
                throw new Exception(MessageService.getMessageFromCode("report_does_not_exist", languageCode).text);
            }

            Report report = reports.get(0);

            // Préparation de la requête du rapport
            query = session.createNativeQuery(report.query);

            // Récupération des valeurs de sortie
            String[] fields;
            String select = report.query.substring(6, report.query.toUpperCase().indexOf("FROM"));
            if (select.contains(",")) {
                fields = select.trim().split(",");
            } else {
                fields = new String[1];
                fields[0] = select.trim();
            }
            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].trim();
                if (fields[i].toUpperCase().contains(" AS ")) {
                    fields[i] = fields[i].substring(fields[i].indexOf(" AS ") + 4).trim();
                }
            }

            // Récupération des paramètres à injecter
            String sqlQuery = report.query;
            while (sqlQuery.contains(":")) {
                int index = sqlQuery.indexOf(':');
                sqlQuery = sqlQuery.substring(index + 1);
                String parameter = sqlQuery.substring(0, sqlQuery.indexOf(' '));
                lastParameter = parameter;

                // Vérification du paramètre actuel
                if (!formParams.containsKey(parameter)) {
                    throw new Exception(MessageService.getMessageFromCode("invalid_report", languageCode).text + " '" + parameter + "'");
                }

                // Vérification du type du paramètre et injection du paramètre dans la réquête préparée
                if (parameter.length() >= 4 && parameter.substring(parameter.length() - 4).toLowerCase().equals("date")) {
                    query.setParameter(parameter, APIFormat.DATETIME_FORMAT.parse(formParams.get(parameter).get(0)));
                } else {
                    query.setParameter(parameter, formParams.get(parameter).get(0));
                }
            }

            // Exécution de la requête SQL
            List resultList = query.getResultList();
            String[][] object = new String[resultList.size()][fields.length];
            for (int i = 0; i < resultList.size(); i++) {
                if (fields.length > 1) {
                    Object[] currentObject = (Object[]) resultList.get(i);
                    for (int j = 0; j < fields.length; j++) {
                        object[i][j] = String.valueOf(currentObject[j]);
                    }
                } else {
                    Object currentObject = resultList.get(i);
                    object[i][0] = String.valueOf(currentObject);
                }
            }

            // Création de la réponse JSON
            status = "OK";
            code = 200;
            WsReportResults results = new WsReportResults(report.code, report.description, fields, object);
            response = new WsGetReportResults(status, null, code, results);
        } catch (ParseException e) {
            try {
                message = "'" + lastParameter + "' " + MessageService.getMessageFromCode("is_not_valid", languageCode).text;
                code = 400;
            } catch (MessageException me) {
                code = 500;
            }
            response = new WsGetReportResults(status, message, code, null);
        } catch (Exception e) {
            message = e.getMessage();
            response = new WsGetReportResults(status, message, code, null);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return response;
    }
}
