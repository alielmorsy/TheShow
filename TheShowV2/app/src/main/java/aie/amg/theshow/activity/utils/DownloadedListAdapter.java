package aie.amg.theshow.activity.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aie.amg.theshow.R;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.Utils;

public class DownloadedListAdapter extends RecyclerView.Adapter<DownloadedListAdapter.ViewHolder> {
    private Context context;
    private List<DownloadFile> files;
    private OnDownloadFileClick onDownloadFileClick;

    public DownloadedListAdapter(Context context) {
        this.context = context;
        files = new ArrayList<>();
    }

    public void setFiles(List<DownloadFile> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloaded_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadFile file = files.get(position);
        String data = context.getString(R.string.downloaded_extra_data);
        String type = file.getType() == Constants.Movie ? "Movie" : "Series";
        String extra = "";
        setImage(file, holder.poster);
        try {
            JSONObject object = new JSONObject(file.getExtraData());
            if (file.getType() == Constants.Series) {
                String tmp = "S%02d E%02d";
                extra = String.format(Locale.getDefault(), tmp, object.getInt("season"), object.getInt("episode"));
            } else {

                extra = Utils.durationToMinutes(object.getInt("duration"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.name.setText(file.getShowName());
        data = String.format(data, type, extra, Utils.sizeToString(file.getDownloaded()));

        holder.extraData.setText(data);
        holder.itemView.setOnClickListener((v) -> {
            if (onDownloadFileClick != null) {
                onDownloadFileClick.onDownloadFileClick(file, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private void setImage(DownloadFile file, ImageView image) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> handler.post(() -> {
            try {
                image.setImageBitmap(Utils.extractBitmapFromVideo(file.getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void setOnDownloadFileClick(OnDownloadFileClick onDownloadFileClick) {
        this.onDownloadFileClick = onDownloadFileClick;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, extraData;
        private ImageView poster;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            extraData = itemView.findViewById(R.id.data);
            poster = itemView.findViewById(R.id.poster);
        }
    }
}
