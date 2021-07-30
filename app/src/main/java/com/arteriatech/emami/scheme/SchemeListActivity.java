package com.arteriatech.emami.scheme;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

public class SchemeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<SchemeListBean> schemeListBeanArrayList = new ArrayList<>();
    private ArrayList<SchemeListBean> schemeSearchList = new ArrayList<>();
    private SchemeListAdapter schemeListAdapter;
    private String schemeIds = "";
    private ProgressDialog prgressDialog = null;
    private View noRecordFound;
    private String searchStr[] = {"Desc", "Code"};
    private Spinner spSearch;
    private String selectedSearchType = "";
    private EditText etSearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_list);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_schemes));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            schemeIds = bundle.getString(Constants.EXTRA_SCHEME_GUID, "");
        }
        if (!Constants.restartApp(SchemeListActivity.this)) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            noRecordFound = (View) findViewById(R.id.noRecordFound);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            schemeListAdapter = new SchemeListAdapter(SchemeListActivity.this, schemeListBeanArrayList);
            recyclerView.setAdapter(schemeListAdapter);
            initUI();
            new GetSchemeList().execute();
        }
    }

    private void initUI() {
        spSearch = (Spinner) findViewById(R.id.spSearch);
        etSearchInput = (EditText) findViewById(R.id.etSearchInput);

        ArrayAdapter<String> searchadapter = new ArrayAdapter<>(this, R.layout.custom_textview, searchStr);
        searchadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearch.setAdapter(searchadapter);
        spSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                selectedSearchType = searchStr[position];
                etSearchInput.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterData(charSequence + "", selectedSearchType);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void refreshAdapter() {
        try {
            schemeListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (schemeListBeanArrayList.isEmpty()) {
            noRecordFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noRecordFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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

    public void filterData(String text, String type) {
        schemeListBeanArrayList.clear();
        if (TextUtils.isEmpty(text)) {
            schemeListBeanArrayList.addAll(schemeSearchList);
        } else {
            for (SchemeListBean schemeListBean : schemeSearchList) {
                if (type.equalsIgnoreCase("Desc")) {
                    if (schemeListBean.getSchemeName().toLowerCase().contains(text.toLowerCase())) {
                        schemeListBeanArrayList.add(schemeListBean);
                    }
                } else if (type.equalsIgnoreCase("Code")) {
                    if (schemeListBean.getSchemeId().toLowerCase().contains(text.toLowerCase())) {
                        schemeListBeanArrayList.add(schemeListBean);
                    }
                }
            }
        }
        refreshAdapter();
    }

    /*load background for get scheme list based on some condition*/
    private class GetSchemeList extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressDialog = Constants.showProgressDialog(SchemeListActivity.this, "", getString(R.string.app_loading));
        }

        @Override
        protected Void doInBackground(String... params) {
            schemeListBeanArrayList.clear();
            try {
                String mStrSchemeQry = "";
                if (!schemeIds.equalsIgnoreCase("")) {
                    if (schemeIds.contains(",")) {
                        String schemeGUIDArray[] = schemeIds.split(",");
                        int totalSize = schemeGUIDArray.length;
                        int i = 0;
                        for (String schemeGUIDVal : schemeGUIDArray) {
                            if (i == 0 && i == totalSize - 1) {
                                mStrSchemeQry = mStrSchemeQry
                                        + "(" + Constants.SchemeGUID + " eq guid'"
                                        + schemeGUIDVal + "')";

                            } else if (i == 0) {
                                mStrSchemeQry = mStrSchemeQry
                                        + "(" + Constants.SchemeGUID + " eq guid'"
                                        + schemeGUIDVal + "'";

                            } else if (i == totalSize - 1) {
                                mStrSchemeQry = mStrSchemeQry
                                        + " or " + Constants.SchemeGUID + " eq guid'"
                                        + schemeGUIDVal + "')";
                            } else {
                                mStrSchemeQry = mStrSchemeQry
                                        + " or " + Constants.SchemeGUID + " eq guid'"
                                        + schemeGUIDVal + "'";
                            }
                            i++;
                        }
                    } else {
                        mStrSchemeQry = mStrSchemeQry + " " + Constants.SchemeGUID + " eq guid'" + schemeIds + "'";
                    }
                }
                schemeSearchList = OfflineManager.getSchemesListGrp(SchemeListActivity.this, mStrSchemeQry);
                schemeListBeanArrayList.addAll(schemeSearchList);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (prgressDialog != null) {
                Constants.hideProgressDialog(prgressDialog);
            }
            refreshAdapter();

        }
    }
}
