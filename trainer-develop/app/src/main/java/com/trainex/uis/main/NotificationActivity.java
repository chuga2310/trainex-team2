package com.trainex.uis.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.listadapter.NotificationAdapter;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.Notification;
import com.trainex.rest.RestClient;

public class NotificationActivity extends AppCompatActivity {
    private ImageView imgBack;
    private RecyclerView lvNotification;
    private ArrayList<Notification> listNotification;
    private String token;
    private int indexPage;
    private int totalPage;
    private boolean isLoading;
    private NotificationAdapter adapter;
    private LinearLayout viewImgBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        listNotification = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        indexPage = 1;
        if (indexPage == 1) {
            getData(indexPage);
        }
        init();
        bind();
    }

    private void init() {
        imgBack = (ImageView) findViewById(R.id.imgBack);
        lvNotification = (RecyclerView) findViewById(R.id.lvNotification);
        viewImgBack = (LinearLayout) findViewById(R.id.viewImgBack);

    }

    private void bind() {
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(NotificationActivity.this);
        lvNotification.setLayoutManager(layoutManager1);
        adapter= new NotificationAdapter(NotificationActivity.this, listNotification);
        lvNotification.setAdapter(adapter);
        lvNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager1 = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int visibleItemCount = layoutManager1.getChildCount();
                int totalItemCount = layoutManager1.getItemCount();
                int firstVisibleItemPosition = layoutManager1.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager1.findLastVisibleItemPosition();

                // Load more if we have reach the end to the recyclerView
                if ((visibleItemCount + lastVisibleItemPosition) >= (totalItemCount - 2) && firstVisibleItemPosition >= 0 && !isLoading) {
                    indexPage++;
                    if (indexPage <= totalPage) {
                        getData(indexPage);
                    }
                    isLoading = true;
                }
            }
        });
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }



    private void getData(int page) {
        Call<JsonElement> callGetNoti = RestClient.getApiInterface().getNotification(token, page);
        callGetNoti.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body().getAsJsonObject();
                    int codeBody = body.get("code").getAsInt();
                    if (codeBody == 200){
                        JsonObject data = body.get("data").getAsJsonObject();
                        totalPage = data.get("last_page").getAsInt();
                        JsonArray listDataNoti = data.get("data").getAsJsonArray();
                        if (listDataNoti.size()>0){
                            for (JsonElement x: listDataNoti){
                                JsonObject obj = x.getAsJsonObject();
                                String content ="";
                                if (obj.has("content")){
                                    if (!obj.get("content").isJsonNull()){
                                        content = obj.get("content").getAsString();
                                    }
                                }
                                String username ="";
                                if (obj.has("username")){
                                    if (!obj.get("username").isJsonNull()){
                                        username = obj.get("username").getAsString();
                                    }
                                }
                                String time ="";
                                if (obj.has("created_at")){
                                    if (!obj.get("created_at").isJsonNull()){
                                        time = obj.get("created_at").getAsString();
                                    }
                                }
                                String date="";
                                try {
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                    cal.setTime(sdf.parse(time));

                                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd - MMM'' yy", Locale.ENGLISH);

                                    if (cal != null) {
                                        date = sdfDate.format(cal.getTime());
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                Notification notification = new Notification(content, username, date);
                                listNotification.add(notification);

                            }
                            adapter.notifyItemInserted(listNotification.size());
                            isLoading = false;
                        }
                    } else{
                        String s = "";
                        if (body.has("error")){
                            s = body.get("error").getAsString();
                        }
                        if (body.has("message")){
                            s = body.get("message").getAsString();
                        }
                        CustomAlertDialog alertDialog = new CustomAlertDialog(NotificationActivity.this,"Error!",s,"Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {
                                finish();
                            }
                        };
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }
}
