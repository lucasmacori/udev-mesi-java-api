package com.udev.mesi.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetToken extends WsResponse {
    public String token;

    public WsGetToken() {
        super();
    }

    public WsGetToken(String status, String message, int code, String token) {
        super(status, message, code);
        this.token = token;
    }
}
