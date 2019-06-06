package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectMessage {
    private String applicationId;
    private String versionName;
    private String nickname;

    public ConnectMessage(String applicationId, String versionName, String nickname) {
        this.applicationId = applicationId;
        this.versionName = versionName;
        this.nickname = nickname;
    }

    public ConnectMessage(String jsonConnectMessageString) throws JSONException {
        JSONObject jsonConnectMessage = new JSONObject(jsonConnectMessageString);
        this.applicationId = jsonConnectMessage.getString("applicationId");
        this.versionName = jsonConnectMessage.getString("versionName");
        this.nickname = jsonConnectMessage.getString("nickname");
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getJsonString() {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("applicationId", applicationId);
            jsonMessage.put("versionName", versionName);
            jsonMessage.put("nickname", nickname);
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
        return jsonMessage.toString();
    }
}
