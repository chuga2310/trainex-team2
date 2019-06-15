package com.trainex.adapter.viewpageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trainex.fragment.insignup.SignupFragment;
import com.trainex.fragment.insignup.TermsFragment;

public class SignUpPagerAdapter extends FragmentPagerAdapter {
    public SignUpPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new SignupFragment();
            case 1:
                return new TermsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
