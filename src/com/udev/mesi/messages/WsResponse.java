package com.udev.mesi.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsResponse {
	
	public String status;
	public String message;
	private int code;
	
	public WsResponse() {}
	
	public WsResponse(String status, String message, int code) {
		this.status = status;
		this.message = message;
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
