package com.trainex.adapter.listadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.trainex.R;
import com.trainex.model.Coach;
import com.trainex.model.Review;

public class ReviewsAdapter extends ArrayAdapter<Review> {
    private ArrayList<Review> listReviews;
    private Context context;
    private ImageView imgAvatar;
    private TextView tvAbout;
    private RatingBar ratingBar;
    private TextView tvName;
    private TextView tvTime;



    public ReviewsAdapter(@NonNull Context context, @NonNull ArrayList<Review> listReviews) {
        super(context, R.layout.item_reviews_trainer_in_details, listReviews);
        this.context = context;
        this.listReviews = listReviews;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reviews_trainer_in_details, parent,false);
        imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        tvAbout = (TextView) view.findViewById(R.id.tvAboutReview);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvTime = (TextView) view.findViewById(R.id.tvTimeReview);

        ratingBar.setRating(listReviews.get(position).getRating());
        Glide.with(context).load(listReviews.get(position).getResAvatar()).into(imgAvatar);
        tvAbout.setText(listReviews.get(position).getComment());
        tvName.setText(listReviews.get(position).getUserName());
        String time ="";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            cal.setTime(sdf.parse(listReviews.get(position).getTime()));

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd - MMM'' yy", Locale.ENGLISH);

            if (cal != null) {
                time = sdfDate.format(cal.getTime());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        tvTime.setText(time);

        return view;
    }
}
