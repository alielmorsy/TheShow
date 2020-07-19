package aie.amg.theshow.observers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

import aie.amg.theshow.models.Series;

public class SeasonsListObserver extends AndroidViewModel {
    private MediatorLiveData<ArrayList<Series.Season>> liveData;
    public SeasonsListObserver(@NonNull Application application) {
        super(application);
        liveData=new MediatorLiveData<>();    }

    public MediatorLiveData<ArrayList<Series.Season>> getLiveData() {
        return liveData;
    }
}
