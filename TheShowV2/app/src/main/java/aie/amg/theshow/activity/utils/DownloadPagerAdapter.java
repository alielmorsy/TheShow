package aie.amg.theshow.activity.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import aie.amg.theshow.activity.fragment.DownloadedFragment;
import aie.amg.theshow.activity.fragment.DownloadingFragment;

public class DownloadPagerAdapter extends FragmentPagerAdapter {

    public DownloadPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DownloadingFragment.newInstance();
            case 1:
                return DownloadedFragment.getInstance();
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Downloading";
        else
            return "Downloaded";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
