package aie.amg.theshow.views.expandable;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExpandableGroup<T> implements Serializable {
    private String title;
    private List<T> items;

    public ExpandableGroup(String title, List<T> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public List<T> getItems() {
        return items;
    }

    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public String toString() {
        return "ExpandableGroup{" +
                "title='" + title + '\'' +
                ", items=" + items +
                '}';
    }

    private ExpandableGroup(Parcel in) {
        title = in.readString();
        byte hasItems = in.readByte();
        int size = in.readInt();
        if (hasItems == 0x01) {
            items = new ArrayList<T>(size);
            Class<?> type = (Class<?>) in.readSerializable();
            in.readList(items, type.getClassLoader());
        } else {
            items = null;
        }
    }

  }
