package com.trainex.adapter.listadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;
import com.trainex.R;

import com.trainex.model.Report;
import com.trainex.uis.main.ReportListActivity;

public class ReportAdapter extends ArrayAdapter<Report> {
    private Context context;
    ArrayList<Report> listReport;
    private CheckBox cbxReport;

    public ReportAdapter(@NonNull Context context, ArrayList<Report> listReport) {
        super(context, R.layout.item_list_report, listReport);
        this.context = context;
        this.listReport = listReport;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_list_report,parent,false);
        cbxReport = (CheckBox) view.findViewById(R.id.cbxReport);
        cbxReport.setText(listReport.get(position).getContent());
        cbxReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    ReportListActivity.addItemListID(listReport.get(position).getId());
                }else{
                    ReportListActivity.removeItemListID(listReport.get(position).getId());
                }
            }
        });
        return view;
    }
}
