package com.trainex.uis.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.model.Event;
import com.trainex.rest.RestClient;

public class DetailEventActivity extends AppCompatActivity {
    private ImageView imgBack;
    private TextView titleDetail;
    private ImageView imgEvent;
    private TextView tvTitleEvent;
    private TextView tvTrainer;
    private TextView tvPrice;
    private TextView tvDateTime;
    private TextView tvLocation;
    private TextView tvContent;
    private Button btnBookNow;
    private int idEvent, price;
    private LinearLayout viewImgBack;
    private String token;
    private  boolean isBooked;
    private String nameEvent, imageEvent, nameTrainer, dateTime, location, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        idEvent = getIntent().getIntExtra("idEvent",-1);
        isBooked = getIntent().getBooleanExtra("isBooked", false);
        init();
        bind();
        if (isBooked){
            btnBookNow.setVisibility(View.GONE);
        }else{
            btnBookNow.setVisibility(View.VISIBLE);
        }
        if (idEvent!=-1){
            Call<JsonElement> callGetEventDetail = RestClient.getApiInterface().getDetailEventByID(idEvent);
            callGetEventDetail.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    int code = response.code();
                    if(code ==200){
                        JsonObject body = response.body().getAsJsonObject();
                        JsonObject data = body.get("data").getAsJsonObject();
                        imageEvent = data.get("event_photo").getAsString();
                        nameEvent = data.get("name").getAsString();
                        nameTrainer = data.get("name_trainer").getAsString();
                        dateTime = data.get("date_time").getAsString();
                        price = data.get("price").getAsInt();
                        location = data.get("location").getAsString();
                        description = data.get("description").getAsString();
                        String dateTimeShow="";
                        try {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                            cal.setTime(sdf.parse(dateTime));

                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd- MMM - yyyy, hh:mm a", Locale.ENGLISH);

                            if (cal != null) {
                                dateTimeShow = sdf2.format(cal.getTime());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Glide.with(getApplicationContext()).load(imageEvent).into(imgEvent);
                        tvTitleEvent.setText(nameEvent);
                        tvTrainer.setText(nameTrainer);
                        tvPrice.setText(price + " AED");
                        tvDateTime.setText(dateTimeShow);
                        tvLocation.setText(location);
                        tvContent.setText(description);
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Toast.makeText(DetailEventActivity.this, "Loading Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void init(){
        imgBack = (ImageView) findViewById(R.id.imgBack);
        titleDetail = (TextView) findViewById(R.id.titleDetail);
        imgEvent = (ImageView) findViewById(R.id.imgEvent);
        tvTitleEvent = (TextView) findViewById(R.id.tvTitleEvent);
        tvTrainer = (TextView) findViewById(R.id.tvTrainer);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvContent = (TextView) findViewById(R.id.tvContent);
        btnBookNow = (Button) findViewById(R.id.btnBookNow);
        viewImgBack = (LinearLayout) findViewById(R.id.viewImgBack);
    }
    private void bind(){
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
                token = prefs.getString("token", "");
                if (token.equalsIgnoreCase("")){
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(DetailEventActivity.this, "Error", "You must login to buy!");
                    alertLoginDialog.show();
                }else{
                    Intent intent = new Intent(DetailEventActivity.this, PaymenttActivity.class);
                    intent.putExtra("title","Book An Event");
                    intent.putExtra("bookingType","event");
                    intent.putExtra("idEvent", idEvent);
                    startActivity(intent);
                }
            }
        });
    }
}
