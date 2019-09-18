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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "language")
    public List<Message> messages;

    public Language() {
    }

    public Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public WsLanguage toWs() {
        return new WsLanguage(code, name);
    }
}
