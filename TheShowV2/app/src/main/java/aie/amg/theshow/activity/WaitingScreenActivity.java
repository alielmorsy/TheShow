package aie.amg.theshow.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import aie.amg.theshow.R;
import aie.amg.theshow.connection.DownloadLibrary;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.InstallVlcLibrary;

public class WaitingScreenActivity extends AppCompatActivity {

    private TextView message;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("update")) {
                String version = intent.getStringExtra("version");
                if (version != null && !version.equals("null")) {
                    if (!Build.VERSION.RELEASE.equals(version)) ;
                    preparePromptDialog(version);
                } else {
                    performGo();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_screen);
        message = findViewById(R.id.message);
        message.setText("Ask for permission Permission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1900);

    }


    public void preparePromptDialog(String version) {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There new update available to the app TheShow--" + version);
        builder.setPositiveButton("Download \uD83D\uDE0A ", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(WaitingScreenActivity.this, "Thanks!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://theshow.xyz/download-page.php"));
            startActivity(intent);

        });
        builder.setNegativeButton("Later \uD83D\uDE22", (dialog, which) -> {
            Toast.makeText(WaitingScreenActivity.this, "Damn... \uD83D\uDE21", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            performGo();
        });
        //  builder.setOnDismissListener(dialog -> performGo());
        builder.create().show();
    }

    private void performUpdate() {

        message.setText("Checking For Update");
        checkForUpdate();
    }

    private void installLib() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        message.setText("Installing Important Files");
        Runnable runnable = () -> {
            try {
                System.loadLibrary("vlc");
                System.loadLibrary("vlcjni");
                System.loadLibrary("c++_shared");
                runOnUiThread(this::performGo);
            } catch (SecurityException | UnsatisfiedLinkError e) {
                try {
                    InstallVlcLibrary.installNativeLibraryPath(this, getClassLoader());
                    runOnUiThread(this::performGo);
                    Log.d("install", "total");
                } catch (Exception e1) {
                    runOnUiThread(this::performGo);
                    Log.d("install", "ali");
                    e1.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }


    private void installLibFiles() {
        String api = InstallVlcLibrary.getCurrentAbi(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_progress, null, false);
        ProgressBar progressBar = view.findViewById(R.id.progress);
        TextView downloaded = view.findViewById(R.id.data);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        DownloadLibrary thread = new DownloadLibrary(this, dialog, api, progressBar, downloaded, getString(R.string.download_text));
        thread.setListener(new DownloadLibrary.OnDownloadEnd() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(WaitingScreenActivity.this::performGo);
            }

            @Override
            public void onDownloadFailed() {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.lib_failed_message, Toast.LENGTH_LONG).show();
                    runOnUiThread(WaitingScreenActivity.this::performGo);
                });
            }
        });
        thread.start();


        dialog.show();
        dialog.setCancelable(false);
    }

    private void getNewCategories() {
        message.setText("Check for anything new");
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.URL + "conf.php?action=news");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(600);
                InputStream inputStream = url.openStream();
                File outFile = new File(Constants.ConfigurationFolder, "cat.dat");
                OutputStream outputStream = new FileOutputStream(outFile);
                byte[] b = new byte[1024];
                int c;
                while ((c = inputStream.read(b)) > 0) {
                    outputStream.write(b, 0, c);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                message.setText("Getting Done");
                performGo();
            });
        });

        thread.start();

    }

    private void performGo() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 1000);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1900) {
            if (grantResults.length == 0) {
                Toast.makeText(this, "Sorry you must accept permission so can use App", Toast.LENGTH_LONG).show();
                System.exit(-1);
            } else {
                for (int g : grantResults) {
                    if (g != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Sorry you must accept permission so can use App", Toast.LENGTH_LONG).show();
                        System.exit(-1);
                    }
                }
                Log.d("permission", "true");
                performUpdate();
            }
        }
    }

    private void checkForUpdate() {

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Constants.URL + "conf.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(600);
                InputStream stream = connection.getInputStream();
                byte[] b = new byte[1024];
                int read = stream.read(b);
                if (read != -1) {
                    String text = new String(b, 0, read);
                    JSONObject object = new JSONObject(text);
                    runOnUiThread(() -> {
                        try {
                            preparePromptDialog(object.getString("version"));
                            Constants.DOWNLOAD_SERVER = object.getString("download_server");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Intent intent = new Intent("update");
                intent.putExtra("version", "null");
                runOnUiThread(this::installLib);
            }
        });
        thread.start();


    }


}
