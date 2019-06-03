package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothMessage {

    enum Type {CONNECT, DISCONNECT, CHAT, FILE}

    private Type messageType;
    private ChatMessage message = null;
    private ConnectMessage connectMessage = null;
    private DisconnectMessage disconnectMessage = null;

    public BluetoothMessage(ChatMessage message) {
        messageType = Type.CHAT;
        this.message = message;
    }

    public BluetoothMessage(ConnectMessage connectMessage) {
        this.messageType = Type.CONNECT;
        this.connectMessage = connectMessage;
    }

    public BluetoothMessage(DisconnectMessage disconnectMessage) {
        this.messageType = Type.DISCONNECT;
        this.disconnectMessage = disconnectMessage;
    }

    public  Type getMessageType() {
        return messageType;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public ConnectMessage getConnectMessage() {
        return connectMessage;
    }

    public String toJSONString() throws JSONException{

        JSONObject serializedMessage = new JSONObject();
        serializedMessage.put("type", messageType.toString());

        String payload = "";

        switch (messageType) {
            case CHAT:
                payload = message.getJsonString();
                break;
            case CONNECT:
                payload = connectMessage.getJsonString();
                break;
            case DISCONNECT:
                payload = "";
                break;
        }

        serializedMessage.put("payload", payload);
        return serializedMessage.toString();
    }

    public static BluetoothMessage fromJSONString(String json) throws JSONException {
        JSONObject jsonMessage = new JSONObject(json);
        String type = jsonMessage.getString("type");
        Type messageType = Type.valueOf(type);
        String payload = jsonMessage.getString("payload");

        switch (messageType) {
            case CHAT: {
                ChatMessage chatMessage = new ChatMessage(payload, false);
                return new BluetoothMessage(chatMessage);
            }

            case CONNECT: {
                ConnectMessage connectMessage = new ConnectMessage(payload);
                return new BluetoothMessage(connectMessage);
            }

            case DISCONNECT:
                DisconnectMessage disconnectMessage = new DisconnectMessage();
                return new BluetoothMessage(disconnectMessage);
        }

        return null;
    }
}