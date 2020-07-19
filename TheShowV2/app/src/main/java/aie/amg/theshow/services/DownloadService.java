package aie.amg.theshow.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import aie.amg.theshow.connection.DownloadThread;
import aie.amg.theshow.database.DownloadDatabaseUtil;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.NotificationUtils;

public class DownloadService extends Service {
    private ArrayList<DownloadFile> files;
    private ArrayList<DownloadThread> threads;
    private DownloadDatabaseUtil util;


    public DownloadService() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        util = new DownloadDatabaseUtil(this);
        files = new ArrayList<>();
        threads = new ArrayList<>();
        NotificationUtils.CreateNotificationForDownloads(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        DownloadFile file = (DownloadFile) intent.getSerializableExtra("file");
        assert action == null : new NullPointerException("Action Can't Be Null");
        switch (action) {
            case "done":
                handleDone(file);
                break;
            case "pause":
                handleDone(file);
                NotificationUtils.removeNotification(file.getId());
                file.setStatus(Constants.DownloadStatus.PAUSED);
                util.update(file);
                break;
            case "start":
                handleStart(file, true);
                break;
            case "continue":
                handleStart(file, false);
                break;
            case "remove":
                handleDone(file);
                NotificationUtils.removeNotification(file.getId());
                util.delete(file);
                break;
            case "retry":
                handleRetry(file);
        }
        return START_STICKY;
    }

    private void handleRetry(DownloadFile file) {
        for (int i = 0; i < files.size(); i++) {
            DownloadFile tmp = files.get(i);
            if (tmp.getId() == file.getId()) {
                int finalI = i;
                new Handler().postDelayed(() -> {
                    DownloadThread thread = new DownloadThread(util, false);
                    thread.execute(file);
                    threads.set(finalI, thread);
                }, 5000);
            }
        }
    }


    private void handleStart(DownloadFile file, boolean New) {
        if (New) {
            Executors.newFixedThreadPool(4).execute(() -> {
                long id = util.add(file);
                file.setId((int) id);
                files.add(file);
                DownloadThread thread = new DownloadThread(util, true);
                thread.execute(file);
                threads.add(thread);
            });
        } else {
            files.add(file);
            DownloadThread thread = new DownloadThread(util, false);
            thread.execute(file);
            threads.add(thread);
        }
    }


    private void handleDone(DownloadFile file) {

        for (int i = 0; i < files.size(); i++) {

            DownloadFile tmp = files.get(i);
            if (tmp.getId() == file.getId()) {
                files.remove(i);
                threads.remove(i).cancel(true);

            }

        }
        if (files.isEmpty()) {
            stopService(new Intent(this, DownloadService.class));
        }
    }

}
