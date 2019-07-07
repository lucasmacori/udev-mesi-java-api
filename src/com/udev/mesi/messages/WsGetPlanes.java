package com.udev.mesi.messages;

import com.udev.mesi.models.WsPlane;
import main.java.com.udev.mesi.entities.Plane;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetPlanes extends WsResponse {
    public WsPlane[] planes;

    public WsGetPlanes() {
        super();
    }

    public WsGetPlanes(String status, String message, int code, List<Plane> planes) {
        super(status, message, code);
        this.planes = WsPlane.getArrayFromList(planes);
    }
}
