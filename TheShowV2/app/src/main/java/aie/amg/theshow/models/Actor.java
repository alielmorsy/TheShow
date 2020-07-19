package aie.amg.theshow.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import aie.amg.theshow.util.Constants;

public class Actor implements Serializable {
    public int resID;
    private String name;
    private String bestWork;
    private String imageUrl;
    private int age;

    public Actor(String name, int resID) {
        this.name = name;
        this.resID = resID;
    }

    public Actor() {
        super();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBestWork() {
        return bestWork;
    }

    public void setBestWork(String bestWork) {
        this.bestWork = bestWork;
    }

    public Bitmap getImageBitmap() throws IOException {
        InputStream inputStream = new URL(imageUrl).openConnection().getInputStream();
        return BitmapFactory.decodeStream(inputStream);

    }

    public void addImage(ImageView imageView) {
      //  Picasso.get().load(Constants.IMAGE_URL + imageUrl + ".jpg").placeholder(R.drawable.downloaded).into(imageView);
    }
}
