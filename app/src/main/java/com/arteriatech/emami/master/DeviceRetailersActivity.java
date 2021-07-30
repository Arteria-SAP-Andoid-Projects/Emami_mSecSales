package com.arteriatech.emami.master;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10526 on 14-07-2016.
 */
public class DeviceRetailersActivity extends AppCompatActivity {
    private RetailerListAdapter retailerAdapter = null;
    private ArrayList<CustomerBean> alRetailerList = null;

    ListView lvRetailerList = null;
    TextView tvEmptyLay = null;

    TextView tvHeaderTitle = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_update_retailer));

        setContentView(R.layout.activity_update_retailer_list);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(DeviceRetailersActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {
        tvHeaderTitle = (TextView) findViewById(R.id.tv_retailer_header);
        lvRetailerList = (ListView) findViewById(R.id.lv_update_ret);
        tvEmptyLay = (TextView) findViewById(R.id.tv_empty_lay);

        tvHeaderTitle.setText(getString(R.string.Device_Retailers));

        getRetailerList();

        EditText edNameSearch = (EditText) findViewById(R.id.et_name_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                retailerAdapter.getFilter().filter(cs); //Filter from my adapter
                retailerAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /*Gets retailer list from device*/
    private void getRetailerList() {
        try {
            List<CustomerBean> listRetailers = OfflineManager.getDeviceRetailerList(Constants.ChannelPartners + "?$filter= sap.islocal() &$orderby=" + Constants.RetailerName + "%20asc");
            ArrayList<CustomerBean> alRetailerList;
            alRetailerList = (ArrayList<CustomerBean>) listRetailers;
            this.retailerAdapter = new RetailerListAdapter(this, alRetailerList);
            lvRetailerList.setAdapter(this.retailerAdapter);
            this.retailerAdapter.notifyDataSetChanged();

            if (alRetailerList != null && alRetailerList.size() > 0)
                tvEmptyLay.setVisibility(View.GONE);
            else
                tvEmptyLay.setVisibility(View.VISIBLE);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Adapter for retailer list*/
    private class RetailerListAdapter extends ArrayAdapter<CustomerBean> {
        private ArrayList<CustomerBean> retOriginalValues;
        private ArrayList<CustomerBean> retDisplayValues;
        private RetailerListFilter filter;

        public RetailerListAdapter(Context context, ArrayList<CustomerBean> items) {
            super(context, R.layout.update_retailer_list_item, items);
            this.retOriginalValues = items;
            this.retDisplayValues = items;
            alRetailerList = items;
        }

        @Override
        public int getCount() {
            return this.retDisplayValues != null ? this.retDisplayValues.size() : 0;
        }

        @Override
        public CustomerBean getItem(int item) {
            CustomerBean retListBean;
            retListBean = this.retDisplayValues != null ? this.retDisplayValues.get(item) : null;
            return retListBean;
        }

        private class ViewHolder {
            TextView tv_retailer_name;
            TextView tvFirstAddress, tv_retailer_msisdn;
        }

        ViewHolder holder = null;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.update_retailer_list_item, parent, false);
                holder = new ViewHolder();
                view.setTag(holder);

                holder.tv_retailer_name = (TextView) view.findViewById(R.id.tv_retailer_name);
                holder.tvFirstAddress = (TextView) view.findViewById(R.id.tv_address_one);
                holder.tv_retailer_msisdn = (TextView) view.findViewById(R.id.tv_retailer_msisdn);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final CustomerBean retailerListBean = retDisplayValues.get(position);

            holder.tv_retailer_name.setText(getString(R.string.str_concat_two_texts, retailerListBean.getRetailerName(), retailerListBean.getMobileNumber()));

            if (!retailerListBean.getAddress1().equalsIgnoreCase("")) {
                holder.tvFirstAddress.setText(getString(R.string.str_concat_two_texts_with_coma, retailerListBean.getAddress1(), retailerListBean.getTownDesc()));
            } else {
                holder.tvFirstAddress.setText(retailerListBean.getTownDesc());
            }

            holder.tv_retailer_msisdn.setText("");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListDetails(v.getId());
                }
            });
            view.setId(position);
            return view;
        }

        public android.widget.Filter getFilter() {
            if (filter == null) {
                filter = new RetailerListFilter();
            }
            return filter;
        }

        /**
         * This class search name based on customer name from list.
         */
        private class RetailerListFilter extends android.widget.Filter {
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                if (retOriginalValues == null) {
                    retOriginalValues = new ArrayList<>(retDisplayValues);
                }
                if (prefix == null || prefix.length() == 0) {
                    results.values = retOriginalValues;
                    results.count = retOriginalValues.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();
                    ArrayList<CustomerBean> filteredItems = new ArrayList<>();
                    int count = retOriginalValues.size();

                    for (int i = 0; i < count; i++) {
                        CustomerBean item = retOriginalValues.get(i);
                        String mStrRetName = item.getRetailerName().toLowerCase();
                        if (mStrRetName.contains(prefixString)) {
                            filteredItems.add(item);
                        }
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence prefix, FilterResults results) {
                //noinspection unchecked
                retDisplayValues = (ArrayList<CustomerBean>) results.values; // has the filtered values
                notifyDataSetChanged();
                alRetailerList = retDisplayValues;
                if (alRetailerList != null && alRetailerList.size() > 0)
                    tvEmptyLay.setVisibility(View.GONE);
                else
                    tvEmptyLay.setVisibility(View.VISIBLE);
            }
        }
    }

    /*On Device retailer Details*/
    private void onListDetails(int id) {
        CustomerBean retailerLB = alRetailerList.get(id);

        Intent intentRetailerDetails = new Intent(this, DeviceRetailerDetailsActivity.class);
        intentRetailerDetails.putExtra(Constants.RetailerName, retailerLB.getRetailerName());
        intentRetailerDetails.putExtra(Constants.CPNo, retailerLB.getCPNo() != null ? retailerLB.getCPNo() : "");
        intentRetailerDetails.putExtra(Constants.CPGUID, retailerLB.getCPGUID());
        intentRetailerDetails.putExtra(Constants.Etag, retailerLB.getEtag());
        intentRetailerDetails.putExtra(Constants.ResourcePath, retailerLB.getSetResourcePath());
        startActivity(intentRetailerDetails);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_map:
                displyLocation();
                break;
        }
        return true;
    }

    /*Navigate to map activity*/
    private void displyLocation() {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra(Constants.NAVFROM, Constants.UpdateList);
        startActivity(i);
    }
}
