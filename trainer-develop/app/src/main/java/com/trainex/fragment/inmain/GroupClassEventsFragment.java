package com.trainex.fragment.inmain;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.trainex.adapter.listadapter.GroupClassEventAdapter;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.model.Event;
import com.trainex.rest.RestClient;

public class GroupClassEventsFragment extends Fragment {
    private ImageView imgBack;
    private TextView titleListCoach;
    private LinearLayout categories;
    private RecyclerView lvEvent;
    private LinearLayout viewImgBack;
    private ArrayList<Event> listEvent=new ArrayList<>();;
    private Bundle savedState = new Bundle();
    private boolean isClosedCategories;
    private int indexPageGroupEvent;
    private int totalPageGroupEvent;
    private GroupClassEventAdapter adapter;
    private boolean isLoading;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        indexPageGroupEvent = savedState.getInt("indexPageGroupEvent",0);
        if (indexPageGroupEvent==0){
            indexPageGroupEvent =1;
            getData(indexPageGroupEvent);
        }

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_class_event, container, false);
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        titleListCoach = (TextView) view.findViewById(R.id.titleListCoach);
        categories = (LinearLayout) view.findViewById(R.id.categories);
        lvEvent = (RecyclerView) view.findViewById(R.id.lvEvent);
        viewImgBack = (LinearLayout) view.findViewById(R.id.viewImgBack);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        bind();
        /**************************/
        if (savedState != null) {
            isClosedCategories = savedState.getBoolean("isCloseCategories");
            if (isClosedCategories) {
                categories.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private void bind() {
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        lvEvent.setLayoutManager(layoutManager1);
        adapter = new GroupClassEventAdapter(listEvent, getContext());;
        lvEvent.setAdapter(adapter);
        lvEvent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (indexPageGroupEvent < totalPageGroupEvent) {
                    LinearLayoutManager layoutManager1 = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                    int visibleItemCount = layoutManager1.getChildCount();
                    int totalItemCount = layoutManager1.getItemCount();
                    int firstVisibleItemPosition = layoutManager1.findFirstVisibleItemPosition() ;
                    int lastVisibleItemPosition = layoutManager1.findLastVisibleItemPosition();

                    // Load more if we have reach the end to the recyclerView

                    if ((lastVisibleItemPosition) == (totalItemCount-2) && firstVisibleItemPosition >= 0 && !isLoading) {
                        indexPageGroupEvent++;
                        Log.e("idex", indexPageGroupEvent+"");
                        getData(indexPageGroupEvent);
                        isLoading =true;
                    }

                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listEvent.clear();
                indexPageGroupEvent = 1;
                getData(indexPageGroupEvent);
            }
        });
    }
    private void getData( int page) {
        Log.e("startLoad",page+"");

        Call<JsonElement> callGetListCategory = RestClient.getApiInterface().getListGroupClassEvent(page);
        callGetListCategory.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                if (response.isSuccessful()) {
                    swipeContainer.setRefreshing(false);
                    int code = response.code();
                    if (code == 200) {
                        JsonObject body = response.body().getAsJsonObject();
                        if (body.get("code").getAsInt() == 200) {
                            JsonObject bigData = body.get("data").getAsJsonObject();
                            totalPageGroupEvent = bigData.get("last_page").getAsInt();
                            JsonArray dataList = bigData.get("data").getAsJsonArray();
                            for (int i = 0; i < dataList.size(); i++) {

                                int id = dataList.get(i).getAsJsonObject().get("id").getAsInt();
                                String name = dataList.get(i).getAsJsonObject().get("name").getAsString();
                                String resImage = dataList.get(i).getAsJsonObject().get("event_photo").getAsString();
                                String dateTime = dataList.get(i).getAsJsonObject().get("date_time").getAsString();
                                int price  = dataList.get(i).getAsJsonObject().get("price").getAsInt();
                                String location = dataList.get(i).getAsJsonObject().get("location").getAsString();
                                String nameTrainer = dataList.get(i).getAsJsonObject().get("name_trainer").getAsString();
                                String dateShow="", timeShow="";
                                try {
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                    cal.setTime(sdf.parse(dateTime));

                                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                                    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

                                    if (cal != null) {
                                        dateShow = sdfDate.format(cal.getTime());
                                        timeShow =sdfTime.format(cal.getTime());
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                Event event = new Event(id, resImage,name,nameTrainer,price, dateShow,timeShow,location,"");
                                listEvent.add(event);

                            }
                            adapter.notifyItemInserted(listEvent.size());
                            isLoading = false;

                        } else {
                            Log.e("responseBodyCode", "" + code);
                        }
                    }else {
                        Log.e("code",response.code()+"");
                    }
                } else {
                    try {
                        Log.e("responseError", response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("errorException", e.toString());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        savedState.putBoolean("isCloseCategories",isClosedCategories);
        savedState.putInt("indexPageGroupEvent", indexPageGroupEvent);
    }


}
