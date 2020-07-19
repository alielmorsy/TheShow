package aie.amg.theshow.activity.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.utils.DownloadedListAdapter;
import aie.amg.theshow.activity.utils.OnDownloadFileClick;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.observers.DownloadListObserver;
import aie.amg.theshow.util.Utils;

public class DownloadedFragment extends Fragment {
    private DownloadListObserver observer;

    public DownloadedFragment() {
        super(R.layout.downloaded_fragment);
    }

    public static DownloadedFragment getInstance() {
        DownloadedFragment fragment = new DownloadedFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(DownloadListObserver.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.list);
        DownloadedListAdapter adapter = new DownloadedListAdapter(getContext());

        adapter.setOnDownloadFileClick(new OnDownloadFileClick() {
            @Override
            public void onDownloadFileClick(DownloadFile file, int position) {
                Utils.startVideoPlay(getActivity(), file.getPath() );
            }

            @Override
            public void onRemoveButtonClick(DownloadFile file) {

            }
        });
        observer.getLiveData(true).observe(getViewLifecycleOwner(), adapter::setFiles);

        list.setAdapter(adapter);
    }
}
