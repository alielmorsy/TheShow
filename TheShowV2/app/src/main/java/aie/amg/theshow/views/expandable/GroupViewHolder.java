package aie.amg.theshow.views.expandable;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public abstract class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private OnGroupClickListener listener;

    public GroupViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            if (listener.onGroupClick(getAdapterPosition())) {
                collapse();
            } else {
                expand();
            }
        }
    }

    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.listener = listener;
    }

    public void expand() {}

    public void collapse() {}

}
