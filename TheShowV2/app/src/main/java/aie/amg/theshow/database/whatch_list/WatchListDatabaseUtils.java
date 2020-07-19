package aie.amg.theshow.database.whatch_list;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

import aie.amg.theshow.models.WatchListItem;

public class WatchListDatabaseUtils {
    private Context context;
    private WatchListDoa doa;

    public WatchListDatabaseUtils(Context context) {
        this.context = context;
        doa = WatchListRoom.getINSTANCE(context).getDoa();
    }

    public long add(WatchListItem item) {
        return doa.addItem(item);
    }

    public LiveData<List<WatchListItem>> getAll() {
        return doa.getAll();
    }

    public void delete(String item) {
        Executors.newFixedThreadPool(4).execute(() -> doa.deleteItem(item));
    }

    public void deleteALL() {
        Executors.newFixedThreadPool(4).execute(doa::deleteAll);
    }

    public boolean inDatabase(String showName) {
        return doa.get(showName) != null;
    }
}
