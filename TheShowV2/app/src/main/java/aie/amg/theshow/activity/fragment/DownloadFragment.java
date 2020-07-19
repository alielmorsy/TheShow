package aie.amg.theshow.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.GenerateLinkActivity;
import aie.amg.theshow.activity.utils.SeasonsAdapter;
import aie.amg.theshow.connection.SeasonTask;
import aie.amg.theshow.models.Movie;
import aie.amg.theshow.models.SeasonExpandableGroup;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.models.Show;
import aie.amg.theshow.observers.SeasonsListObserver;
import aie.amg.theshow.util.Constants;

public class DownloadFragment extends Fragment {

    private SeasonsAdapter adapter;
    private Show show;

    private RewardedAd ad;

    private DataOutputStream dos;

    public DownloadFragment() {
        super(R.layout.download_fragment);
        try {
            dos = new DataOutputStream(new FileOutputStream(new File(Constants.AppFolder, "exception.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DownloadFragment getInstance(Show show) {
        DownloadFragment fragment = new DownloadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("show", show);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        show = (Show) getArguments().getSerializable("show");
        AdView adView = view.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        RecyclerView list = view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SeasonsAdapter(getContext(), new ArrayList<>());
        list.setAdapter(adapter);
        if (show instanceof Series)
            bindList(view);
        else handleMovie(view);
    }

    private void bindList(View view) {
        SeasonsListObserver observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(SeasonsListObserver.class);
        View progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        View errorBox = view.findViewById(R.id.errorBox);
        View retry = view.findViewById(R.id.retry);
        retry.setOnClickListener(v -> {
            new SeasonTask(observer.getLiveData()).execute(show.getId() + "");
            progress.setVisibility(View.VISIBLE);
            errorBox.setVisibility(View.GONE);
        });

        new SeasonTask(observer.getLiveData()).execute(show.getId() + "");
        observer.getLiveData().observe(getViewLifecycleOwner(), season -> {
            progress.setVisibility(View.GONE);

            if (season != null && season.size() > 0) {
                if (errorBox.getVisibility() != View.GONE)
                    errorBox.setVisibility(View.GONE);
                view.findViewById(R.id.list).setVisibility(View.VISIBLE);
                ArrayList<SeasonExpandableGroup> groups = new ArrayList<>();
                for (Series.Season e : season) {
                    SeasonExpandableGroup seasonExpandableGroup = new SeasonExpandableGroup(null, e);
                    groups.add(seasonExpandableGroup);
                }

                adapter.updateList(groups);
            } else errorBox.setVisibility(View.VISIBLE);
        });
        adapter.setOnEpisodeClickListener((episode, position) -> {
            startDownloadEpisode(episode);
        });
    }

    private void startDownloadEpisode(Series.Episode episode) {
        if (ad.isLoaded()) {
            ad.show(getActivity(), new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    handleEpisode(episode);
                }

            });
        } else {

            newAd(getContext(), false);
            Toast.makeText(getContext(), "PLease Wait", Toast.LENGTH_LONG).show();
            ad.loadAd(new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
                @Override
                public void onRewardedAdFailedToLoad(int i) {
                    handleEpisode(episode);
                }

                @Override
                public void onRewardedAdLoaded() {
                    ad.show(getActivity(), new RewardedAdCallback() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            handleEpisode(episode);
                        }
                    });
                }
            });
        }
    }

    private void handleEpisode(Series.Episode episode) {
        startActivity(new Intent(getContext(), GenerateLinkActivity.class).putExtra("episode", episode).putExtra("type", Constants.Episode));

    }


    private void handleMovie(View view) {
        View card = view.findViewById(R.id.movie);
        card.setVisibility(View.VISIBLE);
        ImageView poster = view.findViewById(R.id.poster);
        show.addImage(poster);
        card.setOnClickListener(v -> {
            if (ad.isLoaded()) {
                ad.show(getActivity(), new RewardedAdCallback() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        startMovieDownload();
                    }
                });
            } else {
                new Handler().postDelayed(() -> {
                    if (ad.isLoaded()) {
                        ad.show(getActivity(), new RewardedAdCallback() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                startMovieDownload();
                            }
                        });
                    } else {
                        startMovieDownload();
                    }
                }, 1000);

            }
        });
    }

    private void startMovieDownload() {
        Movie movie = (Movie) show;
        startActivity(new Intent(getContext(), GenerateLinkActivity.class).putExtra("movie", movie).putExtra("type", Constants.Movie));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null)
            adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (adapter != null)
            adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        newAd(context, true);


    }

    private void newAd(Context context, boolean withLoad) {
        ad = new RewardedAd(context, "ca-app-pub-3924327175857175/9859496116");
        if (withLoad)
            ad.loadAd(new AdRequest.Builder().build(), new RewardedAdLoadCallback());
    }
}
