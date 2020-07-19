package aie.amg.theshow.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Objects;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.utils.DownloadingListAdapter;
import aie.amg.theshow.activity.utils.OnDownloadFileClick;
import aie.amg.theshow.database.DownloadDatabaseUtil;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.observers.DownloadListObserver;
import aie.amg.theshow.services.DownloadService;
import aie.amg.theshow.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class DownloadingFragment extends Fragment {

    private DownloadListObserver observer;

    public DownloadingFragment() {
        super(R.layout.downloading_fragment);
    }

    public static DownloadingFragment newInstance() {
        DownloadingFragment fragment = new DownloadingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(DownloadListObserver.class);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView list = view.findViewById(R.id.list);
        DownloadingListAdapter adapter = new DownloadingListAdapter(getActivity());
        list.setAdapter(adapter);
        adapter.setOnDownloadFileClick(
                new OnDownloadFileClick() {
                    @Override
                    public void onDownloadFileClick(DownloadFile file1, int position) {

                        if (Objects.equals(file1.getStatus(), Constants.DownloadStatus.PAUSED)) {

                            file1.setStatus(Constants.DownloadStatus.PENDING);
                            new DownloadDatabaseUtil(getContext()).update(file1);
                            getActivity().startService(new Intent(getContext(), DownloadService.class).putExtra("file", file1).setAction("continue"));
                        } else {
                            file1.setStatus(Constants.DownloadStatus.PAUSED);
                            new DownloadDatabaseUtil(getContext()).update(file1);
                            getActivity().startService(new Intent(getContext(), DownloadService.class).putExtra("file", file1).setAction("pause"));
                        }
                    }

                    @Override
                    public void onRemoveButtonClick(DownloadFile file) {
                        new File(file.getPath()).delete();
                        getActivity().startService(new Intent(getContext(), DownloadService.class).setAction("remove").putExtra("file", file));
                    }
                });
        observer.getLiveData(false).observe(getViewLifecycleOwner(), adapter::setFiles);

    }
}