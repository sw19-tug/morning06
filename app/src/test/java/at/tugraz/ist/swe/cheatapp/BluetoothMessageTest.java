package at.tugraz.ist.swe.cheatapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BluetoothMessageTest {
    @Test
    public void testMessageConstructor() {
        ChatMessage chatMessage = new ChatMessage(4, "This is a test for the Constructor with a ChatMessage object", true);
        BluetoothMessage btMessage = new BluetoothMessage(chatMessage);

        assertEquals(btMessage.getMessageType(), BluetoothMessage.Type.CHAT);
        assertEquals(chatMessage, btMessage.getMessage());
    }

    @Test
    public void testConnectMessageConstructor() {
        ConnectMessage connectMessage = new ConnectMessage("com.example.testapp", "1.3.3.7");
        BluetoothMessage btMessage = new BluetoothMessage(connectMessage);

        assertEquals(btMessage.getMessageType(), BluetoothMessage.Type.CONNECT);
        assertEquals(connectMessage, btMessage.getConnectMessage());
    }

    @Test
    public void testToJSONStringWithConnectMessage() throws JSONException {
        ConnectMessage connectMessage = new ConnectMessage("com.example.testapp", "1.3.3.7");
        BluetoothMessage connectBluetoothMessage = new BluetoothMessage(connectMessage);

        JSONObject jsonBluetoothMessage = new JSONObject();
        jsonBluetoothMessage.put("type", BluetoothMessage.Type.CONNECT);
        jsonBluetoothMessage.put("payload", connectMessage.getJsonString());

        assertEquals(jsonBluetoothMessage.toString(), connectBluetoothMessage.toJSONString());
    }

    @Test
    public void testToJSONStringWithChatMessage() throws JSONException {
        ChatMessage chatMessage = new ChatMessage(4, "This is a Test for the JSON methods", false);
        BluetoothMessage chatBluetoothMessage = new BluetoothMessage(chatMessage);

        JSONObject jsonBluetoothMessage = new JSONObject();
        jsonBluetoothMessage.put("type", BluetoothMessage.Type.CHAT);
        jsonBluetoothMessage.put("payload", chatMessage.getJsonString());

        assertEquals(jsonBluetoothMessage.toString(), chatBluetoothMessage.toJSONString());
    }


    @Test
    public void testFromJSONStringWithConnectMessage() throws JSONException {
        ConnectMessage connectMessage = new ConnectMessage("com.example.testapp", "1.3.3.7");
        BluetoothMessage connectBluetoothMessage1 = new BluetoothMessage(connectMessage);
        BluetoothMessage connectBluetoothMessage2 = BluetoothMessage.fromJSONString(connectBluetoothMessage1.toJSONString());

        assertEquals(connectBluetoothMessage1.toJSONString(), connectBluetoothMessage2.toJSONString());
    }

    @Test
    public void testFromJSONStringWithChatMessage() throws JSONException {
        ChatMessage chatMessage = new ChatMessage(4, "This is a Test for the JSON methods", false);
        BluetoothMessage chatBluetoothMessage1 = new BluetoothMessage(chatMessage);
        BluetoothMessage chatBluetoothMessage2 = BluetoothMessage.fromJSONString(chatBluetoothMessage1.toJSONString());

        assertEquals(chatBluetoothMessage1.toJSONString(), chatBluetoothMessage2.toJSONString());
    }
}