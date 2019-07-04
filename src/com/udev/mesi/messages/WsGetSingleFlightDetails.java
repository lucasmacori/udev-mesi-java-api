package com.udev.mesi.messages;

import com.udev.mesi.models.WsFlightDetails;
import main.java.com.udev.mesi.entities.FlightDetails;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSingleFlightDetails extends WsResponse {

    public WsFlightDetails flightDetails;

    public WsGetSingleFlightDetails() {
        super();
    }

    public WsGetSingleFlightDetails(String status, String message, int code, FlightDetails flightDetails) {
        super(status, message, code);
        if (flightDetails != null) {
            this.flightDetails = flightDetails.toWs();
        }
    }
}