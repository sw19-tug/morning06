package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothMessage {

    enum Type {CONNECT, DISCONNECT, CHAT}

    private Type messageType;
    private JSONObject messagePayload;

    public BluetoothMessage(){
        messagePayload = new JSONObject();
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public void setMessagePayload(JSONObject messagePayload) {
        this.messagePayload = messagePayload;
    }

    public  Type getMessageType() {
        return messageType;
    }

    public JSONObject getMessagePayload() {
        return messagePayload;
    }

    public Message getMessageObject() throws JSONException { // if a BluetoothMessage is received it can be converted to a Message object

        if(messageType != Type.CHAT)
            return null;

        int userId = messagePayload.getInt("userId");
        String messageText = messagePayload.getString("messageText");
        Message messageObject = new Message(userId, messageText, false );   // if I am the receiver, sent is always false
        return messageObject;
    }
}
