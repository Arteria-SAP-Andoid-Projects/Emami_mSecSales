package com.arteriatech.emami.reports;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.adapter.CollectionHisListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;


public class CollectionHistoryFragment extends Fragment {

    private ArrayList<CollectionHistoryBean> alCollectionBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    TextView tvEmptyLay_coll_his = null;
    ListView lv_coll_his_list = null;
    private CollectionHisListAdapter collectionHisListAdapter = null;
    private Bundle bundle;

    View myInflatedView = null;

    public CollectionHistoryFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.bundle = savedInstanceState;

        mStrBundleRetID = getArguments().getString(Constants.CPNo);
        mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
        mStrBundleRetUID = getArguments().getString(Constants.CPUID);
        mStrBundleRetName = getArguments().getString(Constants.RetailerName);
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.fragment_collection_history, container, false);

        initUI();
        return myInflatedView;
    }

    /*InitializesUI*/
    void initUI() {
        lv_coll_his_list = (ListView) myInflatedView.findViewById(R.id.lv_coll_list);
        tvEmptyLay_coll_his = (TextView) myInflatedView.findViewById(R.id.tv_empty_lay_coll_his);
        getCollectionList();
        EditText edNameSearch = (EditText) myInflatedView.findViewById(R.id.ed_collection_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                collectionHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                collectionHisListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /*Gets Collection List*/
    private void getCollectionList() {
        try {
            alCollectionBean = OfflineManager.getCollectionHistoryList(Constants.FinancialPostings + "?$filter=CPNo eq '" + mStrBundleRetID + "'", getActivity(), mStrBundleCPGUID);

            this.collectionHisListAdapter = new CollectionHisListAdapter(getActivity(),
                    alCollectionBean, mStrBundleCPGUID, mStrBundleRetID, mStrBundleRetName, tvEmptyLay_coll_his);
            lv_coll_his_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay_coll_his));
            lv_coll_his_list.setAdapter(this.collectionHisListAdapter);
            this.collectionHisListAdapter.notifyDataSetChanged();

            if(alCollectionBean.size()>0){
                tvEmptyLay_coll_his.setVisibility(View.GONE);
            } else
                tvEmptyLay_coll_his.setVisibility(View.VISIBLE);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
}
