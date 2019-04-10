package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Type;

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
        BluetoothMessage chatMessage = new BluetoothMessage();

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

        connectMessage.setMessageType(BluetoothMessage.Type.CONNECT);
        connectMessage.setMessagePayload(connectJson);

        disconnectMessage.setMessageType(BluetoothMessage.Type.DISCONNECT);
        disconnectMessage.setMessagePayload(disconnectJson);

        chatMessage.setMessageType(BluetoothMessage.Type.CHAT);
        chatMessage.setMessagePayload(chatJson);

        assertEquals(chatJson.toString(), chatMessage.getMessagePayload().toString());
        assertEquals(BluetoothMessage.Type.CHAT, chatMessage.getMessageType());
        assertEquals(connectJson.toString(), connectMessage.getMessagePayload().toString());
        assertEquals(BluetoothMessage.Type.CONNECT, connectMessage.getMessageType());
        assertEquals(disconnectJson.toString(), disconnectMessage.getMessagePayload().toString());
        assertEquals(BluetoothMessage.Type.DISCONNECT, disconnectMessage.getMessageType());
    }

    @Test
    public void testGetMessageObject() throws JSONException
    {
        BluetoothMessage chatMessage = new BluetoothMessage();
        JSONObject chatJson = new JSONObject();
        chatJson.put("userId", 1);
        chatJson.put("timeStamp", "04-04-2019-08-00-00");
        chatJson.put("messageText", "TEST TEXT");

        chatMessage.setMessagePayload(chatJson);
        chatMessage.setMessageType(BluetoothMessage.Type.CHAT);

        Message testMessage = new Message(chatJson.toString(), true);

        assertEquals(testMessage, chatMessage.getMessageObject());
    }
}