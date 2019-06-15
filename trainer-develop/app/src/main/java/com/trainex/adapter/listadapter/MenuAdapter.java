package com.trainex.adapter.listadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import com.trainex.R;

public class MenuAdapter extends ArrayAdapter<String> {
    Context context;
    List<String> listTitle;
    public MenuAdapter(@NonNull Context context, @NonNull List<String> listTitle) {
        super(context, R.layout.item_list_menu, listTitle);
        this.context = context;
        this.listTitle = listTitle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_menu,parent,false);
        TextView title = view.findViewById(R.id.item_title);
        ImageButton open = view.findViewById(R.id.item_open);
        title.setText(listTitle.get(position));
        return view;
    }
}
