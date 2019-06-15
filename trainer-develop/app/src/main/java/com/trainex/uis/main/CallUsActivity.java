package com.trainex.uis.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trainex.R;
import com.trainex.model.Session;

public class CallUsActivity extends AppCompatActivity {
    private TextView title;
    private ImageView imgCall;
    private Button btnDone;
    private TextView tvThankYou;
    private LinearLayout booking;
    private String textTitle;
    private String textThankYou="";
    private TextView tvSessionType;
    private TextView tvPriceSession;
    private Session session;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_us);
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        phone = prefs.getString("phone_number","");
        init();
        bind();
    }
    private void init(){

        title = (TextView) findViewById(R.id.title);
        imgCall = (ImageView) findViewById(R.id.imgCall);
        btnDone = (Button) findViewById(R.id.btnDone);
        tvThankYou = (TextView) findViewById(R.id.tvThankYou);
        booking = (LinearLayout) findViewById(R.id.booking);

        tvSessionType = (TextView) findViewById(R.id.tvSessionType);
        tvPriceSession = (TextView) findViewById(R.id.tvPriceSession);

    }
    private  void bind(){
        textTitle = getIntent().getStringExtra("title");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CallUsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        title.setText(textTitle);

        textThankYou = getIntent().getStringExtra("thankyou");
        if (textThankYou==null){
            tvThankYou.setText("Thank you !");
        }else{
            tvThankYou.setText(textThankYou);
        }
        if (textTitle.equalsIgnoreCase("Buy A Session")){
            booking.setVisibility(View.VISIBLE);
            session = (Session) getIntent().getSerializableExtra("session");
            String sessionType = session.getTypeSession().replace('_', ' ');
            tvSessionType.setText(sessionType);
            tvPriceSession.setText(session.getPrice()+" AED");
        }
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CallUsActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) CallUsActivity.this,
                            Manifest.permission.CALL_PHONE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions((Activity) CallUsActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MainActivity.PERMISSION_CALL);
                        Toast.makeText(CallUsActivity.this, "Press again to call", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+phone));
                    startActivity(callIntent);
                }
            }
        });
    }
}
