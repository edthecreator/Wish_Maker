package com.eddelacruz.wishmaker.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.eddelacruz.wishmaker.RootFragments.FirstTabRootFragment;
import com.eddelacruz.wishmaker.RootFragments.SecondTabRootFragment;

import java.util.ArrayList;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        //for the backstack
        Fragment f1 = new FirstTabRootFragment();
        Fragment f2 = new SecondTabRootFragment();

        fragments.add(f1);
        fragments.add(f2);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
