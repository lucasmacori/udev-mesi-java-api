package com.udev.mesi.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsFlight {
    public Long id;
    public String departureCity;
    public String arrivalCity;
    public boolean isActive;

    public WsFlight() {
    }

    public WsFlight(Long id, String departureCity, String arrivalCity, boolean isActive) {
        this.id = id;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.isActive = isActive;
    }
}
