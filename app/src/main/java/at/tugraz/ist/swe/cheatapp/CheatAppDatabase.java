package at.tugraz.ist.swe.cheatapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ChatMessage.class}, version = 1, exportSchema = false)
public abstract class CheatAppDatabase extends RoomDatabase {

    public abstract MessageDao messageDao();
}
