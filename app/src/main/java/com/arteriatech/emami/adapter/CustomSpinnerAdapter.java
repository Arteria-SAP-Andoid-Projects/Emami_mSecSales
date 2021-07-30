package com.arteriatech.emami.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter {

	private ArrayList<CustomerBean> custom_list;
	private LayoutInflater layoutInflater;
	private ViewHolder holder;
	private static String selected = "";	
	PopupWindow pw;
	
	public static String getSelected() {
		return selected;
	}


	public CustomSpinnerAdapter(Context context, ArrayList<CustomerBean> items, PopupWindow pw) {
		this.pw=pw;
		custom_list = new ArrayList<CustomerBean>();
		custom_list.clear();
		custom_list.addAll(items);
		layoutInflater = LayoutInflater.from(context);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return custom_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	
		if (convertView == null) {
			
			convertView = layoutInflater.inflate(R.layout.drop_down_item, null);
			holder = new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.tv_dropdown);
			holder.chkbox = (ImageView) convertView.findViewById(R.id.iv_dropdpwn);
			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
	CustomerBean custom_obj=custom_list.get(position);
		holder.tv.setText(custom_obj.getRetailerName());

	

		if(CustomerBean.SELECTED__SPINNER_INDEX==position)
			holder.chkbox.setBackgroundResource(android.R.drawable.radiobutton_on_background);
		else
			holder.chkbox.setBackgroundResource(android.R.drawable.radiobutton_off_background);
		return convertView;
	}


		
	private class ViewHolder {
		TextView tv;
		ImageView chkbox;
	}
	
	
	
	
	public CustomerBean setSelected(int position){


		return custom_list.get(position);
			}
}
