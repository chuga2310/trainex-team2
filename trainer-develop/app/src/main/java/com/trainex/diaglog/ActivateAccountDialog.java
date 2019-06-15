package com.trainex.diaglog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trainex.R;
import com.trainex.uis.main.MainActivity;

public class ActivateAccountDialog extends BaseDialog {
    private TextView tvResend;
    private Button btnContinue;
    private TextView tvActivateAccountSkip;
    private boolean isActivatedAccount = false;
    private Context context;


    public ActivateAccountDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    int getLayoutResID() {
        return R.layout.dialog_activate_account;
    }

    @Override
    public void bind() {
        tvResend = (TextView) findViewById(R.id.tvResend);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        tvActivateAccountSkip = (TextView) findViewById(R.id.tvActivateAccountSkip);
    }

    public TextView getTvResend() {
        return tvResend;
    }

    public void setTvResend(TextView tvResend) {
        this.tvResend = tvResend;
    }

    public Button getBtnContinue() {
        return btnContinue;
    }

    public void setBtnContinue(Button btnContinue) {
        this.btnContinue = btnContinue;
    }

    public boolean isActivatedAccount() {
        return isActivatedAccount;
    }

    public void setActivatedAccount(boolean activatedAccount) {
        isActivatedAccount = activatedAccount;
    }

    @Override
    public void init() {
//        btnContinue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isActivatedAccount){
//                    context.startActivity(new Intent(context, MainActivity.class));
//                }else{
//                    dismiss();
//                    String title = "Sorry !";
//                    String content ="Your account is not activated, please go to your email to active";
//                    String textButton = "Ok";
//                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), title, content, textButton) {
//                        @Override
//                        public void doSomethingWhenDismiss() {
//                            showOut();
//                        }
//                    };
//                    dialog.show();
//                }
//            }
//        });
        tvActivateAccountSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });

    }
    public void showOut(){
        this.show();
    }
}
