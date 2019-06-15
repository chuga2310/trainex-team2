package com.trainex.fragment.insignup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trainex.R;
import com.trainex.uis.login_signup.SignUpActivity;

public class TermsFragment extends Fragment {
    private ImageView imgTermsBack;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms, container,false);
        imgTermsBack = (ImageView) view.findViewById(R.id.imgTermsBack);
        imgTermsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.backToSignUp();
            }
        });
        return view;
    }
}
