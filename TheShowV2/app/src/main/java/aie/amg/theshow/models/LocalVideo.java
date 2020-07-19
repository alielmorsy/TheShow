package aie.amg.theshow.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class LocalVideo implements Serializable {

    private  String name;
    private transient Bitmap thump;
    private String location;



    private transient long duration;

    public LocalVideo(String name, String location, Bitmap thump, long duration) {
        this.name = name;
        this.location = location;
        this.thump = thump;
        this.duration = duration;
    }

    public LocalVideo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Bitmap getThump() {
        return thump;
    }

    public void setThump(Bitmap thump) {
        this.thump = thump;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
