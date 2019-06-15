package com.trainex.fragment.inmain;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.trainex.adapter.listadapter.MySessionAdapter;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.MySessionsBookings;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class MySessionsFragment extends Fragment {
    private View view;
    private RecyclerView lvMySession;
    private ArrayList<MySessionsBookings> listMySessions;
    private int indexPage;
    private int totalPage;
    private boolean isLoading;
    private String token;
    private MySessionAdapter adapter;
    private Bundle savedState= new Bundle();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listMySessions = new ArrayList<>();
        indexPage = savedState.getInt("indexPageGroupEvent",0);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        if (indexPage==0){
            indexPage =1;
            getData(indexPage);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_session_list, container, false);
        init();
        bind();
        return view;
    }

    private void init() {
        lvMySession = (RecyclerView) view.findViewById(R.id.lvMySession);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        lvMySession.setLayoutManager(layoutManager1);
    }

    private void bind() {
        adapter = new MySessionAdapter(getContext(), listMySessions);
        lvMySession.setAdapter(adapter);
        lvMySession.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (indexPage < totalPage) {
                    LinearLayoutManager layoutManager1 = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                    int visibleItemCount = layoutManager1.getChildCount();
                    int totalItemCount = layoutManager1.getItemCount();
                    int firstVisibleItemPosition = layoutManager1.findFirstVisibleItemPosition() ;
                    int lastVisibleItemPosition = layoutManager1.findLastVisibleItemPosition();

                    // Load more if we have reach the end to the recyclerView

                    if ((lastVisibleItemPosition) == (totalItemCount-2) && firstVisibleItemPosition >= 0 && !isLoading) {
                        indexPage++;
                        Log.e("idex", indexPage+"");
                        getData(indexPage);
                        isLoading =true;
                    }

                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
    }

    private void getData(int page){
        Call<JsonElement> callGetSessionBooking = RestClient.getApiInterface().getSessionBooking(token, page);
        callGetSessionBooking.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body().getAsJsonObject();
                    int code = body.get("code").getAsInt();
                    if (code == 200){
                        JsonObject bigData = body.get("data").getAsJsonObject();
                        totalPage = bigData.get("last_page").getAsInt();
                        JsonArray data = bigData.get("data").getAsJsonArray();
                        for (JsonElement x: data){
                            JsonObject obj = x.getAsJsonObject();
                            String oderId = obj.get("order_id").getAsString();
                            String sessionType = obj.get("session_type").getAsString().replace('_', ' ');
                            String trainerName = "";
                            if(!obj.get("fullname").isJsonNull()){
                                trainerName =  obj.get("fullname").getAsString();
                            }
                            String phoneNumber = "";
                            if(!obj.get("phone_number").isJsonNull()){
                                phoneNumber =  obj.get("phone_number").getAsString();
                            }
                            String time  = obj.get("created_at").getAsString();
                            int price = Integer.parseInt(obj.get("total_amount").getAsString());
                            String timeShow="";
                            try {
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                cal.setTime(sdf.parse(time));

                                SimpleDateFormat sdfDate = new SimpleDateFormat("dd - MMM - yyyy, hh:mm a", Locale.ENGLISH);

                                if (cal != null) {
                                    timeShow = sdfDate.format(cal.getTime());

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            MySessionsBookings  sessionsBookings = new MySessionsBookings(oderId,price,sessionType,trainerName, timeShow, phoneNumber);
                            listMySessions.add(sessionsBookings);
                            adapter.notifyItemInserted(listMySessions.size());
                            isLoading = false;
                        }
                    }else{
                        String s = "";
                        if (body.has("error")){
                            s = body.get("error").getAsString();
                        }
                        if (body.has("message")){
                            s = body.get("message").getAsString();
                        }
                        CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(),"Error!",s,"Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {
                                MainActivity.showHomeFromLeft();
                            }
                        };
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
