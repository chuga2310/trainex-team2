package com.trainex.rest;

import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @Headers({
            "Accept: application/json",
            "X-Requested-With: XMLHttpRequest",
            "Content-Type: application/json"
    })
    @POST("register")
    Call<JsonElement> registerUser(@Body RequestBody body);



    @Headers("Content-Type: application/json")
    @POST("login")
    Call<JsonElement> loginUser(@Body  RequestBody body);


    @Headers("Content-Type: application/json")
    @POST("forget_password")
    Call<JsonElement> forgetPassword(@Body RequestBody body);

    @GET("group-class-event")
    Call<JsonElement> getListGroupClassEvent(@Query("page") int page);

    @GET("detail-event/{id_event}")
    Call<JsonElement> getDetailEventByID(@Path(value = "id_event", encoded = true) int idEvent);

    @GET("load-training-category")
    Call<JsonElement> getListCategory(@Query("page") int page);


    @Headers({
            "Accept: application/json",
            "X-Requested-With: XMLHttpRequest",
            "Content-Type: application/json"
    })
    @POST("request_booking_free")
    Call<JsonElement> requestFreeSession( @Body RequestBody body, @Header("Authorization") String token);


//    @Headers({"laravel_session: eyJpdiI6Ilg5NVRKNmVkQmJVaDNZTHIyVXBLZmc9PSIsInZhbHVlIjoiYmxTRDFFUFZhOXY5NWhOaFpHYWVpWW1JSFZRXC9EdzJWNWJqY0szbnhocU9WZm5mYUhFRWdlcVc5MWkxYnNZaGVDeFJiQUt6NG9OT2FKQTdON21hQUZnPT0iLCJtYWMiOiJhYTVlN2Y4MDUyNzY0MjNhYmYwZjI1ZjU0N2E1MWIxNzM3N2E3YzlkZjRiYTAzMTlhNDdmZGY5YjIyN2FlOWQ3In0"})
    @POST("get_user_info")
    Call<JsonElement> getUserInfo(@Header("Authorization") String token);


    @POST("get_event_booking")
    Call<JsonElement> getListEvetBooking(@Header("Authorization") String token);

    @POST("contact_us")
    Call<JsonElement> sendContact(@Header("Authorization") String token, @Body RequestBody body);


    @Headers({
            "Accept: application/json",
            "X-Requested-With: XMLHttpRequest",
            "Content-Type: application/json"
    })
    @POST("update_user_info")
    Call<JsonElement> updateUserInfo(@Header("Authorization") String token, @Body RequestBody body);

    @POST("training-category-detail")
    Call<JsonElement> getListTrainer(@Header("Authorization") String token, @Body RequestBody  body,@Query("page") int page);

    @GET("about_us")
    Call<JsonElement> getAboutUs();

    @POST("favorites-trainer")
    Call<JsonElement> getFavoritesTrainers(@Header("Authorization") String token);

    @GET("detail-trainer/{trainer_id}/{session_category_id}")
    Call<JsonElement> getDetailTrainer(@Header("Authorization") String token,@Path(value = "trainer_id", encoded = true) int trainer_id,@Path(value = "session_category_id", encoded = true) int session_category_id);

    @POST("review")
    Call<JsonElement> sendReview(@Header("Authorization") String token, @Body RequestBody body);

    @GET("trainer-locations")
    Call<JsonElement> getListLocations();

    @GET("filter-trainer-locations/{session_category_id}/{locations_name}")
    Call<JsonElement> filterTrainer(@Path(value = "session_category_id", encoded = true) int session_category_id,@Path(value = "locations_name", encoded = true) String locations_name, @Query("page") int page, @Query("rating") boolean isSortRating);

    @POST("favorites")
    Call<JsonElement> favorites(@Header("Authorization") String token, @Query("trainer_id") int id);

    @GET("report_form")
    Call<JsonElement> getReport();

    @GET("trainer_review_list/{trainer_id}")
    Call<JsonElement> getReviews(@Path(value = "trainer_id", encoded = true) int trainer_id, @Query("page") int page);

    @POST("booking")
    Call<JsonElement> sendBooking(@Header("Authorization") String token, @Body RequestBody body);

    @POST("get_session_booking")
    Call<JsonElement> getSessionBooking(@Header("Authorization") String token, @Query("page") int page);

    @POST("report")
    Call<JsonElement> sendReport(@Header("Authorization") String token,@Query("report_form_id") String reportID);

    @POST("get_event_booking")
    Call<JsonElement> getEventBooking(@Header("Authorization") String token, @Query("page") int page);

    @GET("event_trainer/{trainer_id}")
    Call<JsonElement> getEventByIdTrainer(@Path(value = "trainer_id", encoded = true) int trainer_id);


    @Headers({
            "X-Requested-With: XMLHttpRequest"
    })
    @POST("register_trainer")
    Call<JsonElement> registerTrainer(@Header("Authorization") String token, @Body RequestBody body);

    @POST("facebook_login")
    Call<JsonElement> getTokenByFacebook(@Body RequestBody body);

    @POST("get_notification")
    Call<JsonElement> getNotification(@Header("Authorization") String token, @Query("page") int page);

    @Headers({
            "Accept: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    @POST("change_avatar")
    Call<JsonElement> updateAvatar(@Header("Authorization") String token, @Body RequestBody body);

    @POST("resend_active")
    Call<JsonElement> resendActive(@Body RequestBody body);

    @GET("get_phone")
    Call<JsonElement> getPhone();

    @GET("logout")
    Call<JsonElement> callLogout(@Header("Authorization") String token);
}
