package com.trainex.adapter.listadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trainex.R;

import java.util.ArrayList;

import com.trainex.fragment.inmain.DetailTrainerFragment;
import com.trainex.model.CategoryInDetailTrainer;
import com.trainex.model.Session;

public class CategoryInDetailTrainerAdapter extends ArrayAdapter<CategoryInDetailTrainer> {
    private Context context;
    private ArrayList<CategoryInDetailTrainer> listCategory;

    private int idCategory;
    private int idSession;
    private String nameCategory;

    public CategoryInDetailTrainerAdapter(@NonNull Context context, ArrayList<CategoryInDetailTrainer> listCategory) {
        super(context, R.layout.item_category_detail_trainer, listCategory);
        this.context = context;
        this.listCategory = listCategory;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_detail_trainer, parent, false);
        TextView tvNameCategory = (TextView) view.findViewById(R.id.tvNameCategory);
        final ArrayList<Session> listSession;
        final ImageView toogleEvents = (ImageView) view.findViewById(R.id.toogleEvents);
        final ListView lvSession = (ListView) view.findViewById(R.id.lvSession);
        idCategory = listCategory.get(position).getIdCategory();
        idSession = listCategory.get(position).getIdSession();
        listSession = listCategory.get(position).getListSession();
        nameCategory = listCategory.get(position).getNameCategory();
        ListSessionAdapter sessionAdapter = new ListSessionAdapter(context, listSession);
        lvSession.setAdapter(sessionAdapter);
        DetailTrainerFragment.setListViewHeightBasedOnChildren(lvSession, DetailTrainerFragment.getItemHeightofListView(lvSession,0)* (listSession.size()-1));
        tvNameCategory.setText(nameCategory);
        toogleEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listCategory.get(position).isShow()) {
                    toogleEvents.setImageResource(R.mipmap.hide_event_on_details);
                    lvSession.setVisibility(View.VISIBLE);
                    listCategory.get(position).setShow(true);
                    DetailTrainerFragment.setListViewHeightBasedOnChildren(DetailTrainerFragment.lvSessionInCategory, DetailTrainerFragment.getItemHeightofListView(lvSession,0)* (listSession.size()-1));
                } else {
                    toogleEvents.setImageResource(R.mipmap.show_event_on_details);
                    lvSession.setVisibility(View.GONE);
                    listCategory.get(position).setShow(false);
                    DetailTrainerFragment.setListViewHeightBasedOnChildren(DetailTrainerFragment.lvSessionInCategory,DetailTrainerFragment.getItemHeightofListView(lvSession,0)* (listSession.size()-1));
                }
            }
        });

        return view;
    }
}
