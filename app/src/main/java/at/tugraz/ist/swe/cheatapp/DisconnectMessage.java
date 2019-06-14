package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class DisconnectMessage {

    public DisconnectMessage() {
    }

    public DisconnectMessage(String jsonConnectMessageString) throws JSONException {
        JSONObject jsonDisconnectMessage = new JSONObject(jsonConnectMessageString);
    }

    public String getJsonString() {
        JSONObject jsonDisconnectMessage = new JSONObject();
        return jsonDisconnectMessage.toString();
    }
}
