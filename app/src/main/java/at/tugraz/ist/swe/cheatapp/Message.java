package at.tugraz.ist.swe.cheatapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Message {

    @PrimaryKey(autoGenerate = true)
    private int messageId;

    private int userId;
    private String messageText;
    private boolean messageSent;

    public Message(int userId, String messageText, boolean messageSent){
        this.userId = userId;
        this.messageText = messageText;
        this.messageSent = messageSent;
    }

    public int getMessageId(){
        return messageId;
    }

    public void setMessageId(int messageId){
        this.messageId = messageId;
    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public String getMessageText(){
        return messageText;
    }

    public void setMessageText(String messageText){
        this.messageText = messageText;
    }

    public boolean getMessageSent(){
        return messageSent;
    }

    public void setMessageSent(boolean messageSent){
        this.messageSent = messageSent;
    }
}
