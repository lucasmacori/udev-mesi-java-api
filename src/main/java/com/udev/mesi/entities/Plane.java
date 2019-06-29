package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsPlane;

import javax.persistence.*;
import java.util.List;

@Entity
public class Plane implements IEntity {

    @Id
    @Column(nullable = false, length = 10)
    public String ARN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public Model model;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isUnderMaintenance;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isActive;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plane")
    public List<FlightDetails> flightDetails;

    @Override
    public WsPlane toWs() {
        return new WsPlane(ARN, model.toWs(), isUnderMaintenance, isActive);
    }
}
