package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    Long insertMessage(Message message);

    @Query("SELECT * FROM Message WHERE userId=:userId ORDER BY messageId asc")
    List<Message> getRawMessagesByUserId(int userId);       //For testing purposes

    @Query("SELECT * FROM Message WHERE userId=:userId ORDER BY messageId asc")
    LiveData<List<Message>> getMessagesByUserId(int userId);
}
