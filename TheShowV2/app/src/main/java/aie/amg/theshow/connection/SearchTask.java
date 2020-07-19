package aie.amg.theshow.connection;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import aie.amg.theshow.models.Movie;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.models.Show;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.JsonToModel;

public class SearchTask extends AsyncTask<String, Void, ArrayList<Show>> {
    private MediatorLiveData<ArrayList<Show>> liveData;

    public SearchTask(MediatorLiveData<ArrayList<Show>> liveData) {
        this.liveData = liveData;
    }

    @Override
    protected ArrayList<Show> doInBackground(String... strings) {
        try {
            String path = Constants.URL + "Search.php?query=" + strings[0];
            Log.d("path", path);
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(800);
            connection.setConnectTimeout(800);
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            String text = builder.toString();
            ArrayList<Show> list = new ArrayList<>();
            JSONArray array = new JSONArray(text);
            int size = array.length();
            for (int i = 0; i < size; i++) {
                JSONObject object = array.getJSONObject(i);
                if ("movie".equals(object.getString("sType"))) {
                    JsonToModel<Movie> movies = new JsonToModel<>(object, Movie.class);
                    list.add(movies.getObject());
                } else {
                    JsonToModel<Series> movies = new JsonToModel<>(object, Series.class);
                    list.add(movies.getObject());
                }
            }
            return list;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Show> shows) {
        liveData.postValue(shows);
    }
}
