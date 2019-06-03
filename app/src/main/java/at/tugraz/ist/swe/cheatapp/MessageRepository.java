package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class MessageRepository {

    private static String DB_NAME = "cheatapp_db";

    private CheatAppDatabase cheatAppDatabase;

    private MessageRepository(CheatAppDatabase cheatAppDatabase) {
        this.cheatAppDatabase = cheatAppDatabase;
    }

    public static MessageRepository createRepository(Context context) {
        return new MessageRepository(Room.databaseBuilder(context, CheatAppDatabase.class, MessageRepository.DB_NAME).build());
    }

    public static MessageRepository createInMemoryRepository(Context context) {
        return new MessageRepository(Room.inMemoryDatabaseBuilder(context, CheatAppDatabase.class).build());
    }

    public AsyncTask<Void, Void, Void> insertMessage(final ChatMessage newMessage) {
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cheatAppDatabase.messageDao().insertMessage(newMessage);
                return null;
            }
        }.execute();
    }

    public AsyncTask<Void, Void, Void> updateMessage(final ChatMessage message){
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cheatAppDatabase.messageDao().updateMessage(message);
                return null;
            }
        }.execute();
    }


    public List<ChatMessage> getMessagesByMessageUUID(String uuid) {
        return cheatAppDatabase.messageDao().getMessageByMessageUUID(uuid);
    }

    public List<ChatMessage> getRawMessagesByUserId(long userId) {                   //For testing purposes
        return cheatAppDatabase.messageDao().getRawMessagesByUserId(userId);
    }

    public LiveData<List<ChatMessage>> getMessagesByUserId(long userId) {
        return cheatAppDatabase.messageDao().getMessagesByUserId(userId);
    }
}
