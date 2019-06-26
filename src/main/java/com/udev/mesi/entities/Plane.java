package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsPlane;

import javax.persistence.*;

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

    @Override
    public WsPlane toWs(boolean circular) {
        return new WsPlane(ARN, model.toWs(true, circular), isUnderMaintenance, isActive);
    }

    public WsPlane toWs(boolean includeModels, boolean circular) {
        if (includeModels) {
            return toWs(circular);
        }
        return new WsPlane(ARN, null, isUnderMaintenance, isActive);
    }
}
