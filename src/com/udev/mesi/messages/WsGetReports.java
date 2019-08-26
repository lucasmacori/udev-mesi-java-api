package com.udev.mesi.messages;

import com.udev.mesi.models.WsReport;
import main.java.com.udev.mesi.entities.Report;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetReports extends WsResponse {
    public WsReport[] reports;

    public WsGetReports() {
        super();
    }

    public WsGetReports(String status, String message, int code, List<Report> reports) {
        super(status, message, code);
        this.reports = WsReport.getArrayFromList(reports);
    }
}
