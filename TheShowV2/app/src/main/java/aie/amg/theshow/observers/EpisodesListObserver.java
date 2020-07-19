package aie.amg.theshow.observers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

import aie.amg.theshow.models.Series;

public class EpisodesListObserver extends AndroidViewModel {
    private MediatorLiveData<ArrayList<Series.Episode>> liveData;

    public EpisodesListObserver(@NonNull Application application) {
        super(application);
        liveData = new MediatorLiveData<>();
    }

    public MediatorLiveData<ArrayList<Series.Episode>> getLiveData() {
        return liveData;
    }
}
