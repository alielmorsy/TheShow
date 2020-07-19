package aie.amg.theshow.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.DownloadActivity;
import aie.amg.theshow.models.DownloadFile;

public class NotificationUtils {

    private static NotificationManager manager;

    private static String NOTIFICATION_CHANNEL_ID = "aie.download", channelName = "Downloader";
    private static NotificationCompat.Builder notificationBuilder;

    private static String success = "Download Successful";
    private static String failed = "Download Failed";

    private NotificationUtils() {

    }

    public static void CreateNotificationForDownloads(Service context) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground(context);
        else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "ali");
            notificationBuilder.setContentTitle("The Show Downloader");
            notificationBuilder.setContentText("This Notification is used to make you know" +
                    " that the application is started in the background \n " +
                    "Note: it don't take anything from battery anything it small");

            Intent notifyIntent = new Intent(context, DownloadActivity.class);

            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(notifyPendingIntent);

            context.startForeground(Integer.MAX_VALUE, notificationBuilder.build());

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static void startMyOwnForeground(Service context) {

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(context.getColor(R.color.colorAccent));
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        Intent notifyIntent = new Intent(context, DownloadActivity.class);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(notifyPendingIntent);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        context.startForeground(Integer.MAX_VALUE, notification);
    }

    public static void CreateDownloadingNotification(DownloadFile file, Context context) {

        notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setOngoing(true).setContentTitle("Downloading").setContentText(file.getShowName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_MIN).setCategory(Notification.CATEGORY_SERVICE);
        }
        notificationBuilder.setSmallIcon(R.drawable.icon);
        notificationBuilder.setProgress(100, 0, true);
        manager.notify((int) file.getId(), notificationBuilder.build());
    }

    public static void updateProgress(DownloadFile file) {

        float divide = (float) file.getDownloaded() / (float) file.getFileSize();

        float per = divide * 100;
        notificationBuilder.setContentText(per + " %");
        notificationBuilder.setProgress((int) file.getFileSize() / 1000, (int) file.getDownloaded() / 1000, false);
        manager.notify((int) file.getId(), notificationBuilder.build());
    }

    public static void setDone(DownloadFile file, Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.icon);
        notificationBuilder.setOngoing(false).setContentTitle(file.isDone() ? success : failed).setContentText(file.getShowName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_MIN).setCategory(Notification.CATEGORY_SERVICE);
        }
        if (file.isDone())
            notificationBuilder.setSmallIcon(R.drawable.ic_done);
        else
            notificationBuilder.setSmallIcon(R.drawable.ic_error);
        manager.notify((int) file.getId(), notificationBuilder.build());
    }

    public static void removeNotification(long id) {
        manager.cancel((int) id);
    }

}
