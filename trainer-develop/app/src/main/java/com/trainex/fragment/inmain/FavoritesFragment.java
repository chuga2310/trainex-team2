package com.trainex.fragment.inmain;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.listadapter.CoachAdapter;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.Coach;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class FavoritesFragment extends Fragment {

    private RecyclerView lvFavorites;
    private View view;
    private ArrayList<Coach> listCoach;
    private CoachAdapter adapter;
    private String token;
    private SwipeRefreshLayout swipeContainer;
    private ImageView imgShowDrawer;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listCoach = new ArrayList<>();
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container,false);
        init();
        bind();

        return view;
    }
    private void init(){
        lvFavorites = (RecyclerView) view.findViewById(R.id.lvFavorites);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        imgShowDrawer = (ImageView) view.findViewById(R.id.imgShowDrawer);
    }
    private void bind(){
        imgShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showMenu();
            }
        });
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        lvFavorites.setLayoutManager(layoutManager1);
        adapter = new CoachAdapter(getContext(),listCoach, true);
        lvFavorites.setAdapter(adapter);
//        lvFavorites.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager1=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
//                int visibleItemCount        = layoutManager1.getChildCount();
//                int totalItemCount          = layoutManager1.getItemCount();
//                int firstVisibleItemPosition= layoutManager1.findFirstVisibleItemPosition();
//
//                // Load more if we have reach the end to the recyclerView
//                if ( (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
//                    fakeData();
//                    adapter.notifyItemInserted(listCoach.size()-1);
//                }
//            }
//        });



        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listCoach.clear();
                getData();
            }
        });
    }
    private void getData(){
        if (!token.equalsIgnoreCase("")){
            Call<JsonElement> callGetListFavorite= RestClient.getApiInterface().getFavoritesTrainers(token);
            callGetListFavorite.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    swipeContainer.setRefreshing(false);
                    if (response.isSuccessful()){
                        JsonObject body = response.body().getAsJsonObject();
                       if (body.get("code").getAsInt()==200){
                           JsonArray responseList = body.get("data").getAsJsonObject().get("trainer_list").getAsJsonArray();
                           JsonArray listRating = body.get("data").getAsJsonObject().get("review").getAsJsonArray();
                           if (responseList.size()>0){
                               for (int i = 0; i< responseList.size(); i++){
                                   int idTrainer =  responseList.get(i).getAsJsonObject().get("trainer_id").getAsInt();
                                   String nameTrainer="";
                                   if (!responseList.get(i).getAsJsonObject().get("trainer_name").isJsonNull()){
                                       nameTrainer = responseList.get(i).getAsJsonObject().get("trainer_name").getAsString();
                                   }
                                   boolean isFeature=false;
                                   if (!responseList.get(i).getAsJsonObject().get("trainer_feature").isJsonNull()){
                                       if (responseList.get(i).getAsJsonObject().get("trainer_feature").getAsInt()!=0){
                                            isFeature = true;
                                       }
                                   }
                                   String location="";
                                   if (!responseList.get(i).getAsJsonObject().get("trainer_address").isJsonNull()){
                                       location = responseList.get(i).getAsJsonObject().get("trainer_address").getAsString();
                                   }
                                   float rating = 0;
                                   String resAvatar="";
                                   if (!responseList.get(i).getAsJsonObject().get("trainer_avatar").isJsonNull()){
                                       resAvatar = responseList.get(i).getAsJsonObject().get("trainer_avatar").getAsString();
                                   }
                                   for (JsonElement x:listRating){
                                       if (x.getAsJsonObject().has(idTrainer+"")){
                                           rating = x.getAsJsonObject().get(idTrainer+"").getAsFloat();
                                       }
                                   }
                                   Coach coach = new Coach(idTrainer, nameTrainer,location, rating,true,isFeature,resAvatar);
                                   listCoach.add(coach);

                                   adapter.notifyDataSetChanged();
                               }
                           }else if (body.get("code").getAsInt()==400){
                               CustomAlertDialog alertDialog =  new CustomAlertDialog(getContext(), "Something wrong!","You don't favorite any one", "Ok") {
                                   @Override
                                   public void doSomethingWhenDismiss() {

                                   }
                               };
                               alertDialog.show();
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
                    Toast.makeText(getContext(), "Lofad ailed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
