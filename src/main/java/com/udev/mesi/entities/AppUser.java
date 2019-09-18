package main.java.com.udev.mesi.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AppUser {

    @Id
    @Column
    @GeneratedValue
    public long id;

    @Column(nullable = false, unique = true, length = 50)
    public String username;

    @Column(nullable = false, length = 100)
    public String hash;

    public AppUser() {
    }

    public AppUser(String username, String hash) {
        this.username = username;
        this.hash = hash;
    }
}
