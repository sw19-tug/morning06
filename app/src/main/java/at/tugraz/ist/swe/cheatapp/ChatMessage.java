package at.tugraz.ist.swe.cheatapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

@Entity
public class ChatMessage {

    @PrimaryKey(autoGenerate = true)
    private int messageId;

    private long userId;
    private long timestamp;
    private String messageText;
    private boolean messageSent;
    private String messageUUID;
    private boolean messageEdited;

    public ChatMessage(long userId, String messageText, boolean messageSent, boolean messageEdited) {
        this.userId = userId;
        this.messageText = messageText;
        this.messageSent = messageSent;
        this.timestamp = System.currentTimeMillis();
        this.messageUUID = UUID.randomUUID().toString();
        this.messageEdited = messageEdited;
    }

    public ChatMessage(String jsonMessageString, boolean messageSent) throws JSONException {
        JSONObject jsonMessage = new JSONObject(jsonMessageString);
        this.userId = jsonMessage.getLong("userId");
        this.messageText = jsonMessage.getString("messageText");
        this.timestamp = jsonMessage.getLong("timeStamp");
        this.messageUUID = jsonMessage.getString("messageUUID");
        this.messageEdited = jsonMessage.getBoolean("messageEdited");
        this.messageSent = messageSent;

    }

    public ChatMessage(final ChatMessage other) {
        this.userId = other.userId;
        this.timestamp = other.timestamp;
        this.messageText = other.messageText;
        this.messageSent = other.messageSent;
        this.messageUUID = other.messageUUID;
        this.messageEdited = other.messageEdited;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getMessageUUID() {
        return messageUUID;
    }

    public void setMessageUUID(String messageUUID) {
        this.messageUUID = messageUUID;
    }

    public boolean getMessageEdited() {
        return messageEdited;
    }

    public void setMessageEdited(boolean messageEdited) {
        this.messageEdited = messageEdited;
    }

    public String getJsonString() {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("userId", this.userId);
            jsonMessage.put("timeStamp", this.timestamp);
            jsonMessage.put("messageText", messageText);
            jsonMessage.put("messageUUID", messageUUID);
            jsonMessage.put("messageEdited", messageEdited);
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }

        return jsonMessage.toString();
    }
}