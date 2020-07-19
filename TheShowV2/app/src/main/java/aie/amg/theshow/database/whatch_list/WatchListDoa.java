package aie.amg.theshow.database.whatch_list;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import aie.amg.theshow.models.WatchListItem;

@Dao
public interface WatchListDoa {
    @Query("select * from watchlist order by id desc")
    LiveData<List<WatchListItem>> getAll();

    @Insert
    long addItem(WatchListItem watchList);

    @Query("select * from WatchList where showName=:showName")
    WatchListItem get(String showName);

    @Query("delete from watchlist where showName=:name")
    void deleteItem(String name);

    @Query("delete from watchlist")
    void deleteAll();

}
