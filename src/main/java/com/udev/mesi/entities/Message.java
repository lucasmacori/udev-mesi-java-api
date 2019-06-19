package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsMessage;

import javax.persistence.*;

@Entity
public class Message implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    public long id;

    @Column(updatable = false, nullable = false)
    public String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public Language language;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String text;

    @Override
    public WsMessage toWs(boolean circular) {
        if (circular) {
            return new WsMessage(code, language.toWs(false), text);
        }
        return new WsMessage(code, null, text);
    }
}
