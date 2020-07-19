package aie.amg.theshow.models;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import aie.amg.theshow.R;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.JsonToModel;

public class Series extends Show implements Serializable {
    private int sessionCount, episodesCount;
    @JsonToModel.JsonArray(clazz = Season.class)
    private ArrayList<Season> seasons;
    private Episode episode;

    public Series() {
        super(TYPE.SERIES);

    }

    @Override
    public void setName(String name) {
        super.setName(name);
        sType = TYPE.SERIES;
    }

    public Episode getCurrentEpisode() {
        return episode;
    }

    public void setCurrentEpisode(Episode episode) {
        this.episode = episode;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    public Season getSeasonAt(int index) {
        return seasons.get(index);
    }

    public int getEpisodesCount() {
        return episodesCount;
    }

    public void setEpisodesCount(int episodesCount) {
        this.episodesCount = episodesCount;
    }

    public static class Season implements Serializable {
        private int id;
        private int number;
        private int episodesCount;
        private String imageURL;

        @JsonToModel.JsonArray(clazz = Episode.class)
        private ArrayList<Episode> episodes;

        public Season(int number, int episodesCount, String imageURL, ArrayList<Episode> episodes) {
            this.number = number;
            this.episodesCount = episodesCount;
            this.imageURL = imageURL;
            this.episodes = episodes;
        }

        public Season() {

        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getEpisodesCount() {
            return episodesCount;
        }

        public void setEpisodesCount(int episodesCount) {
            this.episodesCount = episodesCount;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public void getImageBitmap(ImageView imageView) {
            Picasso.get().load(Constants.IMAGE_URL + imageURL + ".jpg").placeholder(R.drawable.no_internet).into(imageView);
        }

        public ArrayList<Episode> getEpisodes() {
            return episodes;
        }

        public void setEpisodes(ArrayList<Episode> episodes) {
            this.episodes = episodes;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Episode implements Serializable {
        private int id;
        private int seriesID;
        private String name;
        private String downloadUrl;
        private int number;
        private int seasonNumber;
        private int duration;
        private double rating;
        private String tokenID;
        private String imageUrl;
        private String seriesName;

        public Episode() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getTokenID() {
            return tokenID;
        }

        public void setTokenID(String tokenID) {
            this.tokenID = tokenID;
        }

        public int getSeriesID() {
            return seriesID;
        }

        public void setSeriesID(int seriesID) {
            this.seriesID = seriesID;
        }

        public void getImageUrl(ImageView image) {
            Picasso.get().load(Constants.IMAGE_URL + imageUrl + ".jpg").error(R.drawable.no_internet).into(image);
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getSeriesName() {
            return seriesName;
        }

        public void setSeriesName(String seriesName) {
            this.seriesName = seriesName;
        }
    }
}
