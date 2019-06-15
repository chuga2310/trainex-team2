package com.trainex.fragment.inmain;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.features.imageloader.ImageLoader;
import com.esafirm.imagepicker.features.imageloader.ImageType;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.CallUsActivity;
import com.trainex.uis.main.MainActivity;

public class RegisterTrainerFragment extends Fragment {

    private Spinner spnLocation;
    private ImageView imgShowDrawer;
    private Button btnSubmit;
    private EditText edtName;
    private EditText edtPhone;
    private EditText edtEmail;
    private EditText edtAddress;
    private EditText edtAboutMe;
    private ImageView imgProfilePhoto;
    private LinearLayout layoutCertificates;
    private ImageView imgCertificate1;
    private ImageView imgCertificate2;
    View view;
    private ArrayList<String> listLocation = new ArrayList<>();
    private ArrayList<String> listSuburbs;
    private ArrayAdapter<String> adapterLocation;
    private String token;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapterLocation = new ArrayAdapter(getContext(), R.layout.item_spinner, listLocation);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");
        getDataSpinner();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register_trainer, container, false);
        image1 = null;
        image2 = null;
        avatarFile = null;
        init();
        bind();

        return view;
    }

    private void init() {
        spnLocation = (Spinner) view.findViewById(R.id.spnLocation);
        imgShowDrawer = (ImageView) view.findViewById(R.id.imgShowDrawer);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        edtName = (EditText) view.findViewById(R.id.edtName);
        edtPhone = (EditText) view.findViewById(R.id.edtPhone);
        edtEmail = (EditText) view.findViewById(R.id.edtEmail);
        edtAddress = (EditText) view.findViewById(R.id.edtAddress);
        edtAboutMe = (EditText) view.findViewById(R.id.edtAboutMe);
        imgProfilePhoto = (ImageView) view.findViewById(R.id.imgProfilePhoto);
        layoutCertificates = (LinearLayout) view.findViewById(R.id.layoutCertificates);
        imgCertificate1 = (ImageView) view.findViewById(R.id.imgCertificate1);
        imgCertificate2 = (ImageView) view.findViewById(R.id.imgCertificate2);
    }

    ArrayList<Image> imagesMultiPick = new ArrayList<>();

    private void bind() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.PERMISSION_READ_STORAGE);
            }
        } else {
        }
        spnLocation.setAdapter(adapterLocation);
        imgShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showMenu();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (avatarFile != null) {

                    String name = edtName.getText().toString().trim().replace("\n", " ");;
                    String phone = edtPhone.getText().toString().trim().replace("\n", " ");;
                    String email = edtEmail.getText().toString().trim().replace("\n", " ");;
                    String address = edtAddress.getText().toString().trim().replace("\n", " ");
                    String about = edtAboutMe.getText().toString().trim().replace("\n", " ");;
                    String location = listLocation.get(spnLocation.getSelectedItemPosition()).trim();
                    File avatar = new File(avatarFile.getPath());
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("fullname", name);
                    builder.addFormDataPart("phone_number", phone);
                    builder.addFormDataPart("email", email);
                    builder.addFormDataPart("address", address);
                    builder.addFormDataPart("about_me", about);
                    builder.addFormDataPart("location", location);
                    builder.addFormDataPart("avatar", avatarFile.getName(), RequestBody.create(MediaType.parse("image/*"), avatar));
                    if (image1 != null) {
                        File certificates1 = new File(image1.getPath());
                        builder.addFormDataPart("certificates1", image1.getName(), RequestBody.create(MediaType.parse("image/*"), certificates1));
                        if (image2 != null) {
                            File certificates2 = new File(image2.getPath());
                            builder.addFormDataPart("certificates2", image2.getName(), RequestBody.create(MediaType.parse("image/*"), certificates2));
                        }
                    } else {
                        if (image2 != null) {
                            File certificates2 = new File(image2.getPath());
                            builder.addFormDataPart("certificates1", image2.getName(), RequestBody.create(MediaType.parse("image/*"), certificates2));
                        } else {

                            CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Submit Error", "Certificates is not empty", "Ok") {
                                @Override
                                public void doSomethingWhenDismiss() {

                                }
                            };
                            alertDialog.show();
                            return;
                        }
                    }

                    MultipartBody requestBody = builder.build();
                    final CustomProgressDialog customProgressDialog = new CustomProgressDialog(getContext());
                    customProgressDialog.show();
                    Call<JsonElement> callRegisterTrainer = RestClient.getApiInterface().registerTrainer(token, requestBody);
                    callRegisterTrainer.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            int code = response.code();
                            customProgressDialog.dismiss();
                            if (code == 422) {
                                try {
                                    JSONObject body = new JSONObject(response.errorBody().string());
                                    String error = "";
                                    if (body.has("fullname")) {
                                        error += body.getJSONArray("fullname").get(0).toString() + "\n";
                                    }
                                    if (body.has("email")) {
                                        error += body.getJSONArray("email").get(0).toString() + "\n";
                                    }
                                    if (body.has("phone_number")) {
                                        error += body.getJSONArray("phone_number").get(0).toString() + "\n";
                                        ;
                                    }
                                    if (body.has("address")) {
                                        error += body.getJSONArray("address").get(0).toString() + "\n";
                                    }
                                    if (body.has("about_me")) {
                                        error += body.getJSONArray("about_me").get(0).toString() + "\n";
                                    }
                                    if (body.has("avatar")) {
                                        error += body.getJSONArray("avatar").get(0).toString() + "\n";
                                    }
                                    if (body.has("certificates1")) {
                                        error += body.getJSONArray("certificates1").get(0).toString() + "\n";
                                    }
                                    if (body.has("certificates2")) {
                                        error += body.getJSONArray("certificates2").get(0).toString() + "\n";
                                    }
                                    if (body.has("location")) {
                                        error += body.getJSONArray("location").get(0).toString() + "\n";
                                    }
                                    CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Register Error!", error, "Ok") {
                                        @Override
                                        public void doSomethingWhenDismiss() {

                                        }
                                    };
                                    alertDialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (code == 200) {
                                JsonObject body = response.body().getAsJsonObject();
                                Log.e("body", body.toString());
                                int codeBody = body.get("code").getAsInt();
                                if (codeBody == 200) {
                                    Intent intent = new Intent(getActivity(), CallUsActivity.class);
                                    intent.putExtra("title", "Register As A Trainer");
                                    intent.putExtra("thankyou", "Thank you\nfor registering with us");
                                    startActivity(intent);
                                } else{
                                    String s = "";
                                    if (body.has("error")){
                                        s = body.get("error").getAsString();
                                    }
                                    if (body.has("message")){
                                        s = body.get("message").getAsString();
                                    }
                                    CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(),"Register Error!",s,"Ok") {
                                        @Override
                                        public void doSomethingWhenDismiss() {
                                            MainActivity.showHomeFromLeft();
                                        }
                                    };
                                    alertDialog.show();
                                }
                            } else {
                                try {
                                    Log.e("errorBody", response.errorBody().string());
                                    CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Error: " + response.code(), "Something wrong", "Ok") {
                                        @Override
                                        public void doSomethingWhenDismiss() {

                                        }
                                    };
                                    alertDialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Toast.makeText(getContext(), "Loading failed", Toast.LENGTH_SHORT).show();
                        }
                    });


            }else

            {
                CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Submit Error!", "Profile photo is not empty!", "Ok") {
                    @Override
                    public void doSomethingWhenDismiss() {

                    }
                };
                alertDialog.show();
            }
        }
    });
        imgProfilePhoto.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){
        ImagePicker.create(getActivity())
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
                .start(1); // start image picker activity with request code
    }
    });

        imgCertificate1.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){

        ImagePicker.create(getActivity())
                .returnMode(ReturnMode.NONE) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                .includeVideo(false) // Show video on image picker
                .single() // multi mode (default mode)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .origin(imagesMultiPick) // original selected images, used in multi mode
                .exclude(imagesMultiPick) // exclude anything that in image.getPath()
                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath())
                .enableLog(true) // disabling log
                .start(2); // start image picker activity with request code
    }
    });
        imgCertificate2.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){
        ImagePicker.create(getActivity())
                .returnMode(ReturnMode.NONE) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                .includeVideo(false) // Show video on image picker
                .single() // multi mode (default mode)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .origin(imagesMultiPick) // original selected images, used in multi mode
                .exclude(imagesMultiPick) // exclude anything that in image.getPath()
                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath())
                .enableLog(true) // disabling log
                .start(3); // start image picker activity with request code

    }
    });
}

    Image image1, image2, avatarFile;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            List<Image> images = ImagePicker.getImages(data);
            if (images != null && !images.isEmpty()) {

                imgProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(images.get(0).getPath()));
                imgProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Log.e("path", images.get(0).getPath());
                avatarFile = images.get(0);
            }
        } else if (requestCode == 2) {
            List<Image> images = ImagePicker.getImages(data);
            if (images != null && !images.isEmpty()) {
                if (images.size() == 1) {
                    imgCertificate1.setImageBitmap(BitmapFactory.decodeFile(images.get(0).getPath()));
                    imgCertificate1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                image1 = images.get(0);
            }

        } else if (requestCode == 3) {
            List<Image> images = ImagePicker.getImages(data);
            if (images != null && !images.isEmpty()) {
                if (images.size() == 1) {
                    imgCertificate2.setImageBitmap(BitmapFactory.decodeFile(images.get(0).getPath()));
                    imgCertificate2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                image2 = images.get(0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getDataSpinner() {
        /***** SETTING SPINNER *****/
        listLocation.clear();
        Call<JsonElement> callGetListLocation = RestClient.getApiInterface().getListLocations();
        callGetListLocation.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body().getAsJsonObject();
                    if (body.get("code").getAsInt() == 200) {
                        JsonArray data = body.get("data").getAsJsonArray();
                        for (int i = 0; i < data.size(); i++) {
                            String name = data.get(i).getAsJsonObject().get("name").getAsString();
                            listLocation.add(name);

                        }
                        adapterLocation.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
