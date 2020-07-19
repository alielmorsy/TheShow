package aie.amg.theshow.download;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aie.amg.theshow.App;
import aie.amg.theshow.connection.mega.MegaDecryptor;
import aie.amg.theshow.exceptions.NotIdentifiedLink;
import aie.amg.theshow.util.Constants;

public class LinkCreator {


    public static final String UPTOBOX = "^((https|http):\\/\\/(uptostream|uptobox).com)\\/(.+)$";
    public static final String DRIVE = "^((http|https):\\/\\/(docs|drive).google.com\\/file\\/d\\/.+)$";
    private static final String MEDIAFIRE = "";
    private static final String MEGA = "((http|https).\\/\\/mega.nz\\/.+)$";

    public static int checkLink(String link) throws NotIdentifiedLink {
        Log.d("link", " " + link);
        if (checkRegex(link, MEDIAFIRE)) {
            Log.d("ya", "asdw");
            return Constants.LinkType.MEDIAFIRE;
        } else if (checkRegex(link, MEGA)) {
            return Constants.LinkType.MEGA;
        } else if (checkRegex(link, UPTOBOX)) {
            return Constants.LinkType.UPTOBOX;
        } else if (checkRegex(link, DRIVE)) {
            return Constants.LinkType.DRIVE;
        }
        return -1;
        //throw new NotIdentifiedLink("Sorry Bad Link");
    }

    public static String CreateLink(String token) throws NotIdentifiedLink {
        int type = checkLink(token);
        if(type==-1)
            throw new  NotIdentifiedLink("Bad Link");
        if (type == Constants.LinkType.MEGA) {
            try {
                return MegaDecryptor.createLink(token);
            } catch (Exception e) {
                throw new NotIdentifiedLink("Bad Mega Link", e);
            }
        } else if (type == Constants.LinkType.MEDIAFIRE) {
            return createMediaFireLink(token);
        }
        return null;
    }

    private static boolean checkRegex(String link, String regex) {
        Pattern pattern = Pattern.compile("^((http|https)://(www\\.*)mediafire\\.com/file/.+)$");
        Matcher matcher = pattern.matcher(link);
        return matcher.matches();
    }

    private static String createMediaFireLink(String token) throws NotIdentifiedLink {
        String html;
        try {
            html = webRequest(token);
        } catch (IOException e) {
            throw new NotIdentifiedLink("No Internet access", e);
        }
        try {
            new PrintWriter(new File(Constants.AppFolder,"adw.txt")).println(html);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final String regex = "aria-label=\"Download file\"\\n.+href=\"(.*)\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(html);

        matcher.find();
        return matcher.group(1);
    }

    private static String webRequest(String link) throws IOException {
        URL url = new URL(link);
        InputStream stream = url.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

}
