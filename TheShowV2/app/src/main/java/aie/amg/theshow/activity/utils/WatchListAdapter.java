package aie.amg.theshow.activity.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import aie.amg.theshow.R;
import aie.amg.theshow.models.WatchListItem;
import aie.amg.theshow.services.DownloadService;
import aie.amg.theshow.util.Constants;

public class WatchListAdapter extends RecyclerView.Adapter<MainListAdapter.LinearViewHolder> {
    private Context context;
    private List<WatchListItem> items;
    private OnWatchListItemClick onWatchListItemClick;

    public WatchListAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
    }

    public void setItems(List<WatchListItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainListAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.main_list_linear_item, parent, false);
        return new MainListAdapter.LinearViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainListAdapter.LinearViewHolder holder, int position) {
        WatchListItem item = items.get(position);

        Picasso.get().load(Constants.IMAGE_URL + item.getImageURL() + ".jpg").into(holder.poster);

        holder.year.setVisibility(View.GONE);
        holder.name.setText(item.getShowName());
        holder.type.setText(item.getType() == Constants.Movie ? "Movie" : "Series");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnWatchListItemClick(OnWatchListItemClick onWatchListItemClick) {
        this.onWatchListItemClick = onWatchListItemClick;
    }

    public interface OnWatchListItemClick {
        void onWatchListItemClick(WatchListItem item, int position);
    }
}
