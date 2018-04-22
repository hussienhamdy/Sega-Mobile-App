package com.example.ussien.sega.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ussien.sega.Fragments.NewsFragment;
import com.example.ussien.sega.Fragments.ScoreBoardFragment;

/**
 * Created by ussien on 20/09/2017.
 */

public class ViewPagerAdapter  extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private int gameID;

    public ViewPagerAdapter(FragmentManager fm , int NumOfTabs,int gameID) {
        super(fm);
        this.numOfTabs = NumOfTabs;
        this.gameID=gameID;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("gameID",gameID);
        switch (position) {
            case 0:
                Fragment tab1 = new NewsFragment();
                tab1.setArguments(bundle);
                return tab1;

            case 1:
                Fragment tab2 = new ScoreBoardFragment();
                tab2.setArguments(bundle);
                return tab2;

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return numOfTabs;
    }

}
