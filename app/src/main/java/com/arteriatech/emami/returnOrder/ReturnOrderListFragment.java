package com.arteriatech.emami.returnOrder;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReturnOrderListFragment extends Fragment implements OnClickInterface {
    ArrayList<ReturnOrderBean> returnOrderBeanList = new ArrayList<>();
    ArrayList<ReturnOrderBean> returnOrderBeanSearchList = new ArrayList<>();
    ReturnOrderListAdapter returnOrderListAdapter;
    private RecyclerView recyclerView;
    private TextView noDataFound;
    private EditText edSearch;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
    private int comingFrom = 0;
    private int tabPosition = 0;
    private TextView tvOrderValue;
    private TextView tvOrderId;
    private TextView tvOrderDate;

    public ReturnOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtras = this.getArguments();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            comingFrom = bundleExtras.getInt(Constants.comingFrom, 0);
            tabPosition = bundleExtras.getInt(Constants.EXTRA_TAB_POS, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_return_order_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noDataFound = (TextView) view.findViewById(R.id.no_record_found);
        edSearch = (EditText) view.findViewById(R.id.ed_search);

        tvOrderId = (TextView) view.findViewById(R.id.tv_order_id);
        tvOrderDate = (TextView) view.findViewById(R.id.tv_order_date);
        tvOrderValue = (TextView) view.findViewById(R.id.tv_order_value);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        returnOrderListAdapter = new ReturnOrderListAdapter(getContext(), returnOrderBeanList,returnOrderBeanSearchList);
        returnOrderListAdapter.onItemClick(this);
        recyclerView.setAdapter(returnOrderListAdapter);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                returnOrderListAdapter.filter(s + "", noDataFound, recyclerView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //set text into textview
        setTextIntoTextView();
        //get data from offline database
        if (comingFrom == Constants.RETURN_ORDER_POS && tabPosition == Constants.TAB_POS_1) {
            getRODataFromOfflineDb();
        } else if (comingFrom == Constants.RETURN_ORDER_POS && tabPosition == Constants.TAB_POS_2) {
            getRODataFromDataValt();
        } else if (comingFrom == Constants.SSS_ORDER_POS && tabPosition == Constants.TAB_POS_1) {
            getSSSODataFromOfflineDb();
        } else if (comingFrom == Constants.SSS_ORDER_POS && tabPosition == Constants.TAB_POS_2) {
            getSSSODataFromDataValt();
        } else if (comingFrom == Constants.COMPLAINTS_ORDER_POS) {
            getComplaintsFromOfflineDb();
        }

    }
    /*get complaints from offline db*/
    private void getComplaintsFromOfflineDb() {
        String query = Constants.Complaints;
        try {
            returnOrderBeanList.clear();
            returnOrderBeanList = OfflineManager.getCustomerComplaintList(query, returnOrderBeanList);
            returnOrderListAdapter.filter("", noDataFound, recyclerView);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
    /*set text into text view*/
    private void setTextIntoTextView() {
        if (comingFrom == Constants.RETURN_ORDER_POS) {
            edSearch.setHint(getString(R.string.ro_no_search_hint));
        } else if (comingFrom == Constants.SSS_ORDER_POS) {
            edSearch.setHint(getString(R.string.so_no_search_hint));
            tvOrderId.setText(getString(R.string.so_order_no));
            tvOrderDate.setText(getString(R.string.so_order_date));
            tvOrderValue.setText(getString(R.string.so_order_value));
        } else if (comingFrom == Constants.COMPLAINTS_ORDER_POS) {
            edSearch.setHint(getString(R.string.complaints_no_search_hint));
            tvOrderId.setText(getString(R.string.complaints_order_no));
            tvOrderDate.setText(getString(R.string.complaints_order_date));
            tvOrderValue.setText(getString(R.string.complaints_order_value));
        }
    }
    /*get ssso data from data valt*/
    private void getSSSODataFromDataValt() {
        returnOrderBeanList.clear();
        try {
            returnOrderBeanList = OfflineManager.getSSSoListFromDataValt(getContext(), mStrBundleCPGUID, returnOrderBeanList,mStrBundleRetID);
            returnOrderListAdapter.filter("", noDataFound, recyclerView);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
    /*get ssso data from offline db*/
    private void getSSSODataFromOfflineDb() {
        String query = Constants.SSSOs+"?$filter="+ Constants.SoldToCPGUID+" eq guid'"+Constants.convertStrGUID32to36(mStrBundleCPGUID)+"' " +
                "and "+ Constants.OrderType+" eq '" + Constants.getSOOrderType() + "' ";
        try {
            returnOrderBeanList.clear();
            returnOrderBeanList = OfflineManager.getSecondarySalesOrderList(query, returnOrderBeanList,mStrBundleRetID,getActivity());
            returnOrderListAdapter.filter("", noDataFound, recyclerView);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
    /* get RO data from */
    private void getRODataFromDataValt() {
        returnOrderBeanList.clear();
        try {
            returnOrderBeanList = OfflineManager.getROListFromDataValt(getContext(), mStrBundleCPGUID, returnOrderBeanList);
            returnOrderListAdapter.filter("", noDataFound, recyclerView);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * get data from offline db and refresh recyclerview
     */
    private void getRODataFromOfflineDb() {
        String query = Constants.SSROs+"?$filter="+ Constants.SoldToCPGUID+" eq guid'"+Constants.convertStrGUID32to36(mStrBundleCPGUID)+"' " +
                "and "+ Constants.OrderType+" eq '" + Constants.getReturnOrderType() + "' ";
        try {
            returnOrderBeanList.clear();
            returnOrderBeanList = OfflineManager.getReturnOrderFromOffline(query, returnOrderBeanList);
            returnOrderListAdapter.filter("", noDataFound, recyclerView);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        ReturnOrderBean returnOrderBean = returnOrderBeanSearchList.get(position);
        Intent intent = new Intent(getContext(), ReturnOrderListDetailsActivity.class);
        intent.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intent.putExtra(Constants.CPNo, mStrBundleRetID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetName);
        intent.putExtra(Constants.CPUID, mStrBundleRetUID);
        intent.putExtra(Constants.comingFrom, comingFrom);
        intent.putExtra(Constants.DeviceNo, returnOrderBean.getDeviceNo());
        intent.putExtra(Constants.EXTRA_TAB_POS, tabPosition);
        intent.putExtra(Constants.EXTRA_SSRO_GUID, returnOrderBean.getSSROGUID());
        intent.putExtra(Constants.EXTRA_ORDER_DATE, returnOrderBean.getOrderDate());
        intent.putExtra(Constants.EXTRA_ORDER_IDS, returnOrderBean.getOrderNo());
        intent.putExtra(Constants.EXTRA_ORDER_AMOUNT, returnOrderBean.getNetAmount());
        intent.putExtra(Constants.EXTRA_ORDER_SATUS, returnOrderBean.getStatusID());
        intent.putExtra(Constants.EXTRA_TEMP_STATUS, returnOrderBean.getTempStatus());
        intent.putExtra(Constants.EXTRA_ORDER_CURRENCY, returnOrderBean.getCurrency());
        startActivity(intent);

    }
}
