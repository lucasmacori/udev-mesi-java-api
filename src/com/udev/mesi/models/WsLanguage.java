package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Language;
import main.java.com.udev.mesi.entities.Message;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsLanguage {
    public String code;
    public String name;
    public WsMessage[] messages;

    public WsLanguage() {
    }

    public WsLanguage(String code, String name, List<Message> messages) {
        this.code = code;
        this.name = name;
        this.messages = WsMessage.getArrayFromList(messages, false);
    }

    public static WsLanguage[] getArrayFromList(List<Language> languages, boolean circular) {
        try {
            WsLanguage[] languages_array = new WsLanguage[languages.size()];
            for (int i = 0; i < languages.size(); i++) {
                languages_array[i] = languages.get(i).toWs(circular);
            }
            return languages_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
