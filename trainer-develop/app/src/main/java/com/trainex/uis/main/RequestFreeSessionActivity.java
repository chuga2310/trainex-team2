package com.trainex.uis.main;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;

public class RequestFreeSessionActivity extends AppCompatActivity {
    private ImageButton imgClose;
    private EditText edtDate;
    private Spinner spnHour;
    private Spinner spnMinute;
    private Spinner spn24h;
    private Button btnSubmit;
    private ArrayList<String> listHour;
    private ArrayList<String> listMinute;
    private ArrayList<String> listIs24h;
    private String token;
    private CustomProgressDialog progressDialog;
    private String stringDate, stringTime;
    private int idTrainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_free_session);
        progressDialog = new CustomProgressDialog(RequestFreeSessionActivity.this);
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        idTrainer = getIntent().getIntExtra("idTrainer", 0);
        init();
        bind();

    }

    private void init() {
        imgClose = (ImageButton) findViewById(R.id.imgClose);
        edtDate = (EditText) findViewById(R.id.edtDate);
        spnHour = (Spinner) findViewById(R.id.spnHour);
        spnMinute = (Spinner) findViewById(R.id.spnMinute);
        spn24h = (Spinner) findViewById(R.id.spn24h);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

    }

    private void bind() {
        setUpForSpinner();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!token.equalsIgnoreCase("")) {
                    if (!edtDate.getText().toString().equalsIgnoreCase("")) {
                        progressDialog = new CustomProgressDialog(RequestFreeSessionActivity.this);
                        progressDialog.show();
                        stringTime = spnHour.getSelectedItem().toString() + ":" + spnMinute.getSelectedItem().toString() + " " + spn24h.getSelectedItem().toString();
                        String request = stringDate + " " + stringTime;
                        String dateTimeRequestFreeSession = "{\n" +
                                "  \"date_time\":" + "\"" + request + "\"" + ",\n" +
                                "  \"trainer_id\":" + idTrainer + "\n" +
                                "}";
                        Log.e("token", token);
                        Log.e("dateTime", request);
                        Call<JsonElement> callRequestFreeSession = RestClient.getApiInterface().requestFreeSession(NoteRestAPI.stringToRequestBody(dateTimeRequestFreeSession), token);
                        try {
                            Buffer buffer = new Buffer();
                            NoteRestAPI.stringToRequestBody(dateTimeRequestFreeSession).writeTo(buffer);
                            Log.e("request", buffer.readUtf8());
                        } catch (Exception e) {

                        }
                        Log.e("request", callRequestFreeSession.request().headers().toString());
                        Log.e("request", callRequestFreeSession.request().body().toString());

                        callRequestFreeSession.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                int code = response.code();
                                Log.e("code", code + "");
                                progressDialog.dismiss();
                                if (response.isSuccessful()) {

                                    if (code == 200) {
                                        JsonObject body = response.body().getAsJsonObject();
                                        int codeBody = body.get("code").getAsInt();

                                        if (codeBody == 200) {
                                            Intent intent = new Intent(RequestFreeSessionActivity.this, CallUsActivity.class);
                                            intent.putExtra("title", "Request A Free Session");
                                            intent.putExtra("thankyou", "Thank you for booking !");
                                            startActivity(intent);
                                        }else if(codeBody == 18){
                                            CustomAlertDialog alertDialog = new CustomAlertDialog(RequestFreeSessionActivity.this,"Request Error!","You have already booked","Ok") {
                                                @Override
                                                public void doSomethingWhenDismiss() {
                                                }
                                            };
                                            alertDialog.show();
                                        } else{
                                            String s = "";
                                            if (body.has("error")){
                                                s = body.get("error").getAsString();
                                            }
                                            if (body.has("message")){
                                                s = body.get("message").getAsString();
                                            }
                                            CustomAlertDialog alertDialog = new CustomAlertDialog(RequestFreeSessionActivity.this,"Request Error!",s,"Ok") {
                                                @Override
                                                public void doSomethingWhenDismiss() {
                                                }
                                            };
                                            alertDialog.show();
                                        }
                                    }
                                } else {
                                    try {
                                        Log.e("errorBody", response.errorBody().string());
                                    } catch (Exception e) {
                                        Log.e("errorExcep", e.toString());
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                CustomAlertDialog alertDialog = new CustomAlertDialog(RequestFreeSessionActivity.this, "Connection Error!", "Please check your internet!", "Ok") {
                                    @Override
                                    public void doSomethingWhenDismiss() {

                                    }
                                };
                                alertDialog.show();
                            }
                        });


                    }
                } else {
                    CustomAlertDialog alertDialog = new CustomAlertDialog(RequestFreeSessionActivity.this, "Request Error!", "You must login to request a free session", "Later") {
                        @Override
                        public void doSomethingWhenDismiss() {

                        }
                    };
                    alertDialog.show();
                }

            }
        });


        final Calendar cal = Calendar.getInstance();
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RequestFreeSessionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        stringDate = d + "-" + (m + 1) + "-" + y;
                        edtDate.setText(stringDate);
                        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUpForSpinner() {
        listHour = new ArrayList<>();
        listMinute = new ArrayList<>();
        listIs24h = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            listHour.add(i + "");
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                listMinute.add("0" + i);
            } else {
                listMinute.add(i + "");
            }
        }
        listIs24h.add("AM");
        listIs24h.add("PM");
        ArrayAdapter<String> adapterHour = new ArrayAdapter<String>(RequestFreeSessionActivity.this, R.layout.item_spinner, listHour);
        ArrayAdapter<String> adapterMinute = new ArrayAdapter<String>(RequestFreeSessionActivity.this, R.layout.item_spinner, listMinute);
        ArrayAdapter<String> adapterIs24h = new ArrayAdapter<String>(RequestFreeSessionActivity.this, R.layout.item_spinner, listIs24h);

        spnHour.setAdapter(adapterHour);
        spnMinute.setAdapter(adapterMinute);
        spn24h.setAdapter(adapterIs24h);
    }
}
