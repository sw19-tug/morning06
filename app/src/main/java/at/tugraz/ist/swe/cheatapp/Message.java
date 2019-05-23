package at.tugraz.ist.swe.cheatapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

@Entity
public class Message {

    @PrimaryKey(autoGenerate = true)
    private int messageId;

    private int userId;
    private long timestamp;

    private String messageText;
    private boolean messageSent;

    private String messageUUID;
    private boolean messageEdited;

    @Ignore
    public Message(int userId, String messageText, boolean messageSent) {
        this.userId = userId;
        this.messageText = messageText;
        this.messageSent = messageSent;
        this.timestamp = System.currentTimeMillis();
        this.messageUUID = UUID.randomUUID().toString();
        this.messageEdited = false;
    }

    public Message(int userId, String messageText, boolean messageSent, boolean messageEdited) {
        this.userId = userId;
        this.messageText = messageText;
        this.messageSent = messageSent;
        this.timestamp = System.currentTimeMillis();
        this.messageUUID = UUID.randomUUID().toString();
        this.messageEdited = messageEdited;
    }

    public Message(String jsonMessageString, boolean messageSent) throws JSONException {
        JSONObject jsonMessage = new JSONObject(jsonMessageString);
        this.userId = jsonMessage.getInt("userId");
        this.messageText = jsonMessage.getString("messageText");
        this.messageSent = messageSent;
        this.timestamp = jsonMessage.getLong("timeStamp");
        this.messageUUID = jsonMessage.getString("messageUUID");
        this.messageEdited = jsonMessage.getBoolean("messageEdited");
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public long getTimestamp() { return timestamp;}

    public void setTimestamp(long timestamp) { this.timestamp = timestamp;}

    public void setUserId(int userId) {
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

    public String getJsonString() throws JSONException {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("type", "chat message");
        jsonMessage.put("userId", this.userId);
        jsonMessage.put("timeStamp", this.timestamp);
        jsonMessage.put("messageText", messageText);
        jsonMessage.put("messageUUID", messageUUID);
        jsonMessage.put("messageEdited", messageEdited);
        return jsonMessage.toString();
    }
}
