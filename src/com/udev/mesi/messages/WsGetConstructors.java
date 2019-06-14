package com.udev.mesi.messages;

import com.udev.mesi.models.WsConstructor;
import main.java.com.udev.mesi.entities.Constructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetConstructors extends WsResponse {
	
	public WsConstructor[] constructors;
	
	public WsGetConstructors() {
		super();
	}

	public WsGetConstructors(String status, String message, int code, List<Constructor> constructors) {
		super(status, message, code);
		this.constructors = WsConstructor.getArrayFromList(constructors, true);
	}
}
