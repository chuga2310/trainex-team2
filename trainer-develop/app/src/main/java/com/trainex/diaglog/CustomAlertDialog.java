package com.trainex.diaglog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.trainex.R;

public abstract class CustomAlertDialog extends BaseDialog {
    private String title, content, textButton;
    private TextView tvDlgAlertTitle,tvDlgAleatContent;
    private Button btnDlgAlert;
    public CustomAlertDialog(@NonNull Context context, String title, String content, String textButton) {
        super(context);
        this.title =title;
        this.content =content;
        this.textButton =textButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    int getLayoutResID() {
        return R.layout.diaglog_alert;
    }

    @Override
    public void bind() {
        tvDlgAlertTitle = findViewById(R.id.tvDlgAlertTitle);
        tvDlgAleatContent = findViewById(R.id.tvDlgAleatContent);
        btnDlgAlert = findViewById(R.id.btnDlgAlert);
    }

    @Override
    public void init() {
        tvDlgAlertTitle.setText(this.title);
        tvDlgAleatContent.setText(this.content);
        btnDlgAlert.setText(this.textButton);
        btnDlgAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                doSomethingWhenDismiss();
            }
        });
    }



    abstract public void doSomethingWhenDismiss();
}
