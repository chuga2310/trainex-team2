package com.trainex.fragment.inmain;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class ContactUsFragment extends Fragment {
    View view;
    private ImageView imgShowDrawer;
    private EditText edtComments;
    private Button btnSubmit;
    private String token;
    private CustomProgressDialog progressDialog;
    private TextView tvPhone;
    private String phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        init();
        bind();
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        phone =  prefs.getString("phone_number","");
        return view;
    }

    private void init() {
        imgShowDrawer = (ImageView) view.findViewById(R.id.imgShowDrawer);
        edtComments = (EditText) view.findViewById(R.id.edtComments);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        tvPhone = (TextView) view.findViewById(R.id.tvPhone);
    }

    private void bind() {
//        tvPhone.setText(phone);
        imgShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showMenu();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!token.equalsIgnoreCase("")) {
                    String comment = edtComments.getText().toString().replaceAll("\n", " ");
                    String requestComment = "{\n" +
                            "  \"message\": " + "\"" + comment + "\"" + "\n" +
                            "}";
                    if (!comment.equalsIgnoreCase("")) {
                        progressDialog = new CustomProgressDialog(getContext());
                        progressDialog.show();
                        Call<JsonElement> callSendContact = RestClient.getApiInterface().sendContact(token, NoteRestAPI.stringToRequestBody(requestComment));
                        callSendContact.enqueue(new Callback<JsonElement>() {

                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                if (response.isSuccessful()) {
                                    progressDialog.dismiss();
                                    int code = response.code();
                                    if (code == 200) {
                                        JsonObject body = response.body().getAsJsonObject();
                                        int codeBody = body.get("code").getAsInt();
                                        if (codeBody == 200) {
                                            CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Thank you!", "Your feedback has been sent.", "Ok") {
                                                @Override
                                                public void doSomethingWhenDismiss() {
                                                    edtComments.setText("");
                                                    MainActivity.showHomeFromLeft();
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
                                            CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(),"Send Error!",s,"Ok") {
                                                @Override
                                                public void doSomethingWhenDismiss() {
                                                    MainActivity.showHomeFromLeft();
                                                }
                                            };
                                            alertDialog.show();
                                        }
                                    } try {
                                        Log.e("errorBody", response.errorBody().string());
                                    }catch (Exception e){
                                        Log.e("errorExcep",e.toString());
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Connection Error!", "Please check your internet!", "Ok") {
                                    @Override
                                    public void doSomethingWhenDismiss() {

                                    }
                                };
                                alertDialog.show();
                            }
                        });
                    }else{
                        CustomAlertDialog  alertDialog = new CustomAlertDialog(getContext(), "Error", "Comment is not empty", "Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {

                            }
                        };
                        alertDialog.show();
                    }
                }
            }
        });
    }
}
