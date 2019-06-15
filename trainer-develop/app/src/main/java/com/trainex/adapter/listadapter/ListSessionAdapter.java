package com.trainex.adapter.listadapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.trainex.R;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.model.Session;
import com.trainex.uis.main.PaymenttActivity;
import com.trainex.uis.main.RequestFreeSessionActivity;

public class ListSessionAdapter extends ArrayAdapter<Session> {
    private Context context;
    private List<Session> listSession;
    private TextView tvTypeSession;
    private TextView tvPrice;
    private Button btnBuyNow;
    private String token;
    public ListSessionAdapter(@NonNull Context context, List<Session> listSession) {
        super(context, R.layout.item_list_sessions, listSession);
        this.context = context;
        this.listSession = listSession;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_sessions,parent,false);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        tvTypeSession = (TextView) view.findViewById(R.id.tvTypeSession);
        tvPrice = (TextView) view.findViewById(R.id.tvPriceSession);
        btnBuyNow = (Button) view.findViewById(R.id.btnBuyNow);

        final Session session = new Session(listSession.get(position).getIdSession(),listSession.get(position).getPrice(),listSession.get(position).getTypeSession());

        String textSession = session.getTypeSession().replace("_", " ");
        tvTypeSession.setText(textSession);

        tvPrice.setText(session.getPrice()+" AED");

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (token.equalsIgnoreCase("")){
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(getContext(), "Error", "You must login to but this!");
                    alertLoginDialog.show();
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("session", session);
                    bundle.putString("bookingType", "session");
                    Intent intent = new Intent(getContext(), PaymenttActivity.class);
                    intent.putExtra("title","Buy A Session");
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return listSession.size();
    }
}
