package aie.amg.theshow.connection;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import aie.amg.theshow.models.Series;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.JsonToModel;

public class SeasonTask extends AsyncTask<String, Void, ArrayList<Series.Season>> {
    private MediatorLiveData<ArrayList<Series.Season>> liveData;

    public SeasonTask(MediatorLiveData<ArrayList<Series.Season>> liveData) {
        this.liveData = liveData;
    }

    @Override
    protected ArrayList<Series.Season> doInBackground(String... strings) {
        try {
            String path = Constants.URL + "GetList.php?what=seasons&id=" + strings[0];
            Log.d("path",path);
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(500);
            connection.setConnectTimeout(500);
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JsonToModel<Series.Season> seasonJsonToModel = new JsonToModel<>(builder.toString(), Series.Season.class);
            if (seasonJsonToModel.getType() != JsonToModel.JsonType.Error) {
                return seasonJsonToModel.getArray();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Series.Season> episodes) {
        liveData.postValue(episodes);
    }
}
