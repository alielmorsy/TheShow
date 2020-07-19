package aie.amg.theshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import aie.amg.theshow.R;
import aie.amg.theshow.download.DropApk;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.models.Movie;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.services.DownloadService;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.Utils;

public class GenerateLinkActivity extends AppCompatActivity {
    private DropApk dropApk;
    private String link;
    private InterstitialAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra("type", -1);
        Movie movie = null;
        Series.Episode episode = null;
        String token = null;
        if (type == Constants.Movie) {
            movie = (Movie) getIntent().getSerializableExtra("movie");
            token = movie.getTokenID();
        } else if (type == Constants.Episode) {
            episode = (Series.Episode) getIntent().getSerializableExtra("episode");
            token = episode.getTokenID();
        } else {
            finish();
            return;
        }
        setContentView(R.layout.activity_generate_link);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        Log.d("token", token + "");
        dropApk = new DropApk(token.trim());

        RelativeLayout waiting = findViewById(R.id.waiting);
        RelativeLayout messageLayout = findViewById(R.id.messageLayout);
        RelativeLayout part1 = findViewById(R.id.part1);
        RelativeLayout done = findViewById(R.id.done);

        ImageView codeImage = findViewById(R.id.codeImage);
        ImageView endIcon = findViewById(R.id.icon);

        EditText codeText = findViewById(R.id.codeText);

        TextView message = findViewById(R.id.message);

        Button submit = findViewById(R.id.submit);
        Button watch = findViewById(R.id.watch);
        Button download = findViewById(R.id.download);
        codeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    submit.setEnabled(true);
                } else {
                    submit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Movie finalMovie = movie;
        Series.Episode finalEpisode = episode;

        submit.setOnClickListener(v -> {
            part1.setVisibility(View.GONE);
            waiting.setVisibility(View.VISIBLE);
            dropApk.postCode(codeText.getText().toString());
        });

        dropApk.setOnStateChanged(new DropApk.OnStateChanged() {
            @Override
            public void onImageLinkGenerated(String link) {
                Log.d("link", link + " ");
                waiting.setVisibility(View.GONE);
                part1.setVisibility(View.VISIBLE);
                Picasso.get().load(link).error(R.drawable.no_internet).into(codeImage);
            }

            @Override
            public void onLinkFinish(String link) {
                waiting.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                GenerateLinkActivity.this.link = link;
                if (finalMovie != null)
                    finalMovie.setDownloadLink(link);
                else
                    finalEpisode.setDownloadUrl(link);
                message.setText("Done");
                endIcon.setImageResource(R.drawable.ic_done);
            }

            @Override
            public void onFailedGettingLink(int message1) {
                runOnUiThread(() -> {
                    waiting.setVisibility(View.GONE);
                    messageLayout.setVisibility(View.VISIBLE);
                    endIcon.setImageResource(R.drawable.ic_error);
                    message.setText("فشل الخصول علي اللينك");
                    Log.d("message", "" + message1);
                });
            }
        });
        watch.setOnClickListener((v) -> {
            done.setVisibility(View.GONE);
            if (ad.isLoaded()) {
                ad.show();
                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        performWatch();
                    }
                });
            }else if(ad.isLoading()){
                ad.setAdListener(new AdListener(){
                    @Override
                    public void onAdLoaded() {
                        ad.show();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        performWatch();
                    }

                    @Override
                    public void onAdClosed() {
                        performWatch();
                    }
                });
            }else{
                performWatch();
            }
        });

        download.setOnClickListener((v) -> {
            done.setVisibility(View.GONE);
            waiting.setVisibility(View.VISIBLE);
            if (ad.isLoaded()) {
                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        if (finalEpisode != null)
                            performDownload(finalEpisode);
                        else
                            performDownload(finalMovie);

                    }
                });
                ad.show();
            } else if (ad.isLoading()) {
                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        if (finalEpisode != null)
                            performDownload(finalEpisode);
                        else
                            performDownload(finalMovie);
                    }

                    @Override
                    public void onAdLoaded() {
                        ad.show();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        if (finalEpisode != null)
                            performDownload(finalEpisode);
                        else
                            performDownload(finalMovie);
                    }
                });
            } else {
                if (finalEpisode != null)
                    performDownload(finalEpisode);
                else
                    performDownload(finalMovie);
            }
            waiting.setVisibility(View.GONE);
            messageLayout.setVisibility(View.VISIBLE);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        dropApk.start();
        loadAd();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void performWatch() {
        Utils.startVideoPlay(this, link);
    }

    private void performDownload(Movie movie) {
        DownloadFile file = new DownloadFile();
        file.setLink(link);
        file.setTokenID(movie.getTokenID());
        file.setShowName(movie.getName());
        file.setPath(new File(Constants.MoviesFolder, movie.getName() + "." + movie.getYear() + ".TheShow.mkv").getPath());
        file.setType(Constants.Movie);
        JSONObject object = new JSONObject();
        try {
            object.put("duration", movie.getDuration());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        file.setExtraData(object.toString());
        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction("start");
        intent.putExtra("file", file);
        startService(intent);

    }

    private void performDownload(Series.Episode episode) {
        DownloadFile file = new DownloadFile();
        file.setLink(episode.getDownloadUrl());
        file.setTokenID(episode.getTokenID());
        file.setShowName(episode.getSeriesName());
        String path = String.format(Locale.getDefault(), "%s.S%02d.E%02d.TheShow", episode.getSeriesName(), episode.getSeasonNumber(), episode.getNumber());
        String generated = Utils.generateLinkFromExtraData(episode.getSeriesName(), file.getExtraData());
        File place = new File(Constants.SeriesFolder, generated + "/" + path + ".mkv");

        file.setPath(place.getPath());
        JSONObject object = new JSONObject();
        try {
            object.put("seasonNumber", episode.getSeasonNumber());
            object.put("episodeNumber", episode.getNumber());
            object.put("duration", episode.getDuration());
        } catch (JSONException ignored) {

        }
        file.setExtraData(object.toString());
        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction("start");
        intent.putExtra("file", file);
        startService(intent);

    }

    private void loadAd() {
        ad = new InterstitialAd(this);
        ad.setAdUnitId("ca-app-pub-3924327175857175/3034334947");
        ad.loadAd(new AdRequest.Builder().build());
    }
}
