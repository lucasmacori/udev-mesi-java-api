package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Passenger;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class WsPassenger {

    public Long id;
    public String email;
    public String hash;
    public String firstName;
    public String lastName;
    public String gender;
    public Date birthday;
    public String phoneNumber;
    public String IDNumber;
    private boolean isActive;

    public WsPassenger() {
    }

    public WsPassenger(Long id, String email, String hash, String firstName, String lastName, String gender, Date birthday, String phoneNumber, String IDNumber, boolean isActive) {
        this.id = id;
        this.email = email;
        this.hash = hash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.IDNumber = IDNumber;
        this.isActive = isActive;
    }

    public static WsPassenger[] getArrayFromList(List<Passenger> passengers, boolean includeHash) {
        try {
            WsPassenger[] passengers_array = new WsPassenger[passengers.size()];
            for (int i = 0; i < passengers.size(); i++) {
                WsPassenger passenger = passengers.get(i).toWs();
                if (!includeHash) passenger.hash = null;
                passengers_array[i] = passenger;
            }
            return passengers_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
