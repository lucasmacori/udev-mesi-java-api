package com.udev.mesi.messages;

import com.udev.mesi.models.WsPlane;
import main.java.com.udev.mesi.entities.Plane;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSinglePlane extends WsResponse {

    public WsPlane plane;

    public WsGetSinglePlane() {
        super();
    }

    public WsGetSinglePlane(String status, String message, int code, Plane plane) {
        super(status, message, code);
        if (plane != null) {
            this.plane = plane.toWs();
        }
    }
}