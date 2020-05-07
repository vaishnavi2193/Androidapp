package comp575.helloworld;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Contact.class}, version = 3)
public abstract class ContactRoomDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();

    private static ContactRoomDatabase INSTANCE;

    public static synchronized ContactRoomDatabase getDatabase(Context context) {
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ContactRoomDatabase.class, "contact_databse").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
