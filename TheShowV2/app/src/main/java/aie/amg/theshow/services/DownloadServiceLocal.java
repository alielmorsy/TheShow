package aie.amg.theshow.services;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import aie.amg.theshow.connection.VideoStreamServer;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.util.ConfigurationHelper;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.Utils;

public class DownloadServiceLocal extends Service {
    private ArrayList<Long> longs = new ArrayList<>();
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (longs.size() > 0) {
                longs.remove(0);
            }
            if (longs.size() == 0) {
                stopForeground(true);
                unregisterReceiver(this);
            }
        }
    };
    private boolean isGrid;
    private DownloadManager manager;

    private VideoStreamServer streamServer;

    public DownloadServiceLocal() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new RuntimeException();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isGrid = new ConfigurationHelper(this).isAdm();
        manager = getSystemService(DownloadManager.class);
        streamServer = new VideoStreamServer(this, 1800, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "ali");
            notificationBuilder.setContentTitle("The Show Downloader");
            notificationBuilder.setContentText("This Notification is used to make you know that the application is started in the background \n " +
                    "Note: it don't take from processor and ram anything it small");
            startForeground(1, notificationBuilder.build());

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "started");

        if (intent.getAction().equals("stop")) {
            streamServer.stop();
            return START_NOT_STICKY;
        }
        DownloadFile file = (DownloadFile) intent.getSerializableExtra("file");
        Log.d("file", file.getLink()+" a");
        streamServer.setDownloadFile(file);
        try {
            streamServer.start();

        } catch (IOException e) {
            e.printStackTrace();
            streamServer.stop();
            try {
                streamServer.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        new Handler().postDelayed(() -> {
            if (streamServer.wasStarted()) {
                if (isGrid) {
                    String t = generateLinkFromExtraData(file.getShowName(), file.getExtraData());
                    int a = t.lastIndexOf("/");
                    startService(t.substring(0, Math.min(t.length(), a)));
                } else
                    createRequest(file);
            }
        }, 1200);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (long l : longs) {
            manager.remove(l);
        }
        stopForeground(true);
    }

    private void createRequest(DownloadFile file) {
        String dir = "/TheShow/";
        Log.v("Files", Environment.getExternalStoragePublicDirectory("/TheShow").getPath() + " a");
        if (file.getType() == Constants.Movie) {
            dir = dir + "movies";
        } else {
            dir = dir + "series/" + generateLinkFromExtraData(file.getShowName(), file.getExtraData());

        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://127.0.0.1:1800"));
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedOverRoaming(false);
        request.setTitle("Downloading");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            request.allowScanningByMediaScanner();
        }
        Log.v("dir", dir);
        request.setDestinationInExternalPublicDir(dir, file.getPath());
        request.setDescription(Utils.generateNameFromDownloadFile(file));
        long downloadReference = manager.enqueue(request);
        request.setAllowedOverMetered(false);
        longs.add(downloadReference);
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private String generateLinkFromExtraData(String name, String extra) {
        try {
            JSONObject object = new JSONObject(extra);
            int season = object.getInt("seasonNumber");
            File file = new File(Constants.SeriesFolder, name + "/");
            if (!file.exists()) file.mkdir();
            String path = String.format(Locale.getDefault(), "%s/S%02d", name, season);
            file = new File(Constants.SeriesFolder, path + "/");
            if (!file.exists()) file.mkdir();
            return path;
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return "";
    }

    private void startService(String fileName) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setClassName("com.dev.adm","com.dv.get.AEditor");
        intent.setDataAndType(Uri.parse("http://127.0.0.1:1800/" + fileName + "mkv"), "video/*");

        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
