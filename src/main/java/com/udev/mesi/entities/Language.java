package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsLanguage;

import javax.persistence.*;
import java.util.List;

@Entity
public class Language implements IEntity {
    @Id
    @Column(updatable = false, nullable = false, length = 5)
    public String code;

    @Column(nullable = false, unique = true, length = 25)
    public String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "language")
    public List<Message> messages;

    @Override
    public WsLanguage toWs(boolean circular) {
        if (circular) {
            return new WsLanguage(code, name, messages);
        }
        return new WsLanguage(code, name, null);
    }
}
