package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConnectMessageTest {
    @Test
    public void testGetJsonString() throws JSONException {
        ConnectMessage connectMessage = new ConnectMessage("com.example.testapp", "1.3.3.7", "Cheater");
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("applicationId", "com.example.testapp");
        jsonMessage.put("versionName", "1.3.3.7");
        jsonMessage.put("nickname", "Cheater");
        assertEquals(jsonMessage.toString(), connectMessage.getJsonString());
    }

    @Test
    public void testNewConnectMessageFromJsonString() throws JSONException {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("applicationId", "com.example.testapp");
        jsonMessage.put("versionName", "1.3.3.7");
        jsonMessage.put("nickname", "Cheater");

        ConnectMessage testMessage = new ConnectMessage(jsonMessage.toString());

        assertEquals("com.example.testapp", testMessage.getApplicationId());
        assertEquals("1.3.3.7", testMessage.getVersionName());
        assertEquals("Cheater", testMessage.getNickname());
        assertEquals(jsonMessage.toString(), testMessage.getJsonString());
    }
}
