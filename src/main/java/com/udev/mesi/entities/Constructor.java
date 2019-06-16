package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
public class Constructor implements IEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(updatable = false, nullable = false)
	public Long id;

	@Column(nullable = false, length = 50, unique = true)
	public String name;

	@Column(nullable = false, columnDefinition = "bool default true")
	public boolean isActive;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "constructor")
	public List<Model> models;

	@Override
    public WsConstructor toWs(boolean circular) {
		if (circular) {
			return new WsConstructor(id, name, isActive, models);
        }
        return new WsConstructor(id, name, isActive, null);
	}
}
