package com.arteriatech.emami.feedback;

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
import com.arteriatech.emami.adapter.FeedbackListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.FeedbackBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10742 on 13-01-2017.
 *
 */

public class FeedbackListFragment extends Fragment {

    private FeedbackListAdapter feedbackHisListAdapter = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrCPGUID = "";

    ListView lvFeedbackList = null;
    TextView tvEmptyLay = null;

    Bundle bundleExtras = null;
    View myInflatedView = null;

    public void setArguments(Bundle bundle) {
        bundleExtras = bundle;
        // Inflate the layout for this fragment
        mStrBundleRetID = bundle.getString(Constants.CPNo);
        mStrCPGUID = bundle.getString(Constants.CPGUID);
        mStrBundleRetName = bundle.getString(Constants.RetailerName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.feedback_fragment, container, false);

        initUI();
        return myInflatedView;
    }

    void initUI() {
        lvFeedbackList = (ListView) myInflatedView.findViewById(R.id.lv_feedback);
        tvEmptyLay = (TextView) myInflatedView.findViewById(R.id.tv_empty_lay);

        getFeedbackList(mStrCPGUID);

        EditText edNameSearch = (EditText) myInflatedView.findViewById(R.id.ed_invoice_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (feedbackHisListAdapter != null) {
                    feedbackHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                    feedbackHisListAdapter.notifyDataSetChanged(); //Update my view
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /*Gets Feedback List*/
    private void getFeedbackList(String strCPGUID) {
        try {
//            ArrayList<FeedbackBean> alFeedBackBean = OfflineManager.getFeedBackList(Constants.Feedbacks + "?$filter=" +
//                    Constants.CPGUID + " eq '" + mStrCPGUID
//                    + "'");
            ArrayList<FeedbackBean> alFeedBackBean = OfflineManager.getFeedBackList(Constants.Feedbacks + "?$filter=" +
                    Constants.FromCPGUID + " eq '" + mStrCPGUID
                    + "'");

            feedbackHisListAdapter = new FeedbackListAdapter(getActivity(),
                    R.layout.activity_invoice_history_list, alFeedBackBean, tvEmptyLay, mStrBundleRetID, mStrBundleRetName);
            lvFeedbackList.setAdapter(feedbackHisListAdapter);
            feedbackHisListAdapter.notifyDataSetChanged();

            if (alFeedBackBean != null && alFeedBackBean.size() > 0)
                tvEmptyLay.setVisibility(View.GONE);
            else
                tvEmptyLay.setVisibility(View.VISIBLE);


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
}
