package aie.amg.theshow.activity.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import aie.amg.theshow.R;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.Utils;
import aie.amg.theshow.views.SwagPoints;

public class DownloadingListAdapter extends RecyclerView.Adapter<DownloadingListAdapter.ViewHolder> {
    private Context context;
    private List<DownloadFile> files = new ArrayList<>();
    private OnDownloadFileClick onDownloadFileClick;

    public DownloadingListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloading_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadFile file = files.get(position);
        Log.d("status",file.getStatus());
        holder.name.setText(file.getShowName());
        SwagPoints progress = holder.progress;
        if (!Objects.equals(file.getStatus(), Constants.DownloadStatus.DOWNLOADING)) {
            progress.setText(file.getStatus());
        } else {
            progress.setMax((int) file.getFileSize() / 1024);
            progress.setPoints((int) file.getDownloaded() / 1024);
        }
        holder.size.setText(context.getString(R.string.download_size, Utils.sizeToString(file.getFileSize())));
        try {
            JSONObject object = new JSONObject(file.getExtraData());
            String duration = context.getString(R.string.duration_text, Utils.durationToMinutes(object.getLong("duration")));
            holder.duration.setText(duration);
            if (object.has("seasonNumber")) {
                holder.series.setVisibility(View.VISIBLE);
                holder.episode.setText(context.getString(R.string.episode_text, object.getInt("episodeNumber")));
                holder.season.setText(context.getString(R.string.season_text, object.getInt("seasonNumber")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> {
            if (onDownloadFileClick != null) {
                onDownloadFileClick.onDownloadFileClick(file, position);
            }
        });
        holder.menu.setOnClickListener((v) -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenu().add("Remove").setOnMenuItemClickListener(item -> {
                onDownloadFileClick.onRemoveButtonClick(file);
                return true;
            });
            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void setFiles(List<DownloadFile> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void setOnDownloadFileClick(OnDownloadFileClick onDownloadFileClick) {
        this.onDownloadFileClick = onDownloadFileClick;
    }

    public void updateFile(DownloadFile file1, int position) {
        files.set(position,file1);
        notifyItemChanged(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private SwagPoints progress;
        private View series, menu;
        private TextView name, duration, season, episode, size;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress);

            series = itemView.findViewById(R.id.series);
            menu = itemView.findViewById(R.id.menu);

            name = itemView.findViewById(R.id.name);
            duration = itemView.findViewById(R.id.duration);
            season = itemView.findViewById(R.id.season);
            episode = itemView.findViewById(R.id.episode);
            size = itemView.findViewById(R.id.size);


        }
    }
}
