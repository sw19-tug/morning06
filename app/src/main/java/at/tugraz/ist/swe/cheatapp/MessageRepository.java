package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class MessageRepository {

    private String DB_NAME = "cheatapp_db";

    private CheatAppDatabase cheatAppDatabase;

    public MessageRepository(Context context) {
        cheatAppDatabase = Room.databaseBuilder(context, CheatAppDatabase.class, DB_NAME).build();
    }

    public void insertMessage(int userId, String messageText, boolean messageSent) {

        final Message newMessage = new Message(userId, messageText, messageSent);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cheatAppDatabase.messageDao().insertMessage(newMessage);
                return null;
            }
        }.execute();
    }

    public List<Message> getRawMessagesByUserId(int userId) {                   //For testing purposes
        return cheatAppDatabase.messageDao().getRawMessagesByUserId(userId);
    }

    public LiveData<List<Message>> getMessagesByUserId(int userId) {
        return cheatAppDatabase.messageDao().getMessagesByUserId(userId);
    }
}
