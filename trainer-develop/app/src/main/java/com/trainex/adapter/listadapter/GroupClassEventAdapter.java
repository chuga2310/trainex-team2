package com.trainex.adapter.listadapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.trainex.R;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.model.Event;
import com.trainex.uis.main.DetailEventActivity;
import com.trainex.uis.main.PaymenttActivity;

public class GroupClassEventAdapter extends RecyclerView.Adapter<GroupClassEventAdapter.RecyclerViewHolder> {
    ArrayList<Event> listEvent;
    Context context;
    private String token;
    public GroupClassEventAdapter(ArrayList<Event> listEvent, Context context) {
        this.listEvent = listEvent;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list_group_class_event, viewGroup, false);
       RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, final int i) {
        recyclerViewHolder.tvTitle.setText(listEvent.get(i).getTitle());
        String date = listEvent.get(i).getDate();
        recyclerViewHolder.tvDate.setText(convertDateToString(date));
        recyclerViewHolder.tvTrainer.setText("by "+listEvent.get(i).getTrainer());
        Glide.with(context)
                .load(listEvent.get(i).getResImage())
                .into(recyclerViewHolder.imgResImageEvent);
        String time = listEvent.get(i).getTime();
        recyclerViewHolder.tvTime.setText(convertTimeToString(time));
        recyclerViewHolder.tvLocation.setText(listEvent.get(i).getLocation());
        recyclerViewHolder.tvPrice.setText(listEvent.get(i).getPrice()+" AED");
        recyclerViewHolder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventActivity.class);
                intent.putExtra("idEvent", listEvent.get(i).getId());
                intent.putExtra("isBooked", false);
                context.startActivity(intent);
            }
        });

        recyclerViewHolder.btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = context.getSharedPreferences("MY_SHARE_PREFERENCE", context.MODE_PRIVATE);
                token = prefs.getString("token", "");
                if (token.equalsIgnoreCase("")){
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(context, "Error", "You must login to but this!");
                    alertLoginDialog.show();
                }else{
                    Intent intent = new Intent(context, PaymenttActivity.class);
                    intent.putExtra("title","Book An Event");
                    intent.putExtra("bookingType","event");
                    intent.putExtra("idEvent", listEvent.get(i).getId());
                    context.startActivity(intent);
                }
            }
        });
        recyclerViewHolder.imgEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailEventActivity.class);
                intent.putExtra("idEvent", listEvent.get(i).getId());
                intent.putExtra("isBooked", false);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listEvent.size();
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvTrainer;
        private RoundedImageView imgResImageEvent;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvLocation;
        private TextView tvPrice;
        private Button btnBookNow;
        private Button btnViewDetails;
        private CardView imgEvent;

        public RecyclerViewHolder(@NonNull View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvTrainer = (TextView) view.findViewById(R.id.tvTrainer);
            imgResImageEvent = (RoundedImageView) view.findViewById(R.id.imgResImageEvent);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            btnBookNow = (Button) view.findViewById(R.id.btnBookNow);
            btnViewDetails = (Button) view.findViewById(R.id.btnViewDetails);
            imgEvent = (CardView) view.findViewById(R.id.imgEvent);

        }
    }
    private String convertDateToString(String date){

        Date dateFormat;
        String dateShow;
        try {

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            SimpleDateFormat formatShow = new SimpleDateFormat("dd\nMMM",Locale.ENGLISH);
            dateFormat = format.parse(date);
            dateShow = formatShow.format(dateFormat);
            return dateShow;
        }catch (Exception e){
            Log.e("error",e.toString());
            return "";
        }
    }
    private String convertTimeToString(String time){

        Date timeFormat;
        String timeShow;
        try {

            SimpleDateFormat format = new SimpleDateFormat("h:mm a",Locale.ENGLISH);
            SimpleDateFormat formatShow = new SimpleDateFormat("h:mm\na",Locale.ENGLISH);
            timeFormat = format.parse(time);
            timeShow = formatShow.format(timeFormat);
            return timeShow;
        }catch (Exception e){
            Log.e("error",e.toString());
            return "";
        }
    }
}
