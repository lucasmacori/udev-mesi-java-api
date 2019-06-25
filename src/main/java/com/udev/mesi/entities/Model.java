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
    public Constructor constructor;

    @Column(nullable = false, length = 50)
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
    public WsModel toWs(boolean circular) {
        if (circular) {
            return new WsModel(id, constructor.toWs(false), name, isActive, countEcoSlots, countBusinessSlots, planes, true);
        }
        return new WsModel(id, null, name, isActive, countEcoSlots, countBusinessSlots, null, false);
    }

    public WsModel toWs(boolean includeConstructor, boolean includePlanes) {
        Constructor constructor1 = null;
        List<Plane> planes1 = null;
        if (includeConstructor) {
            constructor1 = constructor;
        }
        if (includePlanes) {
            planes1 = planes;
        }
        return new WsModel(id, constructor1.toWs(false), name, isActive, countEcoSlots, countBusinessSlots, planes1, includePlanes);
    }
}
