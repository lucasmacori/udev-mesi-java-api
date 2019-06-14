package com.udev.mesi.models;

import javax.xml.bind.annotation.XmlRootElement;

import com.udev.mesi.messages.WsResponse;

@XmlRootElement
public class WsConstructor {
	
	public Long id;
	public String name;
	public boolean isActive;
	
	public WsConstructor() {}

	public WsConstructor(long id, String name) {
		this.id = id;
		this.name = name;
		this.isActive = true;
	}

	public WsConstructor(long id, String name, boolean isActive) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}
}
