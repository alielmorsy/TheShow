package aie.amg.theshow.connection;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

import aie.amg.theshow.database.DownloadDatabaseUtil;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.services.DownloadService;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.NotificationUtils;

public class DownloadThread extends AsyncTask<DownloadFile, Integer, DownloadFile> {
    private DownloadDatabaseUtil databaseUtil;
    private DownloadFile file;
    private boolean New;

    public DownloadThread(DownloadDatabaseUtil databaseUtil, boolean New) {
        this.databaseUtil = databaseUtil;
        this.New = New;
    }

    @Override
    protected DownloadFile doInBackground(DownloadFile... downloadFiles) {
        file = downloadFiles[0];

        try {
            Log.d("connecting","hello");
            NotificationUtils.CreateDownloadingNotification(file, databaseUtil.context);
            file.setStatus(Constants.DownloadStatus.CONNECTING);
            databaseUtil.update(file);
            checkIfError(file);
            URL url = new URL(file.getLink());
            Log.d("connecting","hello2");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (file.getDownloaded() != 0) {
                connection.setRequestProperty("Range", "bytes=" + (file.getDownloaded() + 1) + "-");
            }
            connection.connect();
            Log.d("range", " " + connection.getHeaderField("Content-Range"));
            if (connection.getResponseCode() != 200 && connection.getResponseCode() != 206) {
                return null;
            }

            File f = new File(file.getPath());
            if (f.exists()) {
                String path = f.getPath();
                path = path.substring(0, path.lastIndexOf('.'));
                path = path + "1.mkv";
            }
            FileOutputStream fos = new FileOutputStream(new File(file.getPath()), true);
            file.setFileSize(connection.getContentLength());
            InputStream stream = connection.getInputStream();
            byte[] b = new byte[1024];
            int c;
            file.setStatus(Constants.DownloadStatus.DOWNLOADING);
            databaseUtil.update(file);
            int downloaded = 0;
            long beforeNano = System.nanoTime();
            long second = 1000000000;

            while ((c = stream.read(b)) > 0) {
                fos.write(b, 0, c);
                downloaded += c;
                long currentTime = System.nanoTime();
                if ((currentTime - beforeNano) >= second) {
                    file.setDownloaded(downloaded + file.getDownloaded());
                    databaseUtil.update(file);
                    NotificationUtils.updateProgress(file);
                    beforeNano = System.nanoTime();
                    downloaded = 0;

                }

            }
            file.setDone(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void checkIfError(DownloadFile file) throws Exception {
        URL url = new URL(file.getLink());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        if (connection.getResponseCode() != 200 && connection.getResponseCode() != 206) {
            file.setStatus(Constants.DownloadStatus.ERROR);
            databaseUtil.update(file);
            cancel(true);
        }
    }

    @Override
    protected void onPostExecute(DownloadFile file) {
        if (file == null) {
            return;
        }

        Executors.newFixedThreadPool(4).execute(() -> databaseUtil.update(file));
        if (file.isDone())
            databaseUtil.context.startService(new Intent(databaseUtil.context, DownloadService.class).setAction("done").putExtra("file", file));
        else {
            file.setStatus(Constants.DownloadStatus.RETRYING);
            databaseUtil.update(file);
            databaseUtil.context.startService(new Intent(databaseUtil.context, DownloadService.class).setAction("retry").putExtra("file", file));
        }


        NotificationUtils.setDone(file, databaseUtil.context);

    }

    @Override
    protected void onCancelled() {
        if (file != null) {
            NotificationUtils.removeNotification(file.getId());
            file.setStatus(Constants.DownloadStatus.PAUSED);
            databaseUtil.update(file);
        }
    }
}
