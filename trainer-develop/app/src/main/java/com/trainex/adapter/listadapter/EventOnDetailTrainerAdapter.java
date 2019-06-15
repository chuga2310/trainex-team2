package com.trainex.adapter.listadapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.trainex.R;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.model.Event;
import com.trainex.uis.main.DetailEventActivity;
import com.trainex.uis.main.PaymenttActivity;

public class EventOnDetailTrainerAdapter extends ArrayAdapter<Event> {
    private Context context;
    private ArrayList<Event> listEvent;
    private TextView tvTitle;
    private TextView tvTime;
    private ImageView imgEvent;
    private TextView tvLocation;
    private TextView tvContent;
    private Button btnPrice;
    private Button btnViewDetailEvent;
    private Button btnBookNow;
    private String token;
    public EventOnDetailTrainerAdapter(@NonNull Context context, @NonNull ArrayList<Event> objects) {
        super(context, R.layout.item_list_event_in_details, objects);
        this.context = context;
        this.listEvent = objects;
    }

    @Override
    public int getCount() {
        if (listEvent == null) {
            return 0;
        } else if (listEvent.size() < 2) {
            return listEvent.size();
        } else {
            return 2;
        }
    }
    @Override
    public Event getItem(int position) {
        return listEvent.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_event_in_details,parent,false);


        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        imgEvent = (ImageView) view.findViewById(R.id.imgEvent);
        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        btnPrice = (Button) view.findViewById(R.id.btnPrice);
        btnViewDetailEvent = (Button) view.findViewById(R.id.btnViewDetailEvent);
        btnBookNow = (Button) view.findViewById(R.id.btnBookNow);

        tvTitle.setText(listEvent.get(position).getTitle());
        tvTime.setText(listEvent.get(position).getDate());
        Glide.with(context)
                .load(listEvent.get(position).getResImage())
                .into(imgEvent);
        tvLocation.setText(listEvent.get(position).getLocation());
        tvContent.setText(listEvent.get(position).getContent());
        btnPrice.setText(listEvent.get(position).getPrice()+ " AED");
        btnViewDetailEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventActivity.class);
                intent.putExtra("idEvent", listEvent.get(position).getId());
                intent.putExtra("isBooked", false);
                context.startActivity(intent);
            }
        });
        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
                token = prefs.getString("token", "");
                if (token.equalsIgnoreCase("")){
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(getContext(), "Error", "You must login to but this!");
                    alertLoginDialog.show();
                }else{
                    Intent intent = new Intent(context, PaymenttActivity.class);
                    intent.putExtra("title","Book An Event");
                    intent.putExtra("bookingType","event");
                    intent.putExtra("idEvent", listEvent.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }
}
