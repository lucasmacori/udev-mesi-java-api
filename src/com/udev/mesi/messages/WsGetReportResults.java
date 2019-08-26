package com.udev.mesi.messages;

import com.udev.mesi.models.WsReportResults;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetReportResults extends WsResponse {
    public WsReportResults results;

    public WsGetReportResults() {
        super();
    }

    public WsGetReportResults(String status, String message, int code, WsReportResults results) {
        super(status, message, code);
        this.results = results;
    }
}
