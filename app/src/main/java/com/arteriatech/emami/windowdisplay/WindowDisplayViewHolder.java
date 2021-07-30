package com.arteriatech.emami.windowdisplay;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 17-03-2017.
 */
public class WindowDisplayViewHolder extends RecyclerView.ViewHolder {
    public final TextView tvSchemeName;
    public final LinearLayout llStatusColor;

    public WindowDisplayViewHolder(View view) {
        super(view);
        tvSchemeName = (TextView)view.findViewById(R.id.schem_name);
        llStatusColor = (LinearLayout)view.findViewById(R.id.ll_status_color);
    }
}
