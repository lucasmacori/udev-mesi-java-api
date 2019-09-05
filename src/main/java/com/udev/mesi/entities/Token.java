package main.java.com.udev.mesi.entities;

import javax.persistence.*;

@Entity
public class Token {

    @Id
    @Column
    @GeneratedValue
    public long id;

    @Column(nullable = false, length = 100)
    public String token;

    @OneToOne(fetch = FetchType.EAGER)
    public AppUser appUser;
}
