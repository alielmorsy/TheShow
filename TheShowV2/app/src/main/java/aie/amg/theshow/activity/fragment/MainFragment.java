package aie.amg.theshow.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.GenerateLinkActivity;
import aie.amg.theshow.activity.InfoActivity;
import aie.amg.theshow.activity.utils.EndlessRecyclerOnScrollListener;
import aie.amg.theshow.activity.utils.MainEpisodesListAdapter;
import aie.amg.theshow.activity.utils.MainListAdapter;
import aie.amg.theshow.connection.GetEpisodesList;
import aie.amg.theshow.connection.GetList;
import aie.amg.theshow.connection.SearchTask;
import aie.amg.theshow.models.DownloadFile;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.observers.EpisodesListObserver;
import aie.amg.theshow.observers.ShowListObserver;
import aie.amg.theshow.services.DownloadService;
import aie.amg.theshow.util.ConfigurationHelper;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.Utils;
import aie.amg.theshow.views.MyButton;

public class MainFragment extends Fragment {
    public static final String MOVIES = "movies";
    public static final String SERIES = "series";
    public static final String EPISODES = "episodes";
    public static final int news = 1;
    public static final int best = 2;
    DataOutputStream dos;
    private boolean isGrid;
    private RecyclerView list;
    private ProgressBar progress;
    private View errorBox;
    private MyButton retry;
    private SwipeRefreshLayout swipe;
    private String type;
    private int what;
    private int numberOfCol = 1, numberOfItems;
    private MainListAdapter adapter;
    private boolean stopGetting = false;
    private RewardedAd ad;
    private boolean downloadLocal;

    public MainFragment() {
        super(R.layout.main_fragment_layout);
        try {
            dos = new DataOutputStream(new FileOutputStream(new File(Constants.AppFolder, "exception.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static MainFragment getInstance(String type, int what) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putInt("what", what);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static MainFragment getInstance(String query) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", "search");
        bundle.putString("query", query);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            type = args.getString("type", MOVIES);
            what = args.getInt("what", news);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        list = view.findViewById(R.id.list);
        progress = view.findViewById(R.id.progress);
        errorBox = view.findViewById(R.id.errorBox);
        retry = view.findViewById(R.id.retry);
        swipe = view.findViewById(R.id.swipe);
        swipe.setEnabled(false);

        if (isGrid) {
            numberOfCol = Utils.calculateNoOfColumns(getContext());
            numberOfItems = Math.max(numberOfCol * 8, 24);
            list.setLayoutManager(new GridLayoutManager(getContext(), numberOfCol));
        } else {
            numberOfItems = 25;
            list.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (MainFragment.EPISODES.equals(type)) {
            bindEpisodesList();
        } else if ("search".equals(type)) {
            createSearch(args.getString("query"));
        } else
            bindList();


    }

    private void createSearch(String query) {
        adapter = new MainListAdapter(getContext(), isGrid);
        list.setAdapter(adapter);
        ShowListObserver observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(ShowListObserver.class);
        new SearchTask(observer.getLiveData()).execute(query);
        retry.setOnClickListener(v -> {
            list.setVisibility(View.GONE);
            errorBox.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            new SearchTask(observer.getLiveData()).execute(query);
        });
        observer.getLiveData().observe(getViewLifecycleOwner(), shows -> {
            swipe.setRefreshing(false);
            if (errorBox.getVisibility() == View.VISIBLE) {
                errorBox.setVisibility(View.GONE);
            }
            progress.setVisibility(View.GONE);
            if (shows != null && shows.size() > 0) {
                list.setVisibility(View.VISIBLE);
                list.post(() -> adapter.setList(shows));

            } else if (shows == null && adapter.getItemCount() == 0) {
                errorBox.setVisibility(View.VISIBLE);
            }


        });
        adapter.setOnShowClickListener((show, position) -> {
            startActivity(new Intent(getContext(), InfoActivity.class).putExtra("show", show));

        });
    }

    private void bindList() {
        adapter = new MainListAdapter(getContext(), isGrid);
        list.setAdapter(adapter);

        ShowListObserver observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(ShowListObserver.class);
        new GetList(observer.getLiveData()).execute(type, what + "", "&from=0&to=" + numberOfItems);
        retry.setOnClickListener(v -> {
            list.setVisibility(View.GONE);
            errorBox.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            new GetList(observer.getLiveData()).execute(type, what + "", "&from=0&to=" + numberOfItems);
        });
        observer.getLiveData().observe(getViewLifecycleOwner(), shows -> {
            swipe.setRefreshing(false);
            if (errorBox.getVisibility() == View.VISIBLE) {
                errorBox.setVisibility(View.GONE);
            }
            progress.setVisibility(View.GONE);
            if (shows != null && shows.size() > 0) {
                list.setVisibility(View.VISIBLE);
                list.post(() -> adapter.setList(shows));

            } else if (shows == null && adapter.getItemCount() == 0) {
                errorBox.setVisibility(View.VISIBLE);
                return;
            } else {
                Snackbar.make(list, R.string.failed_load, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.retry, v -> {
                    swipe.setRefreshing(true);
                    new GetList(observer.getLiveData()).execute(type, what + "", String.format(Locale.getDefault(), "&from=%d&to=%d", adapter.getItemCount() + 1, adapter.getItemCount() + 1 + numberOfItems));
                });
                return;
            }
            if (shows.size() < numberOfItems) {
                stopGetting = true;

            }
        });

        list.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) list.getLayoutManager(), numberOfCol) {
            @Override
            public void onLoadMore(int current_page) {
                if (!stopGetting) {
                    new GetList(observer.getLiveData()).execute(type, what + "", String.format(Locale.getDefault(), "&from=%d&to=%d", adapter.getItemCount() + 1, adapter.getItemCount() + 1 + numberOfItems));
                    swipe.setRefreshing(true);
                }
            }
        });
        adapter.setOnShowClickListener((show, position) -> {
            startActivity(new Intent(getContext(), InfoActivity.class).putExtra("show", show));

        });
    }

    private void bindEpisodesList() {
        MainEpisodesListAdapter adapter = new MainEpisodesListAdapter(getContext(), isGrid);
        list.setAdapter(adapter);
        EpisodesListObserver observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(EpisodesListObserver.class);
        new GetEpisodesList(observer.getLiveData()).execute("&from=0&to=25");
        retry.setOnClickListener(v -> {
            list.setVisibility(View.GONE);
            errorBox.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            new GetEpisodesList(observer.getLiveData()).execute("&from=0&to=25");
        });

        observer.getLiveData().observe(getViewLifecycleOwner(), shows -> {
            swipe.setRefreshing(false);
            if (errorBox.getVisibility() == View.VISIBLE) {
                errorBox.setVisibility(View.GONE);
            }
            progress.setVisibility(View.GONE);
            if (shows != null && shows.size() > 0) {
                list.setVisibility(View.VISIBLE);
                list.post(() -> adapter.setList(shows));

            } else if (shows == null && adapter.getItemCount() == 0) {
                errorBox.setVisibility(View.VISIBLE);
                return;
            }
            if (shows != null && shows.size() < numberOfItems) {
                stopGetting = true;
            }
        });
        adapter.setOnEpisodeClickListener((episode, position) -> onEpisodeClickListener(episode));
        list.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) list.getLayoutManager(), numberOfCol) {
            @Override
            public void onLoadMore(int current_page) {
                if (!stopGetting) {
                    new GetEpisodesList(observer.getLiveData()).execute(String.format(Locale.ENGLISH, "&from=%d&to=%d", adapter.getItemCount() + 1, adapter.getItemCount() + 1 + numberOfItems));
                    swipe.setRefreshing(true);
                }
            }
        });
    }

    private void onEpisodeClickListener(Series.Episode episode) {
        if (ad.isLoaded()) {
            ad.show(getActivity(), new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    startActivity(new Intent(getContext(), GenerateLinkActivity.class).putExtra("token", episode.getTokenID()));

                }
            });
        } else {
            new Handler().postDelayed(()->{
                if(ad.isLoaded()){
                    ad.show(getActivity(), new RewardedAdCallback() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            startActivity(new Intent(getContext(), GenerateLinkActivity.class).putExtra("token", episode.getTokenID()));

                        }
                    });

                }else
                    startActivity(new Intent(getContext(), GenerateLinkActivity.class).putExtra("token", episode.getTokenID()));

            },1002);
        }
    }


    private void startDownload(Series.Episode episode, int type) {
        DownloadFile file = new DownloadFile();
        file.setLink(episode.getDownloadUrl());
        file.setTokenID(episode.getTokenID());
        file.setShowName(episode.getSeriesName());

        String path = String.format(Locale.getDefault(), "%s.S%02d.E%02d.TheShow.mkv", episode.getSeriesName(), episode.getSeasonNumber(), episode.getNumber());
        File place = new File(Constants.SeriesFolder, Utils.generateLinkFromExtraData(episode.getSeriesName(), file.getExtraData()) + "/" + path);
        file.setPath(place.getPath());
        JSONObject object = new JSONObject();
        try {
            object.put("seasonNumber", episode.getSeasonNumber());
            object.put("episodeNumber", episode.getNumber());
            object.put("duration", episode.getDuration());
        } catch (JSONException ignored) {
        }
        file.setExtraData(object.toString());
        Class clzz = DownloadService.class;
        Intent intent = new Intent(getContext(), clzz);
        intent.setAction("start");
        intent.putExtra("file", file);

        getActivity().startService(intent);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        newAd(context, true);
        downloadLocal = new ConfigurationHelper(context).isLocalDownload();
        isGrid = new ConfigurationHelper(context).isListGridMode();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void newAd(Context context, boolean withLoad) {
        ad = new RewardedAd(context, "ca-app-pub-3924327175857175/9859496116");
        if (withLoad)
            ad.loadAd(new AdRequest.Builder().build(), new RewardedAdLoadCallback());
    }

}
