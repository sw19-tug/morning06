package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectMessage {
    private String applicationId;
    private String versionName;
    private String nickname;
    private String profilePicture;

    public ConnectMessage(String applicationId, String versionName, String nickname,
                          String profilePicture) {
        this.applicationId = applicationId;
        this.versionName = versionName;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
    }

    public ConnectMessage(String jsonConnectMessageString) throws JSONException {
        JSONObject jsonConnectMessage = new JSONObject(jsonConnectMessageString);
        applicationId = jsonConnectMessage.getString("applicationId");
        versionName = jsonConnectMessage.getString("versionName");
        nickname = jsonConnectMessage.getString("nickname");
        profilePicture = jsonConnectMessage.getString("profilePicture");
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getJsonString() {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("applicationId", applicationId);
            jsonMessage.put("versionName", versionName);
            jsonMessage.put("nickname", nickname);
            jsonMessage.put("profilePicture", profilePicture);
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
        return jsonMessage.toString();
    }
}
