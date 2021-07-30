package com.arteriatech.emami.distributor;

import android.app.Activity;
import android.content.Context;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

public class DistributorViewPresenterImpl implements DistributorPresenter{
    Context context;
    Activity activity;
    ArrayList<DistributorBean> alDistributorBeans;
    CustomerBean customerBeans;
    DistributorView distributorView ;
    public DistributorViewPresenterImpl(Context context, Activity activity, DistributorView distributorView) {
        this.activity = activity;
        this.context = context;
        this.distributorView = distributorView;
    }

    @Override
    public void distributorList() {
        String qry = Constants.CPSPRelations;
        if(distributorView != null){
            distributorView.showProgress();
        }
        alDistributorBeans = OfflineManager.getDistributorsList(qry);
        if(alDistributorBeans != null && alDistributorBeans.size()>0){
            if(distributorView != null){
                distributorView.hideProgress();
                distributorView.spdistributorData(alDistributorBeans);
            }else {
                if(distributorView != null) {
                    distributorView.hideProgress();
                }
            }
        }else {
            if(distributorView != null) {
                distributorView.hideProgress();
            }
        }

    }

    @Override
    public void getDistributorData(String cpNo) {
        String qry = Constants.Customers + "?$filter=CustomerNo eq '" + cpNo + "'";
        if(distributorView != null){
            distributorView.showProgress();
        }
        customerBeans = OfflineManager.getDistributorListData(qry);
        if(customerBeans != null ){
            if(distributorView != null){
                distributorView.hideProgress();
                distributorView.setdistributorData(customerBeans);
            }else {
                if(distributorView != null) {
                    distributorView.hideProgress();
                }
            }
        }


    }
}
