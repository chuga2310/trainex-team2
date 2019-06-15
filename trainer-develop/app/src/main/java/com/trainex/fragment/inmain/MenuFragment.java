package com.trainex.fragment.inmain;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.trainex.R;
import com.trainex.adapter.listadapter.MenuAdapter;
import com.trainex.uis.main.MainActivity;


public class MenuFragment extends Fragment {
    ListView listItem;
    List<String> listTitle;
    ImageButton imgClose;
    SelectMenu selectMenu;
    String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        listItem = view.findViewById(R.id.listMenu);
        imgClose = view.findViewById(R.id.imgClose);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        listTitle = new ArrayList<>();
        listTitle.add("Home");
        listTitle.add("My Bookings");
        listTitle.add("Notifications");
        listTitle.add("Favorites");
        listTitle.add("About Us");
        listTitle.add("Terms & Conditions");
        listTitle.add("My Profile");
        listTitle.add("Contact Us");
        listTitle.add("Share App");
        listTitle.add("Logout");
        MenuAdapter adapter = new MenuAdapter(getActivity().getApplicationContext(), listTitle);
        listItem.setAdapter(adapter);
        selectMenu =(SelectMenu) getArguments().getParcelable("menuSelect");
        bind();

        return view;
    }

    private void bind() {

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.closeMenu();
            }
        });
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectMenu.setOnMenuSelection(i);
            }
        });
    }

    public abstract static class SelectMenu implements Parcelable {
        public abstract void setOnMenuSelection(int i);
    }
}
