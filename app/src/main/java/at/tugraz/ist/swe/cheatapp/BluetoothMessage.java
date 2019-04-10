package at.tugraz.ist.swe.cheatapp;

import org.json.JSONObject;

public class BluetoothMessage {

    enum Type {CONNECT, DISCONNECT, CHAT}

    Type messageType;
    JSONObject messagePayload;

    public BluetoothMessage(){
        messagePayload = new JSONObject();
    }

    public  Type getMessageType() {
        return messageType;
    }

    public JSONObject getMessagePayload() {
        return messagePayload;
    }
}
