package com.trainex.uis.login_signup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class StartActivity extends Activity {
    private ImageView imgStartLogo;
    private LinearLayout linearBottom;
    private Button btnStartSignin;
    private Button btnStartJoin;
    private static final int REQUEST_CODE_INTERNET = 23;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return;
        }
        setContentView(R.layout.activity_start);
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.trainex", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        requestPerMissionRealTime(Manifest.permission.INTERNET, REQUEST_CODE_INTERNET);

        //Setup full screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //bind view
        imgStartLogo = findViewById(R.id.imgStartLogo);
        imgStartLogo = (ImageView) findViewById(R.id.imgStartLogo);
        linearBottom = (LinearLayout) findViewById(R.id.linearBottom);
        btnStartSignin = (Button) findViewById(R.id.btnStartSignin);
        btnStartJoin = (Button) findViewById(R.id.btnStartJoin);
        linearBottom.setVisibility(View.INVISIBLE);
        //set animation

        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        if (!token.equalsIgnoreCase("")){
            Call<JsonElement> checkToken  = RestClient.getApiInterface().getUserInfo(token);
            checkToken.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        int code = response.code();
                        JsonObject body = response.body().getAsJsonObject();
                        if (body.get("code").getAsInt()==200){
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            SharedPreferences.Editor editor = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE).edit();
                            editor.putString("token", token.trim());
                            editor.apply();
                            callGetPhone();
                            startActivity(intent);
                        }else{
                            callGetPhone();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    Animation animLogo = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_logo);
                                    imgStartLogo.startAnimation(animLogo);
                                    Animation animButton =AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottom_button);
                                    linearBottom.startAnimation(animButton);
                                }
                            }, 2000);
                        }
                    }else{
                        callGetPhone();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Animation animLogo = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_logo);
                                imgStartLogo.startAnimation(animLogo);
                                Animation animButton =AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottom_button);
                                linearBottom.startAnimation(animButton);
                            }
                        }, 2000);
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Toast.makeText(StartActivity.this, "Load failed", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            callGetPhone();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Animation animLogo = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_logo);
                    imgStartLogo.startAnimation(animLogo);
                    Animation animButton =AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottom_button);
                    linearBottom.startAnimation(animButton);
                }
            }, 2000);
        }
        init();

    }
    private void init(){
        btnStartSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });
        btnStartJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, SignUpActivity.class));
            }
        });
    }
    private void requestPerMissionRealTime(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this,Manifest.permission.INTERNET)) {
                Toast.makeText(StartActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(StartActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(StartActivity.this,new String[]{Manifest.permission.INTERNET}, REQUEST_CODE_INTERNET);
        } else {
            if (requestCode == REQUEST_CODE_INTERNET) {
                //TODO when opened internet
            }
        }
    }
    private  void callGetPhone(){
        Call<JsonElement> getPhone = RestClient.getApiInterface().getPhone();
        getPhone.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    int code = response.body().getAsJsonObject().get("code").getAsInt();
                    if (code == 200){
                        String phone = response.body().getAsJsonObject().get("data")
                                .getAsJsonObject().get("phone_number").getAsString();
                        SharedPreferences.Editor editor = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE).edit();
                        editor.putString("phone_number",phone.trim());
                        editor.apply();
                    }else{
                        String phone = response.body().getAsJsonObject().get("data")
                                .getAsJsonObject().get("phone_number").getAsString();
                        SharedPreferences.Editor editor = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE).edit();
                        editor.putString("phone_number","");
                        editor.apply();
                    }
                }else{
                    String phone = response.body().getAsJsonObject().get("data")
                            .getAsJsonObject().get("phone_number").getAsString();
                    SharedPreferences.Editor editor = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE).edit();
                    editor.putString("phone_number","");
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(StartActivity.this, "Load failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            //Internet
            if (requestCode == REQUEST_CODE_INTERNET) {
                Toast.makeText(this, "Turn on Internet", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }


    }
}
