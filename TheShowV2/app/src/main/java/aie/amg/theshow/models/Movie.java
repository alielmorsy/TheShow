package aie.amg.theshow.models;

import java.io.Serializable;

import aie.amg.theshow.connection.mega.MegaDecryptor;

public class Movie extends Show implements Serializable {
    private long duration;
    private String tokenID;
    private String downloadLink;



    public Movie() {
        super(TYPE.MOVIE);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDuration(String duration) {
        this.duration = Long.parseLong(duration);
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
