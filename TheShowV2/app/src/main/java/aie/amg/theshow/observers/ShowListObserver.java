package aie.amg.theshow.observers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

import aie.amg.theshow.models.Show;

public class ShowListObserver extends AndroidViewModel {
    private MediatorLiveData<ArrayList<Show>> liveData;

    public ShowListObserver(@NonNull Application application) {
        super(application);
        liveData = new MediatorLiveData<>();

    }

    public MediatorLiveData<ArrayList<Show>> getLiveData() {
        return liveData;
    }
}
