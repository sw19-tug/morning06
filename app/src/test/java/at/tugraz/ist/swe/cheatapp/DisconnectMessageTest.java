package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DisconnectMessageTest {
    @Test
    public void testGetJsonString() throws JSONException {
        DisconnectMessage connectMessage = new DisconnectMessage();
        JSONObject jsonMessage = new JSONObject();
        assertEquals(jsonMessage.toString(), connectMessage.getJsonString());
    }

    @Test
    public void testNewConnectMessageFromJsonString() throws JSONException {
        JSONObject jsonMessage = new JSONObject();
        DisconnectMessage testMessage = new DisconnectMessage(jsonMessage.toString());
        assertEquals(jsonMessage.toString(), testMessage.getJsonString());
    }
}
