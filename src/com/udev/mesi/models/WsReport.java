package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Report;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsReport {
    public String code;
    public String description;
    public String query;
    public boolean isActive;

    public WsReport() {
    }

    public WsReport(String code, String description, String query, boolean isActive) {
        this.code = code;
        this.description = description;
        this.query = query;
        this.isActive = isActive;
    }

    public static WsReport[] getArrayFromList(List<Report> reports) {
        try {
            WsReport[] reports_array = new WsReport[reports.size()];
            for (int i = 0; i < reports.size(); i++) {
                reports_array[i] = reports.get(i).toWs();
            }
            return reports_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
