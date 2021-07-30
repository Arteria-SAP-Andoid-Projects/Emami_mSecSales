package com.arteriatech.emami.invoicecreate.invoicereview;


import com.arteriatech.emami.mbo.InvoiceCreateBean;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface InvoiceReviewPresenter {
    void onStart();

    void onDestroy();

    boolean validateFields(InvoiceCreateBean soCreateBean);

    void getProductRelInfo(String FeedbackId);

    void onAsignData(String save, String strRejReason, String strRejReasonDesc, InvoiceCreateBean feedbackBean);
    void approveData(String ids, String description, String approvalStatus);
    void onSaveData();
}
