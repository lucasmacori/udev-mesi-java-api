package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsModel;

import javax.persistence.*;
import java.util.List;

@Entity
public class Model implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @org.hibernate.annotations.Index(name = "manufacturerIndex")
    public Manufacturer manufacturer;

    @Column(nullable = false, length = 50)
    @org.hibernate.annotations.Index(name = "nameIndex")
    public String name;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isActive;

    @Column(nullable = false, columnDefinition = "int default 0")
    public int countEcoSlots;

    @Column(nullable = false, columnDefinition = "int default 0")
    public int countBusinessSlots;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "model")
    public List<Plane> planes;

    @Override
    public WsModel toWs() {
        return new WsModel(id, manufacturer.toWs(), name, isActive, countEcoSlots, countBusinessSlots);
    }
}
