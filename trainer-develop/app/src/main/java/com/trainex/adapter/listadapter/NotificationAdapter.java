package com.trainex.adapter.listadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.trainex.R;
import com.trainex.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.RecyclerViewHolder> {
    private ArrayList<Notification> listNotification;
    private Context context;

    public NotificationAdapter(Context context,ArrayList<Notification> listNotification) {
        this.listNotification = listNotification;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_notification, viewGroup, false);
        NotificationAdapter.RecyclerViewHolder viewHolder = new NotificationAdapter.RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        recyclerViewHolder.tvContent.setText(listNotification.get(i).getContent());
        recyclerViewHolder.tvTrainer.setText(listNotification.get(i).getTrainer());
        recyclerViewHolder.tvDate.setText(listNotification.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return listNotification.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView tvContent;
        private TextView tvTrainer;
        private TextView tvDate;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvTrainer = (TextView) itemView.findViewById(R.id.tvTrainer);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
