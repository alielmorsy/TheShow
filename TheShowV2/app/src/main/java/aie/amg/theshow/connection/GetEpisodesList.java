package aie.amg.theshow.connection;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import aie.amg.theshow.activity.fragment.MainFragment;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.JsonToModel;

public class GetEpisodesList extends AsyncTask<String, Void, ArrayList<Series.Episode>> {
    private MediatorLiveData<ArrayList<Series.Episode>> liveData;

    public GetEpisodesList(MediatorLiveData<ArrayList<Series.Episode>> liveData) {
        this.liveData = liveData;
    }

    @Override
    protected ArrayList<Series.Episode> doInBackground(String... strings) {
        try {
            String path = Constants.URL + "GetList.php?type=" + MainFragment.EPISODES + strings[0]+"&list=1";
            URL url = new URL(path);
            Log.d("path",path);
            InputStream stream = url.openStream();
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            String text = builder.toString();
            JsonToModel<Series.Episode> episodes = new JsonToModel<>(text, Series.Episode.class);
            if (episodes.getType() != JsonToModel.JsonType.Error) {
                return episodes.getArray();
            }
            return null;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Series.Episode> episodes) {
        liveData.postValue(episodes);
    }
}
