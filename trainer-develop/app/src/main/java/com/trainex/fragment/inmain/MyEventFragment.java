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
import com.trainex.adapter.listadapter.MyEventAdapter;
import com.trainex.adapter.listadapter.MySessionAdapter;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.MyEventsBooking;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class MyEventFragment extends Fragment {
   private View view;
    private ArrayList<MyEventsBooking> listMyEvent;
    private RecyclerView lvMyEvents;
    private int indexPage;
    private int totalPage;
    private boolean isLoading;
    private String token;
    private MyEventAdapter adapter;
    private Bundle savedState= new Bundle();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listMyEvent = new ArrayList<>();
        indexPage = savedState.getInt("indexPageGroupEvent",0);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        if (indexPage==0){
            indexPage =1;
            getData(indexPage);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                view = inflater.inflate(R.layout.fragment_my_events,container,false);
        Log.e("createEvetm", 123+"");
        init();
        bind();

        return view;
    }




    private void init(){
        lvMyEvents = view.findViewById(R.id.lvMyEvents);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        lvMyEvents.setLayoutManager(layoutManager1);
    }
    private void bind(){
        adapter = new MyEventAdapter(getContext(),listMyEvent);
        lvMyEvents.setAdapter(adapter);
        lvMyEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        Call<JsonElement> callGetEventBooking = RestClient.getApiInterface().getEventBooking(token, page);
        callGetEventBooking.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body().getAsJsonObject();
                    int codeBody = body.get("code").getAsInt();
                    if (codeBody==200){
                        JsonObject data = body.get("data").getAsJsonObject();
                        totalPage = data.get("last_page").getAsInt();
                        JsonArray dataEvent = data.get("data").getAsJsonArray();
                        if (dataEvent.size()>0){
                            for (JsonElement x: dataEvent){
                                JsonObject obj = x.getAsJsonObject();
                                String oderId = obj.get("order_id").getAsString();
                                String name = obj.get("name").getAsString();
                                String date = obj.get("date_time").getAsString();
                                int price = obj.get("total_amount").getAsInt();
                                int idEvent = obj.get("event_id").getAsInt();
                                String resImage = obj.get("event_photo").getAsString();
                                String location = obj.get("location").getAsString();
                                String description = obj.get("description").getAsString();
                                String phoneNumber = "";
                                if(!obj.get("phone_number").isJsonNull()){
                                    phoneNumber =  obj.get("phone_number").getAsString();
                                }
                                String timeShow="";
                                try {
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                    cal.setTime(sdf.parse(date));

                                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd - MMM - yyyy, hh:mm a", Locale.ENGLISH);

                                    if (cal != null) {
                                        timeShow = sdfDate.format(cal.getTime());

                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                MyEventsBooking myEventsBooking = new MyEventsBooking(
                                        oderId,
                                        name,
                                        resImage,
                                        price,
                                        timeShow,
                                        location,
                                        description,
                                        idEvent,
                                        phoneNumber
                                );
                                listMyEvent.add(myEventsBooking);
                                adapter.notifyItemInserted(listMyEvent.size());
                                isLoading = false;
                            }
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
                Toast.makeText(getContext(), "Loading failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
