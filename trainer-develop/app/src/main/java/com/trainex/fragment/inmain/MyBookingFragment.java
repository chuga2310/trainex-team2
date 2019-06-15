package com.trainex.fragment.inmain;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trainex.R;
import com.trainex.adapter.viewpageradapter.FragmentBookingPagerAdapter;
import com.trainex.uis.main.MainActivity;

public class MyBookingFragment extends Fragment {
    View view;
    private ImageView imgShowDrawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private    FragmentBookingPagerAdapter adapter;
    private MySessionsFragment mySessionsFragment =new MySessionsFragment();
    private MyEventFragment myEventFragment = new MyEventFragment();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("create",123+"");
        view = inflater.inflate(R.layout.fragment_mybooking, container, false);
        init();
        bind();

        return view;
    }

    private void init() {
        imgShowDrawer = (ImageView) view.findViewById(R.id.imgShowDrawer);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    private void bind() {
        imgShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showMenu();
            }
        });


        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new FragmentBookingPagerAdapter(getChildFragmentManager());
        adapter.addFragment(mySessionsFragment,"Sessions");
        adapter.addFragment(myEventFragment, "Events");
    }
}
