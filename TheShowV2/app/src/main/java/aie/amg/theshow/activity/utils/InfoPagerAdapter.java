package aie.amg.theshow.activity.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import aie.amg.theshow.R;
import aie.amg.theshow.activity.fragment.DownloadFragment;
import aie.amg.theshow.activity.fragment.InfoFragment;
import aie.amg.theshow.models.Show;

public class InfoPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    private Fragment[] fragments = new Fragment[2];

    public InfoPagerAdapter(Context context, @NonNull FragmentManager fm, Show show) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        fragments[0] = InfoFragment.getInstance(show);
        fragments[1]= DownloadFragment.getInstance(show);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.info_title);
        } else {
            return context.getString(R.string.download_title);
        }
    }
}
