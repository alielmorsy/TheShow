package aie.amg.theshow.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import aie.amg.theshow.R;
import aie.amg.theshow.util.Constants;

public class RequestActivity extends AppCompatActivity {
    InterstitialAd ad;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EditText editText = findViewById(R.id.requestName);
        view = findViewById(R.id.request);

        view.setOnClickListener(v -> {
            String text = editText.getText().toString();
            if (text.length() != 0) {
                Toast.makeText(this, "Please Wait", Toast.LENGTH_LONG).show();
                if (ad.isLoaded()) {
                    ad.show();
                } else
                    ad.loadAd(new AdRequest.Builder().build());

                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        if (i >= Constants.BaseAd) {
                            sendRequest(text);
                        }
                    }

                    @Override
                    public void onAdClosed() {
                        sendRequest(text);
                    }
                });
                view.setEnabled(false);
            }
        });
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        ad = new InterstitialAd(this);
        ad.setAdUnitId("ca-app-pub-3924327175857175/6870789550");
              ad.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void sendRequest(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.waiting_dialog, null, false);
        ProgressBar progressBar = view.findViewById(R.id.progress);
        TextView textView = view.findViewById(R.id.message);
        textView.setPadding(11, 11, 11, 11);
        builder.setView(view);
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.URL + "sendRequest.php?request=" + text);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                runOnUiThread(() -> textView.setText("Sending Request"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                if ("done".equals(line)) {
                    runOnUiThread(() -> {
                        textView.setText("Done");

                        progressBar.setVisibility(View.GONE);
                        view.setEnabled(true);
                    });
                } else {
                    runOnUiThread(() -> {
                        textView.setText("Can't connect to server please try again after minute");
                        progressBar.setVisibility(View.GONE);
                        view.setEnabled(true);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> RequestActivity.this.view.setEnabled(true));
        });
        AlertDialog dialog1 = builder.create();
        dialog1.show();
        dialog1.setOnCancelListener(dialog2 -> {
            thread.interrupt();
            view.setEnabled(true);
        });
        thread.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
