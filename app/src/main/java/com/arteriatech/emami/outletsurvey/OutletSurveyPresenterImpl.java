package com.arteriatech.emami.outletsurvey;

import android.app.Activity;
import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.store.OfflineManager;

public class OutletSurveyPresenterImpl implements OutletSurveyPresenterView {

    private Activity activity;
    private OutletSurveyView outletSurveyView;
    private Context context;
    private String cpNo;
    private OutletSurveyBean alRetailerData = null;

    public OutletSurveyPresenterImpl(Activity activity, OutletSurveyView outletSurveyView, Context context,String id) {
        this.activity = activity;
        this.outletSurveyView = outletSurveyView;
        this.context = context;
        this.cpNo = id;
    }

    @Override
    public void start() {
        if (outletSurveyView != null) {
            outletSurveyView.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {//$select=" + Constants.CPNo + ","
//            +Constants.RetailerName+","+Constants.Address1+","+Constants.Address2+","+Constants.Address3+","+Constants.TownDesc+","
//                    +Constants.DistrictDesc+","+Constants.Landmark+","+Constants.Latitude+","+Constants.Longitude+","+Constants.CityDesc+","
//                    +Constants.PostalCode+","+Constants.MobileNo+","+Constants.CPUID+","+Constants.CPGUID+","+Constants.DOB+","
//                    +Constants.Anniversary+","+Constants.OwnerName+" " +
//                    "&
                    alRetailerData = OfflineManager.getRetailerOutletSurveyList(Constants.ChannelPartners + "?$filter=(" + Constants.CPNo + " eq "+ "'"+cpNo+"' and " + Constants.CPNo + " ne null)" +
                            " and " + Constants.StatusID + " eq '01' and " + Constants.ApprvlStatusID + " eq '03'", "");
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (outletSurveyView != null) {
                            outletSurveyView.hideProgressDialog();
                            outletSurveyView.getRetailerList(alRetailerData);
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        outletSurveyView = null;
    }
}
