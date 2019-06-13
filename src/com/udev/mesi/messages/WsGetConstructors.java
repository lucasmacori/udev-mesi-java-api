package com.udev.mesi.messages;

import javax.xml.bind.annotation.XmlRootElement;

import com.udev.mesi.models.WsConstructor;

import main.java.com.udev.mesi.entities.Constructor;

@XmlRootElement
public class WsGetConstructors extends WsResponse {
	
	public WsConstructor[] constructors;
	
	public WsGetConstructors() {
		super();
	}
	
	public WsGetConstructors(String status, String message, WsConstructor[] constructors) {
		super(status, message);
		this.constructors = constructors;
	}
	
	public WsGetConstructors(String status, String message, Object[] constructors) {
		super(status, message);
		this.constructors = new WsConstructor[constructors.length];
		for (int i = 0; i < constructors.length; i++) {
			Constructor constructor = (Constructor) constructors[i];
			WsConstructor wsConstructor = new WsConstructor();
			wsConstructor.id = constructor.id;
			wsConstructor.name = constructor.name;
			this.constructors[i] = wsConstructor;
		}
	}
}
