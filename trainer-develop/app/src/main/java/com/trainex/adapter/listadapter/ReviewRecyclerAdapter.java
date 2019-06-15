package com.trainex.adapter.listadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.trainex.R;
import com.trainex.model.Review;

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.RecyclerViewHolder> {
    private Context context;
    private ArrayList<Review> listReview;

    public ReviewRecyclerAdapter(Context context, ArrayList<Review> listReview) {
        this.context = context;
        this.listReview = listReview;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_reviews, viewGroup, false);
        ReviewRecyclerAdapter.RecyclerViewHolder viewHolder = new ReviewRecyclerAdapter.RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        recyclerViewHolder.ratingBar.setRating(listReview.get(i).getRating());
        Glide.with(context).load(listReview.get(i).getResAvatar()).into(recyclerViewHolder.imgAvatar);
        recyclerViewHolder.tvAbout.setText(listReview.get(i).getComment());
        recyclerViewHolder.tvName.setText(listReview.get(i).getUserName());
        String time ="";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            cal.setTime(sdf.parse(listReview.get(i).getTime()));

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd - MMM'' yy");

            if (cal != null) {
                time = sdfDate.format(cal.getTime());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        recyclerViewHolder.tvTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return listReview.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgAvatar;
        private TextView tvAbout;
        private RatingBar ratingBar;
        private TextView tvName;
        private TextView tvTime;
        public RecyclerViewHolder(@NonNull View view) {
            super(view);
            imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
            tvAbout = (TextView) view.findViewById(R.id.tvAboutReview);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvTime = (TextView) view.findViewById(R.id.tvTimeReview);
        }
    }
}
