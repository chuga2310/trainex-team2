package com.trainex.diaglog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.rest.RestClient;
import com.trainex.uis.login_signup.LoginActivity;
import com.trainex.uis.main.MainActivity;

public class AlertLoginDialog extends BaseDialog {
    private String title, content, textButton;
    private TextView tvDlgAlertTitle,tvDlgAleatContent;
    private Button btnLogin, btnLater;
    private String textBtnOk, textBtnCancel;
    String token;
    public AlertLoginDialog(@NonNull Context context, String title, String content) {
        super(context);
        this.title =title;
        this.content =content;
    }

    public AlertLoginDialog(@NonNull Context context, String title, String content, String textBtnOk, String textBtnCancel) {
        super(context);
        this.title =title;
        this.content =content;
        this.textBtnOk = textBtnOk;
        this.textBtnCancel = textBtnCancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
    }

    @Override
    int getLayoutResID() {
        return R.layout.dialog_alert_login;
    }

    @Override
    public void bind() {
        tvDlgAlertTitle = findViewById(R.id.tvDlgAlertTitle);
        tvDlgAleatContent = findViewById(R.id.tvDlgAleatContent);
        btnLogin = findViewById(R.id.btnLogin);
        btnLater = findViewById(R.id.btnLater);
        if (textBtnOk!=null){
            btnLogin.setText(textBtnOk);
        }
        if (textBtnCancel!=null){
            btnLater.setText(textBtnCancel);
        }
    }

    @Override
    public void init() {
        tvDlgAlertTitle.setText(this.title);
        tvDlgAleatContent.setText(this.content);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textBtnOk == "Logout"){
                    dismiss();
                    final CustomProgressDialog dialog = new CustomProgressDialog(getContext());
                    dialog.show();
                    Call<JsonElement> callLogout = RestClient.getApiInterface().callLogout(token);
                    callLogout.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            if (response.isSuccessful()){
                                int code = response.code();
                                if(code == 200){
                                    JsonObject body = response.body().getAsJsonObject();
                                    int codeBody = body.get("code").getAsInt();
                                    if(codeBody == 200){

                                        dialog.dismiss();
                                        Intent intent = new Intent(getContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getContext().startActivity(intent);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Toast.makeText(getContext(), "Load Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            }
        });
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
