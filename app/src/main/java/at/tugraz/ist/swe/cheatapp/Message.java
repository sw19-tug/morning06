package at.tugraz.ist.swe.cheatapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public class Message {

    @PrimaryKey(autoGenerate = true)
    private int messageId;

    private long userId;
    private long timestamp;
    private String messageText;
    private boolean messageSent;

    public Message(long userId, String messageText, boolean messageSent) {
        this.userId = userId;
        this.messageText = messageText;
        this.messageSent = messageSent;
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String jsonMessageString, boolean messageSent) throws JSONException {
        JSONObject jsonMessage = new JSONObject(jsonMessageString);
        this.userId = jsonMessage.getLong("userId");
        this.messageText = jsonMessage.getString("messageText");
        this.messageSent = messageSent;
        this.timestamp = jsonMessage.getLong("timeStamp");
    }

    public Message(final Message other) {
        this.userId = other.userId;
        this.timestamp = other.timestamp;
        this.messageText = other.messageText;
        this.messageSent = other.messageSent;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getJsonString() {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("userId", this.userId);
            jsonMessage.put("timeStamp", this.timestamp);
            jsonMessage.put("messageText", messageText);
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }

        return jsonMessage.toString();
    }
}
