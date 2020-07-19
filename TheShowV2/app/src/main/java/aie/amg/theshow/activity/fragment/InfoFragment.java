package aie.amg.theshow.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.InfoActivity;
import aie.amg.theshow.activity.TypeActivity;
import aie.amg.theshow.activity.utils.MainListAdapter;
import aie.amg.theshow.models.Movie;
import aie.amg.theshow.models.Series;
import aie.amg.theshow.models.Show;
import aie.amg.theshow.observers.ShowListObserver;
import aie.amg.theshow.util.Constants;
import aie.amg.theshow.util.JsonToModel;
import aie.amg.theshow.util.Utils;

public class InfoFragment extends Fragment {
    private Movie movie;
    private Series series;
    private int type;
    private Show show;
    private InterstitialAd ad;
    private UnifiedNativeAd nativeAd;


    public InfoFragment() {
        super(R.layout.info_fragment);
    }

    public static InfoFragment getInstance(Show show) {
        InfoFragment fragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("show", show);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            show = (Show) getArguments().getSerializable("show");
        } else
            throw new IllegalArgumentException("None Show");
    }

    private void loadAd(View view) {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), "ca-app-pub-3924327175857175/2004980845");

        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            nativeAd = unifiedNativeAd;

            UnifiedNativeAdView adView = view.findViewById(R.id.native_ad);
            populateUnifiedNativeAdView(unifiedNativeAd, adView);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                view.findViewById(R.id.adCard).setVisibility(View.VISIBLE);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        loadAd(view);
        assert show != null : "Sorry show =null";
        show.addImage(view.findViewById(R.id.poster));
        TextView type1 = view.findViewById(R.id.type);
        if (show instanceof Series) {
            series = (Series) show;
            view.findViewById(R.id.series).setVisibility(View.VISIBLE);
            type = Constants.Series;
            TextView eCount = view.findViewById(R.id.episodeCount);
            eCount.setText(getString(R.string.episodes_count, series.getEpisodesCount()));
            TextView sCount = view.findViewById(R.id.seasons_count);
            sCount.setText(getString(R.string.seasons_count, series.getSessionCount()));
            type1.setText("مسلسل");
        } else {
            type = Constants.Movie;
            movie = (Movie) show;
            TextView duration = view.findViewById(R.id.duration);
            duration.setVisibility(View.VISIBLE);
            duration.setText(Utils.durationToMinutes(movie.getDuration()));
            type1.setText("فيلم");
        }
        TextView name = view.findViewById(R.id.name);
        name.setText(show.getName());

        TextView rating = view.findViewById(R.id.rating);
        rating.setText(String.valueOf(show.getRating()));

        TextView summary = view.findViewById(R.id.summary);
        summary.setText(show.getDescription());
        generateTypesList(view);
        getSimilar(view);
    }

    private void generateTypesList(View view) {
        String types = show.getType().trim();
        types = types.replaceAll(" ", "");
        RecyclerView list = view.findViewById(R.id.typesList);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        list.setLayoutManager(manager);
        TypesAdapter adapter = new TypesAdapter(getContext(), types.split(","));
        adapter.onTypeClickListener = type -> {
            if (ad.isLoaded()) {
                ad.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        goFragment(type);
                    }
                });
                ad.show();
            } else {
                goFragment(type);
            }
        };
        list.setAdapter(adapter);
    }

    private void getSimilar(View view) {
        ShowListObserver observer = new ViewModelProvider(getViewModelStore(), getDefaultViewModelProviderFactory()).get(ShowListObserver.class);
        makeThread(observer);
        RecyclerView list = view.findViewById(R.id.similarList);
        MainListAdapter adapter = new MainListAdapter(getContext(), true);
        adapter.setWithAnimation(false);
        adapter.setOnShowClickListener(((show1, position) -> {
            startActivity(new Intent(getContext(), InfoActivity.class).putExtra("show", show1));
        }));
        list.setAdapter(adapter);
        observer.getLiveData().observe(getViewLifecycleOwner(), shows -> {
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            if (shows != null && shows.size() > 0) {
                list.setVisibility(View.VISIBLE);
                list.post(() -> adapter.setList(shows));
            } else
                view.findViewById(R.id.message).setVisibility(View.GONE);
        });
    }

    private void makeThread(ShowListObserver observer) {
        Runnable runnable = () -> {
            try {
                StringBuilder builder = new StringBuilder();
                String type = show.getsType() == Show.TYPE.SERIES ? "series" : "movies";
                URL url = new URL(Constants.URL + "GetList.php?what=similar&show=" + type + "&id=" + show.getId());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(500);
                connection.setReadTimeout(500);
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                String text = builder.toString();
                if (type.equals("movies")) {
                    JsonToModel<Movie> toModel = new JsonToModel<>(text, Movie.class);
                    if (toModel.getType() == JsonToModel.JsonType.Error) {
                        observer.getLiveData().postValue(null);
                        return;
                    }
                    ArrayList<Show> shows = new ArrayList<>(toModel.getArray());
                    observer.getLiveData().postValue(shows);
                } else {
                    JsonToModel<Series> toModel = new JsonToModel<>(text, Series.class);
                    if (toModel.getType() == JsonToModel.JsonType.Error) {
                        observer.getLiveData().postValue(null);
                        return;
                    }
                    ArrayList<Show> shows = new ArrayList<>(toModel.getArray());
                    observer.getLiveData().postValue(shows);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }

    private void goFragment(String type) {
        startActivity(new Intent(getContext(), TypeActivity.class).putExtra("type", type));
    }

    private void loadAd(Context context) {
        ad = new InterstitialAd(context);
        ad.setAdUnitId("ca-app-pub-3924327175857175/3034334947");
        ad.loadAd(new AdRequest.Builder().build());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loadAd(context);
    }

    interface OnTypeClickListener {
        void onTypeClickListener(String type);
    }

    static class TypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        String[] strings;
        OnTypeClickListener onTypeClickListener;
        private Context context;

        TypesAdapter(Context context, String[] strings) {
            this.context = context;
            this.strings = strings;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.types_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView text = (TextView) holder.itemView;
            text.setOnClickListener((v) -> onTypeClickListener.onTypeClickListener(strings[position]));

            text.setText(strings[position]);
        }

        @Override
        public int getItemCount() {
            return strings.length;
        }

    }
}
