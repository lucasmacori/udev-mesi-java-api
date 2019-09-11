package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsReport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Report implements IEntity {

    @Id
    @Column(nullable = false)
    public String code;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false, columnDefinition = "bool default true")
    public boolean isActive;

    @Column(nullable = false, length = 1000)
    public String query;

    @Override
    public WsReport toWs() {
        return new WsReport(code, description, query, isActive);
    }
}
