package com.trainex.fragment.inmain;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.listadapter.CategoryInDetailTrainerAdapter;
import com.trainex.adapter.listadapter.EventOnDetailTrainerAdapter;
import com.trainex.adapter.listadapter.ListSessionAdapter;
import com.trainex.adapter.listadapter.ReviewsAdapter;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.CategoryInDetailTrainer;
import com.trainex.model.Event;
import com.trainex.model.Review;
import com.trainex.model.Session;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.AddReviewActivity;
import com.trainex.uis.main.MainActivity;
import com.trainex.uis.main.ReportListActivity;
import com.trainex.uis.main.RequestFreeSessionActivity;
import com.trainex.uis.main.ReviewsActivity;

public class DetailTrainerFragment extends Fragment {
    private ImageView imgBack;
    private TextView title;
    private ImageView imgAvatar;
    private ImageView imgFavorites;
    private RatingBar ratingBar;
    private TextView tvLocation;
    private TextView tvTime;
    private TextView tvPrefferedLocations;
    private TextView tvAboutMe;
    private Button btnShowMore;
    private TextView tvQualification;
    private TextView tvSpaccalities;
    private ImageView toogleEvents;
    private LinearLayout layoutEvents;
    private ArrayList<Event> listEvent = new ArrayList<>();
    ;
    private ListView lvEvents;
    private boolean isShowingEvent = false;
    private ListView listTrainersReview;
    private ArrayList<Review> listReviews = new ArrayList<>();
    private Button btnReview;
    private Button btnReqestFree;
    private Button btnBuyASession;
    private View view;
    private NestedScrollView scrollView;
    private ListView lvSession;
    private ArrayList<Session> listSessions = new ArrayList<>();
    private int idTrainer;
    private int idCategory;
    private String token;
    private ListSessionAdapter sessionAdapter;
    private Call<JsonElement> callGetDetailTrainer;
    private FrameLayout layoutSession;
    private ReviewsAdapter reviewsAdapter;
    private RelativeLayout loadingPanel;
    private ProgressBar progessBar;
    public static ListView lvSessionInCategory;
    private static ArrayList<CategoryInDetailTrainer> listCategoryInDetail;
    CategoryInDetailTrainerAdapter categoryInDetailTrainerAdapter;
    private EventOnDetailTrainerAdapter eventAdapter;
    private boolean isLoading;
    private LinearLayout viewImgBack;
    public static final int REQUEST_REVIEWS = 153;
    private Button btnShowMoreAbout;
    private boolean isShowAllAbout = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        idTrainer = getArguments().getInt("idTrainer", -1);
        idCategory = getArguments().getInt("idCategory", 0);
        SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
        token = prefs.getString("token", "");

    }

    boolean isFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_trainer, container, false);
        listCategoryInDetail = new ArrayList<>();
        init();

        bind();
        //List Event
        if (idCategory == 0) {
            lvSession.setVisibility(View.GONE);
        } else {
            lvSessionInCategory.setVisibility(View.GONE);
        }
        eventAdapter = new EventOnDetailTrainerAdapter(getContext(), listEvent);
        lvEvents.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

        //List Trainer
        reviewsAdapter = new ReviewsAdapter(getContext(), listReviews);
        listTrainersReview.setAdapter(reviewsAdapter);
        eventAdapter.notifyDataSetChanged();

        categoryInDetailTrainerAdapter = new CategoryInDetailTrainerAdapter(getContext(), listCategoryInDetail);
        lvSessionInCategory.setAdapter(categoryInDetailTrainerAdapter);

        getData();
        getReviews();
        return view;
    }

    private void init() {
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        title = (TextView) view.findViewById(R.id.title);
        imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        imgFavorites = (ImageView) view.findViewById(R.id.imgFavorites);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvPrefferedLocations = (TextView) view.findViewById(R.id.tvPrefferedLocations);
        tvAboutMe = (TextView) view.findViewById(R.id.tvAboutMe);
        btnShowMore = (Button) view.findViewById(R.id.btnShowMore);
        tvQualification = (TextView) view.findViewById(R.id.tvQualification);
        tvSpaccalities = (TextView) view.findViewById(R.id.tvSpaccalities);
        toogleEvents = (ImageView) view.findViewById(R.id.toogleEvents);
        layoutEvents = view.findViewById(R.id.layoutEvents);
        listTrainersReview = (ListView) view.findViewById(R.id.listTrainersReview);
        lvEvents = (ListView) view.findViewById(R.id.listEvents);
        btnReview = view.findViewById(R.id.btnReview);
        btnReqestFree = view.findViewById(R.id.btnReqestFree);
        btnBuyASession = view.findViewById(R.id.btnBuyASession);
        lvSession = (ListView) view.findViewById(R.id.listSession);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        layoutSession = (FrameLayout) view.findViewById(R.id.layoutSession);
        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        progessBar = (ProgressBar) view.findViewById(R.id.progessBar);
        lvSessionInCategory = (ListView) view.findViewById(R.id.listSessionInCategory);
        viewImgBack = (LinearLayout) view.findViewById(R.id.viewImgBack);
        btnShowMoreAbout = (Button) view.findViewById(R.id.btnShowMoreAbout);
    }

    private void bind() {
        if (!isShowingEvent) {
            toogleEvents.setImageResource(R.mipmap.show_event_on_details);
            layoutEvents.setVisibility(View.GONE);
        }
        toogleEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShowingEvent) {
                    if (listEvent.size() == 0) {
                        getDataEvent();
                    }
                    toogleEvents.setImageResource(R.mipmap.hide_event_on_details);
                    layoutEvents.setVisibility(View.VISIBLE);
                    isShowingEvent = true;
                } else {
                    toogleEvents.setImageResource(R.mipmap.show_event_on_details);
                    layoutEvents.setVisibility(View.GONE);
                    isShowingEvent = false;
                }
            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!token.equalsIgnoreCase("")) {
                    Intent intent = new Intent(getContext(), AddReviewActivity.class);
                    intent.putExtra("idTrainer", idTrainer);
                    startActivityForResult(intent, REQUEST_REVIEWS);
                } else {
                    AlertLoginDialog loginDialog = new AlertLoginDialog(getContext(), "Review Error!", "You need login to report");
                    loginDialog.show();
                }
            }
        });
        btnReqestFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (token.equalsIgnoreCase("")) {
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(getContext(), "Error", "You must login to request free!");
                    alertLoginDialog.show();
                } else {
                    Intent intent = new Intent(getContext(), RequestFreeSessionActivity.class);
                    intent.putExtra("idTrainer", idTrainer);
                    startActivity(intent);
                }
            }
        });
        sessionAdapter = new ListSessionAdapter(getContext(), listSessions);
        lvSession.setAdapter(sessionAdapter);

        btnBuyASession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, layoutSession.getTop());
                    }
                });
            }
        });
        viewImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        progessBar.getIndeterminateDrawable()
                .setColorFilter(getContext().getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        btnShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReviewsActivity.class);
                intent.putExtra("idTrainer", idTrainer);
                getContext().startActivity(intent);
            }
        });


        btnShowMoreAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowAllAbout == false) {
                    tvAboutMe.setMaxLines(Integer.MAX_VALUE);
                    btnShowMoreAbout.setText("Show less");
                    isShowAllAbout = true;
                } else {
                    tvAboutMe.setMaxLines(3);
                    btnShowMoreAbout.setText("Show more");
                    isShowAllAbout = false;
                }
            }
        });
    }

    String description = "";

    private void getData() {
        callGetDetailTrainer = RestClient.getApiInterface().getDetailTrainer(token, idTrainer, idCategory);
        Log.e("category", idCategory + "");
        callGetDetailTrainer.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body().getAsJsonObject();
                    JsonObject data = body.get("data").getAsJsonObject();
                    JsonObject detail = data.get("trainerDetail").getAsJsonObject();
                    float rating = 0;
                    Log.e("detail", body.toString());
                    if (!data.get("rating").isJsonNull()) {
                        rating = data.get("rating").getAsJsonObject().get("rating").getAsFloat();
                    }
                    JsonArray listReviews = data.get("review_list").getAsJsonArray();
                    isFavorite = false;
                    if (!data.get("favorite").isJsonNull()) {
                        isFavorite = data.get("favorite").getAsBoolean();
                    }
                    int trainerId = idTrainer;
                    String name = "";
                    if (!detail.get("trainer_name").isJsonNull()) {
                        name = detail.get("trainer_name").getAsString();
                    }
                    String resAvatar = "";
                    if (!detail.getAsJsonObject().get("trainer_avatar").isJsonNull()) {
                        resAvatar = detail.getAsJsonObject().get("trainer_avatar").getAsString();
                    }
                    String address = "";
                    if (!detail.get("trainer_address").isJsonNull()) {
                        address = detail.get("trainer_address").getAsString();
                    }
                    if (!detail.get("trainer_description").isJsonNull()) {
                        description = detail.get("trainer_description").getAsString();
                    }
                    String prefferedTime = "";
                    if (!detail.get("trainer_preffered_time").isJsonNull()) {
                        prefferedTime = detail.get("trainer_preffered_time").getAsString();
                    }
                    String prefferedLocaion = "";
                    if (!detail.get("trainer_preffered_locations").isJsonNull()) {
                        prefferedLocaion = detail.get("trainer_preffered_locations").getAsString();
                    }
                    String exp = "";
                    if (!detail.get("exp").isJsonNull()) {
                        exp = detail.get("exp").getAsString();
                    }
                    String specialities = "";
                    if (!detail.get("specialities").isJsonNull()) {
                        specialities = detail.get("specialities").getAsString();
                    }
                    if (idCategory > 0) {
                        int idSession = detail.get("session_id").getAsInt();
                        if (detail.has("1_session")) {
                            if (!detail.get("1_session").isJsonNull()) {
                                if (detail.get("1_session").getAsInt() > 0) {
                                    int price = detail.get("1_session").getAsInt();
                                    listSessions.add(new Session(idSession, price, "1_session"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        if (detail.has("6_sessions")) {
                            if (!detail.get("6_sessions").isJsonNull()) {
                                if (detail.get("6_sessions").getAsInt() > 0) {
                                    int price = detail.get("6_sessions").getAsInt();
                                    listSessions.add(new Session(idSession, price, "6_sessions"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("12_sessions")) {
                            if (!detail.get("12_sessions").isJsonNull()) {
                                if (detail.get("12_sessions").getAsInt() > 0) {
                                    int price = detail.get("12_sessions").getAsInt();
                                    listSessions.add(new Session(idSession, price, "12_sessions"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("24_session")) {
                            if (!detail.get("24_session").isJsonNull()) {
                                if (detail.get("24_session").getAsInt() > 0) {
                                    int price = detail.get("24_session").getAsInt();
                                    listSessions.add(new Session(idSession, price, "24_session"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("48_sessions")) {
                            if (!detail.get("48_sessions").isJsonNull()) {
                                if (detail.get("48_sessions").getAsInt() > 0) {
                                    int price = detail.get("48_sessions").getAsInt();
                                    listSessions.add(new Session(idSession, price, "48_sessions"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("100_sessions")) {
                            if (!detail.get("100_sessions").isJsonNull()) {
                                if (detail.get("100_sessions").getAsInt() > 0) {
                                    int price = detail.get("100_sessions").getAsInt();
                                    listSessions.add(new Session(idSession, price, "100_sessions"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("1_month")) {
                            if (!detail.get("1_month").isJsonNull()) {
                                if (detail.get("1_month").getAsInt() > 0) {
                                    int price = detail.get("1_month").getAsInt();
                                    listSessions.add(new Session(idSession, price, "1_month"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("3_months")) {
                            if (!detail.get("3_months").isJsonNull()) {
                                if (detail.get("3_months").getAsInt() > 0) {
                                    int price = detail.get("3_months").getAsInt();
                                    listSessions.add(new Session(idSession, price, "3_months"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("6_months")) {
                            if (!detail.get("6_months").isJsonNull()) {
                                if (detail.get("6_months").getAsInt() > 0) {
                                    int price = detail.get("6_months").getAsInt();
                                    listSessions.add(new Session(idSession, price, "6_months"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (detail.has("12_months")) {
                            if (!detail.get("12_months").isJsonNull()) {
                                if (detail.get("12_months").getAsInt() > 0) {
                                    int price = detail.get("12_months").getAsInt();
                                    listSessions.add(new Session(idSession, price, "12_months"));
                                    sessionAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        setListViewHeightBasedOnChildren(lvSession, (getItemHeightofListView(lvSession, 0)) * (listSessions.size() - 1));
                    } else {
                        JsonArray listCategory = detail.get("category_list").getAsJsonArray();
                        for (JsonElement x : listCategory) {
                            JsonObject obj = x.getAsJsonObject();
                            int idCategory = obj.get("category_id").getAsInt();
                            String nameCategory = obj.get("category_name").getAsString();
                            int idSession = obj.get("session_id").getAsInt();
                            ArrayList<Session> listSessionInCategory = new ArrayList<>();
                            if (obj.has("1_session")) {
                                if (!obj.get("1_session").isJsonNull()) {
                                    if (obj.get("1_session").getAsInt() > 0) {
                                        int price = obj.get("1_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "1_session"));
                                    }
                                }
                            }
                            if (obj.has("6_session")) {
                                if (!obj.get("6_session").isJsonNull()) {
                                    if (obj.get("6_session").getAsInt() > 0) {
                                        int price = obj.get("6_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "6_session"));
                                    }
                                }
                            }
                            if (obj.has("12_session")) {
                                if (!obj.get("12_session").isJsonNull()) {
                                    if (obj.get("12_session").getAsInt() > 0) {
                                        int price = obj.get("12_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "12_session"));
                                    }
                                }
                            }
                            if (obj.has("24_session")) {
                                if (!obj.get("24_session").isJsonNull()) {
                                    if (obj.get("24_session").getAsInt() > 0) {
                                        int price = obj.get("24_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "24_session"));
                                    }
                                }
                            }
                            if (obj.has("48_session")) {
                                if (!obj.get("48_session").isJsonNull()) {
                                    if (obj.get("48_session").getAsInt() > 0) {
                                        int price = obj.get("48_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "48_session"));
                                    }
                                }
                            }
                            if (obj.has("100_session")) {
                                if (!obj.get("100_session").isJsonNull()) {
                                    if (obj.get("100_session").getAsInt() > 0) {
                                        int price = obj.get("100_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "100_session"));
                                    }
                                }
                            }
                            if (obj.has("1_month_session")) {
                                if (!obj.get("1_month_session").isJsonNull()) {
                                    if (obj.get("1_month_session").getAsInt() > 0) {
                                        int price = obj.get("1_month_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "1_month_session"));
                                    }
                                }
                            }
                            if (obj.has("3_month_session")) {
                                if (!obj.get("3_month_session").isJsonNull()) {
                                    if (obj.get("3_month_session").getAsInt() > 0) {
                                        int price = obj.get("3_month_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "3_month_session"));
                                    }
                                }
                            }
                            if (obj.has("6_month_session")) {
                                if (!obj.get("6_month_session").isJsonNull()) {
                                    if (obj.get("6_month_session").getAsInt() > 0) {
                                        int price = obj.get("6_month_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "6_month_session"));
                                    }
                                }
                            }
                            if (obj.has("12_month_session")) {
                                if (!obj.get("12_month_session").isJsonNull()) {
                                    if (obj.get("12_month_session").getAsInt() > 0) {
                                        int price = obj.get("12_month_session").getAsInt();
                                        listSessionInCategory.add(new Session(idSession, price, "12_month_session"));
                                    }
                                }
                            }

                            CategoryInDetailTrainer categoryInDetailTrainer = new CategoryInDetailTrainer(idCategory, nameCategory, listSessionInCategory, idSession, true);
                            Log.e("listCategory", "onResponse: " + listSessionInCategory.size());
                            listCategoryInDetail.add(categoryInDetailTrainer);

                        }
                        categoryInDetailTrainerAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(lvSessionInCategory, getItemHeightofListView(lvSessionInCategory, 0) * (listCategoryInDetail.size() - 1));
                    }
                    title.setText(name);
                    Glide.with(getContext()).load(resAvatar).into(imgAvatar);
                    if (isFavorite) {
                        imgFavorites.setImageResource(R.mipmap.icon_heart_active);
                    } else {
                        imgFavorites.setImageResource(R.mipmap.icon_heart_deactive);
                    }
                    imgFavorites.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!token.equalsIgnoreCase("")) {
                                SharedPreferences prefs = getContext().getSharedPreferences("MY_SHARE_PREFERENCE", getContext().MODE_PRIVATE);
                                token = prefs.getString("token", "");
                                Call<JsonElement> callFavorite = RestClient.getApiInterface().favorites(token, idTrainer);
                                callFavorite.enqueue(new Callback<JsonElement>() {
                                    @Override
                                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                        Log.d("code", response.code() + "");
                                        if (response.isSuccessful()) {
                                            JsonObject body = response.body().getAsJsonObject();
                                            int codeBody = body.get("code").getAsInt();
                                            if (codeBody == 11) {
                                                imgFavorites.setImageResource(R.mipmap.icon_heart_active);
                                                isFavorite = true;
                                            } else if (codeBody == 12) {
                                                imgFavorites.setImageResource(R.mipmap.icon_heart_deactive);
                                                isFavorite = false;
                                            } else {
                                                String s = "";
                                                if (body.has("error")) {
                                                    s = body.get("error").getAsString();
                                                }
                                                if (body.has("message")) {
                                                    s = body.get("message").getAsString();
                                                }
                                                CustomAlertDialog alertDialog = new CustomAlertDialog(getContext(), "Error!", s, "Ok") {
                                                    @Override
                                                    public void doSomethingWhenDismiss() {
                                                    }
                                                };
                                                alertDialog.show();
                                            }

                                        } else {
                                            try {
                                                Log.e("errorBody", response.errorBody().string());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonElement> call, Throwable t) {

                                    }
                                });
                            } else {
                                AlertLoginDialog alertLoginDialog = new AlertLoginDialog(getContext(), "Favorite Error!", "You must login to favorite");
                                alertLoginDialog.show();
                            }

                        }
                    });


                    ratingBar.setRating(rating);
                    tvLocation.setText(address);
                    tvTime.setText(prefferedTime);
                    tvPrefferedLocations.setText(prefferedLocaion);
                    tvAboutMe.setText(description);
                    tvQualification.setText(exp);
                    tvSpaccalities.setText(specialities);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, 0);
                            loadingPanel.setVisibility(View.GONE);
                            scrollView.animate()
                                    .alpha(1.0f)
                                    .setDuration(200)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            scrollView.setVisibility(View.VISIBLE);
                                        }
                                    });
                        }
                    });

                    tvAboutMe.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (tvAboutMe.getLineCount() < 4) {
                                btnShowMoreAbout.setVisibility(View.GONE);
                                tvAboutMe.setMaxLines(tvAboutMe.getLineCount());
                            } else {
                                btnShowMoreAbout.setVisibility(View.VISIBLE);
                                tvAboutMe.setMaxLines(3);
                                tvAboutMe.setEllipsize(TextUtils.TruncateAt.END);
                                tvAboutMe.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataEvent() {
        Call<JsonElement> callGetEventByIdTrainer = RestClient.getApiInterface().getEventByIdTrainer(idTrainer);
        callGetEventByIdTrainer.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body().getAsJsonObject();
                    int codeBody = body.get("code").getAsInt();
                    Log.e("bodyEvent", body.toString());
                    if (codeBody == 200) {
                        JsonArray data = body.get("data").getAsJsonArray();

                        if (data.size() > 0) {
                            for (JsonElement x : data) {
                                JsonObject obj = x.getAsJsonObject();
                                String name = obj.get("name").getAsString();
                                String location = obj.get("location").getAsString();
                                String date = obj.get("date_time").getAsString();
                                String timeShow = "";
                                try {
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                    cal.setTime(sdf.parse(date));

                                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd - MMM - yyyy, hh:mm a", Locale.ENGLISH);

                                    if (cal != null) {
                                        timeShow = sdfDate.format(cal.getTime());

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int price = obj.get("price").getAsInt();
                                int idEvent = obj.get("id").getAsInt();
                                String resImage = obj.get("event_photo").getAsString();
                                String description = obj.get("description").getAsString();
                                Event event = new Event(idEvent, resImage, name, "", price, timeShow, "", location, description);
                                listEvent.add(event);

                            }
                            setListViewHeightBasedOnChildren(lvEvents, getItemHeightofListView(lvEvents, 0) * (listEvent.size() - 1));
                            eventAdapter.notifyDataSetChanged();
                            isLoading = true;

                            Log.e("sizeEvent", listEvent.size() + "");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }

    public static void setListViewHeightBasedOnChildren
            (ListView listView, int addmore) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight +
                (listView.getDividerHeight() * (listAdapter.getCount() - 1) + addmore);

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    // To calculate the total height of all items in ListView call with items = adapter.getCount()
    public static int getItemHeightofListView(ListView listView, int items) {
        ListAdapter adapter = listView.getAdapter();

        int grossElementHeight = 0;
        for (int i = 0; i < items; i++) {
            View childView = adapter.getView(i, null, listView);
            childView.measure(UNBOUNDED, UNBOUNDED);
            grossElementHeight += childView.getMeasuredHeight();
        }
        return grossElementHeight;
    }

    private void getReviews() {
        Call<JsonElement> callGetReviews = RestClient.getApiInterface().getReviews(idTrainer, 1);
        callGetReviews.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Log.e("body", response.body().getAsJsonObject().toString());
                    JsonObject body = response.body().getAsJsonObject();
                    int codeBody = body.get("code").getAsInt();
                    if (codeBody == 200) {
                        JsonObject data = body.get("data").getAsJsonObject();
                        int totalComment = data.get("total").getAsInt();
                        if (totalComment > 5) {
                            btnShowMore.setVisibility(View.VISIBLE);
                        }
                        JsonArray dataReviews = data.get("data").getAsJsonArray();
                        if (dataReviews.size() > 0) {
                            for (JsonElement x : dataReviews) {
                                JsonObject reviewObject = x.getAsJsonObject();
                                String name = "";
                                if (!reviewObject.get("username").isJsonNull()) {
                                    name = reviewObject.get("username").getAsString();
                                }
                                int idUser = -1;
                                if (!reviewObject.get("user_id").isJsonNull()) {
                                    idUser = reviewObject.get("user_id").getAsInt();
                                }
                                float rating = 0;
                                if (!reviewObject.get("rating").isJsonNull()) {
                                    rating = reviewObject.get("rating").getAsFloat();
                                }
                                String comment = "";
                                if (!reviewObject.get("comment").isJsonNull()) {
                                    comment = reviewObject.get("comment").getAsString();
                                }
                                String resAvatar = "";
                                if (!reviewObject.get("avatar").isJsonNull()) {
                                    resAvatar = reviewObject.get("avatar").getAsString();
                                }
                                String time = "";
                                if (!reviewObject.get("created_at").isJsonNull()) {
                                    time = reviewObject.get("created_at").getAsString();
                                }
                                Review review = new Review(idUser, name, rating, comment, resAvatar, time);
                                listReviews.add(review);
                            }
                            reviewsAdapter.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(listTrainersReview, (getItemHeightofListView(listTrainersReview, 0)) * (listReviews.size() - 1));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REVIEWS) {
            if (resultCode == getActivity().RESULT_OK) {
                boolean isReload = data.getBooleanExtra("isReload", false);
                if (isReload) {
                    listReviews.clear();
                    listSessions.clear();
                    getData();
                    getReviews();
                }
            }
        }
    }
}
