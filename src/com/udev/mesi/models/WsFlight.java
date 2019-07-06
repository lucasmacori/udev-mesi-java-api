package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Flight;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsFlight {
    public Long id;
    public String departureCity;
    public String arrivalCity;
    private boolean isActive;

    public WsFlight() {
    }

    public WsFlight(Long id, String departureCity, String arrivalCity, boolean isActive) {
        this.id = id;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.isActive = isActive;
    }

    public static WsFlight[] getArrayFromList(List<Flight> flights) {
        try {
            WsFlight[] flights_array = new WsFlight[flights.size()];
            for (int i = 0; i < flights.size(); i++) {
                flights_array[i] = flights.get(i).toWs();
            }
            return flights_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
