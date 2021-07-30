package com.arteriatech.emami.visit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.MerchandisingBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10763 on 12/19/2016.
 */

public class MerchandisingDeviceListFragment extends Fragment {

    private String mStrBundleRetID = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    public String[] tempMerDevList = null;

    int pendingMerVal = 0, penReqount = 0;
    boolean flagDevFrg = true;
    ScrollView scrollViewDevFrg;
    TableLayout tableLayoutDevFrg;
    View myInflatedView = null;
    private ArrayList<MerchandisingBean> alMercBeanDevFrg;

    public MerchandisingDeviceListFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStrBundleRetID = getArguments().getString(Constants.CPNo);
        mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
        mStrBundleRetUID = getArguments().getString(Constants.CPUID);
        mStrBundleRetName = getArguments().getString(Constants.RetailerName);
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.fragment_dev_merch_list, container, false);

        initUI();
        return myInflatedView;
    }

    /*InitializesUI*/
    void initUI() {
        getDeviceMerchndisingList();
    }


    // Todo get device merchandising list from offline store
    private void getDeviceMerchndisingList() {
        try {

            alMercBeanDevFrg = OfflineManager.getMerchandisingList(Constants.MerchReviews + " " + Constants.isLocalFilterQry + " and "
                    + Constants.CPGUID + " eq '" + mStrBundleCPGUID.toUpperCase() + "' &$orderby=" + Constants.MerchReviewDate + "%20desc", Constants.DeviceMechindising);
            pendingMerVal = 0;
            if (tempMerDevList != null) {
                tempMerDevList = null;
                penReqount = 0;

            }
            if (alMercBeanDevFrg != null && alMercBeanDevFrg.size() > 0) {
                tempMerDevList = new String[alMercBeanDevFrg.size()];
                for (int k = 0; k < alMercBeanDevFrg.size(); k++) {
                    tempMerDevList[k] = alMercBeanDevFrg.get(k).getEtag();
                    pendingMerVal++;
                }
            }


            displyMerivews();


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    // TODO display Merchandising values
    private void displyMerivews() {
        scrollViewDevFrg = (ScrollView) myInflatedView.findViewById(R.id.scroll_snap_shot_dev_list);
        if (!flagDevFrg) {
            scrollViewDevFrg.removeAllViews();
        }

        flagDevFrg = false;

        tableLayoutDevFrg = (TableLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.item_table, null);
        if (!alMercBeanDevFrg.isEmpty()
                && alMercBeanDevFrg.size() > 0) {
            LinearLayout llTargetTable = null;

            for (int incremnetVal = 0; incremnetVal < alMercBeanDevFrg.size(); incremnetVal++) {

                final MerchandisingBean merchandisingBean = alMercBeanDevFrg.get(incremnetVal);
                llTargetTable = (LinearLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.merchandising_list_item, null);

                ((TextView) llTargetTable.findViewById(R.id.tvDateValue))
                        .setText(Constants.convertDateIntoDeviceFormat(getContext(), alMercBeanDevFrg.get(incremnetVal).getMerchReviewDate()));

                ((TextView) llTargetTable.findViewById(R.id.tvSnapTypeValue))
                        .setText(alMercBeanDevFrg.get(incremnetVal).getMerchReviewTypeDesc());

                ImageView imv = (ImageView) llTargetTable
                        .findViewById(R.id.imgImageValue);
                imv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent toSnapdetails = new Intent(getActivity(), MerchandisingDetailsActivity.class);
                        toSnapdetails.putExtra(Constants.CPNo, mStrBundleRetID);
                        toSnapdetails.putExtra(Constants.CPUID, mStrBundleRetUID);
                        toSnapdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
                        toSnapdetails.putExtra(Constants.MerchReviewGUID, merchandisingBean.getMerchReviewGUID());
                        toSnapdetails.putExtra(Constants.MerchReviewTypeDesc, merchandisingBean.getMerchReviewTypeDesc());
                        toSnapdetails.putExtra(Constants.Remarks, merchandisingBean.getRemarks());
                        toSnapdetails.putExtra(Constants.Etag, merchandisingBean.getEtag());
                        toSnapdetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
                        toSnapdetails.putExtra(Constants.SetResourcePath, merchandisingBean.getResourcePath());
                        toSnapdetails.putExtra(Constants.ImagePath, merchandisingBean.getImagePath());
                        toSnapdetails.putExtra(Constants.Image, "");
                        startActivity(toSnapdetails);


                    }

                    ;
                });

                llTargetTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toSnapdetails = new Intent(getActivity(), MerchandisingDetailsActivity.class);
                        toSnapdetails.putExtra(Constants.CPNo, mStrBundleRetID);
                        toSnapdetails.putExtra(Constants.CPUID, mStrBundleRetUID);
                        toSnapdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
                        toSnapdetails.putExtra(Constants.MerchReviewGUID, merchandisingBean.getMerchReviewGUID());
                        toSnapdetails.putExtra(Constants.MerchReviewTypeDesc, merchandisingBean.getMerchReviewTypeDesc());
                        toSnapdetails.putExtra(Constants.Remarks, merchandisingBean.getRemarks());
                        toSnapdetails.putExtra(Constants.Etag, merchandisingBean.getEtag());
                        toSnapdetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
                        toSnapdetails.putExtra(Constants.SetResourcePath, merchandisingBean.getResourcePath());
                        toSnapdetails.putExtra(Constants.ImagePath, merchandisingBean.getImagePath());
                        toSnapdetails.putExtra(Constants.Image, "");
                        startActivity(toSnapdetails);
                    }
                });

                LinearLayout llLine = new LinearLayout(getActivity());
                llLine.setBackgroundColor(Color.parseColor("#000000"));
                llLine.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
                tableLayoutDevFrg.addView(llLine);

                tableLayoutDevFrg.addView(llTargetTable);
            }

            LinearLayout llLine = new LinearLayout(getActivity());
            llLine.setBackgroundColor(Color.parseColor("#000000"));
            llLine.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
            tableLayoutDevFrg.addView(llLine);

        } else {
            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(getActivity())
                    .inflate(R.layout.empty_layout, null);

            tableLayoutDevFrg.addView(llEmptyLayout);
        }
        scrollViewDevFrg.addView(tableLayoutDevFrg);
        scrollViewDevFrg.requestLayout();
    }


}