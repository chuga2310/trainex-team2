package com.trainex.uis.main;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.listadapter.ReviewRecyclerAdapter;
import com.trainex.model.Review;
import com.trainex.rest.RestClient;

public class ReviewsActivity extends AppCompatActivity {
    private ImageView imgBack;
    private TextView title;
    private RecyclerView lvReviews;
private ReviewRecyclerAdapter recyclerAdapter;
private ArrayList<Review> listReview;
private  int idTrainer;
private int indexPage=1;
private int totalPage;
private boolean isLoading = false;
    private LinearLayout viewImgBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        idTrainer= getIntent().getIntExtra("idTrainer", -1);
        init();
        bind();
    }
    private void init(){
        imgBack = (ImageView) findViewById(R.id.imgBack);
        title = (TextView) findViewById(R.id.title);
        lvReviews = (RecyclerView) findViewById(R.id.lvReviews);
        viewImgBack = (LinearLayout) findViewById(R.id.viewImgBack);

    }
    private void bind(){
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        listReview = new ArrayList<>();
        recyclerAdapter = new ReviewRecyclerAdapter(ReviewsActivity.this, listReview);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(ReviewsActivity.this){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("probe", "meet a IOOBE in RecyclerView");
                }
            }
        };
        lvReviews.setLayoutManager(layoutManager1);
        lvReviews.setAdapter(recyclerAdapter);
        lvReviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getReview(indexPage);
                    }
                    isLoading=true;
                }
            }
        });
        getReview(indexPage);
    }
    private  void getReview(int page){
        Call<JsonElement> callGetReview = RestClient.getApiInterface().getReviews(idTrainer, page);
        callGetReview.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body().getAsJsonObject();
                    int codeBody = body.get("code").getAsInt();
                    if (codeBody == 200){
                        JsonObject data = body.get("data").getAsJsonObject();
                        JsonArray dataReviews = data.get("data").getAsJsonArray();
                        if (dataReviews.size()>0){
                            for (JsonElement x:dataReviews){
                                JsonObject reviewObject = x.getAsJsonObject();
                                String name = "";
                                if (!reviewObject.get("username").isJsonNull()){
                                    name = reviewObject.get("username").getAsString();
                                }
                                int idUser = -1;
                                if (!reviewObject.get("user_id").isJsonNull()){
                                    idUser = reviewObject.get("user_id").getAsInt();
                                }
                                float rating = 0;
                                if (!reviewObject.get("rating").isJsonNull()){
                                    rating = reviewObject.get("rating").getAsFloat();
                                }
                                String comment = "";
                                if (!reviewObject.get("comment").isJsonNull()){
                                    comment = reviewObject.get("comment").getAsString();
                                }
                                String resAvatar = "";
                                if (!reviewObject.get("avatar").isJsonNull()){
                                    resAvatar = reviewObject.get("avatar").getAsString();
                                }
                                String time = "";
                                if (!reviewObject.get("created_at").isJsonNull()){
                                    time = reviewObject.get("created_at").getAsString();
                                }
                                Review review = new Review(idUser,name,rating, comment,resAvatar, time);
                                isLoading= false;
                                listReview.add(review);
                                recyclerAdapter.notifyItemInserted(listReview.size());
                            }
                        }
                        totalPage = data.get("last_page").getAsInt();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(ReviewsActivity.this, "Load Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
