package aie.amg.theshow.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import aie.amg.theshow.models.DownloadFile;

@Dao
public interface DownloadDoa {

    @Query(value = "SELECT * FROM DownloadFiles order by id ASC ")
    LiveData<List<DownloadFile>> getAll();

    @Query(value = "SELECT * FROM DownloadFiles WHERE id=:id")
    DownloadFile get(int id);

    @Query(value = "SELECT * FROM DownloadFiles WHERE showName=:fileName LIMIT 1")
        // and extraData=:extraData
    DownloadFile get(String fileName);

    //@Query(value = "DELETE FROM DownloadFiles WHERE id=:id")

    @Delete
    void delete(DownloadFile file);

    @Query(value = "DELETE FROM DownloadFiles")
    void deleteAll();

    @Insert
    long add(DownloadFile file);

    @Update
    void updateDownloaded(DownloadFile file);

    @Query("select * from DownloadFiles where done=:done order by id desc")
    LiveData<List<DownloadFile>> getType(boolean done);
}
