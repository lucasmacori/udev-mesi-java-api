package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Constructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsConstructor {
	
	public Long id;
	public String name;
	public boolean isActive;
	public WsModel[] models;

	public WsConstructor() {}

	public WsConstructor(long id, String name, boolean isActive, List models) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
		this.models = WsModel.getArrayFromList(models, false);
	}

	public static WsConstructor[] getArrayFromList(List<Constructor> constructors, boolean circular) {
		try {
			WsConstructor[] constructors_array = new WsConstructor[constructors.size()];
			for (int i = 0; i < constructors.size(); i++) {
				constructors_array[i] = (WsConstructor) constructors.get(i).toWs(circular);
			}
			return constructors_array;
		} catch (NullPointerException e) {
			return null;
		}
	}
}
