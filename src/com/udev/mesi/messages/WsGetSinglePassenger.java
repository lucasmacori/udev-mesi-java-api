package com.udev.mesi.messages;

import com.udev.mesi.models.WsPassenger;
import main.java.com.udev.mesi.entities.Passenger;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSinglePassenger extends WsResponse {
    public WsPassenger passenger;

    public WsGetSinglePassenger() {
        super();
    }

    public WsGetSinglePassenger(String status, String message, int code, Passenger passenger) {
        super(status, message, code);
        if (passenger != null) {
            this.passenger = passenger.toWs();
            this.passenger.hash = null;
        }
    }
}
