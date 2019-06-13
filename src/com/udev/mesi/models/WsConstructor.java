package com.udev.mesi.models;

import javax.xml.bind.annotation.XmlRootElement;

import com.udev.mesi.messages.WsResponse;

@XmlRootElement
public class WsConstructor {
	
	public Long id;
	public String name;
	
	public WsConstructor() {}
	
	public WsConstructor(long id, String name) {
		this.id = id;
		this.name = name;
	}
}
