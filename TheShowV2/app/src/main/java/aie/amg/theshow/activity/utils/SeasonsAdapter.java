package aie.amg.theshow.activity.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.utils.interfaces.OnEpisodeClickListener;
import aie.amg.theshow.models.SeasonExpandableGroup;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.views.expandable.ChildViewHolder;
import aie.amg.theshow.views.expandable.ExpandableGroup;
import aie.amg.theshow.views.expandable.ExpandableRecyclerViewAdapter;
import aie.amg.theshow.views.expandable.GroupViewHolder;

public class SeasonsAdapter extends ExpandableRecyclerViewAdapter<SeasonsAdapter.SeasonViewHolder, SeasonsAdapter.EpisodeViewHolder> {

    private Context context;

    private OnEpisodeClickListener onEpisodeClickListener;

    public SeasonsAdapter(Context context, List<SeasonExpandableGroup> groups) {
        super(groups);
        this.context = context;

    }

    @Override
    public SeasonViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expandable_item_headerr, parent, false);
        return new SeasonViewHolder(view);
    }

    @Override
    public EpisodeViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expandable_item_content, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(EpisodeViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        SeasonExpandableGroup seasonExpandableGroup = (SeasonExpandableGroup) group;
        List<Series.Episode> episodes = seasonExpandableGroup.getItems();
        Series.Episode episode = episodes.get(childIndex);
        episode.getImageUrl(holder.poster);
        holder.name.setText(episode.getName());
        holder.rating.setText(episode.getRating() + "");
        holder.episode.setText(context.getString(R.string.episode_text, episode.getNumber()));
        holder.itemView.setOnClickListener(v -> {
            if (onEpisodeClickListener != null) {
                onEpisodeClickListener.onEpisodeClickListener(episode, childIndex);
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(SeasonViewHolder holder, int flatPosition, ExpandableGroup group) {

        SeasonExpandableGroup seasonExpandableGroup = (SeasonExpandableGroup) group;
        Series.Season season = seasonExpandableGroup.getSeason();
        season.getImageBitmap(holder.poster);
        holder.title.setText(context.getString(R.string.season_text, season.getNumber()));
    }

    @Override
    public void updateList(List<? extends ExpandableGroup> groups) {
        super.updateList(groups);
        notifyDataSetChanged();
    }

    public void setOnEpisodeClickListener(OnEpisodeClickListener onEpisodeClickListener) {
        this.onEpisodeClickListener = onEpisodeClickListener;
    }

    static class SeasonViewHolder extends GroupViewHolder {
        ImageView poster;
        TextView title;

        public SeasonViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
        }
    }

    static class EpisodeViewHolder extends ChildViewHolder {
        ImageView poster;
        TextView name, episode, duration, rating;


        EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            name = itemView.findViewById(R.id.name);
            duration = itemView.findViewById(R.id.duration);
            episode = itemView.findViewById(R.id.episode);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
