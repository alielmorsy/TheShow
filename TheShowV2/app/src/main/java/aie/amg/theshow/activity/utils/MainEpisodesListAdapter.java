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
import aie.amg.theshow.activity.utils.interfaces.OnEpisodeClickListener;
import aie.amg.theshow.models.Series;

public class MainEpisodesListAdapter extends RecyclerView.Adapter<MainEpisodesListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Series.Episode> list;
    private int lastPosition;
    private boolean isGrid;
    private OnEpisodeClickListener onEpisodeClickListener;

    public MainEpisodesListAdapter(Context context, boolean isGrid) {
        this.context = context;
        list = new ArrayList<>();
        this.isGrid = isGrid;
    }


    public void setList(ArrayList<Series.Episode> list) {
        this.list.addAll(list);
        notifyItemRangeInserted(getItemCount(), list.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isGrid) {
            View view = LayoutInflater.from(context).inflate(R.layout.main_list_episode_grid_item, parent, false);
            return new ViewHolder(view);
        }
        View v = LayoutInflater.from(context).inflate(R.layout.episode_main_list_linear_item, parent, false);
        return new MainEpisodesListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainEpisodesListAdapter.ViewHolder holder, int position) {
        Series.Episode episode = list.get(position);

        holder.name.setText(episode.getSeriesName());
        holder.rating.setText(episode.getRating() + "");
        episode.getImageUrl(holder.poster);
        holder.seasonNumber.setText(String.format(context.getString(R.string.season_text), episode.getSeasonNumber()));
        holder.episodeNumber.setText(String.format(context.getString(R.string.episode_text), episode.getNumber()));
        setAnimation(holder.itemView, position);
        holder.itemView.setOnClickListener((v) -> {
            if (onEpisodeClickListener != null) {
                onEpisodeClickListener.onEpisodeClickListener(episode, position);
            }
        });
    }

    private void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_item_anim);
            animation.setDuration(1000);

            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        } else if (position < lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_item_anim2);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnEpisodeClickListener(OnEpisodeClickListener onEpisodeClickListener) {
        this.onEpisodeClickListener = onEpisodeClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView name, episodeNumber, rating, seasonNumber;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            episodeNumber = itemView.findViewById(R.id.episodeNumber);
            rating = itemView.findViewById(R.id.rating);
            seasonNumber = itemView.findViewById(R.id.season);

            poster = itemView.findViewById(R.id.poster);
        }
    }


}
