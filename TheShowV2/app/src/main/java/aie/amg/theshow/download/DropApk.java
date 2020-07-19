package aie.amg.theshow.download;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aie.amg.theshow.util.Constants;

public class DropApk {
    public static final int SUCCESS = 0;
    public static final int NO_INTERNET = 1;
    public static final int CANT_EXTRACT = 2;
    public static final int BAD_CODE = 3;
    private static final String url = "https://dropapk.to/";
    private static final int BROKEN = -1;

    private String token;
    private String rand;
    private OnStateChanged onStateChanged;
    private boolean isStarted = false;
    private GetCode task1;
    private GetGeneratedLink task2;

    public DropApk(String token) {
        this.token = token;
    }

    public void start() {
        if (isStarted)
            return;
        if (onStateChanged == null || token == null) {
            throw new IllegalArgumentException("onStateChanged and token mustn't be null");
        }
        isStarted = true;
        task1 = new GetCode(onStateChanged);
        task1.execute(token);
        ;
    }

    public void postCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code can't be null");
        }
        if (!isStarted) {
            throw new IllegalStateException("Must call method #start() first");
        }

        task2 = new GetGeneratedLink(onStateChanged);
        task2.execute(token, code, rand);
    }

    public void stop() {
        if (isStarted) {
            if (task1 != null) {
                task1.cancel(true);
                task1 = null;
            }
            if (task2 != null) {
                task2.cancel(true);
                task2 = null;
            }
        }
        isStarted = false;
    }

    public void setOnStateChanged(OnStateChanged onStateChanged) {
        this.onStateChanged = onStateChanged;
    }

    public boolean isStarted() {
        return isStarted;
    }

    private void setRand(String rand) {
        this.rand = rand;
    }

    public interface OnStateChanged extends OnGettingLinkState {
        void onImageLinkGenerated(String link);
    }

    abstract static class Task extends AsyncTask<String, Void, String> {
        protected OnStateChanged onStateChanged;

        public Task(OnStateChanged onStateChanged) {
            this.onStateChanged = onStateChanged;
        }

        protected String getByPattern(String text, String regex, boolean withThrow) throws IOException {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            matcher.find();
            try {
                    return matcher.group(1);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                if (withThrow)
                    throw new IOException("Can't extract link", e);

            }

            return null;
        }

        protected String getHtmlText(InputStream stream) throws IOException {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        }
    }

    public class GetGeneratedLink extends Task {

        public GetGeneratedLink(OnStateChanged onStateChanged) {
            super(onStateChanged);
        }

        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            String code = strings[1];
            String rand = strings[2];
            try {
                String linkRegex = "<a href=\"(.+)\" class=\"btn btn-primary btn-block\">";
                URL url = new URL(Constants.DOWNLOAD_SERVER + token);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                String request = "code=%s&op=download2&id=%s&referer=%s&method_free=&method_premium=&adblock_detected=0&rand=%s";
                request = String.format(Locale.ENGLISH, request, code, token, Constants.DOWNLOAD_SERVER + token, rand);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                out.write(request.getBytes());
                String html = getHtmlText(connection.getInputStream());
                FileOutputStream fos = new FileOutputStream("/sdcard/ht.html");
                fos.write(html.getBytes());
                fos.flush();
                String link = getByPattern(html, linkRegex, false);
                Log.d("ali", link + " ");
                return link;
            } catch (IOException e) {
                e.printStackTrace();
                isStarted = false;
                if (e.getMessage() != null && e.getMessage().contains("thread")) {
                    onStateChanged.onFailedGettingLink(DropApk.BROKEN);
                    return null;
                }

                onStateChanged.onFailedGettingLink(DropApk.NO_INTERNET);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                onStateChanged.onFailedGettingLink(DropApk.BAD_CODE);
            } else {
                onStateChanged.onLinkFinish(s);
            }
        }
    }

    public class GetCode extends Task {

        GetCode(OnStateChanged onStateChanged) {
            super(onStateChanged);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String imageRegex = "<td align=right><img src=\"(.*)\"/>";
                String randRegex = "<input type=\"hidden\" name=\"rand\" value=\"(.+)\">";

                URL url = new URL(DropApk.url + strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                String request = "op=download1&id=%s&referer=%s&method_free=Free Download >>&method_premium=&adblock_detected=0";
                request = String.format(Locale.ENGLISH, request, strings[0], Constants.DOWNLOAD_SERVER + strings[0]);
                connection.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
                InputStream stream = connection.getInputStream();
                String html = getHtmlText(stream);

                String imageLink = getByPattern(html, imageRegex, true);
                String rand = getByPattern(html, randRegex, true);
                setRand(rand);
                Log.d("rand", rand);
                return imageLink;
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("thread")) {
                    onStateChanged.onFailedGettingLink(DropApk.BROKEN);
                    return null;
                }
                e.printStackTrace();
                onStateChanged.onFailedGettingLink(DropApk.NO_INTERNET);
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                onStateChanged.onImageLinkGenerated(s);
            }

        }
    }
}
