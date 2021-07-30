package com.arteriatech.emami.reports;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.google.gson.Gson;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by ${e10526} on ${17-04-2016}.
 *
 */
public class SchemesActivity extends ListActivity implements View.OnClickListener{
    private SchemeListAdapter schemesListAdapter = null;
    private ArrayList<SchemeBean> alSchemesList;
    private ArrayList<SchemeBean> alDataValutList;
    TextView tv_submit,tv_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemes_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ImageView iv_pop_up_menu = (ImageView) findViewById(R.id.iv_pop_up_menu);
        iv_pop_up_menu.setVisibility(View.GONE);
        if (!Constants.restartApp(SchemesActivity.this)) {
            tv_back = (TextView) findViewById(R.id.tv_back);
            tv_back.setVisibility(View.VISIBLE);
            tv_back.setOnClickListener(this);

            tv_submit = (TextView) findViewById(R.id.tv_submit);
            tv_submit.setVisibility(View.GONE);
            tv_submit.setOnClickListener(this);

            TextView tvSalesPersonName = (TextView) findViewById(R.id.tvRegistrationHeader);
            TextView tv_sales_person_mobile_no = (TextView) findViewById(R.id.tv_sales_person_mobile_no);
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
                    0);
            String salesPersonName = settings.getString("SalesPersonName", "");
            String salesPersonMobNo = settings.getString("SalesPersonMobileNo", "");
            tvSalesPersonName.setText(salesPersonName);
            tv_sales_person_mobile_no.setText(salesPersonMobNo);

            getSchemesDetails();


            Gson gson = new Gson();
            Hashtable dbHeaderTable = new Hashtable();

            try {
                String jsonFromMap = gson.toJson(alDataValutList);

                //noinspection unchecked
                dbHeaderTable.put("ITEMS", jsonFromMap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);

            //noinspection deprecation
            try {
                //noinspection deprecation
                LogonCore.getInstance().addObjectToStore("Schemes", jsonHeaderObject.toString());
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }

            EditText edNameSearch = (EditText) findViewById(R.id.et_name_search);
            edNameSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    schemesListAdapter.getFilter().filter(cs); //Filter from my adapter
                    schemesListAdapter.notifyDataSetChanged(); //Update my view
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                public void afterTextChanged(Editable arg0) {
                }
            });
        }
    }



    private void getSchemesDetails(){
        try {
            alSchemesList = OfflineManager.getSchemeList("Schemes?$orderby=SchemeDesc%20desc");

            alDataValutList = OfflineManager.getSchemeGuidSetFlagEmpty("Schemes?$orderby=SchemeDesc%20desc");

//            alSchemesList = OfflineManager.getSchemeList("Schemes?$filter=ValidFrom le datetime'" + Constants.getNewDate() + "' and ValidTo ge datetime'" + Constants.getNewDate() + "' &$orderby=SchemeDesc%20desc");

            this.schemesListAdapter = new SchemeListAdapter(this, alSchemesList);
            setListAdapter(this.schemesListAdapter);
            this.schemesListAdapter.notifyDataSetChanged();
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private class SchemeListAdapter extends ArrayAdapter<SchemeBean> {
        private ArrayList<SchemeBean> schemesOriginalValues;
        private ArrayList<SchemeBean> schemesDisplayValues;
        private RetailerListFilter filter;

        public SchemeListAdapter(Context context, ArrayList<SchemeBean> items) {
            super(context, R.layout.schemes_list_adapter, items);
            this.schemesOriginalValues = items;
            this.schemesDisplayValues = items;
            alSchemesList = items;
        }

        @Override
        public int getCount() {
            return this.schemesDisplayValues != null ? this.schemesDisplayValues.size() : 0;
        }

        @Override
        public SchemeBean getItem(int item) {
            SchemeBean schemesLB;
            schemesLB = this.schemesDisplayValues != null ? this.schemesDisplayValues.get(item) : null;
            return schemesLB;
        }

        private class ViewHolder {
            TextView tv_scheme_name_value;
            TextView tv_scheme_description_value;
        }

        ViewHolder holder = null;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.schemes_list_adapter, parent,false);
                holder = new ViewHolder();
                view.setTag(holder);

                holder.tv_scheme_name_value = (TextView) view.findViewById(R.id.tv_scheme_name_value);
                holder.tv_scheme_description_value = (TextView) view.findViewById(R.id.tv_scheme_description_value);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }



            final SchemeBean schemeListBean = schemesDisplayValues.get(position);

            holder.tv_scheme_name_value.setText(schemeListBean.getItemNo());
            holder.tv_scheme_description_value.setText(schemeListBean.getSchemeDesc());



           /* view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onListDetails(v.getId());
                }
            });*/
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
                if (schemesOriginalValues == null) {
                    schemesOriginalValues = new ArrayList<>(schemesDisplayValues);
                }
                if (prefix == null || prefix.length() == 0) {
                    results.values = schemesOriginalValues;
                    results.count = schemesOriginalValues.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();
                    ArrayList<SchemeBean> filteredItems = new ArrayList<>();
                    int count = schemesOriginalValues.size();

                    for (int i = 0; i < count; i++) {
                        SchemeBean item = schemesOriginalValues.get(i);
                        String mSirSchemeDescription = item.getSchemeDesc().toLowerCase();
                        if (mSirSchemeDescription.contains(prefixString)) {
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
                schemesDisplayValues = (ArrayList<SchemeBean>) results.values; // has the filtered values
                notifyDataSetChanged();
                alSchemesList = schemesDisplayValues;
            }
        }
    }

    public void showPopup(View v) {
        UtilConstants.showPopup(getApplicationContext(), v, SchemesActivity.this,
                R.menu.menu_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
//                onSave();
                break;
        }
    }

}

