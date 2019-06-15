package com.trainex.uis.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.trainex.R;

public class TermActivity extends AppCompatActivity {
    private ImageButton imgTermsBack;
    private View layoutBackround;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_terms);

        imgTermsBack = (ImageButton) findViewById(R.id.imgTermsBack);

        imgTermsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutBackround = (View) findViewById(R.id.layoutBackround);
        layoutBackround.setAlpha(0.8f);

    }
}
