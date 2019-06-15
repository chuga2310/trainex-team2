package com.trainex.fragment.inmain;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.listadapter.CoachAdapter;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.Coach;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class ListCoachFragment extends Fragment {
    private View view;
    private RecyclerView lvCoach;
    private ArrayList<Coach> listCoach= new ArrayList<>();;
    private String title;
    private TextView titleListCoach;
    private ImageView closeCategories;
    private LinearLayout categories;
    private ImageView imgBack;
    public ListCoachFragment() {
        super();
    }
    private ImageView imgFilter;
    private ImageView imgFilterBar;
    private FrameLayout layoutFilter;
    private LinearLayout layoutList;
    private RatingBar ratingBar;
    private boolean isShowingFilter;
    private Spinner spnLocation;
    ArrayList<String> listLocation=new ArrayList<>();;
    private Button btnClear;
    private Button btnFilter;
    private boolean isClosedCategories;
    private Bundle savedState = new Bundle();
    private CoachAdapter adapter;
    private int idCategory;
    private String token;
    private int indexPage;
    private int totalPage;
    private boolean isLoading;
    private View chkIsSort;
    private LinearLayout viewImgBack;
    private SwipeRefreshLayout swipeContainer;

    ArrayAdapter<String> adapterLocation;
    ArrayList<JsonObject> listRating = new ArrayList<>();
    ArrayList<Integer> listFavorite = new ArrayList<>();
    private boolean isSort = false;
    private boolean isFilter = false;
    private int oldPage;
    private TextView tvDescription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        title = getArguments().getString("title");
        idCategory = getArguments().getInt("idCategory");
        indexPage = savedState.getInt("indexPageCategory",1);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        if (indexPage==1){
            getData(indexPage);
        }
        getDataSpinner();
        Log.e("token", token);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_coach,container,false);
        lvCoach = view.findViewById(R.id.lvCoach);
        titleListCoach = view.findViewById(R.id.titleListCoach);
        categories = view.findViewById(R.id.categories);
        closeCategories = view.findViewById(R.id.closeCategories);
        imgBack = view.findViewById(R.id.imgBack);
        imgFilter = view.findViewById(R.id.imgFilter);
        imgFilterBar = (ImageView) view.findViewById(R.id.imgFilterBar);
        layoutFilter = (FrameLayout) view.findViewById(R.id.layoutFilter);
        layoutList = view.findViewById(R.id.layoutList);
        ratingBar =view.findViewById(R.id.ratingBar);
        spnLocation = (Spinner) view.findViewById(R.id.spnLocation);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        btnFilter = (Button) view.findViewById(R.id.btnFilter);
        chkIsSort = (View) view.findViewById(R.id.chkIsSort);
        viewImgBack = (LinearLayout) view.findViewById(R.id.viewImgBack);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        /***** SETTING RATING *****/
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        isShowingFilter = false;
        titleListCoach.setText(title);
        /************************/
        adapterLocation = new ArrayAdapter(getContext(),R.layout.item_spinner,listLocation);
        spnLocation.setAdapter(adapterLocation);
        /************************/

        for (int i= 0; i<listCoach.size();i++){
            if (listCoach.get(i).isFeatured()){
                Coach coach = listCoach.get(i);
                listCoach.remove(i);
                listCoach.add(0,coach);
            }
        }

        switch (title){
            case "Personal Training 1 on 1":{
                tvDescription.setText(getString(R.string.persional_training_1on1));
                break;
            }
            case "Personal Training Online":{
                tvDescription.setText(getString(R.string.persional_training_online));
                break;
            }
            case "EMS 1 on 1":{
                tvDescription.setText(getString(R.string.ems));
                break;
            }
            case "Sports Massage":{
                tvDescription.setText(getString(R.string.sport));
                break;
            }
            default:{
                categories.setVisibility(View.GONE);
                isClosedCategories = true;
            }
        }

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext()){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("probe", "meet a IOOBE in RecyclerView");
                }
            }
        };
        lvCoach.setLayoutManager(layoutManager1);
       adapter = new CoachAdapter(getContext(),listCoach, false);
        lvCoach.setAdapter(adapter);
        lvCoach.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager1=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int visibleItemCount        = layoutManager1.getChildCount();
                int totalItemCount          = layoutManager1.getItemCount();
                int firstVisibleItemPosition= layoutManager1.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager1.findLastVisibleItemPosition();

                // Load more if we have reach the end to the recyclerView
                if ((visibleItemCount + lastVisibleItemPosition) >= (totalItemCount-2) && firstVisibleItemPosition >= 0  && !isLoading) {
                    indexPage++;
                   if (indexPage<=totalPage){
                       if (isFilter){
                           filterByLocation(indexPage);
                       }else{
                           getData(indexPage);
                       }
                   }
                    isLoading=true;
                }
            }
        });
        bind();

        if (savedState!=null){
            isClosedCategories = savedState.getBoolean("isCloseCategories");
            if (isClosedCategories){
                categories.setVisibility(View.GONE);
            }
        }

        closeCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categories.setVisibility(View.GONE);
                isClosedCategories = true;

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState.putBoolean("isCloseCategories",isClosedCategories);
        savedState.putInt("indexPage",indexPage);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void bind(){
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showHomeFromLeft();
                MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilter();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.e("rate",ratingBar.getRating()+"");
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPage = indexPage;
                indexPage = 1;
                filterByLocation(indexPage);
            }
        });
        chkIsSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSort){
                    chkIsSort.setBackground(getResources().getDrawable(R.drawable.checkbox_filter_true));
                    isSort = true;
                }else{
                    chkIsSort.setBackground(getResources().getDrawable(R.drawable.checkbox_filter));
                    isSort = false;
                }
            }
        });
        spnLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isFilter = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listCoach.clear();
                indexPage =1;
                getData(indexPage);
                showFilter();
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listCoach.clear();
                indexPage = 1;
                listFavorite.clear();
                listRating.clear();
                getData(indexPage);
            }
        });
    }
    private void getData(int page){
        String request = "{\n" +
                "  \"session_category_id\": " + "\"" + idCategory + "\"" + "\n" +
                "}";
        Call<JsonElement> callGetLoadListTrainer = RestClient.getApiInterface().getListTrainer(token,NoteRestAPI.stringToRequestBody(request),page);
        callGetLoadListTrainer.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    swipeContainer.setRefreshing(false);
                    int code = response.code();
                    if (code == 200) {
                        Log.e("code",code+"");
                        JsonObject body = response.body().getAsJsonObject();
                        Log.e("body", response.body().toString());
                        if (body.get("code").getAsInt() == 200) {
                            int oldSize = listCoach.size();
                            if (isFilter){
                                listCoach.clear();
                            }
                            JsonArray trainer_list = body.get("data").getAsJsonObject().get("trainer_list").getAsJsonObject().get("data").getAsJsonArray();
                            JsonArray traier_rating = body.get("data").getAsJsonObject().get("review").getAsJsonArray();
                            if (body.get("data").getAsJsonObject().has("favorite")){
                                JsonArray trainer_favorite = body.get("data").getAsJsonObject().get("favorite").getAsJsonArray();
                                if (trainer_favorite.size()>0){
                                    for (JsonElement x: trainer_favorite){
                                        listFavorite.add(x.getAsInt());
                                    }
                                }
                            }
                            if (traier_rating.size()>0){
                                for (JsonElement x: traier_rating){
                                    listRating.add(x.getAsJsonObject());
                                }
                            }
                            totalPage=body.get("data").getAsJsonObject().get("trainer_list").getAsJsonObject().get("last_page").getAsInt();
                            for (int i = 0; i<trainer_list.size();i++){
                                int id  = trainer_list.get(i).getAsJsonObject().get("trainer_id").getAsInt();
                                String name ="";
                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_name").isJsonNull()){
                                    name = trainer_list.get(i).getAsJsonObject().get("trainer_name").getAsString();
                                }
                                String resAvatar="";
                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_avatar").isJsonNull()){
                                    resAvatar = trainer_list.get(i).getAsJsonObject().get("trainer_avatar").getAsString();
                                }
                                String location="";

                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_address").isJsonNull()){
                                    location = trainer_list.get(i).getAsJsonObject().get("trainer_address").getAsString();
                                }
                                boolean isFeature=false;
                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_feature").isJsonNull()){
                                    isFeature = (trainer_list.get(i).getAsJsonObject().get("trainer_feature").getAsInt() ==1);
                                }
                                float rating = 0;
                                for (JsonObject x:listRating){
                                    if (x.has(id+"")){
                                        rating = x.get(id+"").getAsFloat();
                                    }
                                }
                                boolean isFavorite = false;
                                for (float x:listFavorite){
                                    if (x == id){
                                        isFavorite = true;
                                    }
                                }
                                int idCategory = trainer_list.get(i).getAsJsonObject().get("session_category_id").getAsInt();
                                Coach coach = new Coach(id,name,location, rating,isFavorite, isFeature, resAvatar, idCategory);
                                listCoach.add(coach);

                            }
                            adapter.notifyDataSetChanged();
                            Log.e("listCoach", listCoach.size()+"");
                            isLoading = false;
                        } else {
                            Log.e("responseBodyCode", "" + body.get("code").getAsInt()+"");
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
    private void showFilter(){
        if (isShowingFilter){
            imgFilterBar.setVisibility(View.INVISIBLE);
            layoutFilter.setVisibility(View.INVISIBLE);
            layoutList.setVisibility(View.VISIBLE);
            isShowingFilter = false;
            Log.d("checkFilter", "123");
        }else{
            imgFilterBar.setVisibility(View.VISIBLE);
            layoutFilter.setVisibility(View.VISIBLE);
            layoutList.setVisibility(View.INVISIBLE);

            isShowingFilter = true;
            Log.d("checkFilter", "size: " +listLocation.size());
        }
    }
    private void getDataSpinner(){
        /***** SETTING SPINNER *****/
        listLocation.clear();
        Call<JsonElement> callGetListLocation=  RestClient.getApiInterface().getListLocations();
        callGetListLocation.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body().getAsJsonObject();
                    if (body.get("code").getAsInt()==200){
                        JsonArray data = body.get("data").getAsJsonArray();
                        for (int i = 0; i< data.size();i++){
                            String name = data.get(i).getAsJsonObject().get("name").getAsString().trim();
                            listLocation.add(name);

                        }
                        adapterLocation.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void filterByLocation(int page){

        Call<JsonElement> callFilterTrainer = RestClient.getApiInterface().filterTrainer(idCategory, listLocation.get(spnLocation.getSelectedItemPosition()), page, isSort);
        callFilterTrainer.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    int oldSize = listCoach.size();

                    int code  = response.code();
                    if (code == 200) {
                        Log.e("code",code+"");
                        JsonObject body = response.body().getAsJsonObject();
                        Log.e("body", response.body().toString());
                        if (body.get("code").getAsInt() == 200) {
                            listCoach.clear();
                            JsonArray trainer_list = body.get("data").getAsJsonObject().get("trainer_list").getAsJsonObject().get("data").getAsJsonArray();
                            JsonArray traier_rating = body.get("data").getAsJsonObject().get("review").getAsJsonArray();
                            if (body.get("data").getAsJsonObject().has("favorite")){
                                JsonArray trainer_favorite = body.get("data").getAsJsonObject().get("favorite").getAsJsonArray();
                                if (trainer_favorite.size()>0){
                                    for (JsonElement x: trainer_favorite){
                                        listFavorite.add(x.getAsInt());
                                    }
                                }
                            }
                            if (traier_rating.size()>0){
                                for (JsonElement x: traier_rating){
                                    listRating.add(x.getAsJsonObject());
                                }
                            }
                            totalPage=body.get("data").getAsJsonObject().get("trainer_list").getAsJsonObject().get("last_page").getAsInt();
                            for (int i = 0; i<trainer_list.size();i++){
                                int id  = trainer_list.get(i).getAsJsonObject().get("trainer_id").getAsInt();
                                String name ="";
                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_name").isJsonNull()){
                                    name = trainer_list.get(i).getAsJsonObject().get("trainer_name").getAsString();
                                }
                                String resAvatar = trainer_list.get(i).getAsJsonObject().get("trainer_avatar").getAsString();
                                String location="";

                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_address").isJsonNull()){
                                    location = trainer_list.get(i).getAsJsonObject().get("trainer_address").getAsString();
                                }
                                boolean isFeature=false;
//                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_feature").isJsonNull()){
//                                    isFeature = (trainer_list.get(i).getAsJsonObject().get("trainer_feature").getAsInt() ==1);
//                                }
                                float rating = 0;
                                for (JsonObject x:listRating){
                                    if (x.has(id+"")){
                                        rating = x.get(id+"").getAsFloat();
                                    }
                                }
                                boolean isFavorite = false;
                                for (float x:listFavorite){
                                    if (x == id){
                                        isFavorite = true;
                                    }
                                }
                                int idCategory = trainer_list.get(i).getAsJsonObject().get("session_category_id").getAsInt();
                                String preLocation = "";
                                if (!trainer_list.get(i).getAsJsonObject().get("trainer_preffered_locations").isJsonNull()){
                                    preLocation =trainer_list.get(i).getAsJsonObject().get("trainer_preffered_locations").getAsString();
                                }
                                Coach coach = new Coach(id,name,"",location,preLocation,"","","", rating,isFavorite, isFeature, resAvatar, idCategory);
                                listCoach.add(coach);
                            }
                            isLoading = false;
                            isFilter = true;
                            Log.e("listCoach", listCoach.size()+"");
                            adapter.notifyItemRangeRemoved(0,oldSize);
                            adapter.notifyItemRangeInserted(0, listCoach.size());
                            showFilter();
                        } else if(body.get("code").getAsInt()==400){
                           CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Alert!","No trainers found in this location", "Ok") {
                               @Override
                               public void doSomethingWhenDismiss() {

                               }
                           };
                           alertDialog.show();
                           indexPage = oldPage;
                        }
                    }else {
                        Log.e("code",response.code()+"");
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
