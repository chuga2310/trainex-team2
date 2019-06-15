package com.trainex.rest;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class NoteRestAPI {
    String fullname;
    String email;
    String password;
    String password_confirm;
    String username;
    String phone_number;
    String code;
    String dateTime;

    String register = "{\n" +
            "  \"fullname\": " + "\"" + fullname + "\"" + ",\n" +
            "  \"email\": " + "\"" + email + "\"" + ",\n" +
            "  \"password\": " + "\"" + password + "\"" + ",\n" +
            "  \"password_confirm\": " + "\"" + password_confirm + "\"" + ",\n" +
            "  \"username\":" + "\"" + username + "\"" + ",\n" +
            "  \"phone_number\":" + "\"" + phone_number + "\"" + "\n" +
            "}";
    String login = "{\n" +
            "  \"username\":" + "\"" + username + "\"" + "\n," +
            "  \"password\": " + "\"" + password + "\"" + "\n" +
            "}";

    String forgetPassword = "{\n" +
            "  \"email\": " + "\"" + email + "\"" + "\n" +
            "}";

    String resetPassword = "{\n" +
            "  \"email\": " + email + ",\n" +
            "  \"pasword\": " + password + ",\n" +
            "  \"code\":" + code + "\n" +
            "}";


    String dateTimeRequestFreeSession = "{\n" +
            "  \"date_time\": " + "\"" + dateTime + "\"" + "\n" +
            "}";

    public static RequestBody stringToRequestBody(String string) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, string);
        return body;
    }
}
