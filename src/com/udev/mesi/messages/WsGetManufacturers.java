package com.udev.mesi.messages;

import com.udev.mesi.models.WsManufacturer;
import main.java.com.udev.mesi.entities.Manufacturer;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetManufacturers extends WsResponse {

    public WsManufacturer[] manufacturers;

    public WsGetManufacturers() {
		super();
	}

    public WsGetManufacturers(String status, String message, int code, List<Manufacturer> manufacturers) {
		super(status, message, code);
        this.manufacturers = WsManufacturer.getArrayFromList(manufacturers);
	}
}
