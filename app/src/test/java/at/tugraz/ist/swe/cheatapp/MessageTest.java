package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MessageTest {


    @Test
    public void testGetJsonString() throws JSONException {
        Message message = new Message(5, "Hello, this is a test message! :)", true);
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("type", "chat message");
        jsonMessage.put("userId", 5);
        jsonMessage.put("messageText", "Hello, this is a test message! :)");
        assertEquals(jsonMessage.toString(), message.getJsonString());
    }

    @Test
    public void testNewMessageFromJsonString() throws JSONException {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("type", "chat message");
        jsonMessage.put("userId", 4);
        jsonMessage.put("messageText", "Ok, I will see! ;)");

        Message testMessage = new Message(jsonMessage.toString(), false);

        assertEquals(4, testMessage.getUserId());
        assertEquals("Ok, I will see! ;)", testMessage.getMessageText());
        assertEquals(false, testMessage.getMessageSent());
        assertEquals(jsonMessage.toString(), testMessage.getJsonString());
    }

    @Test
    public void testBluetoothMessageClass() throws JSONException
    {
        BluetoothMessage connectMessage = new BluetoothMessage();
        BluetoothMessage disconnectMessage = new BluetoothMessage();
        BluetoothMessage chatMessage = new BLuetoothMessage();

        connectMessage.type = Type.CONNECT;
        connectMessage.payload.put("userId", 1);
        connectMessage.payload.put("timeStamp", "04-04-2019-08-00-00");

        disconnectMessage.type = Type.DISCONNECT;
        disconnectMessage.payload.put("userId", 1);
        disconnectMessage.payload.put("timeStamp","04-04-2019-08-00-00");

        chatMessage.type = Type.CHAT;
        chatMessage.payload.put("userId", 1);
        chatMessage.payload.put("timeStamp", "04-04-2019-08-00-00");
        chatMessage.payload.put("messageText", "TEST TEXT");

        JSONObject chatJson = new JSONObject();
        chatJson.put("userId", 1);
        chatJson.put("timeStamp", "04-04-2019-08-00-00");
        chatJson.put("messageText", "TEST TEXT");
        
        JSONObject connectJson = new JSONObject();
        connectJson.put("userId", 1);
        connectJson.put("timeStamp", "04-04-2019-08-00-00");

        JSONObject disconnectJson = new JSONObject();
        disconnectJson.put("userId", 1);
        disconnectJson.put("timeStamp", "04-04-2019-08-00-00");

        assertEquals(chatJson.toString(), chatMessage.payload.getJsonString());
        assertEquals(connectJson.toString(), connectMessage.payload.getJsonString());
        assertEquals(disconnectJson.toString(), disconnectMessage.payload.getJsonString());
    }
}