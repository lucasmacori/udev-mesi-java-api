package com.udev.mesi.models;

import main.java.com.udev.mesi.entities.Message;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class WsMessage {
    public String code;
    public WsLanguage language;
    public String text;

    public WsMessage() {
    }

    public WsMessage(String code, WsLanguage language, String text) {
        this.code = code;
        this.language = language;
        this.text = text;
    }

    public static WsMessage[] getArrayFromList(List<Message> messages, boolean circular) {
        try {
            WsMessage[] messages_array = new WsMessage[messages.size()];
            for (int i = 0; i < messages.size(); i++) {
                messages_array[i] = messages.get(i).toWs(circular);
            }
            return messages_array;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
