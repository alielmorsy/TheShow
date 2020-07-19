package aie.amg.theshow.database.whatch_list;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import aie.amg.theshow.models.WatchListItem;

@Database(entities = WatchListItem.class, exportSchema = false, version = 1)
public abstract class WatchListRoom extends RoomDatabase {

    private static volatile WatchListRoom INSTANCE;
    private static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static WatchListRoom getINSTANCE(Context context) {
        if (INSTANCE == null) {
            synchronized (new Object()) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, WatchListRoom.class, "WatchList").addCallback(callback).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract WatchListDoa getDoa();
}
