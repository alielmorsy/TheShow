package aie.amg.theshow.connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aie.amg.theshow.download.LinkCreator;
import aie.amg.theshow.exceptions.NotIdentifiedLink;

public class UptToBox {
    private String waitToken;
    private String fileCode;
    private String downloadLink;

    private int time;
    private boolean canDownload;
    private OnGettingLinkSuccess onGettingLink;

    public UptToBox(String link) throws NotIdentifiedLink {
        getCode(link);
        getWaitingToken();
    }

    private void getCode(String link) throws NotIdentifiedLink {
        Pattern pattern = Pattern.compile(LinkCreator.UPTOBOX);
        Matcher matcher = pattern.matcher(link);
        boolean find = matcher.find();
        if (!find)
            throw new NotIdentifiedLink("Bad Link");
        else
            fileCode = matcher.group(4);
    }

    private void getWaitingToken() throws NotIdentifiedLink {
        try {
            URL url = new URL("https://uptobox.com/api/link?file_code=" + fileCode);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            JSONObject object = new JSONObject(builder.toString());
            int status = object.getInt("status");
            if (status == 0) {
                JSONObject data = object.getJSONObject("data");
                downloadLink = data.getString("dlLink");
                canDownload = true;
            } else if (status == 16) {
                waitToken = object.getString("waitingToken");
                time = object.getInt("waiting") * 1000;
                startTimer();
            }
        } catch (IOException | JSONException e) {

            throw new NotIdentifiedLink("Problem In Internet", e);
        }
    }

    private void startTimer() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    postWaitingToken();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, time);
    }

    private void postWaitingToken() throws IOException, JSONException {
        URL url = new URL("https://uptobox.com/api/link?file_code=" + fileCode);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        JSONObject object = new JSONObject(builder.toString());
        int status = object.getInt("status");
        if (status == 0) {
            JSONObject data = object.getJSONObject("data");
            downloadLink = data.getString("dlLink");
            if (onGettingLink != null)
                onGettingLink.onGettingLink(downloadLink);
            canDownload = true;

        }
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public boolean CanDownload() {
        return canDownload;
    }

    public void setOnGettingLink(OnGettingLinkSuccess onGettingLink) {
        this.onGettingLink = onGettingLink;
    }

    public interface OnGettingLinkSuccess {
        void onGettingLink(String link);
    }
}
