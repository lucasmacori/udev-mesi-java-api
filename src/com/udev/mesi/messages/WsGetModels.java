package com.udev.mesi.messages;

import com.udev.mesi.models.WsModel;
import main.java.com.udev.mesi.entities.Model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsGetModels extends WsResponse {

    public WsModel[] models;

    public WsGetModels() {
        super();
    }

    public WsGetModels(String status, String message, int code, List<Model> models, boolean includePlanes) {
        super(status, message, code);
        this.models = WsModel.getArrayFromList(models, true);
    }
}
