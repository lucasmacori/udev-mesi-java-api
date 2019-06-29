package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsFlight;

import javax.persistence.*;
import java.util.List;

@Entity
public class Flight implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    public Long id;

    @Column(nullable = false, length = 75)
    public String departureCity;

    @Column(nullable = false, length = 75)
    public String arrivalCity;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isActive;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flight")
    public List<FlightDetails> flightsDetails;

    @Override
    public WsFlight toWs() {
        return new WsFlight(id, departureCity, arrivalCity, isActive);
    }
}
