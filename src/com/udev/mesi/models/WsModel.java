package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsModel {
    public Long id;
    public WsConstructor constructor;
    public String name;
    public boolean isActive;
    public int countEcoSlots;
    public int countBusinessSlots;
    public WsPlane[] planes;

    public WsModel() {
    }

    public WsModel(long id, WsConstructor constructor, String name, boolean isActive, int countEcoSlots, int countBusinessSlots, List planes) {
        this.id = id;
        this.constructor = constructor;
        this.name = name;
        this.isActive = isActive;
        this.countEcoSlots = countEcoSlots;
        this.countBusinessSlots = countBusinessSlots;
        this.planes = WsPlane.getArrayFromList(planes, false);
    }

    public static WsModel[] getArrayFromList(List<Model> models, boolean circular) {
        try {
            WsModel[] models_array = new WsModel[models.size()];
            for (int i = 0; i < models.size(); i++) {
                models_array[i] = models.get(i).toWs(circular);
            }
            return models_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
