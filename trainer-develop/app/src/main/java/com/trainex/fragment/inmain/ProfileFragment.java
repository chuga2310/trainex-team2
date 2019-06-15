package com.trainex.fragment.inmain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class ProfileFragment extends Fragment {
    View view;
    private ImageView imgShowDrawer;
    private EditText edtEmail;
    private ImageButton btnEditEmail;
    private EditText edtPhone;
    private ImageButton btnEditPhone;
    private Button btnUpdate;
    private String email="",
            phone="",
            resAvatar="http://trainex.vj-soft.com/uploads/noavatar.png",
            username="",
            fullname="";
    String token;
    private TextView tvFullname;
    private TextView tvUsername;
    private Call<JsonElement> getInfo, updateInfo;
    private CircleImageView imgAvatar;
    Thread thread = new Thread();
    CustomProgressDialog progressDialog;
    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        if (!token.equalsIgnoreCase("")) {
            getInfo = RestClient.getApiInterface().getUserInfo(token);
            getInfo.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        int code = response.code();
                        JsonObject body = response.body().getAsJsonObject();
                        if (code == 200) {
                            int codeBody = body.get("code").getAsInt();
                            if (codeBody == 200) {
                                JsonObject data = body.get("data").getAsJsonObject();
                                JsonObject info = data.get("about_us").getAsJsonObject();
                                if (data.has("about_us")){
                                    if (!data.get("about_us").isJsonNull()){
                                        username = "";
                                        if (!info.get("username").isJsonNull()){
                                            username =info.get("username").getAsString();
                                        }
                                        email = "";
                                        if (!info.get("email").isJsonNull()){
                                            email =info.get("email").getAsString();
                                        }
                                        phone = "";
                                        if (!info.get("phone").isJsonNull()){
                                            phone =info.get("phone").getAsString();
                                        }
                                        fullname = "";
                                        if (!info.get("name").isJsonNull()){
                                            fullname =info.get("name").getAsString();
                                        }
                                        if (!info.get("avatar").isJsonNull()){
                                            resAvatar = info.get("avatar").getAsString();
                                        }
                                        Glide.with(context).load(resAvatar).into(imgAvatar);
                                        tvUsername.setText(username);
                                        edtEmail.setText(email);
                                        edtPhone.setText(phone);
                                        tvFullname.setText(fullname);
                                    }
                                }

                            }else{
                                String s = "";
                                if (body.has("error")){
                                    s = body.get("error").getAsString();
                                }
                                if (body.has("message")){
                                    s = body.get("message").getAsString();
                                }
                                CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(),"Update error!",s,"Ok") {
                                    @Override
                                    public void doSomethingWhenDismiss() {
                                        MainActivity.showHomeFromLeft();
                                    }
                                };
                                alertDialog.show();
                            }

                        }
                    } else {
                        try {
                            Log.e("errorBody", response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("errorBodyE", e.toString());
                        }
                    }
                    Log.e("headerToken", call.request().headers().toString());
                    try {
                        okio.Buffer buffer = new okio.Buffer();
                        call.request().body().writeTo(buffer);
                        Log.e("body", buffer.readUtf8());
                    } catch (Exception e) {
                        Log.e("bodyE", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Log.e("fail", t.toString());
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        bind();
        avatarFile= null;
        return view;
    }

    private void init() {

        imgShowDrawer = (ImageView) view.findViewById(R.id.imgShowDrawer);
        edtEmail = (EditText) view.findViewById(R.id.edtEmail);
        btnEditEmail = (ImageButton) view.findViewById(R.id.btnEditEmail);
        edtPhone = (EditText) view.findViewById(R.id.edtPhone);
        btnEditPhone = (ImageButton) view.findViewById(R.id.btnEditPhone);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        tvFullname = (TextView) view.findViewById(R.id.tvFullname);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);

        imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);

    }

    private void bind() {

        btnEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!token.equalsIgnoreCase("")) {
                    btnEditEmail.setColorFilter(getResources().getColor(R.color.colorAccent));
                    edtEmail.setEnabled(true);
                    edtEmail.requestFocus();
                    btnUpdate.setBackgroundResource(R.drawable.background_button_login_signup_touchable);
                    btnUpdate.setAlpha(1);
                    btnUpdate.setEnabled(true);
                } else {
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(getContext(), "Error!", "You must login to edit email");
                    alertLoginDialog.show();
                }
            }
        });
        btnEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!token.equalsIgnoreCase("")) {
                    btnEditPhone.setColorFilter(getResources().getColor(R.color.colorAccent));
                    edtPhone.setEnabled(true);
                    edtPhone.requestFocus();
                    btnUpdate.setEnabled(true);
                    btnUpdate.setBackgroundResource(R.drawable.background_button_login_signup_touchable);
                    btnUpdate.setAlpha(1);
                } else {
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(getContext(), "Error!", "You must login to edit phone number");
                    alertLoginDialog.show();
                }

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View viewChild) {
                progressDialog = new CustomProgressDialog(getContext());
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnUpdate.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                if (!edtEmail.getText().toString().equals(email)) {
                    if (edtPhone.getText().toString().equals(phone)) {
                        //Email changed and phone not change
                        String request = "{\n" +
                                "  \"email\": " + "\"" + edtEmail.getText().toString() + "\"" + "\n" +
                                "}";
                        callUpdate(request);
                        progressDialog.show();

                    }else {
                        //Email changed and phone changed
                        String request = "{\n" +
                                "  \"email\": " + "\"" + edtEmail.getText().toString() + "\"" + ",\n" +
                                "  \"phone_number\": " + "\"" + edtPhone.getText().toString() + "\"" + "\n" +
                                "}";
                        callUpdate(request);
                        progressDialog.show();
                    }
                }else{
                    //Email not changed and phone changed
                    if (!edtPhone.getText().toString().equals(phone)){
                        String request = "{\n" +
                                "  \"phone_number\": " + "\"" + edtPhone.getText().toString() + "\"" + "\n" +
                                "}";
                        callUpdate(request);
                        progressDialog.show();
                    }
                }
                if (avatarFile!=null){
                    updateImage();
                    progressDialog.show();
                }
            }
        });
        imgShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showMenu();
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.ImagePickerWithFragment.create(getActivity())
                        .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                        .folderMode(false) // folder mode (false by default)
                        .toolbarFolderTitle("Folder") // folder selection title
                        .toolbarImageTitle("Tap to select") // image selection title
                        .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                        .includeVideo(false) // Show video on image picker
                        .single() // single mode
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                        .imageFullDirectory(Environment.getExternalStorageDirectory().getPath())
                        .enableLog(true) // disabling log
                        .start(4); // start image picker activity with request code
            }
        });
    }
    private void updateImage(){
        File avatar = new File(avatarFile.getPath());
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("avatar", avatarFile.getName(), RequestBody.create(MediaType.parse("image/*"), avatar));
        MultipartBody requestBody = builder.build();
            Call<JsonElement> callUpdateAvatar = RestClient.getApiInterface().updateAvatar(token, requestBody);
        callUpdateAvatar.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                int code = response.code();
                if (code==200){
                    btnUpdate.setBackgroundResource(R.drawable.background_button_disable);
                    btnUpdate.setAlpha((float) 0.8);
                    btnUpdate.setEnabled(false);
                    Toast.makeText(getContext(), "Profile pic updated successfully!", Toast.LENGTH_SHORT).show();
                }else{
                    CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Error: "+ code,"Something wroong","Ok") {
                        @Override
                        public void doSomethingWhenDismiss() {

                        }
                    };
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load failed!", Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }
    private void callUpdate(String request) {
        Call<JsonElement> callUpdateInfo = RestClient.getApiInterface().updateUserInfo(token, NoteRestAPI.stringToRequestBody(request));
        callUpdateInfo.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                int code = response.code();
                if (code == 200) {
                    btnEditPhone.setColorFilter(getResources().getColor(android.R.color.white));
                    btnEditEmail.setColorFilter(getResources().getColor(android.R.color.white));
                    btnUpdate.setBackgroundResource(R.drawable.background_button_disable);
                    btnUpdate.setAlpha((float) 0.8);
                    btnUpdate.setEnabled(false);
                    edtPhone.setEnabled(false);
                    edtEmail.setEnabled(false);
                    email = response.body().getAsJsonObject().get("data").getAsJsonObject().get("email").getAsString();
                    phone = response.body().getAsJsonObject().get("data").getAsJsonObject().get("phone_number").getAsString();
                    Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
                } else if (code == 422) {
                    String stringError="";
                    String errBody;
                    try {
                       errBody = response.errorBody().string();
                        JSONObject error = new JSONObject(errBody);

                        if (error.has("email")){
                            JSONArray errorEmail = error.getJSONArray("email");
                            stringError += errorEmail.getString(0) +"\n";
                        }
                        if (error.has("phone_number")){
                            JSONArray errorPhone = error.getJSONArray("phone_number");
                            stringError += errorPhone.getString(0);
                        }
                        CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(),"Update error!", stringError,"Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {

                            }
                        };
                        alertDialog.show();
                    } catch (Exception e) {
                        Log.e("errorExcep", e.toString());
                        e.printStackTrace();
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
                Toast.makeText(getContext(), "Load failed!", Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }
    private Image avatarFile;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4) {
            List<Image> images = ImagePicker.getImages(data);
            if (images != null && !images.isEmpty()) {
                btnUpdate.setBackgroundResource(R.drawable.background_button_login_signup_touchable);
                btnUpdate.setAlpha(1);
                btnUpdate.setEnabled(true);
                imgAvatar.setImageBitmap(BitmapFactory.decodeFile(images.get(0).getPath()));
                Log.e("path", images.get(0).getPath());
                avatarFile = images.get(0);
            }
        }
    }
}
