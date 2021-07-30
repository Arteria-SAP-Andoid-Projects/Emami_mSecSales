package com.arteriatech.emami.invoicecreate.invoicecreatesteptwo;


import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.mbo.ValueHelpBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface MaterialSelView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);

    void displayByCollectionData(ArrayList<ValueHelpBean> alPaymentMode);

    void displayInvoiceData(ArrayList<InvoiceBean> alInvList);

    void errorPaymentMode(String message);

    void showMessage(String message, boolean isSimpleDialog);
}
