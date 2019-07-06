package com.udev.mesi.models;

import com.udev.mesi.config.APIDateFormat;
import main.java.com.udev.mesi.entities.FlightDetails;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class WsFlightDetails {
    public Long id;
    public String departureDateTime;
    public String arrivalDateTime;
    public boolean isActive;
    public WsFlight flight;
    public WsPlane plane;

    public WsFlightDetails() {
    }

    public WsFlightDetails(Long id, Date departureDateTime, Date arrivalDateTime, boolean isActive, WsFlight flight, WsPlane plane) {
        this.id = id;
        this.departureDateTime = APIDateFormat.DATETIME_FORMAT.format(departureDateTime);
        this.arrivalDateTime = APIDateFormat.DATETIME_FORMAT.format(arrivalDateTime);
        this.isActive = isActive;
        this.flight = flight;
        this.plane = plane;
    }

    public static WsFlightDetails[] getArrayFromList(List<FlightDetails> flightDetails) {
        try {
            WsFlightDetails[] flightDetails_array = new WsFlightDetails[flightDetails.size()];
            for (int i = 0; i < flightDetails.size(); i++) {
                flightDetails_array[i] = flightDetails.get(i).toWs();
            }
            return flightDetails_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
