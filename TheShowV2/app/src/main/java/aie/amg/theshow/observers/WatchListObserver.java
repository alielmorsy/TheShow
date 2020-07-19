package aie.amg.theshow.observers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import aie.amg.theshow.database.whatch_list.WatchListDatabaseUtils;
import aie.amg.theshow.database.whatch_list.WatchListDoa;
import aie.amg.theshow.models.WatchListItem;

public class WatchListObserver extends AndroidViewModel {
    private WatchListDatabaseUtils utils;
    public WatchListObserver(@NonNull Application application) {
        super(application);
        utils=new WatchListDatabaseUtils(application);
    }
    public LiveData<List<WatchListItem>> getList(){
        return utils.getAll();
    }
}
