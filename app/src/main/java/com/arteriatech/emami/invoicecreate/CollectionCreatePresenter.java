package com.arteriatech.emami.invoicecreate;


import com.arteriatech.emami.mbo.InvoiceCreateBean;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface CollectionCreatePresenter {
    void onStart();
    void getInvoices();

    void onDestroy();

    boolean validateFields(InvoiceCreateBean collectionBean, String syncType);


    void onAsignData(String save, String strRefType, InvoiceCreateBean collectionBean, String comingFrom);
    void onSaveData();
}
