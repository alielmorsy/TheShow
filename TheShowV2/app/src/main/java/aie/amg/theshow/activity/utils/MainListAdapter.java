package aie.amg.theshow.activity.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.utils.interfaces.OnShowClickListener;
import aie.amg.theshow.models.Show;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.GridViewHolder> {
    private Context context;
    private ArrayList<Show> list;
    private boolean isGrid, withAnimation = true;
    private int lastPosition = -1;

    private OnShowClickListener onShowClickListener;

    public MainListAdapter(Context context, boolean isGrid) {
        this.context = context;
        this.isGrid = isGrid;
        list = new ArrayList<>();
    }

    public void setList(ArrayList<Show> list) {
        this.list.addAll(list);
        notifyItemRangeInserted(getItemCount(), list.size());
    }

    @NonNull
    @Override
    public MainListAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isGrid) {
            View grid = LayoutInflater.from(context).inflate(R.layout.main_list_grid_items, parent, false);
            return new GridViewHolder(grid);
        }
        View v = LayoutInflater.from(context).inflate(R.layout.main_list_linear_item, parent, false);
        return new LinearViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainListAdapter.GridViewHolder holder, int position) {
        Show show = list.get(position);
        holder.name.setText(show.getName());
        if (!isGrid) {
            holder.type.setText(show.getType());
            LinearViewHolder ho = (LinearViewHolder) holder;
            ho.year.setText(show.getYear() + "");
        } else holder.type.setText(show.getType().split(",")[0]);
        holder.rating.setText(show.getRating() + "");
        show.addImage(holder.poster);
        holder.itemView.setOnClickListener(v -> {
            if (onShowClickListener != null) {
                onShowClickListener.onShowClickListener(show, position);
            }
        });
        if (withAnimation)
            setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setAnimation(View view, int position) {

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_item_anim);
            animation.setDuration(1000);
            view.startAnimation(animation);
            lastPosition = position;
        } else if (position < lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_item_anim2);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void setOnShowClickListener(OnShowClickListener onShowClickListener) {
        this.onShowClickListener = onShowClickListener;
    }

    public void setWithAnimation(boolean withAnimation) {
        this.withAnimation = withAnimation;
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, rating;
        ImageView poster;

        GridViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            rating = itemView.findViewById(R.id.rating);

            poster = itemView.findViewById(R.id.poster);
        }
    }

    static class LinearViewHolder extends GridViewHolder {
        TextView year;

        LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            year = itemView.findViewById(R.id.year);
        }
    }
}
