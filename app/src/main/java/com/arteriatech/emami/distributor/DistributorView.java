package com.arteriatech.emami.distributor;

import com.arteriatech.emami.mbo.CustomerBean;

import java.util.ArrayList;

public interface DistributorView {
    void hideProgress();
    void showProgress();
    void spdistributorData(ArrayList<DistributorBean> alDistributorBeans);
    void setdistributorData(CustomerBean alCustomerBean);
}
