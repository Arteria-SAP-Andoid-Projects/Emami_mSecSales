package com.arteriatech.emami.alerts;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.emami.mbo.AlertsBean;
import com.arteriatech.emami.msecsales.R;

import java.util.List;

/**
 * Created by e10763 on 3/8/2017.
 */
public class AlertsAdapter extends ArrayAdapter<AlertsBean> {

    private List<AlertsBean> retalertValues;
    private Activity context;


    public AlertsAdapter(Activity context, List<AlertsBean> objects) {
        super(context, R.layout.alerts_item_list, objects);
        this.retalertValues = objects;
        this.context = context;

    }

    @Override
    public int getCount() {
        return this.retalertValues != null ? this.retalertValues.size() : 0;
    }

    @Override
    public AlertsBean getItem(int item) {
        AlertsBean alertsBean;
        alertsBean = this.retalertValues != null ? this.retalertValues.get(item) : null;
        return alertsBean;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.alerts_item_list, parent, false);

        }

        ImageView ivalerts = (ImageView) view.findViewById(R.id.img_alerts);
        TextView tvalerts = (TextView) view.findViewById(R.id.tv_alert);
        final AlertsBean alertsBean = retalertValues.get(position);
        String alertText = alertsBean.getAlertText();
        if (!TextUtils.isEmpty(alertText)) {
            tvalerts.setText(alertsBean.getAlertText());
        } else {
            tvalerts.setText("No Alerts");
        }
        return view;
    }
}
