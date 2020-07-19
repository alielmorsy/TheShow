package aie.amg.theshow.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import aie.amg.theshow.models.DownloadFile;


@Database(entities = {DownloadFile.class}, version = 1,exportSchema = false)
public abstract class DownloadRoom extends RoomDatabase {

     private volatile static DownloadRoom INSTANCE;
    private static Callback callback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);


        }
    };

    public static DownloadRoom getINSTANCE(Context context) {
        if (INSTANCE == null) {
            synchronized (new Object()) {

                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, DownloadRoom.class, "DownloadFiles").addCallback(callback).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract DownloadDoa getDoa();
}
