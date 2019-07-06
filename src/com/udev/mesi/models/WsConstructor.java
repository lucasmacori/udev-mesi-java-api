package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Constructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsConstructor {
	
	public Long id;
	public String name;
    private boolean isActive;

	public WsConstructor() {}

	public WsConstructor(long id, String name, boolean isActive) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}

	public static WsConstructor[] getArrayFromList(List<Constructor> constructors) {
		try {
			WsConstructor[] constructors_array = new WsConstructor[constructors.size()];
			for (int i = 0; i < constructors.size(); i++) {
				constructors_array[i] = constructors.get(i).toWs();
			}
			return constructors_array;
		} catch (NullPointerException e) {
			return null;
		}
	}
}
