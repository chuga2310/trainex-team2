package com.trainex.uis.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.Session;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;

public class PaymenttActivity extends AppCompatActivity {
    private ImageView imgBack;
    private ImageView imgPayWithCard;
    private ImageView imgPayWithCash;
    private Button btnContinue;
    private TextView title;
    private String textTitle;
    private LinearLayout booking;
    private boolean choosePayment; //true  = Card || false = Cash
    private Session session;
    private String bookingType;
    private String token;
    private TextView tvSessionType;
    private TextView tvPrice;
    private LinearLayout viewImgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bookingType = getIntent().getStringExtra("bookingType");
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        init();
        bind();
    }

    private void init() {
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgPayWithCard = (ImageView) findViewById(R.id.imgPayWithCard);
        imgPayWithCash = (ImageView) findViewById(R.id.imgPayWithCash);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        title = (TextView) findViewById(R.id.title);
        booking = (LinearLayout) findViewById(R.id.booking);
        tvSessionType = (TextView) findViewById(R.id.tvSessionType);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        viewImgBack = (LinearLayout) findViewById(R.id.viewImgBack);

    }

    private void bind() {
        textTitle = getIntent().getStringExtra("title");
        title.setText(textTitle);
        imgPayWithCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePayment = true;
                imgPayWithCard.setBackgroundResource(R.drawable.background_payment_choose);
                imgPayWithCash.setBackgroundResource(R.drawable.background_payment_default);
                btnContinue.setBackgroundResource(R.drawable.background_button_login_signup_touchable);
                btnContinue.setAlpha(1);
                btnContinue.setEnabled(true);
            }
        });
        imgPayWithCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePayment = false;
                imgPayWithCash.setBackgroundResource(R.drawable.background_payment_choose);
                imgPayWithCard.setBackgroundResource(R.drawable.background_payment_default);
                btnContinue.setBackgroundResource(R.drawable.background_button_login_signup_touchable);
                btnContinue.setEnabled(true);
                btnContinue.setAlpha(1);

            }
        });

        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (bookingType.equalsIgnoreCase("session")) {
            booking.setVisibility(View.VISIBLE);
            session = (Session) getIntent().getSerializableExtra("session");
            final int idSession = session.getIdSession();
            int price = session.getPrice();
            final String session_type = session.getTypeSession();
            tvSessionType.setText(session_type.replace('_', ' '));
            tvPrice.setText(price + " AED");
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    booking(idSession);
                }
            });
        } else {
            final int idEvent = getIntent().getIntExtra("idEvent", -1);
            if (idEvent > -1) {
                btnContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        booking(idEvent);
                    }
                });
            }
        }


    }

    private void booking(int event_or_session_id) {

        String pay_type = choosePayment ? "card" : "cash";
        String typeSession = session != null ? session.getTypeSession() : "";
        String request = "{\n" +
                "  \"booking_type\": " + "\"" + bookingType + "\"" + ",\n" +
                "  \"event_or_session_id\": " + event_or_session_id + ",\n" +
                "  \"pay_type\": " + "\"" + pay_type + "\"" + ",\n" +
                "  \"session_type\":" + "\"" + typeSession + "\"" + "\n" +
                "}";

        Call<JsonElement> callBooking = RestClient.getApiInterface().sendBooking(token, NoteRestAPI.stringToRequestBody(request));
        callBooking.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body().getAsJsonObject();
                    int code = body.get("code").getAsInt();
                    if (code == 200) {
                        if (body.get("data").getAsJsonObject().has("scalar")) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("session", session);
                            Intent intent = new Intent(PaymenttActivity.this, WebViewActivity.class);
                            intent.putExtra("title", textTitle);
                            intent.putExtras(bundle);
                            intent.putExtra("link", body.get("data").getAsJsonObject().get("scalar").getAsString());
                            startActivity(intent);

                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("session", session);
                            Intent intent = new Intent(PaymenttActivity.this, CallUsActivity.class);
                            intent.putExtra("title", textTitle);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } else if (code == 18) {
                        String s = "";
                        if (body.has("error")) {
                            s = body.get("error").getAsString();
                        }
                        if (body.has("message")) {
                            s = body.get("message").getAsString();
                        }
                        CustomAlertDialog alertDialog = new CustomAlertDialog(PaymenttActivity.this, "Warning!", "You have already booked before", "Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {
                                finish();
                            }
                        };
                        alertDialog.show();
                    } else {
                        Log.e("body",body.toString() );
                        String s = "";
                        if (body.has("error")) {
                            s = body.get("error").getAsString();
                        }
                        if (body.has("message")) {
                            s = body.get("message").getAsString();
                        }
                        CustomAlertDialog alertDialog = new CustomAlertDialog(PaymenttActivity.this, "Warning!", s, "Ok") {
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
                Toast.makeText(PaymenttActivity.this, "Loading Failed!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
