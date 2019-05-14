package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
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
        jsonMessage.put("timeStamp", message.getTimestamp());
        assertEquals(jsonMessage.toString(), message.getJsonString());
    }

    @Test
    public void testNewMessageFromJsonString() throws JSONException {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("userId", 4);
        jsonMessage.put("messageText", "Ok, I will see! ;)");
        long systemTime = System.currentTimeMillis();
        jsonMessage.put("timeStamp", systemTime);

        Message testMessage = new Message(jsonMessage.toString(), false);
        testMessage.setUserId(5);
        testMessage.setMessageText("Test setter method!");
        testMessage.setMessageSent(true);

        JSONObject jsonMessage2 = new JSONObject();
        jsonMessage2.put("userId", 5);
        jsonMessage2.put("messageText", "Test setter method!");
        jsonMessage2.put("timeStamp", systemTime);

        assertEquals(5, testMessage.getUserId());
        assertEquals("Test setter method!", testMessage.getMessageText());
        assertTrue(testMessage.getMessageSent());
        assertEquals(jsonMessage2.toString(), testMessage.getJsonString());
    }

    @Test
    public void testBluetoothMessageClass() throws JSONException
    {
        JSONObject chatJson = new JSONObject();
        chatJson.put("userId", 1);
        chatJson.put("messageText", "TEST TEXT");
        long systemTime = System.currentTimeMillis();
        chatJson.put("timeStamp", systemTime);

        Message chatMessage2 = new Message(chatJson.toString(), true);
        assertEquals(chatJson.toString(), chatMessage2.getJsonString());

        Message chatMessage3 = new Message(chatMessage2);
        assertEquals(chatMessage2.getJsonString(), chatMessage3.getJsonString());
    }
}