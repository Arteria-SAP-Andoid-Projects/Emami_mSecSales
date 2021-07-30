package com.arteriatech.emami.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.mbo.BirthdaysBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10526 on 09-12-2016.
 *
 */

public class BirthdayListAdapter extends RecyclerView.Adapter<BirthdayListAdapter.ViewHolder> {
    private ArrayList<BirthdaysBean> alBirthdays;
    private Context context;
    private String[] splitDayMonth;
    private RecyclerView recyclerView;
    private TextView tvEmptyListLay;

    public BirthdayListAdapter(ArrayList<BirthdaysBean> alBirthdays, Context context, String[] splitDayMonth,
                               RecyclerView recyclerView, TextView tvEmptyListLay) {
        this.alBirthdays = alBirthdays;
        this.context = context;
        this.splitDayMonth = splitDayMonth;
        this.recyclerView = recyclerView;
        this.tvEmptyListLay = tvEmptyListLay;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alerts_list_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        final BirthdaysBean birthdayBean = alBirthdays.get(i);


        viewHolder.tvRetailerName.setText(birthdayBean.getRetailerName());
        viewHolder.tv_retailer_owner_name.setText(birthdayBean.getOwnerName());

        if (!"".equalsIgnoreCase(birthdayBean.getAlertGUID())) {
            viewHolder.tvRetailerName.setText(birthdayBean.getAlertText());
            viewHolder.tv_retailer_owner_name.setText("");
        }
        if (birthdayBean.getAppointmentAlert()) {
            String startTime = UtilConstants.convertTimeOnly(birthdayBean.getAppointmentTime()).substring(0, 5);
            String endTime = UtilConstants.convertTimeOnly(birthdayBean.getAppointmentEndTime()).substring(0, 5);
            viewHolder.tv_retailer_owner_name.setText(birthdayBean.getAppointmentType() + " "
                    + startTime + " "
                    + endTime);
            viewHolder.iv_appointment_icon.setImageResource(R.drawable.ic_calendar);
        }
        if (!birthdayBean.getDOB().equalsIgnoreCase("") && ConstantsUtils.showImage(birthdayBean.getDOB())) {//birthdayBean.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0])
            viewHolder.iv_dob_icon.setVisibility(View.VISIBLE);
            viewHolder.iv_dob_icon.setImageResource(R.drawable.ic_birthday);
        } else {
            viewHolder.iv_dob_icon.setVisibility(View.GONE);
        }

        if (!birthdayBean.getAnniversary().equalsIgnoreCase("") && ConstantsUtils.showImage(birthdayBean.getAnniversary())) {//birthdayBean.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0])
            viewHolder.iv_anversiry_icon.setVisibility(View.VISIBLE);
            viewHolder.iv_anversiry_icon.setImageResource(R.drawable.ic_anverisary_new);
        } else {
            viewHolder.iv_anversiry_icon.setVisibility(View.GONE);
        }


        if (birthdayBean.getAppointmentAlert()) {
            viewHolder.iv_appointment_icon.setVisibility(View.VISIBLE);
            viewHolder.iv_appointment_icon.setImageResource(R.drawable.ic_calendar_32);
            viewHolder.iv_anversiry_icon.setVisibility(View.GONE);
            viewHolder.iv_dob_icon.setVisibility(View.GONE);
        } else {
            viewHolder.iv_appointment_icon.setVisibility(View.GONE);
        }
        if (!"".equalsIgnoreCase(birthdayBean.getAlertGUID())) {
            viewHolder.ivMobileNo.setVisibility(View.INVISIBLE);
            viewHolder.iv_anversiry_icon.setImageResource(R.drawable.ic_alertsmsg);
            viewHolder.tv_retailer_owner_name.setVisibility(View.GONE);
            viewHolder.iv_anversiry_icon.setVisibility(View.VISIBLE);
            viewHolder.iv_dob_icon.setVisibility(View.GONE);
        }
        viewHolder.ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!birthdayBean.getMobileNo().equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (birthdayBean.getMobileNo())));
                    context.startActivity(dialIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
       /* if (alBirthdays.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyListLay.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyListLay.setVisibility(View.GONE);
        }*/
        return alBirthdays!=null?alBirthdays.size():0;
    }

    public void removeItem(int position) {
        alBirthdays.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alBirthdays.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMobileNo;
        TextView tvRetailerName;
        TextView tv_retailer_owner_name;
        ImageView iv_dob_icon;
        private ImageView iv_anversiry_icon, iv_appointment_icon,ic_phone_icon_new;

        public ViewHolder(View view) {
            super(view);

            tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
            ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
            tv_retailer_owner_name = (TextView) view.findViewById(R.id.tv_retailer_owner_name);
            iv_dob_icon = (ImageView) view.findViewById(R.id.iv_dob_icon);
            iv_anversiry_icon = (ImageView) view.findViewById(R.id.iv_anversiry_icon);
            iv_appointment_icon = (ImageView) view.findViewById(R.id.iv_appointment_icon);
        }
    }
}
