package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsManufacturer;

import javax.persistence.*;
import java.util.List;

@Entity
public class Manufacturer implements IEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	public Long id;

	@Column(nullable = false, length = 50, unique = true)
	public String name;

	@Column(nullable = false, columnDefinition = "bool default true")
	public boolean isActive;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "manufacturer")
	public List<Model> models;

	@Override
	public WsManufacturer toWs() {
		return new WsManufacturer(id, name, isActive);
	}
}
