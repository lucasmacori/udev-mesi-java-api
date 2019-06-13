package com.udev.mesi.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsResponse {
	
	public String status;
	public String message;
	
	public WsResponse() {}
	
	public WsResponse(String status, String message) {
		this.status = status;
		this.message = message;
	}
}
