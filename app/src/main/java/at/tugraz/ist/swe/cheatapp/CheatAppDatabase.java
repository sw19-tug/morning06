package at.tugraz.ist.swe.cheatapp;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Message.class}, version = 1)
public abstract class CheatAppDatabase extends RoomDatabase {

    public abstract MessageDao messageDao();
}
