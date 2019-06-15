package com.trainex.uis.login_signup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    //
    private ImageView imgStartLogo;
    private LinearLayout linearLogin;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLoginSignIn;
    private Button btnForgotPassword;
    private LinearLayout loginFacebook;
    private TextView tvSignup;
    private TextView tvSkip;
    private ViewPager vpgLogin;
    private List<Fragment> listFragment;
    private RelativeLayout layoutLogin;
    private LinearLayout layoutForgotPassword;
    private ImageButton imgForgotPassBack;
    private boolean isInForgotPass = false;
    private Animation animLoginIn, animForgotOut, animLoginOut, animForgotIn;
    private EditText edtEmailForgotPass;
    private Button btnRequestPass;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imgStartLogo = (ImageView) findViewById(R.id.imgLoginLogo);
        linearLogin = (LinearLayout) findViewById(R.id.linearLogin);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLoginSignIn = (Button) findViewById(R.id.btnLoginSignIn);
        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        loginFacebook = (LinearLayout) findViewById(R.id.loginFacebook);
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        tvSkip = (TextView) findViewById(R.id.tvLoginSkip);
        imgForgotPassBack = findViewById(R.id.imgForgotPassBack);
        layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
        layoutForgotPassword = (LinearLayout) findViewById(R.id.layoutForgotPassword);
        //bind Forgotpass
        edtEmailForgotPass = findViewById(R.id.edtEmailForgotPass);
        btnRequestPass = findViewById(R.id.btnRequestPass);
        //Animation
        animLoginIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.anim_in_from_left);
        animForgotOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.anim_out_to_right);
        animLoginOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.anim_out_to_left);
        animForgotIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.anim_in_from_right);

        callbackManager = CallbackManager.Factory.create();

        bind();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnLoginSignIn.setClickable(true);
        btnForgotPassword.setClickable(true);
        loginFacebook.setClickable(true);
        tvSignup.setClickable(true);
        tvSkip.setClickable(true);

        btnRequestPass.setEnabled(false);
        btnRequestPass.setClickable(false);
        edtEmailForgotPass.setEnabled(false);
        edtEmailForgotPass.setFocusableInTouchMode(false);
        imgForgotPassBack.setClickable(false);
    }

    private void bind() {
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animLayout();
            }
        });
        imgForgotPassBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutLogin.startAnimation(animLoginIn);
                layoutForgotPassword.startAnimation(animForgotOut);
                layoutForgotPassword.setVisibility(View.VISIBLE);
                setEnableView(isInForgotPass);
                isInForgotPass = false;
            }
        });
        btnRequestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmailForgotPass.getText().toString();
                if (!email.isEmpty()) {
                    if (validateEmail(email)){
                        final CustomProgressDialog progressDialog = new CustomProgressDialog(LoginActivity.this);
                        progressDialog.show();
                        String forgetPassword = "{\n" +
                                "  \"email\": " + "\"" + email + "\"" + "\n" +
                                "}";
                        Call<JsonElement> callForgotPass = RestClient.getApiInterface().forgetPassword(stringToRequestBody(forgetPassword));
                        callForgotPass.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                progressDialog.dismiss();
                                if (response.isSuccessful()) {
                                    int code = response.code();
                                    if (code == 200) {
                                        JsonObject body = response.body().getAsJsonObject();
                                        int codeBody = body.get("code").getAsInt();
                                        if (codeBody == 400) {
                                            String error = body.get("error").getAsString();
                                            CustomAlertDialog alertDialog = new CustomAlertDialog(LoginActivity.this, "Error!", error, "Ok") {
                                                @Override
                                                public void doSomethingWhenDismiss() {

                                                }
                                            };
                                            alertDialog.show();
                                        } else if (codeBody == 200) {
                                            CustomAlertDialog alertDialog = new CustomAlertDialog(LoginActivity.this, "Password Reset", "Please check your email for instructions\n to reset the password", "Ok") {
                                                @Override
                                                public void doSomethingWhenDismiss() {
                                                    this.dismiss();
                                                    layoutLogin.startAnimation(animLoginIn);
                                                    layoutForgotPassword.startAnimation(animForgotOut);
                                                    layoutForgotPassword.setVisibility(View.VISIBLE);
                                                    setEnableView(isInForgotPass);
                                                    isInForgotPass = false;

                                                }
                                            };
                                            alertDialog.show();
                                        }
                                    }
                                }
                            }


                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {

                            }
                        });
                    }else{
                        CustomAlertDialog alertDialog = new CustomAlertDialog(LoginActivity.this, "Request Error!", "Email is not valid!", "Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {

                            }
                        };
                        alertDialog.show();
                    }
                }
            }
        });
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE).edit();
                editor.putString("token","");
                editor.apply();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        btnLoginSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnLoginSignIn.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    CustomAlertDialog dialog = new CustomAlertDialog(LoginActivity.this, "Login Error!", "Please enter your username and password", "Ok") {
                        @Override
                        public void doSomethingWhenDismiss() {

                        }
                    };
                    dialog.show();
                } else {
                    final CustomProgressDialog progressDialog = new CustomProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    String login = "{\n" +
                            "  \"username\":" + "\"" + username + "\"" + "\n," +
                            "  \"password\": " + "\"" + password + "\"" + "\n" +
                            "}";
                    try {
                        JSONObject jsonObject = new JSONObject(login);
                        Log.e("json", jsonObject.toString());
                        Log.e("user",jsonObject.opt("username").toString());
                    }catch (Exception e){

                    }

                    Call<JsonElement> callLogin = RestClient.getApiInterface().loginUser(stringToRequestBody(login));
                    callLogin.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful()) {
                                Log.e("responeBody", response.body().toString());
                                int code = response.code();
                                Log.e("responeCode", code + "");
                                if (code == 200) {
                                    JsonElement body = response.body();
                                    int codeBody = body.getAsJsonObject().get("code").getAsInt();
                                    if (codeBody == 200) {
                                        JsonObject data = body.getAsJsonObject().get("data").getAsJsonObject();
                                        String token = data.get("token").getAsString();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        SharedPreferences.Editor editor = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE).edit();
                                        editor.putString("token", token.trim());
                                        editor.apply();
                                        startActivity(intent);
                                    } else if (codeBody == 2) {
                                        String error = body.getAsJsonObject().get("error").getAsString();
                                        CustomAlertDialog alertDialog = new CustomAlertDialog(LoginActivity.this, "Login Error!", error, "Ok") {
                                            @Override
                                            public void doSomethingWhenDismiss() {

                                            }
                                        };
                                        alertDialog.show();
                                    } else {
                                        String title = "Login error !";
                                        String content = body.getAsJsonObject().get("error").getAsString();
                                        String textButton = "Ok";
                                        CustomAlertDialog dialog = new CustomAlertDialog(LoginActivity.this, title, content, textButton) {
                                            @Override
                                            public void doSomethingWhenDismiss() {

                                            }
                                        };
                                        dialog.show();
                                    }
                                }
                            } else {
                                try {
                                    Log.e("ErrorCode", response.code() + "");
                                    Log.e("ErrorBody", response.errorBody().string());
                                } catch (Exception e) {
//                                    Log.e("ErrorException", e.toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Log.e("ErrorFailure", t.toString());
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

        //Login facebook
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final String tokenFacebook = loginResult.getAccessToken().getToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.i("MainActivity", "@@@response: " + response.toString());
                                        String forgetPassword = "{\n" +
                                                "  \"facebook_token\": " + "\"" + tokenFacebook + "\"" + "\n" +
                                                "}";

                                        Log.e("tokenFacebook", tokenFacebook);
                                        Call<JsonElement> getToken = RestClient.getApiInterface().getTokenByFacebook(NoteRestAPI.stringToRequestBody(forgetPassword));
                                        getToken.enqueue(new Callback<JsonElement>() {
                                            @Override
                                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                                if (response.isSuccessful()){
                                                    JsonObject body = response.body().getAsJsonObject();
                                                    int codeBody = body.get("code").getAsInt();
                                                    Log.e("body", body.toString());

                                                    if (codeBody == 200){
                                                        String token  = body.get("data").getAsJsonObject().get("token").getAsString();
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        SharedPreferences.Editor editor = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE).edit();
                                                        editor.putString("token", token);
                                                        editor.apply();
                                                        startActivity(intent);
                                                    }else{
                                                        CustomAlertDialog alertDialog = new CustomAlertDialog(LoginActivity.this, "Login Error!","Please check your account. It has something wrong", "Ok") {
                                                            @Override
                                                            public void doSomethingWhenDismiss() {

                                                            }
                                                        };
                                                        alertDialog.show();

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                                Toast.makeText(LoginActivity.this, "Load failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        try {
                                            String name = object.getString("name");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

//
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isInForgotPass) {
                layoutLogin.startAnimation(animLoginIn);
                layoutForgotPassword.startAnimation(animForgotOut);
                layoutForgotPassword.setVisibility(View.VISIBLE);
                setEnableView(isInForgotPass);
                isInForgotPass = false;
            } else {
                finish();
            }

        }
        return false;
    }

    public void animLayout() {
        if (!isInForgotPass) {
            layoutForgotPassword.startAnimation(animForgotIn);
            layoutLogin.startAnimation(animLoginOut);
            layoutLogin.setVisibility(View.VISIBLE);
            setEnableView(isInForgotPass);
            isInForgotPass = true;
        } else {
            layoutLogin.startAnimation(animLoginIn);
            layoutForgotPassword.startAnimation(animForgotOut);
            layoutForgotPassword.setVisibility(View.VISIBLE);
            setEnableView(isInForgotPass);
            isInForgotPass = false;

        }
    }

    private void setEnableView(boolean isInForgotPass) {

        if (isInForgotPass) {
            edtUsername.setVisibility(View.VISIBLE);
            edtPassword.setVisibility(View.VISIBLE);
            edtEmailForgotPass.setVisibility(View.INVISIBLE);
        } else {
            edtUsername.setVisibility(View.INVISIBLE);
            edtPassword.setVisibility(View.INVISIBLE);
            edtEmailForgotPass.setVisibility(View.VISIBLE);
        }

        btnLoginSignIn.setClickable(isInForgotPass);
        btnForgotPassword.setClickable(isInForgotPass);
        loginFacebook.setClickable(isInForgotPass);
        tvSignup.setClickable(isInForgotPass);
        tvSkip.setClickable(isInForgotPass);

        btnRequestPass.setEnabled(!isInForgotPass);
        btnRequestPass.setClickable(!isInForgotPass);
        edtEmailForgotPass.setEnabled(!isInForgotPass);
        edtEmailForgotPass.setText("");
        edtEmailForgotPass.setFocusableInTouchMode(!isInForgotPass);
        imgForgotPassBack.setClickable(!isInForgotPass);
    }

    private RequestBody stringToRequestBody(String string) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, string);
        return body;
    }
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
