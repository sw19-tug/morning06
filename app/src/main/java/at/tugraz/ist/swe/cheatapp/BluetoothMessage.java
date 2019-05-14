package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothMessage {

    enum Type {CONNECT, DISCONNECT, CHAT}

    private Type messageType;
    private Message message = null;
    private ConnectMessage connectMessage = null;

    public BluetoothMessage(Message message) {
        messageType = Type.CHAT;
        this.message = message;
    }

    public BluetoothMessage(ConnectMessage connectMessage) {
        this.messageType = Type.CONNECT;
        this.connectMessage = connectMessage;
    }

    public  Type getMessageType() {
        return messageType;
    }

    public Message getMessage() {
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
                // TODO: MessageSent parameter?
                Message message = new Message(payload, false);
                return new BluetoothMessage(message);
            }

            case CONNECT: {
                ConnectMessage connectMessage = new ConnectMessage(payload);
                return new BluetoothMessage(connectMessage);
            }

            case DISCONNECT:
                break;
        }

        return null;
    }
}