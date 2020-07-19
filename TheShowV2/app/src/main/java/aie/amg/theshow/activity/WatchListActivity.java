package aie.amg.theshow.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.utils.WatchListAdapter;
import aie.amg.theshow.models.Movie;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.models.Show;
import aie.amg.theshow.observers.WatchListObserver;
import aie.amg.theshow.services.DownloadService;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.JsonToModel;

public class WatchListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);
        prepareList();
    }

    private void prepareList() {
        RecyclerView list = findViewById(R.id.list);
        WatchListAdapter adapter = new WatchListAdapter(this);
        list.setAdapter(adapter);
        adapter.setOnWatchListItemClick((item, position) -> {
            createDialog();
            new GetShow().execute(item.getShowID(), item.getType());
        });
        WatchListObserver observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(WatchListObserver.class);
        observer.getList().observe(this, adapter::setItems);
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ProgressBar progressBar = new ProgressBar(this);
        builder.setView(progressBar);
        builder.show();
    }

    class GetShow extends AsyncTask<Integer, Void, Show> {

        @Override
        protected Show doInBackground(Integer... ids) {
            String path = Constants.URL + "movie/" + ids[0];
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject object = new JSONObject(builder.toString());
                if (object.has("status")) {
                    createMessage(object.getString("status"));
                } else {

                    if (ids[1] == Constants.Movie) {
                        JsonToModel<Movie> movieJsonToModel = new JsonToModel<Movie>(object, Movie.class);
                        return movieJsonToModel.getObject();
                    } else {
                        JsonToModel<Series> seriesJsonToModel = new JsonToModel<Series>(object, Series.class);
                        return seriesJsonToModel.getObject();
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Show show) {
            if (show != null)
                startActivity(new Intent(getApplicationContext(), InfoActivity.class).putExtra("show", show));
            else {
                createMessage("Can't Get Information About this Show");
            }
        }

        private void createMessage(String message) {
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage(message);
                builder.show();
            });
        }
    }

}
