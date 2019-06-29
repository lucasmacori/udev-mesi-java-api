package com.udev.mesi.messages;

import com.udev.mesi.models.WsFlightDetails;
import main.java.com.udev.mesi.entities.FlightDetails;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetFlightDetails extends WsResponse {
    public WsFlightDetails[] flightDetails;

    public WsGetFlightDetails() {
        super();
    }

    public WsGetFlightDetails(String status, String message, int code, List<FlightDetails> flightDetails) {
        super(status, message, code);
        this.flightDetails = WsFlightDetails.getArrayFromList(flightDetails);
    }
}
