package com.trainex.adapter.listadapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.trainex.R;
import com.trainex.model.MyEventsBooking;
import com.trainex.uis.main.DetailEventActivity;
import com.trainex.uis.main.MainActivity;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.RecyclerViewHolder> {
    private Context context;
    private ArrayList<MyEventsBooking> listEvents;
    private static final int MY_PERMISSIONS_REQUEST_CALL = 99;
    private String phone;
    public MyEventAdapter(Context context, ArrayList<MyEventsBooking> listEvents) {
        this.context = context;
        this.listEvents = listEvents;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_my_event, viewGroup, false);
        MyEventAdapter.RecyclerViewHolder viewHolder = new MyEventAdapter.RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, final int i) {
        MyEventsBooking myEventsBooking = new MyEventsBooking();
        myEventsBooking.setOrderID(listEvents.get(i).getOrderID());
        myEventsBooking.setContent(listEvents.get(i).getContent());
        myEventsBooking.setDateTime(listEvents.get(i).getDateTime());
        myEventsBooking.setLocation(listEvents.get(i).getLocation());
        myEventsBooking.setTitle(listEvents.get(i).getTitle());
        myEventsBooking.setPrice(listEvents.get(i).getPrice());
        myEventsBooking.setResImage(listEvents.get(i).getResImage());
        SharedPreferences prefs = context.getSharedPreferences("MY_SHARE_PREFERENCE", context.MODE_PRIVATE);
        phone = prefs.getString("phone_number","");
        recyclerViewHolder.tvTitle.setText(myEventsBooking.getTitle());
        recyclerViewHolder.tvOrderID.setText(myEventsBooking.getOrderID()+"");

        recyclerViewHolder.tvContent.setText(myEventsBooking.getContent());
        recyclerViewHolder.tvLocation.setText(myEventsBooking.getLocation());
        recyclerViewHolder.tvTime.setText(myEventsBooking.getDateTime());
        recyclerViewHolder.btnPrice.setText(myEventsBooking.getPrice()+" AED");
        Glide.with(context).load(myEventsBooking.getResImage()).into(recyclerViewHolder.imgEvent);
        recyclerViewHolder.btnViewDetailEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventActivity.class);
                intent.putExtra("idEvent", listEvents.get(i).getIdEvent());
                intent.putExtra("isBooked", true);
                context.startActivity(intent);
            }
        });
        recyclerViewHolder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            Manifest.permission.CALL_PHONE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MainActivity.PERMISSION_CALL);
                    }
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+phone));
                    context.startActivity(callIntent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderID;
        private TextView tvTitle;
        private TextView tvTime;
        private ImageView imgEvent;
        private TextView tvLocation;
        private TextView tvContent;
        private Button btnPrice;
        private Button btnViewDetailEvent;
        private LinearLayout btnCall;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderID = (TextView) itemView.findViewById(R.id.tvOrderIDEvents);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            imgEvent = (ImageView) itemView.findViewById(R.id.imgEvent);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            btnPrice = (Button) itemView.findViewById(R.id.btnPrice);
            btnViewDetailEvent = (Button) itemView.findViewById(R.id.btnViewDetailEvent);
            btnCall = (LinearLayout) itemView.findViewById(R.id.btnCall);
        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
