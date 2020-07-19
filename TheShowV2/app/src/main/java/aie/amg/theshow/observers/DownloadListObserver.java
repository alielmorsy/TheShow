package aie.amg.theshow.observers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import aie.amg.theshow.database.DownloadDatabaseUtil;
import aie.amg.theshow.models.DownloadFile;

public class DownloadListObserver extends AndroidViewModel {
    private DownloadDatabaseUtil utils;

    public DownloadListObserver(@NonNull Application application) {
        super(application);
        utils = new DownloadDatabaseUtil(application);
    }

    public LiveData<List<DownloadFile>> getLiveData(boolean done) {
        return utils.getType(done);
    }
}
