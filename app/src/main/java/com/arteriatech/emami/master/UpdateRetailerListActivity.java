package com.arteriatech.emami.master;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ExpandAnimation;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${e10526} on ${30-04-2016}.
 */
public class UpdateRetailerListActivity extends AppCompatActivity {
    private RetailerListAdapter retailerAdapter = null;
    private ArrayList<CustomerBean> alRetailerList = new ArrayList<>();
    private ProgressDialog pdLoadDialog;
    ListView lvRetailerList = null;
    TextView tvEmptyLay = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_update_retailer));

        setContentView(R.layout.activity_update_retailer_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(UpdateRetailerListActivity.this)) {
            initUI();
        }

    }

    /*Initializes UI*/
    void initUI() {

        lvRetailerList = (ListView) findViewById(R.id.lv_update_ret);
        tvEmptyLay = (TextView) findViewById(R.id.tv_empty_lay);

//        getRetailerList();

        EditText edNameSearch = (EditText) findViewById(R.id.et_name_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (retailerAdapter != null) {
                    retailerAdapter.getFilter().filter(cs); //Filter from my adapter
                    retailerAdapter.notifyDataSetChanged(); //Update my view
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
        loadAsyncTask();
    }

    /*Gets list of retailer to update*/
    private void getRetailerList() {
        try {
            List<CustomerBean> listRetailers = OfflineManager.getRetailerList(Constants.ChannelPartners + "?$filter=(" + Constants.CPNo + " ne '' and " + Constants.CPNo + " ne null)" +
                    " and " + Constants.ApprvlStatusID + " eq '03' &$orderby=" + Constants.RetailerName + "%20asc", "");
            alRetailerList.clear();
            alRetailerList = (ArrayList<CustomerBean>) listRetailers;


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
    }

    /*Adapter for retailer to display in List*/
    private class RetailerListAdapter extends ArrayAdapter<CustomerBean> {
        private ArrayList<CustomerBean> retOriginalValues;
        private ArrayList<CustomerBean> retDisplayValues;
        private RetailerListFilter filter;

        public RetailerListAdapter(Context context, ArrayList<CustomerBean> items) {
            super(context, R.layout.adhoc_list_adapter, items);
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
            TextView tvRetailerName;
            TextView tv_retailer_mob_no, tv_address2, tv_status_color, tv_down_color;
            ImageView ivMobileNo, iv_expand_icon;
        }

        ViewHolder holder = null;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.adhoc_list_adapter, parent, false);
                holder = new ViewHolder();
                view.setTag(holder);


                holder.tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
                holder.ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
                holder.tv_retailer_mob_no = (TextView) view.findViewById(R.id.tv_retailer_mob_no);
                holder.tv_status_color = (TextView) view.findViewById(R.id.tv_status_color);
                holder.tv_down_color = (TextView) view.findViewById(R.id.tv_down_color);


                holder.tv_address2 = (TextView) view.findViewById(R.id.tv_address2);
                holder.iv_expand_icon = (ImageView) view.findViewById(R.id.iv_expand_icon);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final CustomerBean retailerListBean = retDisplayValues.get(position);

            holder.tvRetailerName.setText(retailerListBean.getRetailerName());
            holder.tv_retailer_mob_no.setText(retailerListBean.getUID());

            String cityVal = "";

            if (!retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")) {
                cityVal = retailerListBean.getLandMark() + "," + retailerListBean.getCity();
            } else if (!retailerListBean.getLandMark().equalsIgnoreCase("") && retailerListBean.getCity().equalsIgnoreCase("")) {
                cityVal = retailerListBean.getLandMark();
            } else if (retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")) {
                cityVal = retailerListBean.getCity();
            } else {
                cityVal = "";
            }

            String disticVal = "";

            if (!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")) {
                disticVal = retailerListBean.getDistrictDesc() + " " + retailerListBean.getPostalCode();
            } else if (!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && retailerListBean.getPostalCode().equalsIgnoreCase("")) {
                disticVal = retailerListBean.getDistrictDesc();
            } else if (retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")) {
                disticVal = retailerListBean.getPostalCode();
            } else {
                disticVal = "";
            }

            String addressVa = "";
            try {
                if (!retailerListBean.getAddress1().equalsIgnoreCase("")) {
                    addressVa = retailerListBean.getAddress1();
                }

                if (retailerListBean.getAddress2() != null && !retailerListBean.getAddress2().equalsIgnoreCase("")) {
                    addressVa = addressVa + "," + retailerListBean.getAddress2();
                }

                if (!retailerListBean.getAddress3().equalsIgnoreCase("")) {
                    addressVa = addressVa + "," + retailerListBean.getAddress3();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            double mDouLatVal = retailerListBean.getLatVal();
            double mDouLongVal = retailerListBean.getLongVal();

            //        LinearLayout ll_lat_long = (LinearLayout) view.findViewById(R.id.ll_lat_long);
            ImageView iv_lat_long_icon = (ImageView) view.findViewById(R.id.iv_lat_long_icon);

            if (mDouLatVal == 0.0 || mDouLongVal == 0.0) {

                iv_lat_long_icon.setImageResource(R.drawable.ic_loca_red_mark_small);
            } else {
                iv_lat_long_icon.setImageResource(R.drawable.ic_loca_green_mark_small);
            }

            holder.tv_address2.setText(getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + cityVal + "\n" + disticVal));


            final View testView = view;
            holder.iv_expand_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ImageView img = (ImageView) v;

                    if (retailerListBean.isAddressEnabled()) {
                        retailerListBean.setIsAddressEnabled(false);
                        img.setImageResource(R.drawable.down);

                    } else {
                        retailerListBean.setIsAddressEnabled(true);
                        img.setImageResource(R.drawable.up);

                    }

                    View toolbarEmptyText = testView.findViewById(R.id.tv_empty_text);
                    ExpandAnimation expandemptytext = new ExpandAnimation(toolbarEmptyText, 50);
                    toolbarEmptyText.startAnimation(expandemptytext);

                    View toolbar = testView.findViewById(R.id.tv_address2);
                    ExpandAnimation expandAni = new ExpandAnimation(toolbar, 50);
                    toolbar.startAnimation(expandAni);

                    View toolbarSpace = testView.findViewById(R.id.tv_down_color);
                    ExpandAnimation expandAniSpace = new ExpandAnimation(toolbarSpace, 50);
                    toolbarSpace.startAnimation(expandAniSpace);
                }
            });


            holder.tv_status_color.setBackgroundResource(R.color.WHITE);
            holder.tv_down_color.setBackgroundResource(R.color.WHITE);

            holder.ivMobileNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!retailerListBean.getMobileNumber().equalsIgnoreCase("")) {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (retailerListBean.getMobileNumber())));
                        startActivity(dialIntent);
                    }
                }
            });

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

    /*Navigating to Details screen for selected retailer*/
    private void onListDetails(int id) {
        final CustomerBean retailerLB = alRetailerList.get(id);

//        try {
//            String mStrVisitStartEndQry=Constants.RoutePlans+"?$filter="+Constants.VisitDate+" eq datetime'" + UtilConstants.getNewDate() + "' and "+Constants.CPGuid+" eq guid'"+retailerLB.getCPGUID()+"' " ;
//            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
//                builder.setMessage(R.string.do_u_want_delete_etop_up_retailer_from_beat_plan)
//                        .setCancelable(false)
//                        .setPositiveButton(R.string.yes,
//                                new DialogInterface.OnClickListener() {
//
//                                    @SuppressLint("NewApi")
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//
//                                        Intent intentRetailerDetails = new Intent(UpdateRetailerListActivity.this, UpdateRetailerActivity.class);
//                                        intentRetailerDetails.putExtra(Constants.RetailerName, retailerLB.getRetailerName());
//                                        intentRetailerDetails.putExtra(Constants.CPNo, retailerLB.getCPNo() != null ? retailerLB.getCPNo() : "");
//                                        intentRetailerDetails.putExtra(Constants.CPGUID, retailerLB.getCPGUID());
//                                        startActivity(intentRetailerDetails);
//                                    }
//                                })
//                        .setNegativeButton(R.string.no,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//
//
//                builder.show();
//            } else {
        Intent intentRetailerDetails = new Intent(UpdateRetailerListActivity.this, UpdateRetailerActivity.class);
        intentRetailerDetails.putExtra(Constants.RetailerName, retailerLB.getRetailerName());
        intentRetailerDetails.putExtra(Constants.CPNo, retailerLB.getCPNo() != null ? retailerLB.getCPNo() : "");
        intentRetailerDetails.putExtra(Constants.CPGUID, retailerLB.getCPGUID());
        startActivity(intentRetailerDetails);
//            }
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    /*AsyncTask to get Retailers List*/
    private class GetRetailerList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(UpdateRetailerListActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            getRetailerList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayRetailerList();
        }
    }

    private void displayRetailerList() {

        this.retailerAdapter = new RetailerListAdapter(this, alRetailerList);
        lvRetailerList.setAdapter(this.retailerAdapter);
        this.retailerAdapter.notifyDataSetChanged();

        if (alRetailerList != null && alRetailerList.size() > 0)
            tvEmptyLay.setVisibility(View.GONE);
        else
            tvEmptyLay.setVisibility(View.VISIBLE);
    }

    private void loadAsyncTask() {
        try {
            new GetRetailerList().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}