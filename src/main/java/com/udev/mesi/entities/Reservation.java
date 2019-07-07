package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsReservation;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Reservation implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    public Long id;

    @Column(nullable = false, columnDefinition = "timestamp default now()")
    public Date reservationDate;

    @Column(nullable = false)
    public char reservationClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public FlightDetails flightDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public Passenger passenger;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isActive;

    @Override
    public WsReservation toWs() {
        return new WsReservation(id, reservationDate, reservationClass, isActive);
    }
}
