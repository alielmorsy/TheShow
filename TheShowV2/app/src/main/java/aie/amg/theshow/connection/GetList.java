 package aie.amg.theshow.connection;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import aie.amg.theshow.activity.fragment.MainFragment;
import aie.amg.theshow.models.Movie;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.models.Show;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.JsonToModel;

public class GetList extends AsyncTask<String, Show, ArrayList<Show>> {
    private MediatorLiveData<ArrayList<Show>> liveData;

    public GetList(MediatorLiveData<ArrayList<Show>> liveData) {
        this.liveData = liveData;
    }


    @Override
    protected ArrayList<Show> doInBackground(String... strings) {
        String link = Constants.URL + "GetList.php?type=" + strings[0] + "&list=" + strings[1] + strings[2];
        StringBuilder builder = new StringBuilder();
        DataOutputStream dos = null;
        try {
            FileOutputStream fos = new FileOutputStream(Constants.AppFolder + "/ali.txt");
            dos = new DataOutputStream(fos);
            Log.d("link", link);
            dos.writeBytes(link + " \n");
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(800);
            connection.setConnectTimeout(800);

            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            String text = builder.toString();
            dos.writeBytes(text + " \n");
            if (strings[0].equals(MainFragment.MOVIES)) {
                JsonToModel<Movie> toMovie = new JsonToModel<Movie>(text, Movie.class);
                if (toMovie.getType() == JsonToModel.JsonType.Array) {
                    return new ArrayList<>(toMovie.getArray());
                } else if (toMovie.getType() == JsonToModel.JsonType.OBJECT) {
                    ArrayList<Show> list = new ArrayList<>();
                    list.add(toMovie.getObject());
                    return list;
                } else {
                    Log.d("builder", builder.toString());
                    return null;
                }

            } else if (strings[0].equals(MainFragment.SERIES)) {
                JsonToModel<Series> toSeries = new JsonToModel<>(text, Series.class);
                if (toSeries.getType() == JsonToModel.JsonType.Array) {
                    return new ArrayList<>(toSeries.getArray());
                } else if (toSeries.getType() == JsonToModel.JsonType.OBJECT) {
                    ArrayList<Show> list = new ArrayList<>();
                    list.add(toSeries.getObject());
                    return list;
                } else {
                    Log.d("builder", builder.toString());
                    return null;
                }
            }
        } catch (IOException | JSONException e) {
            try {
                dos.writeBytes(e.getLocalizedMessage() + " \n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Show> shows) {
        liveData.postValue(shows);
    }

}
