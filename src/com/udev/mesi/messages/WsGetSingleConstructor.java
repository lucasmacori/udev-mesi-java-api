package com.udev.mesi.messages;

import com.udev.mesi.models.WsConstructor;
import main.java.com.udev.mesi.entities.Constructor;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSingleConstructor extends WsResponse {

    public WsConstructor constructor;

    public WsGetSingleConstructor() {
        super();
    }

    public WsGetSingleConstructor(String status, String message, int code, Constructor constructor) {
        super(status, message, code);
        if (constructor != null) {
            this.constructor = constructor.toWs();
        }
    }
}