package aie.amg.theshow.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.Executors;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.utils.InfoPagerAdapter;
import aie.amg.theshow.database.whatch_list.WatchListDatabaseUtils;
import aie.amg.theshow.models.Movie;
import aie.amg.theshow.models.Show;
import aie.amg.theshow.models.WatchListItem;
import aie.amg.theshow.util.Constants;

public class InfoActivity extends AppCompatActivity {
    private boolean inWatchList = false;
    private WatchListDatabaseUtils utils;
    private Show show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        show = (Show) getIntent().getSerializableExtra("show");
        setContentView(R.layout.activity_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(show.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new InfoPagerAdapter(this, getSupportFragmentManager(), show));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        utils = new WatchListDatabaseUtils(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info_activity, menu);
        Executors.newFixedThreadPool(4).execute(() -> {
            inWatchList = utils.inDatabase(show.getName());
            if (inWatchList) {
                runOnUiThread(() -> menu.getItem(0).setIcon(R.drawable.ic_playlist_added));
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        changeWatchList(item);
        return super.onOptionsItemSelected(item);
    }

    private void changeWatchList(MenuItem item) {
        if (inWatchList) {
            item.setIcon(R.drawable.ic_playlist);
            Executors.newFixedThreadPool(4).execute(() -> {
                utils.delete(show.getName());
            });
        } else {
            WatchListItem watchListItem = new WatchListItem();
            watchListItem.setImageURL(show.getImageURL());
            watchListItem.setRating(show.getRating());
            watchListItem.setShowID(show.getId());
            watchListItem.setType(show instanceof Movie ? Constants.Movie : Constants.Series);
            watchListItem.setShowName(show.getName());
            Executors.newFixedThreadPool(4).execute(() -> {
                utils.add(watchListItem);
            });
            item.setIcon(R.drawable.ic_playlist_added);
        }
    }

}