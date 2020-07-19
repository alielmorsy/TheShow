package aie.amg.theshow.util;

import java.io.File;

public class Constants {

    // Types of show send to show download activity
    public static final int Episode = 0;
    public static final int Season = 1;
    public static final int Movie = 2;
    public static final int Series = 1;
    public static final int LocalVideo = 3;
    //public static final String IMAGE_URL = "https://192.168.43.108/TheShow/images/";
    //App Folder

    public static final File AppFolder = new File("/sdcard/TheShow/");
    //Tmp Folder
    public static final File TmpFolder = new File(AppFolder, "tmp/");
    public static final File LibFolder = new File(AppFolder, "lib/");
    public static final File MoviesFolder = new File(AppFolder, "movies/");
    public static final File SeriesFolder = new File(AppFolder, "series/");
    public static final File ConfigurationFolder = new File(AppFolder, "conf/");
    public static final File SubtitleFolder = new File(AppFolder, "subtitles/");

    public static final int BaseAd = 3;

    public static String DOWNLOAD_SERVER = "https://dropapk.to/";

    public static String URL = "https://theshowapp.000webhostapp.com/theshow/";
    //public static String URL = "http://192.168.43.108/TheShow2/";
    public static String IMAGE_URL = "https://theshowapp.000webhostapp.com/images/";
    //public static String IMAGE_URL = "https://192.168.43.108/images/";

    static {
        if (!AppFolder.exists())
            AppFolder.mkdir();
        if (!TmpFolder.exists())
            TmpFolder.mkdir();
        if (!LibFolder.exists()) LibFolder.mkdir();
        if (!MoviesFolder.exists()) MoviesFolder.mkdir();
        if (!SeriesFolder.exists()) SeriesFolder.mkdir();
        if (!ConfigurationFolder.exists()) ConfigurationFolder.mkdir();
        if (!SubtitleFolder.exists()) SubtitleFolder.mkdir();
    }

    public static class DownloadStatus {
        public static final String PAUSED = "Paused";
        public static final String PENDING = "Pending";
        public static final String CONNECTING = "Connecting";
        public static final String DOWNLOADING = "Downloading";
        public static final String RETRYING = "Retrying";

        public static final String ERROR = "Can't Download";
    }

    public static class LinkType {
        public static final int MEGA = 0;
        public static final int MEDIAFIRE = 1;
        public static final int UPTOBOX = 2;
        public static final int DRIVE = 3;
    }

}
