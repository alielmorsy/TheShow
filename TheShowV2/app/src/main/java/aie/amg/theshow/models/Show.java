package aie.amg.theshow.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;

import aie.amg.theshow.R;
import aie.amg.theshow.util.Constants;

public class Show implements Serializable {

    TYPE sType;
    private int id;
    private String name;
    private String type;
    private String imageURL;
    private String description;
    private float rating;
    private int reID;
    private int year;
    private ImageLoc imageFrom;
    private long numberDownloads;
    private String language;


    public Show(TYPE type) {
        sType = type;
    }

    public String getName() {
        return name == null ? "" : name.replace("\n", "").replace("\r", " ");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(double rating) {

        this.rating = (float) rating;
    }

    public void setRating(String rating) {
        if (rating == null) {
            this.rating = 0;
            return;
        }
        this.rating = Float.parseFloat(rating);

    }
    public void setRating(int rating) {

        this.rating =rating;

    }
    public int getReID() {
        return reID;
    }

    public void setReID(int reID) {
        this.reID = reID;
    }

    public ImageLoc getImageFrom() {
        return imageFrom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setYear(String year) {
        this.year = Integer.parseInt(year);

    }

    public TYPE getsType() {
        return sType;
    }

    public long getNumberDownloads() {
        return numberDownloads;
    }

    public void setNumberDownloads(int numberDownloads) {
        this.numberDownloads = numberDownloads;
    }

    public void setNumberDownloads(String numberDownloads) {
        this.numberDownloads = Integer.parseInt(numberDownloads);

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void addImage(ImageView imageView) {
        Picasso.get().load(Constants.IMAGE_URL + imageURL + ".jpg").placeholder(R.drawable.no_internet).error(R.drawable.no_internet).into(imageView);
    }

    public void setBitmap(Activity activity, View view) {
        new Thread(() -> {
            try {
                Bitmap bitmap = Picasso.get().load(Constants.IMAGE_URL + imageURL + ".jpg").get();
                activity.runOnUiThread(() -> {
                    view.setBackground(new BitmapDrawable(activity.getResources(), bitmap));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public enum ImageLoc {
        URL,
        RES
    }

    public enum TYPE {
        MOVIE,
        SERIES
    }


}


