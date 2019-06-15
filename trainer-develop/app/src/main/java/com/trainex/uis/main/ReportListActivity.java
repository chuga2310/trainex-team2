package com.trainex.uis.main;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.listadapter.ReportAdapter;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.Report;
import com.trainex.rest.RestClient;

public class ReportListActivity extends AppCompatActivity {
    private ImageButton imgClose;
    private Button btnReportSubmit;
    private ListView lvReport;
    private ArrayList<Report> listReport = new ArrayList<>();
    private ReportAdapter adapter;
    private int idTrainer;
    String token;
    private static ArrayList<Integer> listIdChecked=  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        idTrainer = getIntent().getIntExtra("idTrainer", -1);
        imgClose = (ImageButton) findViewById(R.id.imgClose);
        btnReportSubmit = (Button) findViewById(R.id.btnReportSubmit);
        lvReport = (ListView) findViewById(R.id.lvReport);
        bind();
    }

    private void bind() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnReportSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report();
            }
        });
        adapter = new ReportAdapter(ReportListActivity.this, listReport);
        lvReport.setAdapter(adapter);
        getDataReport();


    }

    public static void addItemListID(int idTrainer){
        listIdChecked.add(idTrainer);
    }
    public static void removeItemListID(int idTrainer){
        if (listIdChecked.size()>0){
            for (int i =  0; i<listIdChecked.size();i++){
                if (idTrainer == listIdChecked.get(i)){
                    listIdChecked.remove(i);
                }
            }
        }
    }
    private void report(){
        String reportListId ="";
        if (listIdChecked.size()>0){
            for (int i= 0; i< listIdChecked.size();i++){
                if (i<listIdChecked.size()-1){
                    reportListId+=listIdChecked.get(i)+",";
                }else{
                    reportListId+=listIdChecked.get(i);
                }
            }
        }
        Call<JsonElement> sendReport = RestClient.getApiInterface().sendReport(token,reportListId);
        sendReport.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()){
                    JsonObject body = response.body().getAsJsonObject();
                    int code = body.get("code").getAsInt();
                    if (code == 200){
                        CustomAlertDialog dialog = new CustomAlertDialog(ReportListActivity.this, "Report Listing", "Your request send to admin, We shall look in to it at the earliest", "Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {
                                finish();
                            }
                        };
                        dialog.show();
                    } else{
                        String s = "";
                        if (body.has("error")){
                            s = body.get("error").getAsString();
                        }
                        if (body.has("message")){
                            s = body.get("message").getAsString();
                        }
                        CustomAlertDialog alertDialog = new CustomAlertDialog(ReportListActivity.this,"Report Error!",s,"Ok") {
                            @Override
                            public void doSomethingWhenDismiss() {
                                finish();
                            }
                        };
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(ReportListActivity.this, "Load failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getDataReport() {
        Call<JsonElement> callGetReport = RestClient.getApiInterface().getReport();
        callGetReport.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body().getAsJsonObject();
                    int codeBody = body.get("code").getAsInt();
                    if (codeBody == 200) {
                        JsonArray data = body.get("data").getAsJsonArray();
                        for (JsonElement x : data) {
                            int id = x.getAsJsonObject().get("id").getAsInt();
                            String content = x.getAsJsonObject().get("report_content").getAsString();
                            Report report = new Report(id, content);
                            listReport.add(report);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(ReportListActivity.this, "Load failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
