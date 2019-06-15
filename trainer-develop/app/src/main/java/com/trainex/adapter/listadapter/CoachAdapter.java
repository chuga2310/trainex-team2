package com.trainex.adapter.listadapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.Coach;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;
import com.trainex.uis.main.ReportListActivity;

public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.RecyclerViewHolder> {
    private ArrayList<Coach> listCoach;
    private Context context;
    private String token;
    private boolean isInFavorite = false;

    public CoachAdapter(Context context,ArrayList<Coach> listCoach,boolean isInFavorite) {
        this.listCoach = listCoach;
        this.context = context;
        this.isInFavorite = isInFavorite;
        SharedPreferences prefs = context.getSharedPreferences("MY_SHARE_PREFERENCE", context.MODE_PRIVATE);
        token = prefs.getString("token", "");
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list_coach, viewGroup, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, final int position) {
        recyclerViewHolder.tvName.setText(listCoach.get(position).getName());
        recyclerViewHolder.tvLocation.setText(listCoach.get(position).getLocation());
        if (listCoach.get(position).isFavorites()){
            recyclerViewHolder. imgFavorites.setImageResource(R.mipmap.icon_heart_active);
        }else{
            recyclerViewHolder.imgFavorites.setImageResource(R.mipmap.icon_heart_deactive);
        }
        if (listCoach.get(position).isFeatured()){
            recyclerViewHolder.imgFeatured.setVisibility(View.VISIBLE);
        }else{
            recyclerViewHolder.imgFeatured.setVisibility(View.INVISIBLE);
        }
        recyclerViewHolder.rating.setStepSize(0.5f);
        recyclerViewHolder.rating.setRating(listCoach.get(position).getRating());

//        Bitmap bitmapResource =  BitmapFactory.decodeResource(context.getResources(), listCoach.get(position).getResAvatar());
        Glide.with(context).load(listCoach.get(position).getResAvatar()).into(recyclerViewHolder.imgCoachAvatar);

        recyclerViewHolder.imgFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!token.equalsIgnoreCase("")){
                    Call<JsonElement> callFavorite = RestClient.getApiInterface().favorites(token,listCoach.get(position).getId());
                    callFavorite.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            Log.d("code", response.code()+"");
                            if (response.isSuccessful()){
                                JsonObject body = response.body().getAsJsonObject();
                                int codeBody = body.get("code").getAsInt();
                                if (codeBody ==11){
                                    recyclerViewHolder.imgFavorites.setImageResource(R.mipmap.icon_heart_active);
                                    listCoach.get(position).setFavorites(true);
                                }else if (codeBody ==12){
                                    recyclerViewHolder.imgFavorites.setImageResource(R.mipmap.icon_heart_deactive);
                                    listCoach.get(position).setFavorites(false);
                                    if (isInFavorite){
                                        listCoach.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }else{
                                    String s = "";
                                    if (body.has("error")){
                                        s = body.get("error").getAsString();
                                    }
                                    if (body.has("message")){
                                        s = body.get("message").getAsString();
                                    }
                                    CustomAlertDialog alertDialog = new CustomAlertDialog(context,"Error!",s,"Ok") {
                                        @Override
                                        public void doSomethingWhenDismiss() {
                                        }
                                    };
                                    alertDialog.show();
                                }

                            }else{
                                try {
                                    Log.e("errorBody", response.errorBody().string());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {

                        }
                    });
                }else{
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(context, "Favorite Error!","You must login to favorite");
                    alertLoginDialog.show();
                }
            }
        });
        recyclerViewHolder.imgReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (token.equalsIgnoreCase("")){
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(context, "Error", "You must login to report!");
                    alertLoginDialog.show();
                }else{
                    Intent intent = new Intent(context, ReportListActivity.class);
                    intent.putExtra("idTrainer", listCoach.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });
        recyclerViewHolder.btnViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showDetailTrainer(listCoach.get(position).getId(), listCoach.get(position).getIdCategory());
                if (isInFavorite){
                    MainActivity.isInDetailFavorite = true;
                }else{
                    MainActivity.isInDetailFavorite = false;
                }
            }
        });
        recyclerViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showDetailTrainer(listCoach.get(position).getId(), listCoach.get(position).getIdCategory());
                if (isInFavorite){
                    MainActivity.isInDetailFavorite = true;
                }else{
                    MainActivity.isInDetailFavorite = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCoach.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private RoundedImageView imgCoachAvatar;
        private ImageView imgFavorites;
        private TextView tvName;
        private TextView tvLocation;
        private ImageView imgFeatured;
        private RatingBar rating;
        private Button btnViewDetail;
        private ImageButton imgReports;
        private LinearLayout layout;


        public RecyclerViewHolder(@NonNull View view) {
            super(view);
            imgCoachAvatar = (RoundedImageView) view.findViewById(R.id.imgCoachAvatar);
            imgFavorites = (ImageView) view.findViewById(R.id.imgFavorites);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            imgFeatured = (ImageView) view.findViewById(R.id.imgFeatured);
            rating = (RatingBar) view.findViewById(R.id.rating);
            btnViewDetail = (Button) view.findViewById(R.id.btnViewDetail);
            imgReports = (ImageButton) view.findViewById(R.id.imgReports);
            layout = (LinearLayout) view.findViewById(R.id.layout);
        }
    }

}
