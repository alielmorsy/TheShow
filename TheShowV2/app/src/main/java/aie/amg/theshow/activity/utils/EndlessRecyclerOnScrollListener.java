package aie.amg.theshow.activity.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.Collections;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;
    private int lastVisibleItem;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, int col) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.visibleItemCount = col;

    }




    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        totalItemCount = mLinearLayoutManager.getItemCount();
        lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                // the loading has finished
                loading = false;
                previousTotal = totalItemCount;
            }
         //   new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("1AE9A0E962BAE89D4CC2B672D6409DB3")).build();
        }
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            //End of the items
            onLoadMore(current_page);
            loading = true;

        }
    }

    public abstract void onLoadMore(int current_page);

}
