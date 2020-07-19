package aie.amg.theshow.models;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import aie.amg.theshow.util.Constants;


@Entity(tableName = "DownloadFiles")
public class DownloadFile implements Serializable {


    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo
    private String showName;
    @ColumnInfo
    private String path;
    @ColumnInfo
    private long fileSize;
    @ColumnInfo
    private long downloaded;
    @ColumnInfo
    private String tokenID;
    @ColumnInfo
    private String link;
    @ColumnInfo
    private boolean done;
    @ColumnInfo
    private String extraData;
    @ColumnInfo
    private int type;

    @ColumnInfo(defaultValue = Constants.DownloadStatus.PENDING)
    private String status = Constants.DownloadStatus.PENDING;

    @Ignore
    public DownloadFile(String showName, long fileSize, long downloaded) {
        this.showName = showName;
        this.fileSize = fileSize;
        this.downloaded = downloaded;
    }

    public DownloadFile() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
