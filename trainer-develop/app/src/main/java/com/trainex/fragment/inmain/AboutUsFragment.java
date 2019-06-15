package com.trainex.fragment.inmain;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class AboutUsFragment extends Fragment {
    ImageView imgShowDrawer;
    View view;
    private TextView tvAbout;
    private String aboutUs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Call<JsonElement> callGetAbout = RestClient.getApiInterface().getAboutUs();
        callGetAbout.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    JsonObject data = response.body().getAsJsonObject().get("data").getAsJsonObject();
                    aboutUs = data.get("about_us").getAsString();
                    tvAbout.setText(aboutUs);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_us,container,false);
        init();
        bind();

        return view;
    }
    private void init(){
        imgShowDrawer = view.findViewById(R.id.imgShowDrawer);
        tvAbout = (TextView) view.findViewById(R.id.tvAbout);

    }
    private void bind(){
        imgShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showMenu();
            }
        });



    }
}
