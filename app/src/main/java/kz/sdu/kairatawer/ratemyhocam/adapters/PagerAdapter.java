package kz.sdu.kairatawer.ratemyhocam.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kz.sdu.kairatawer.ratemyhocam.fragments.FirstFragment;
import kz.sdu.kairatawer.ratemyhocam.fragments.SecondFragment;
import kz.sdu.kairatawer.ratemyhocam.fragments.ThirdFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String teacherId;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String teacherId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.teacherId = teacherId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("teacherId", teacherId);

        switch (position) {
            case 0:
                FirstFragment tab1 = new FirstFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                SecondFragment tab2 = new SecondFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                ThirdFragment tab3 = new ThirdFragment();
                tab3.setArguments(bundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}