package com.udev.mesi.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsReportResults {
    public String code;
    public String description;
    public String[] fields;
    public String[][] results;

    public WsReportResults() {
    }

    public WsReportResults(String code, String description, String[] fields, String[][] results) {
        this.code = code;
        this.description = description;
        this.fields = fields;
        this.results = results;
    }
}
