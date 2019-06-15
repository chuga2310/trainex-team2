package com.trainex.uis.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.fragment.inmain.DetailTrainerFragment;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;

public class AddReviewActivity extends AppCompatActivity {
    private ImageView imgBack;
    private RatingBar ratingBar;
    private EditText edtReviews;
    private Button btnSubmit;
    private String token;
    private int idTrainer;
    private LinearLayout viewImgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        idTrainer = getIntent().getIntExtra("idTrainer", -1);

        init();
        bind();
        ratingBar.setRating(5);
    }

    private void init() {
        imgBack = (ImageView) findViewById(R.id.imgBack);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        edtReviews = (EditText) findViewById(R.id.edtReviews);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        viewImgBack = (LinearLayout) findViewById(R.id.viewImgBack);

    }

    private void bind() {
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnSubmit.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                final float rating = ratingBar.getRating();
                final String comment = edtReviews.getText().toString().replace("\n", " ");
                if(!comment.equals("")){
                    Log.e("comment", comment);
                    String request = "{\n" +
                            "  \"rating\": "+ "\"" + rating  + "\""+ ",\n" +
                            "  \"trainer_id\": " + idTrainer + ",\n" +
                            "  \"comment\":" + "\"" + comment + "\"" + "\n" +
                            "}";
                    Log.d("ratingBar", rating+"");
                    Call<JsonElement> callSendReviews = RestClient.getApiInterface().sendReview(token, NoteRestAPI.stringToRequestBody(request));
                    callSendReviews.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                JsonObject body = response.body().getAsJsonObject();
                                int codeBody = body.get("code").getAsInt();
                                if (codeBody == 200){
                                    Log.e("body", body.toString());
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("isReload",true);
                                    setResult(RESULT_OK,returnIntent);
                                    finish();
                                } else{
                                    String s = "";
                                    if (body.has("error")){
                                        s = body.get("error").getAsString();
                                    }
                                    if (body.has("message")){
                                        s = body.get("message").getAsString();
                                    }
                                    CustomAlertDialog alertDialog = new CustomAlertDialog(AddReviewActivity.this,"Review Error!",s,"Ok") {
                                        @Override
                                        public void doSomethingWhenDismiss() {
                                            finish();
                                        }
                                    };
                                    alertDialog.show();
                                }


                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Toast.makeText(AddReviewActivity.this, "Load failed!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Toast.makeText(AddReviewActivity.this, "Please write a review", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
