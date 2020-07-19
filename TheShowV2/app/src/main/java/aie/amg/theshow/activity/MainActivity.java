package aie.amg.theshow.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.fragment.MainFragment;
import aie.amg.theshow.util.ConfigurationHelper;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ConfigurationHelper confHelper;
    private Drawer drawer;
    private boolean isSearch = false;
    private SearchView searchView;
    private GoogleSignInClient mGoogleSignInClient;
    private AccountHeaderBuilder headerBuilder;
    private AccountHeader header;
    private ProfileDrawerItem profile;
    private int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        confHelper = new ConfigurationHelper(this);
//        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("1AE9A0E962BAE89D4CC2B672D6409DB3")).build());

        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        addFragment(MainFragment.getInstance(MainFragment.MOVIES, MainFragment.news), "أفضل الأفلام");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d("account", (account == null) + "asdw");
        setupSlider(toolbar, account);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_support_us, menu);

        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchViewMenuItem.getActionView();
        ImageView v = searchView.findViewById(androidx.appcompat.R.id.search_button);
        v.setImageResource(android.R.drawable.ic_menu_search);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        searchView.setQueryHint("Search in videos");
        searchView.setOnQueryTextListener(this);

        searchView.setOnCloseListener(() -> {
            isSearch = false;
            searchView.clearFocus();
            addFragment(MainFragment.getInstance(MainFragment.MOVIES, MainFragment.news), "احدث اضافات الأفلام");
            return true;
        });
        return super.onCreateOptionsMenu(menu);

    }


    private void setupSlider(Toolbar toolbar, GoogleSignInAccount account) {

        headerBuilder = new AccountHeaderBuilder();
        headerBuilder.withActivity(this);
        ArrayList<IProfile> profiles = new ArrayList<>();
        profile = new ProfileDrawerItem();

        profile.withTextColorRes(R.color.primaryText);

        if (account == null) {
            profile.withName("Sign In");

        } else {
            profile.withName(account.getDisplayName());
            profile.withIcon(account.getPhotoUrl());
            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Picasso.get().load(uri).placeholder(placeholder).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {
                    Picasso.get().cancelRequest(imageView);
                }
            });

        }
        profiles.add(profile);
        headerBuilder.withProfiles(profiles);

        headerBuilder.withOnAccountHeaderListener((view, profile1, current) -> {
            if ("Sign In".equals(profile1.getName().getText(this))) {
                signIn();
            }
            return true;
        });
        headerBuilder.withTextColorRes(R.color.primaryText);

        header = headerBuilder.build();

        List<IDrawerItem> drawerItems = setupDrawerItems();

        drawer = new DrawerBuilder().withActivity(this).withToolbar(toolbar).withDrawerItems(drawerItems).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                long identifier = drawerItem.getIdentifier();
                PrimaryDrawerItem pr = (PrimaryDrawerItem) drawerItem;
                String name = pr.getName().getText(getApplication());

                switch ((int) identifier) {
                    case 10:

                        addFragment(MainFragment.getInstance(MainFragment.MOVIES, MainFragment.news), "احدث اضافات الأفلام");
                        break;
                    case 11:
                        addFragment(MainFragment.getInstance(MainFragment.MOVIES, MainFragment.best), name);
                        break;
                    case 20:
                        addFragment(MainFragment.getInstance(MainFragment.SERIES, MainFragment.news), "أحدث المسلسلات");
                        break;
                    case 21:
                        addFragment(MainFragment.getInstance(MainFragment.SERIES, MainFragment.best), name);
                        break;
                    case 30:
                        addFragment(MainFragment.getInstance(MainFragment.EPISODES, MainFragment.news), name);
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), RequestActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), SupportUsActivity.class));
                        break;
                    case 100:
                        startActivity(new Intent(getApplicationContext(),DownloadActivity.class));
                        break;
                }
                drawer.closeDrawer();
                return true;
            }
        }).withCloseOnClick(true)
                .withSliderBackgroundColorRes(R.color.background)
                .withAccountHeader(header)
                .withSelectedItem(10).build();
    }

    private List<IDrawerItem> setupDrawerItems() {
        List<IDrawerItem> drawerItems = new ArrayList<>();
        //Movies
        SectionDrawerItem movies = new SectionDrawerItem().withName(R.string.moveis).withTextColorRes(R.color.primaryText);
        movies.withDivider(false);
        drawerItems.add(movies);
        PrimaryDrawerItem moviesNews = new PrimaryDrawerItem().withName(R.string.news)
                .withTextColorRes(R.color.primaryText).withSetSelected(true)
                .withSelectedColorRes(R.color.selected).withIcon(R.drawable.ic_news)
                .withSelectedIconColorRes(R.color.background).withIdentifier(10);
        drawerItems.add(moviesNews);

        drawerItems.add(new DividerDrawerItem());
        //Series
        SectionDrawerItem series = new SectionDrawerItem().withName(R.string.series).withSelectable(false).withTextColorRes(R.color.primaryText);
        series.withDivider(false);
        drawerItems.add(series);
        PrimaryDrawerItem seriesNews = new PrimaryDrawerItem().withName(R.string.news)
                .withTextColorRes(R.color.primaryText).withSelectable(true).withSetSelected(true)
                .withSelectedColorRes(R.color.selected).withIcon(R.drawable.ic_news)
                .withSelectedIconColorRes(R.color.background).withIdentifier(20);
        drawerItems.add(seriesNews);
        PrimaryDrawerItem episodes = new PrimaryDrawerItem().withName(R.string.new_episodes)
                .withTextColorRes(R.color.primaryText).withSelectable(true).withSetSelected(true)
                .withSelectedColorRes(R.color.selected).withIcon(R.drawable.ic_news)
                .withSelectedIconColorRes(R.color.background).withIdentifier(30);
        drawerItems.add(episodes);

        drawerItems.add(new DividerDrawerItem());
        //Others
        SectionDrawerItem list = new SectionDrawerItem().withName(R.string.drawer_list).withSelectable(false).withTextColorRes(R.color.primaryText);
        list.withDivider(false);
        drawerItems.add(list);
        PrimaryDrawerItem bestMovies = new PrimaryDrawerItem().withName(R.string.best_movies)
                .withTextColorRes(R.color.primaryText)
                .withSelectedColorRes(R.color.selected).withIcon(R.drawable.ic_best)
                .withSelectedIconColorRes(R.color.background).withIdentifier(11);
        drawerItems.add(bestMovies);
        PrimaryDrawerItem bestSeries = new PrimaryDrawerItem().withName(R.string.best_series)
                .withTextColorRes(R.color.primaryText)
                .withSelectedColorRes(R.color.selected).withIcon(R.drawable.ic_best)
                .withSelectedIconColorRes(R.color.background).withIdentifier(21);
        drawerItems.add(bestSeries);
        drawerItems.add(new DividerDrawerItem());
        if (confHelper.isAnimationOpened())
            drawerItems.addAll(generateAnimSliding());

        SectionDrawerItem others = new SectionDrawerItem().withName(R.string.others).withSelectable(false).withTextColorRes(R.color.primaryText);
        list.withDivider(false);

        PrimaryDrawerItem downloads = new PrimaryDrawerItem().withName(R.string.downloads)
                .withTextColorRes(R.color.primaryText).withIdentifier(100)
                .withSelectedColorRes(R.color.selected).withSelectedIconColorRes(R.color.background);
        drawerItems.add(downloads);

        PrimaryDrawerItem request = new PrimaryDrawerItem().withName(R.string.request)
                .withTextColorRes(R.color.primaryText).withSelectable(true).withSetSelected(true).withIdentifier(3)
                .withSelectedColorRes(R.color.selected).withSelectedIconColorRes(R.color.background);
        drawerItems.add(request);
        PrimaryDrawerItem supportUs = new PrimaryDrawerItem().withName(R.string.support_us)
                .withTextColorRes(R.color.primaryText).withIdentifier(4)
                .withSelectedColorRes(R.color.selected).withSelectedIconColorRes(R.color.background);
        drawerItems.add(supportUs);
        PrimaryDrawerItem aboutUS = new PrimaryDrawerItem().withName(R.string.about_us)
                .withTextColorRes(R.color.primaryText).withIdentifier(1)
                .withSelectedColorRes(R.color.selected).withSelectedIconColorRes(R.color.background);
        drawerItems.add(aboutUS);
        PrimaryDrawerItem setting = new PrimaryDrawerItem().withName(R.string.settings)
                .withTextColorRes(R.color.primaryText).withIdentifier(2)
                .withSelectedColorRes(R.color.selected).withSelectedIconColorRes(R.color.background);
        drawerItems.add(setting);

        return drawerItems;
    }

    private List<IDrawerItem> generateAnimSliding() {
        List<IDrawerItem> drawerItems = new ArrayList<>();
        SectionDrawerItem animy = new SectionDrawerItem().withName(R.string.animy).withSelectable(false).withTextColorRes(R.color.primaryText);
        animy.withDivider(false);

        drawerItems.add(animy);
        PrimaryDrawerItem animSeries = new PrimaryDrawerItem().withName(R.string.new_anim_series)
                .withTextColorRes(R.color.primaryText).withSelectable(true).withSetSelected(true)
                .withSelectedColorRes(R.color.selected).withIcon(R.drawable.ic_news)
                .withSelectedIconColorRes(R.color.background);
        drawerItems.add(animSeries);
        PrimaryDrawerItem animMovies = new PrimaryDrawerItem().withName(R.string.new_anim_movies)
                .withTextColorRes(R.color.primaryText).withSelectable(true).withSetSelected(true)
                .withSelectedColorRes(R.color.selected).withIcon(R.drawable.ic_news)
                .withSelectedIconColorRes(R.color.background);
        drawerItems.add(animMovies);
        drawerItems.add(new DividerDrawerItem());
        return drawerItems;
    }

    private void addFragment(Fragment fragment, String name) {

        ActionBar bar = getSupportActionBar();
        if (bar != null && !name.isEmpty()) {
            bar.setTitle(name);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment, name).commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.isEmpty()) {
            Toast.makeText(this, "يجب ان تكتب شيئا", Toast.LENGTH_LONG).show();
            return true;
        }
        isSearch = true;
        addFragment(MainFragment.getInstance(query), "");
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isSearch) {
            searchView.setIconified(true);
            searchView.setIconifiedByDefault(true);
            searchView.clearFocus();
            isSearch = false;
            addFragment(MainFragment.getInstance(MainFragment.MOVIES, MainFragment.news), "احدث اضافات الأفلام");
        } else
            super.onBackPressed();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 0) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            updateUI(account);
        } catch (ApiException e) {

            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            profile.withIcon(account.getPhotoUrl());
            profile.withName(account.getDisplayName());
            header.updateProfile(profile);
        } else
            Toast.makeText(this, "Sorry Can't Login", Toast.LENGTH_LONG).show();
    }
}
