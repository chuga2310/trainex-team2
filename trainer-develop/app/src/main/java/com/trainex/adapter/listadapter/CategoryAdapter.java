package com.trainex.adapter.listadapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.trainex.R;
import com.trainex.fragment.inmain.ListCoachFragment;
import com.trainex.model.Category;
import com.trainex.uis.main.MainActivity;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.RecyclerViewHolder> {
    private Context context;
    private ArrayList<Category> listCategory;

    public CategoryAdapter(Context context, ArrayList<Category> listCategory) {
        this.context = context;
        this.listCategory = listCategory;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list_category, viewGroup, false);

        CategoryAdapter.RecyclerViewHolder viewHolder = new CategoryAdapter.RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, final int i) {
        Glide.with(context)
                .load(listCategory.get(i).getResImage())
                .into(recyclerViewHolder.imgCategory);
        recyclerViewHolder.tvName.setText(listCategory.get(i).getName());
//        if (i % 2 == 0) {
//            CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
//            layoutParams.setMargins(0,8, 8,8);
//            recyclerViewHolder.training1on1.setLayoutParams(layoutParams);
//        }else{
//            CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
//            layoutParams.setMargins(8,8, 0,8);
//            recyclerViewHolder.training1on1.setLayoutParams(layoutParams);
//        }
        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListCoachFragment listCoachFragment = new ListCoachFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("idCategory", listCategory.get(i).getId());
                MainActivity.showListTrainer(listCoachFragment,listCategory.get(i).getName(),listCategory.get(i).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CardView training1on1;
        private ImageView imgCategory;
        private TextView tvName;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            training1on1 = (CardView) itemView.findViewById(R.id.training1on1);
            imgCategory = (ImageView) itemView.findViewById(R.id.imgCategory);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}
