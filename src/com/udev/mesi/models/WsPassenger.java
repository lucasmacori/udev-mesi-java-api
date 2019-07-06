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
    public String salt;
    public char pepper;
    public String firstName;
    public String lastName;
    public char gender;
    public Date birthday;
    public String phoneNumber;
    public String IDNumber;
    public boolean isActive;

    public WsPassenger() {
    }

    public WsPassenger(Long id, String email, String hash, String salt, char pepper, String firstName, String lastName, char gender, Date birthday, String phoneNumber, String IDNumber, boolean isActive) {
        this.id = id;
        this.email = email;
        this.hash = hash;
        this.salt = salt;
        this.pepper = pepper;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.IDNumber = IDNumber;
        this.isActive = isActive;
    }

    public static WsPassenger[] getArrayFromList(List<Passenger> passengers) {
        try {
            WsPassenger[] passengers_array = new WsPassenger[passengers.size()];
            for (int i = 0; i < passengers.size(); i++) {
                passengers_array[i] = passengers.get(i).toWs();
            }
            return passengers_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
