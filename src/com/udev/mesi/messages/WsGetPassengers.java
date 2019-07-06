package com.udev.mesi.messages;

import com.udev.mesi.models.WsPassenger;
import main.java.com.udev.mesi.entities.Passenger;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetPassengers extends WsResponse {
    public WsPassenger[] passengers;

    public WsGetPassengers() {
        super();
    }

    public WsGetPassengers(String status, String message, int code, List<Passenger> passengers) {
        super(status, message, code);
        this.passengers = WsPassenger.getArrayFromList(passengers, false);
    }
}
