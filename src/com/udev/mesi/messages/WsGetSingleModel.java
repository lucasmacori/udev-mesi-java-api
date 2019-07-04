package com.udev.mesi.messages;

import com.udev.mesi.models.WsModel;
import main.java.com.udev.mesi.entities.Model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsGetSingleModel extends WsResponse {

    public WsModel model;

    public WsGetSingleModel() {
        super();
    }

    public WsGetSingleModel(String status, String message, int code, Model model) {
        super(status, message, code);
        if (model != null) {
            this.model = model.toWs();
        }
    }
}