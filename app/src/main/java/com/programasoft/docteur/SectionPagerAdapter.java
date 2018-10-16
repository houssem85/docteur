package com.programasoft.docteur;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ASUS on 10/10/2018.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {case 0:return new rechercheFragment();
            case 1:return new MonPositionFragment();
            case 2:return new FavorisFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {case 0:return "Rechercher";
            case 1:return "Mon position";
            case 2:return "Favoris";
            default:
                return null;

        }
    }
}
