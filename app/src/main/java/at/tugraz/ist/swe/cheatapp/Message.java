package at.tugraz.ist.swe.cheatapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public class Message {

    @PrimaryKey(autoGenerate = true)
    private int messageId;

    private long userId;
    private String messageText;
    private boolean messageSent;

    public Message(long userId, String messageText, boolean messageSent) {
        this.userId = userId;
        this.messageText = messageText;
        this.messageSent = messageSent;
    }

    public Message(String jsonMessageString, boolean messageSent) throws JSONException {
        JSONObject jsonMessage = new JSONObject(jsonMessageString);
        this.userId = jsonMessage.getLong("userId");
        this.messageText = jsonMessage.getString("messageText");
        this.messageSent = messageSent;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(boolean messageSent) {
        this.messageSent = messageSent;
    }

    public String getJsonString() throws JSONException {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("type", "chat message");
        jsonMessage.put("userId", this.userId);
        jsonMessage.put("messageText", messageText);
        return jsonMessage.toString();
    }
}
