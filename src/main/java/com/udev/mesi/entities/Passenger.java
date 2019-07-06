package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsPassenger;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Passenger implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    public Long id;

    @Column(nullable = false, length = 50, unique = true)
    public String email;

    @Column(length = 255)
    public String hash;

    @Column(length = 35, nullable = false)
    public String firstName;

    @Column(length = 40, nullable = false)
    public String lastName;

    @Column(length = 1, nullable = false)
    public char gender;

    @Column(nullable = false)
    public Date birthday;

    @Column(nullable = false, unique = true, length = 15)
    public String phoneNumber;

    @Column(nullable = false, unique = true, length = 20)
    public String IDNumber;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isActive;

    @Override
    public WsPassenger toWs() {
        return new WsPassenger(id, email, hash, firstName, lastName, gender + "", birthday, phoneNumber, IDNumber, isActive);
    }
}
