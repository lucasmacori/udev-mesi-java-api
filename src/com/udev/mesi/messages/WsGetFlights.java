package com.udev.mesi.messages;

import com.udev.mesi.models.WsFlight;
import main.java.com.udev.mesi.entities.Flight;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetFlights extends WsResponse {

    public WsFlight[] flights;

    public WsGetFlights() {
        super();
    }

    public WsGetFlights(String status, String message, int code, List<Flight> flights) {
        super(status, message, code);
        this.flights = WsFlight.getArrayFromList(flights);
    }
}
