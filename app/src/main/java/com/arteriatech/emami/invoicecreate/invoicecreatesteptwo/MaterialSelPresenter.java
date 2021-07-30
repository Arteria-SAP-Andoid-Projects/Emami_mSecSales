package com.arteriatech.emami.invoicecreate.invoicecreatesteptwo;


import com.arteriatech.emami.mbo.InvoiceCreateBean;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface MaterialSelPresenter {
    void onStart();
    void getInvoices();

    void onDestroy();

    boolean validateFields(InvoiceCreateBean collectionBean, String syncType);


    void onAsignData(String save, String strRefType, InvoiceCreateBean collectionBean, String comingFrom);
    void onSaveData();
}
