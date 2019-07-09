package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Manufacturer;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsManufacturer {
	
	public Long id;
	public String name;
    private boolean isActive;

    public WsManufacturer() {
    }

    public WsManufacturer(long id, String name, boolean isActive) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}

    public static WsManufacturer[] getArrayFromList(List<Manufacturer> manufacturers) {
		try {
            WsManufacturer[] manufacturers_array = new WsManufacturer[manufacturers.size()];
            for (int i = 0; i < manufacturers.size(); i++) {
                manufacturers_array[i] = manufacturers.get(i).toWs();
			}
            return manufacturers_array;
		} catch (NullPointerException e) {
			return null;
		}
	}
}
