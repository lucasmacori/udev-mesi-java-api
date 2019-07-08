package com.udev.mesi.messages;

import com.udev.mesi.models.WsReservation;
import main.java.com.udev.mesi.entities.Reservation;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetReservations extends WsResponse {
    public WsReservation[] reservations;

    public WsGetReservations() {
        super();
    }

    public WsGetReservations(String status, String message, int code, List<Reservation> reservations) {
        super(status, message, code);
        this.reservations = WsReservation.getArrayFromList(reservations);
    }
}
