package aie.amg.theshow.connection;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import aie.amg.theshow.R;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.InstallVlcLibrary;

public class DownloadLibrary extends Thread {
    private Activity context;
    private String api;
    private ProgressBar progress;
    private TextView downloaded;
    private String text;
    private int downloadCount = 0, total;
    private float totalFloat;
    private AlertDialog dialog;
    private OnDownloadEnd listener;

    public DownloadLibrary(Activity context, AlertDialog dialog, String api, ProgressBar progress, TextView downloaded, String text) {
        this.context = context;
        this.api = api;
        this.progress = progress;
        this.downloaded = downloaded;
        this.text = text;
        this.dialog = dialog;
    }

    public static int getFileSize(String url) throws IOException {

        HttpURLConnection conn = null;
        try {
            URL link = new URL(url);
            conn = (HttpURLConnection) link.openConnection();
            //List<String> cookies = conn.getHeaderFields().get("Set-Cookie");

            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    @Override
    public void run() {

        try {
            if (!Constants.TmpFolder.exists()) Constants.TmpFolder.mkdir();
            OutputStream file = new FileOutputStream(new File(Constants.TmpFolder, "jni.tmp"));

            URL url = new URL(Constants.URL + "getSo.php?api=" + api);
            total = getFileSize(Constants.URL + "getSo.php?api=" + api);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(2000);

            InputStream stream = connection.getInputStream();
            connection.connect();
            context.runOnUiThread(() -> progress.setMax(total / 1024));
            int c;
            byte[] b = new byte[1024];
            context.runOnUiThread(() -> progress.setIndeterminate(false));
            while ((c = stream.read(b)) > 0) {
                file.write(b, 0, c);
                downloadCount += c;
                context.runOnUiThread(this::updateViews);
            }
        } catch (IOException e) {
            context.runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                downloaded.setText(R.string.download_failed);
                dialog.setCancelable(false);
                listener.onDownloadFailed();
            });

            e.printStackTrace();
            return;
        }
        context.runOnUiThread(() -> {
            progress.setIndeterminate(true);
            downloaded.setText(R.string.download_done_lib);
        });

        try {
            deCompress();
            Log.d("compress", "done");
            InstallVlcLibrary.installNativeLibraryPath(context, getContextClassLoader());
            context.runOnUiThread(() -> {
                downloaded.setText("Install successfully");
                dialog.setCancelable(false);
            });
            sleep(4000);
            context.runOnUiThread(() -> {
                dialog.dismiss();
                listener.onDownloadSuccess();
            });

        } catch (Exception e) {
            context.runOnUiThread(() -> {
                dialog.setCancelable(false);
            });
            listener.onDownloadFailed();
            e.printStackTrace();
        }
    }

    private void updateViews() {

        progress.setProgress((downloadCount / 1024));
        float download = downloadCount / (float) (1024 * 1024);
        String copy = String.format(text, download, ((float) total / (float) (1024 * 1024)));
        downloaded.setText(copy);
    }

    private void deCompress() throws IOException {
        ZipInputStream input = new ZipInputStream(new FileInputStream(new File(Constants.TmpFolder, "jni.tmp")));
        ZipEntry entry = input.getNextEntry();
        byte[] bytes = new byte[1024];
        int c;
        while (entry != null) {
            String s = context.getApplicationInfo().nativeLibraryDir;
            //  File file = new File(new File(s), entry.getName());
            File file = new File(Constants.LibFolder, entry.getName());
            FileOutputStream outputStream = new FileOutputStream(file);
            while ((c = input.read(bytes)) > 0) {
                outputStream.write(bytes, 0, c);
            }
            outputStream.flush();
            outputStream.close();

            entry = input.getNextEntry();
        }

    }

    public void setListener(OnDownloadEnd listener) {
        this.listener = listener;
    }

    public interface OnDownloadEnd {
        void onDownloadSuccess();

        void onDownloadFailed();
    }
}
