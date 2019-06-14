package main.java.com.udev.mesi.entities;

import com.udev.mesi.models.WsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Constructor implements IEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable = false, nullable = false)
	public Long id;
	
	@Column(name = "name", nullable = false, length = 50, unique = true)
	public String name;

	@Column(name = "is_active", nullable = false, columnDefinition = "bool default true")
	public boolean isActive;

	@Override
	public Object toWs() {
		return new WsConstructor(id, name, isActive);
	}
}
