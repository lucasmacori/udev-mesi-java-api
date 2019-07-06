package com.udev.mesi.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsExists extends WsResponse {
    public boolean exists;

    public WsExists() {
        super();
    }

    public WsExists(String status, String message, int code, boolean exists) {
        super(status, message, code);
        this.exists = exists;
    }
}
