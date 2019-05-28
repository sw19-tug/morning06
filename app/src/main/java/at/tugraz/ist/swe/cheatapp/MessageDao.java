package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    Long insertMessage(ChatMessage message);

    @Query("SELECT * FROM ChatMessage WHERE userId=:userId ORDER BY messageId asc")
    List<ChatMessage> getRawMessagesByUserId(long userId);       //For testing purposes

    @Query("SELECT * FROM ChatMessage WHERE userId=:userId ORDER BY messageId asc")
    LiveData<List<ChatMessage>> getMessagesByUserId(long userId);
}
