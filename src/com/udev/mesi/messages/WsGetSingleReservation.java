package com.udev.mesi.messages;

import com.udev.mesi.models.WsReservation;
import main.java.com.udev.mesi.entities.Reservation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSingleReservation extends WsResponse {

    public WsReservation reservation;

    public WsGetSingleReservation() {
        super();
    }

    public WsGetSingleReservation(String status, String message, int code, Reservation reservation) {
        super(status, message, code);
        if (reservation != null) {
            this.reservation = reservation.toWs();
        }
    }
}
