package com.udev.mesi.messages;

import com.udev.mesi.models.WsReport;
import main.java.com.udev.mesi.entities.Report;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSingleReport extends WsResponse {

    public WsReport report;

    public WsGetSingleReport() {
        super();
    }

    public WsGetSingleReport(String status, String message, int code, Report report) {
        super(status, message, code);
        if (report != null) {
            this.report = report.toWs();
        }
    }

    public WsGetSingleReport(String status, String message, int code, WsReport report) {
        super(status, message, code);
        this.report = report;
    }
}