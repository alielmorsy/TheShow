package aie.amg.theshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.utils.EndlessRecyclerOnScrollListener;
import aie.amg.theshow.activity.utils.MainListAdapter;
import aie.amg.theshow.connection.GetCategoryList;
import aie.amg.theshow.observers.ShowListObserver;
import aie.amg.theshow.util.ConfigurationHelper;
import aie.amg.theshow.util.Utils;

public class TypeActivity extends AppCompatActivity {

    private int numberOfCol = 1;
    private int numberOfItems;
    private boolean isGrid;
    private View errorBox, progress, retry;
    private boolean stopGetting;
    private SwipeRefreshLayout swipe;
    private String type;
    private RecyclerView list;
    private ShowListObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        setContentView(R.layout.activity_type);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipe = findViewById(R.id.swipe);
        swipe.setEnabled(false);

        isGrid = new ConfigurationHelper(this).isListGridMode();

        errorBox = findViewById(R.id.errorBox);
        progress = findViewById(R.id.progress);
        retry = findViewById(R.id.retry);

        createList();
    }

    private void createList() {
        observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(ShowListObserver.class);

        retry.setOnClickListener((v) -> {

            new GetCategoryList(observer.getLiveData()).execute(type, String.format(Locale.getDefault(), "&from=%d&to=%d", 0, numberOfItems));

        });

        list = findViewById(R.id.list);
        if (isGrid) {
            numberOfCol = Utils.calculateNoOfColumns(this);
            numberOfItems = Math.max(numberOfCol * 8, 24);
            list.setLayoutManager(new GridLayoutManager(this, numberOfCol));
        } else {
            numberOfItems = 24;
            list.setLayoutManager(new LinearLayoutManager(this));
        }
        MainListAdapter adapter = new MainListAdapter(this, isGrid);
        adapter.setOnShowClickListener((show, position) -> {
            startActivity(new Intent(this, InfoActivity.class).putExtra("show", show));
        });
        list.setAdapter(adapter);

        observer.getLiveData().observe(this, shows -> {
            swipe.setRefreshing(false);
            if (errorBox.getVisibility() == View.VISIBLE) {
                errorBox.setVisibility(View.GONE);
            }
            progress.setVisibility(View.GONE);
            if (shows != null && shows.size() > 0) {
                list.setVisibility(View.VISIBLE);
                list.post(() -> adapter.setList(shows));

            } else if (shows == null && adapter.getItemCount() == 0) {
                errorBox.setVisibility(View.VISIBLE);
                return;
            } else {
                Snackbar.make(list, R.string.failed_load, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.retry, v -> {
                    swipe.setRefreshing(true);
                    new GetCategoryList(observer.getLiveData()).execute(type, String.format(Locale.getDefault(), "&from=%d&to=%d", adapter.getItemCount() + 1, adapter.getItemCount() + 1 + numberOfItems));
                });
                return;
            }
            if (shows.size() < numberOfItems) {
                stopGetting = true;

            }
        });
        new GetCategoryList(observer.getLiveData()).execute(type, String.format(Locale.getDefault(), "&from=%d&to=%d", 0, numberOfItems));

        list.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) list.getLayoutManager(), numberOfCol) {
            @Override
            public void onLoadMore(int current_page) {
                if (!stopGetting) {
                    swipe.setRefreshing(true);
                    new GetCategoryList(observer.getLiveData()).execute(type, String.format(Locale.getDefault(), "&from=%d&to=%d", adapter.getItemCount() + 1, adapter.getItemCount() + 1 + numberOfItems));

                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
