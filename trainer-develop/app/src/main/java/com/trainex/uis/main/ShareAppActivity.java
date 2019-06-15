package com.trainex.uis.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.JsonElement;

import java.io.File;
import java.nio.Buffer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.rest.RestClient;

public class ShareAppActivity extends AppCompatActivity {
    private RelativeLayout layoutToolbar;
    private ImageView imgBack;
    private ImageView imgShareInsta;
    private ImageView imgShareFacebook;
    private String token;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private LinearLayout viewImgBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_app);
        callbackManager = CallbackManager.Factory.create();
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        init();
        bind();
    }
    private void init(){
        layoutToolbar = (RelativeLayout) findViewById(R.id.layout_toolbar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgShareInsta = (ImageView) findViewById(R.id.imgShareInsta);
        imgShareFacebook = (ImageView) findViewById(R.id.imgShareFacebook);
        viewImgBack = (LinearLayout) findViewById(R.id.viewImgBack);

    }
    private void bind(){
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgShareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDialog = new ShareDialog(ShareAppActivity.this);
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.VJsoft.chayanvang"))
                        .build();
                shareDialog.show(ShareAppActivity.this, content);
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(ShareAppActivity.this, "Share success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(ShareAppActivity.this, "Share error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        imgShareInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createInstagramIntent();


            }
        });
    }
    private void createInstagramIntent(){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType("text/plain");

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.VJsoft.chayanvang");
        share.setPackage("com.instagram.android");
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
