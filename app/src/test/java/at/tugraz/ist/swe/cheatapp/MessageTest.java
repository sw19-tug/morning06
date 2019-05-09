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
        jsonMessage.put("userId", 5);
        jsonMessage.put("messageText", "Hello, this is a test message! :)");
        assertEquals(jsonMessage.toString(), message.getJsonString());
    }

    @Test
    public void testNewMessageFromJsonString() throws JSONException {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("userId", 4);
        jsonMessage.put("messageText", "Ok, I will see! ;)");

        Message testMessage = new Message(jsonMessage.toString(), false);
        testMessage.setUserId(5);
        testMessage.setMessageText("Test setter method!");
        testMessage.setMessageSent(true);

        JSONObject jsonMessage2 = new JSONObject();
        jsonMessage2.put("userId", 5);
        jsonMessage2.put("messageText", "Test setter method!");

        assertEquals(5, testMessage.getUserId());
        assertEquals("Test setter method!", testMessage.getMessageText());
        assertEquals(true, testMessage.getMessageSent());
        assertEquals(jsonMessage2.toString(), testMessage.getJsonString());
    }

    @Test
    public void testBluetoothMessageClass() throws JSONException
    {
        JSONObject chatJson = new JSONObject();
        chatJson.put("userId", 1);
        chatJson.put("messageText", "TEST TEXT");

        Message chatMessage1 = new Message(1, "TEST TEXT", true);
        assertEquals(chatJson.toString(), chatMessage1.getJsonString());

        Message chatMessage2 = new Message(chatJson.toString(), true);
        assertEquals(chatJson.toString(), chatMessage2.getJsonString());

        Message chatMessage3 = new Message(chatMessage2);
        assertEquals(chatMessage2.getJsonString(), chatMessage3.getJsonString());
    }
}