package com.udev.mesi.messages;

import com.udev.mesi.models.WsFlight;
import main.java.com.udev.mesi.entities.Flight;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSingleFlight extends WsResponse {

    public WsFlight flight;

    public WsGetSingleFlight() {
        super();
    }

    public WsGetSingleFlight(String status, String message, int code, Flight flight) {
        super(status, message, code);
        if (flight != null) {
            this.flight = flight.toWs();
        }
    }
}
