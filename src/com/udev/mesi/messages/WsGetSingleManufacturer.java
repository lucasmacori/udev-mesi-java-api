package com.udev.mesi.messages;

import com.udev.mesi.models.WsManufacturer;
import main.java.com.udev.mesi.entities.Manufacturer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSingleManufacturer extends WsResponse {

    public WsManufacturer manufacturer;

    public WsGetSingleManufacturer() {
        super();
    }

    public WsGetSingleManufacturer(String status, String message, int code, Manufacturer manufacturer) {
        super(status, message, code);
        if (manufacturer != null) {
            this.manufacturer = manufacturer.toWs();
        }
    }
}