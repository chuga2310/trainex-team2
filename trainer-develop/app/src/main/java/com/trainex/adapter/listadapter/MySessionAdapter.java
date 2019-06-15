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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.trainex.R;
import com.trainex.model.MySessionsBookings;

public class MySessionAdapter extends RecyclerView.Adapter<MySessionAdapter.RecyclerViewHolder> {

    private ArrayList<MySessionsBookings> listSession;
    private Context context;
    private static final int MY_PERMISSIONS_REQUEST_CALL = 99;
    private String phone;
    public MySessionAdapter(Context context,ArrayList<MySessionsBookings> listCoach) {
        this.listSession = listCoach;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_my_session, viewGroup, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, final int i) {
        MySessionsBookings mySessionsBookings
                = new MySessionsBookings(listSession.get(i).getOrderID(),
                                        listSession.get(i).getPrice(),
                                        listSession.get(i).getSession(),
                                        listSession.get(i).getTrainer(),
                                        listSession.get(i).getDateTime(),
                                        listSession.get(i).getPhoneNumber());
        recyclerViewHolder.tvOrderID.setText(mySessionsBookings.getOrderID()+"");
        recyclerViewHolder.tvPrice.setText(mySessionsBookings.getPrice()+" AED");
        recyclerViewHolder.tvSessions.setText(mySessionsBookings.getSession());
        SharedPreferences prefs = context.getSharedPreferences("MY_SHARE_PREFERENCE", context.MODE_PRIVATE);
        phone = prefs.getString("phone_number","");
        recyclerViewHolder.tvTime.setText(mySessionsBookings.getDateTime());
        recyclerViewHolder.tvTrainer.setText(mySessionsBookings.getTrainer());
        recyclerViewHolder.imgCall.setOnClickListener(new View.OnClickListener() {
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
                                MY_PERMISSIONS_REQUEST_CALL);
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
        return listSession.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView tvOrderID;
        private TextView tvPrice;
        private TextView tvSessions;
        private TextView tvTrainer;
        private TextView tvTime;
        private ImageView imgCall;



        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderID = (TextView) itemView.findViewById(R.id.tvOrderIDSessions);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvSessions = (TextView) itemView.findViewById(R.id.tvSessions);
            tvTrainer = (TextView) itemView.findViewById(R.id.tvTrainer);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            imgCall = (ImageView) itemView. findViewById(R.id.imgCall);

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
