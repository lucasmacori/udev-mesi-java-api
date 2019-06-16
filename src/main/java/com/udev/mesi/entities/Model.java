package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsModel;

import javax.persistence.*;

@Entity
public class Model implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false, nullable = false)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public Constructor constructor;

    @Column(nullable = false, length = 50)
    public String name;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isActive;

    @Column(nullable = false, columnDefinition = "int default 0")
    public int countEcoSlots;

    @Column(nullable = false, columnDefinition = "int default 0")
    public int countBusinessSlots;

    @Override
    public WsModel toWs(boolean circular) {
        if (circular) {
            return new WsModel(id, constructor.toWs(false), name, isActive, countEcoSlots, countBusinessSlots);
        }
        return new WsModel(id, null, name, isActive, countEcoSlots, countBusinessSlots);
    }
}
