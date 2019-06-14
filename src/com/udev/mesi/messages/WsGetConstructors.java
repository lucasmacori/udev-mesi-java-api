package com.udev.mesi.messages;

import javax.xml.bind.annotation.XmlRootElement;

import com.udev.mesi.models.WsConstructor;

import main.java.com.udev.mesi.entities.Constructor;

import java.util.List;

@XmlRootElement
public class WsGetConstructors extends WsResponse {
	
	public WsConstructor[] constructors;
	
	public WsGetConstructors() {
		super();
	}

	public WsGetConstructors(String status, String message, int code, List<Constructor> constructors) {
		super(status, message, code);
		try {
			this.constructors = new WsConstructor[constructors.size()];
			for (int i = 0; i < constructors.size(); i++) {
				this.constructors[i] = (WsConstructor) constructors.get(i).toWs();
			}
		} catch (NullPointerException e) {
			this.constructors = null;
		}
	}
}
