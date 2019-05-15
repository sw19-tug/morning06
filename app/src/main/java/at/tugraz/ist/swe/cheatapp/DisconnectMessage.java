package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class DisconnectMessage {

    public DisconnectMessage() {

    }

    public DisconnectMessage(String jsonConnectMessageString) throws JSONException {
        JSONObject jsonConnectMessage = new JSONObject(jsonConnectMessageString);
    }

    public String getJsonString() {
        return "";
    }
}
