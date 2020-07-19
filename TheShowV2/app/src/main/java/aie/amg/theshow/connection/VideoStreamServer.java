package aie.amg.theshow.connection;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aie.amg.theshow.connection.mega.MegaDecryptor;
import aie.amg.theshow.models.DownloadFile;
import fi.iki.elonen.NanoHTTPD;


public class VideoStreamServer extends NanoHTTPD {
    private String url;
    private DownloadFile file;
    private long contentLength;
    private MegaDecryptor decryptor;

    private String contentRange = null;

    public VideoStreamServer(Context context, int port, String url) {
        super(port);
        this.url = url;

    }

    @Override
    public Response serve(IHTTPSession session) {

        Response response = null;
        try {
            String requestRange = session.getHeaders().get("range");

            decryptor.setDecrypt();

            InputStream inputStream = prepareConnection(requestRange);
            inputStream = decryptor.createCipherStream(inputStream);
            if (requestRange == null) {

                response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "video/x-matroska", inputStream, contentLength);
            } else {

                Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(requestRange);
                matcher.find();
                long start = 0;
                try {
                    start = Long.parseLong(matcher.group(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                long restLength = contentLength - start;
                Log.d("restLength", restLength + " ");
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, "video/x-matroska", inputStream, restLength);

                response.addHeader("Content-Range", contentRange);

                response.setKeepAlive(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDownloadFile(DownloadFile file) {
        url = file.getLink();
        this.file = file;
        try {
            decryptor = new MegaDecryptor(file.getTokenID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  decryptor = new MegaDecryptor("0!KAZ0QSrK!isXGprskZbLP4KnLNuNHcbI279s6FnLcsj8Vydm_sio");
    }

    private InputStream prepareConnection(String request) throws Exception {
        URL url = new URL(this.url);
        setContentLength();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (request != null) {
            Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(request);

            matcher.find();
            long start = 0;
            try {
                Log.d("RangeRequest", request);
                start = Long.parseLong(matcher.group(1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            connection.setRequestProperty("Range", "bytes=" + (start + 1) + "-");
        }
        InputStream inputStream = connection.getInputStream();

        if (connection.getResponseCode() >= 400) {
            String newLink = MegaDecryptor.createLink(file.getTokenID());
            file.setLink(newLink);

            return prepareConnection(request);
        } else {
            // contentLength = connection.getContentLength();
            contentRange = connection.getHeaderField("Content-Range");
            Log.d("Length-Range", contentLength + " " + contentRange);
            return inputStream;
        }
    }

    private void setContentLength() throws Exception {
        URL url = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        Log.d("connect", "connected");
        if (connection.getResponseCode() == 200)
            contentLength = connection.getContentLength();
        else {
            Log.d("lol", "asdwwww");
            String newLink = MegaDecryptor.createLink(file.getTokenID());
            file.setLink(newLink);
            setContentLength();
        }

    }

}
