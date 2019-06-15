package com.trainex.fragment.insignup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.viewpageradapter.SignUpPagerAdapter;
import com.trainex.diaglog.ActivateAccountDialog;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.rest.NoteRestAPI;
import com.trainex.rest.RestClient;
import com.trainex.uis.login_signup.LoginActivity;
import com.trainex.uis.login_signup.SignUpActivity;
import com.trainex.uis.main.MainActivity;

public class SignupFragment extends Fragment {
    private EditText edtSignUpName;
    private EditText edtSignUpNickname;
    private EditText edtSignUpPhone;
    private EditText edtSignUpEmail;
    private EditText edtSignUpPassword;
    private EditText edtSignUpConfirmPassword;
    private Button btnSignUp;
    private TextView tvTerms;
    private LinearLayout signUpFacebook;
    private TextView tvSigninAnAcc;
    private TextView tvSkip;
    private String name, nickname, phone, email, pass, cfpass;
    SignUpPagerAdapter adapter;
    private CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        edtSignUpName = (EditText) view.findViewById(R.id.edtSignUpName);
        edtSignUpNickname = (EditText) view.findViewById(R.id.edtSignUpNickname);
        edtSignUpPhone = (EditText) view.findViewById(R.id.edtSignUpPhone);
        edtSignUpEmail = (EditText) view.findViewById(R.id.edtSignUpEmail);
        edtSignUpPassword = (EditText) view.findViewById(R.id.edtSignUpPassword);
        edtSignUpConfirmPassword = (EditText) view.findViewById(R.id.edtSignUpConfirmPassword);
        btnSignUp = (Button) view.findViewById(R.id.btnSignUp);
        tvTerms = (TextView) view.findViewById(R.id.tvTerms);
        signUpFacebook = (LinearLayout) view.findViewById(R.id.signUpFacebook);
        tvSigninAnAcc = (TextView) view.findViewById(R.id.tvSigninAnAcc);
        tvSkip = (TextView) view.findViewById(R.id.tvSignupSkip);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String tokenFacebook = loginResult.getAccessToken().getToken();
                        Log.e("tokenFacebook", tokenFacebook);
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
                                    if (codeBody == 200){
                                        String token  = body.get("data").getAsJsonObject().get("token").getAsString();
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        SharedPreferences.Editor editor = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE).edit();
                                        editor.putString("token", token);
                                        editor.apply();
                                        startActivity(intent);
                                    }else{
                                        CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Login Error!","Please check your account. It has something wrong", "Ok") {
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
                                Toast.makeText(getContext(), "Load failed", Toast.LENGTH_SHORT).show();
                            }
                        });
//
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        init();
        return view;
    }

    public void init() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnSignUp.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                name = edtSignUpName.getText().toString();
                nickname = edtSignUpNickname.getText().toString();
                phone = edtSignUpPhone.getText().toString();
                email = edtSignUpEmail.getText().toString();
                pass = edtSignUpPassword.getText().toString();
                cfpass = edtSignUpConfirmPassword.getText().toString();
                Log.e("signup", name + " " + nickname + " " + phone + " " + email + " " + pass + " " + cfpass);
                if (validateFields()) {
                    final CustomProgressDialog progressDialog = new CustomProgressDialog(getContext());
                    progressDialog.show();
                    String register = "{\n" +
                            "  \"fullname\": " + "\"" + name + "\"" + ",\n" +
                            "  \"email\": " + "\"" + email + "\"" + ",\n" +
                            "  \"password\": " + "\"" + pass + "\"" + ",\n" +
                            "  \"password_confirm\": " + "\"" + cfpass + "\"" + ",\n" +
                            "  \"username\":" + "\"" + nickname + "\"" + ",\n" +
                            "  \"phone_number\":" + "\"" + phone + "\"" + "\n" +
                            "}";

                    Call<JsonElement> callRegisterUser = RestClient.getApiInterface().registerUser(stringToRequestBody(register));
                    callRegisterUser.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful()) {
                                if (response.code() == 200) {
                                    showActiveAccountDialog();
                                }
                            } else {
                                try {
                                    int errorCode = response.code();
                                    String error = response.errorBody().string();
                                    Log.e("responseCode", response.code() + "");

                                    Log.e("reponseError", error);
                                    JsonParser parser = new JsonParser();
                                    JsonElement object = (JsonElement) parser.parse(error);
                                    if (errorCode == 422) {
                                        String contentAlert = "";
                                        JsonElement userElement = object.getAsJsonObject().get("username");
                                        String username;
                                        if (userElement != null) {
                                            username = userElement.getAsJsonArray().get(0).getAsString();
                                            contentAlert += (username + "\n");
                                        }
                                        JsonElement emailElement = object.getAsJsonObject().get("email");
                                        String email;
                                        if (emailElement != null) {
                                            email = emailElement.getAsJsonArray().get(0).getAsString();
                                            contentAlert += (email + "\n");
                                        }
                                        CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Register Error!", contentAlert, "Ok") {
                                            @Override
                                            public void doSomethingWhenDismiss() {

                                            }
                                        };
                                        alertDialog.show();
                                    }
                                } catch (Exception e) {
                                    Log.e("reponseErrorException", e.toString());
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.e("reponseErrorFailure", t.toString());
                            Toast.makeText(getContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.goToTerm();
            }
        });
        tvSigninAnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE).edit();
                editor.putString("token","");
                editor.apply();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        signUpFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private boolean validateFields() {
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Name is not empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (nickname.isEmpty()) {
            Toast.makeText(getContext(), "Username is not empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (phone.isEmpty()) {
            Toast.makeText(getContext(), "Phone is not empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email is not empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (pass.isEmpty()) {
            Toast.makeText(getContext(), "Password is not empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (cfpass.isEmpty()) {
            Toast.makeText(getContext(), "Confirm password is not empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (nickname.length() < 6) {
            Toast.makeText(getContext(), "Username must be at least 6 characters long", Toast.LENGTH_LONG).show();
            return false;
        } else if (phone.length() < 10 || phone.length() > 11) {
            Toast.makeText(getContext(), "Phone must between 10-11 characters long", Toast.LENGTH_LONG).show();
            return false;
        } else if (!validateEmail(email)) {
            Toast.makeText(getContext(), "Not a valid email", Toast.LENGTH_LONG).show();
            return false;
        } else if (pass.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters long", Toast.LENGTH_LONG).show();
            return false;
        } else if (!cfpass.equals(pass)) {
            Toast.makeText(getContext(), "Confirm password and Password do not match", Toast.LENGTH_LONG).show();
            return false;
        } else {
            boolean isHaveSpace = false;
            for (int i = 0; i < nickname.length(); i++) {
                if (nickname.charAt(i) == ' ') {
                    Toast.makeText(getContext(), "Username must don't have space", Toast.LENGTH_LONG).show();
                    isHaveSpace = true;
                }
            }
            if (!isHaveSpace) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void showActiveAccountDialog() {
        final ActivateAccountDialog activateAccountDialog = new ActivateAccountDialog(getContext());

        activateAccountDialog.show();
        activateAccountDialog.getBtnContinue().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomProgressDialog progressDialog = new CustomProgressDialog(getContext());
                progressDialog.show();
                String login = "{\n" +
                        "  \"username\":" + "\"" + nickname + "\"" + "\n," +
                        "  \"password\": " + "\"" + pass + "\"" + "\n" +
                        "}";
                Call<JsonElement> callLogin = RestClient.getApiInterface().loginUser(stringToRequestBody(login));
                callLogin.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            int code = response.code();
                            Log.e("responeCode", code + "");
                            if (code == 200) {
                                JsonElement body = response.body();
                                int codeBody = body.getAsJsonObject().get("code").getAsInt();
                                if (codeBody == 200) {
                                    JsonObject data = body.getAsJsonObject().get("data").getAsJsonObject();
                                    String token = data.get("token").getAsString();
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE).edit();
                                    editor.putString("token", token);
                                    editor.apply();
                                    startActivity(intent);
                                } else if (codeBody == 2) {
                                    String error = body.getAsJsonObject().get("error").getAsString();
                                    CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Login Error!", error, "Ok") {
                                        @Override
                                        public void doSomethingWhenDismiss() {

                                        }
                                    };
                                    alertDialog.show();
                                } else if (codeBody == 3) {
                                    String title = "Sorry !";
                                    String content = "Your account is not activated, please go to your email to active";
                                    String textButton = "Ok";
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), title, content, textButton) {
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
                                Log.e("ErrorException", e.toString());
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
        });
        activateAccountDialog.getTvResend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomProgressDialog progressDialog = new CustomProgressDialog(getContext());
                progressDialog.show();
                String request = "{\n" +
                        "  \"email\": " + "\"" + email + "\"" + "\n" +
                        "}";
                Call<JsonElement> callResendActive = RestClient.getApiInterface().resendActive(stringToRequestBody(request));
                callResendActive.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        int code = response.code();
                        if (response.isSuccessful()){
                            JsonObject body = response.body().getAsJsonObject();
                            int codeBody = body.get("code").getAsInt();
                            progressDialog.dismiss();
                            if (codeBody == 200){
                                Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
                            }else{

                                CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Send error", body.get("error").getAsString(), "Ok") {
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
                        Toast.makeText(getContext(), "Load failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private RequestBody stringToRequestBody(String string) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, string);
        return body;
    }
}
