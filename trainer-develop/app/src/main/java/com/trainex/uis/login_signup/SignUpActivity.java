package com.trainex.uis.login_signup;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.facebook.internal.CallbackManagerImpl;

import com.trainex.R;
import com.trainex.adapter.viewpageradapter.SignUpPagerAdapter;
import com.trainex.fragment.insignup.SignupFragment;

public class SignUpActivity extends AppCompatActivity {
    private static ViewPager vpgSignup;
    private SignUpPagerAdapter adapter;
    public static boolean isInSignup =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        vpgSignup = findViewById(R.id.vpgSignup);
        adapter = new SignUpPagerAdapter(getSupportFragmentManager());
        vpgSignup.setAdapter(adapter);
    }
    public static void goToTerm(){
        vpgSignup.setCurrentItem(1,true);
        isInSignup =false;
    }
    public static void backToSignUp(){
        vpgSignup.setCurrentItem(0,true);
        isInSignup = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (isInSignup){
                    finish();
                }else{
                    backToSignUp();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()){
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpgSignup + ":" + 0);
            page.onActivityResult(requestCode, resultCode, data);
        }
    }
}
