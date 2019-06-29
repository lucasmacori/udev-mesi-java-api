package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Language;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsLanguage {
    public String code;
    public String name;

    public WsLanguage() {
    }

    public WsLanguage(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WsLanguage[] getArrayFromList(List<Language> languages) {
        try {
            WsLanguage[] languages_array = new WsLanguage[languages.size()];
            for (int i = 0; i < languages.size(); i++) {
                languages_array[i] = languages.get(i).toWs();
            }
            return languages_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
