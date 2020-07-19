package aie.amg.theshow.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;

import aie.amg.theshow.R;
import aie.amg.theshow.models.DownloadFile;

import static aie.amg.theshow.connection.DownloadLibrary.getFileSize;

public class Utils {
    public static final int KB = 1024;
    public static final int MB = KB * 1024;
    public static final int GB = MB * 1024;
    private static int downloadCount = 0;
    private static int total;

    private Utils() {

    }

    public static String sizeToString(long length) {
        String size;
        DecimalFormat format = new DecimalFormat("#.##");
        if (length < MB) {

            size = format.format(length / (float) KB) + " KB";
        } else if (length < GB) {
            size = format.format(length / (float) MB) + " MB";
        } else {
            size = format.format(length / (float) GB) + " GB";
        }
        return size;
    }

    public static void getNewUpdate(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_progress, null, false);
        ProgressBar progressBar = view.findViewById(R.id.progress);
        TextView downloaded = view.findViewById(R.id.data);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        String text = context.getString(R.string.download_text);
        new Thread(() -> {
            try {
                FileOutputStream outputStream = new FileOutputStream(Constants.AppFolder.getPath() + "/app.apk");

                URL url = new URL(Constants.URL + "app.apk");
                total = getFileSize(Constants.URL + "app.apk");

                InputStream stream = url.openStream();
                int c;
                context.runOnUiThread(() -> progressBar.setIndeterminate(false));
                byte[] b = new byte[1024];
                while ((c = stream.read(b)) > 0) {
                    outputStream.write(b, 0, c);
                    downloadCount += c;
                    context.runOnUiThread(() -> {
                        Utils.updateViews(text, progressBar, downloaded);
                    });
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.AppFolder.getPath() + "/app.apk"));
                context.startActivity(intent);
                outputStream.flush();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
                context.runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    downloaded.setText(R.string.download_failed);
                    dialog.setCancelable(false);
                });

            }
        }).start();
    }

    private static void updateViews(String text, ProgressBar progress, TextView downloaded) {

        progress.setProgress((downloadCount / 1024));
        float download = downloadCount / (float) (1024 * 1024);
        String copy = String.format(text, download, ((float) total / (float) (1024 * 1024)));
        downloaded.setText(copy);
    }

    public static int getWidth(Activity context) {
        DisplayMetrics matrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(matrics);

        return matrics.widthPixels;

    }

    public static int getHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static String generateNameFromDownloadFile(DownloadFile file) {
        String title = file.getShowName();
        if (file.getType() == Constants.Series) {
            try {
                JSONObject object = new JSONObject(file.getExtraData());
                if (file.getType() == Constants.Series) {
                    String tmp = String.format(Locale.getDefault(), "S%02d E%02d", object.getInt("seasonNumber"), object.getInt("episodeNumber"));

                    title = title + " " + tmp;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return title;
    }

    public static String durationToMinutes(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60) {
            return "00h00s";
        }
        long seconds = milliseconds % 60;
        long minutes = (milliseconds / 60) % 60;
        long hours = milliseconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {

            return mFormatter.format("%dh %02dm %02ds", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02dm %02d s", minutes, seconds).toString();
        }
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 115;
        int columnCount = (int) (dpWidth / scalingFactor);
        return (Math.max(columnCount, 2));
    }

    public static Bitmap extractBitmapFromVideo(String location) throws IOException {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bitmap = ThumbnailUtils.createVideoThumbnail(new File(location), new Size(150, 150), new CancellationSignal());
        } else {
            bitmap = ThumbnailUtils.createVideoThumbnail(location, MediaStore.Video.Thumbnails.MINI_KIND);

        }
        return bitmap;
    }

    public static String generateLinkFromExtraData(String name, String extra) {
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

    public static void startVideoPlay(Activity activity, String link) {

        if (link != null) {
            Intent intent;
            PackageManager packageManager = activity.getPackageManager();
            try {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setClassName(activity, "com.mxtech.videoplayer.pro");
                intent.setDataAndType(Uri.parse(link), "video/*");
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //MX Player pro isn't installed
                try {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName(activity, "com.mxtech.videoplayer.ad");
                    intent.setDataAndType(Uri.parse(link), "video/*");
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException er) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(link), "video/*");
                    activity.startActivity(intent);
                }
            }
        }
    }
}
