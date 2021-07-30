package com.arteriatech.emami.reports;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.InvoiceHisListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10742 on 10-01-2017.
 *
 */

public class InvoiceHistoryFragment extends Fragment{

    private InvoiceHisListAdapter invoiceHisListAdapter = null;
    private ArrayList<InvoiceHistoryBean> alInvoiceBean;
    private String mStrBundleRetID = "",mStrBundleCPGUID="";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";

    Spinner spinvHisStatus;

    String selectedStatus;
    //new
    TextView tvEmptyLay = null;
    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;

    private String[][] arrayInvStatusVal;
    EditText edNameSearch;

    //new
    ListView lv_inv_his_list = null;
    private Bundle bundleExtras;
    View myInflatedView = null;

    public InvoiceHistoryFragment() {
    }

    public void setArguments(Bundle bundle){
        bundleExtras =bundle;
        // Inflate the layout for this fragment
        mStrBundleRetID = bundle.getString(Constants.CPNo);
        mStrBundleCPGUID = bundle.getString(Constants.CPGUID);
        mStrBundleRetName = bundle.getString(Constants.RetailerName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.activity_invoice_history_list, container, false);

        initUI();
        return myInflatedView;
    }

    void initUI(){
        lv_inv_his_list = (ListView)myInflatedView.findViewById(R.id.lv_inv_hist_list);

        tvEmptyLay = (TextView)myInflatedView.findViewById(R.id.tv_empty_lay);

        spinvHisStatus = (Spinner)myInflatedView.findViewById(R.id.spin_invoice_his_status_id);

        edNameSearch = (EditText) myInflatedView.findViewById(R.id.ed_invoice_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                invoiceHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                invoiceHisListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
        getInvStatus();
        getStatus();



    }
    private void clearEditTextSearchBox(){
        if(edNameSearch!=null && edNameSearch.getText().toString().length()>0)
            edNameSearch.setText("");
    }

    /*gets status for invoices*/
    public void getStatus(){
        if(arrayInvStatusVal ==null){
            arrayInvStatusVal = new String[2][1];
            arrayInvStatusVal[0][0]="";
            arrayInvStatusVal[1][0]="";
        }

        String[][] tempStatusArray = new String[3][arrayInvStatusVal[0].length+1];
        tempStatusArray[0][0] = Constants.str_00;
        tempStatusArray[1][0] = Constants.All;
        tempStatusArray[2][0] = "";
        for(int i=1; i<arrayInvStatusVal[0].length+1;i++){
            tempStatusArray[0][i] = arrayInvStatusVal[0][i-1];
            tempStatusArray[1][i] = arrayInvStatusVal[1][i-1];
            tempStatusArray[2][i] = arrayInvStatusVal[2][i-1];
        }
        arrayInvStatusVal = tempStatusArray;

        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.custom_textview, arrayInvStatusVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spinvHisStatus.setAdapter(productCategoryAdapter);


        spinvHisStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {

                selectedStatus = arrayInvStatusVal[0][position];

                clearEditTextSearchBox();
                if (selectedStatus.equalsIgnoreCase(Constants.str_00)) {

                    getInvoiceList("");
                }else{
                    getInvoiceList(selectedStatus);
                }

            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });;
    }


    /*gets List of invoices*/
    private void getInvoiceList(String status){
        try {
            if(status.equalsIgnoreCase("")){

//                alInvoiceBean = OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES+"?$filter="+Constants.SoldToID+" eq '"+mStrBundleRetID+"' " +
//                        "and "+Constants.InvoiceDate+" ge datetime'" + Constants.getLastMonthDate() + "' ",getActivity(),status,mStrBundleCPGUID);
                alInvoiceBean = OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES+"?$filter="+ Constants.SoldToID+" eq '"+mStrBundleRetID+"' " +
                        " ",getActivity(),status,mStrBundleCPGUID);

            }else{
//                alInvoiceBean = OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES+"?$filter="+Constants.SoldToID+" eq '"+mStrBundleRetID+"'"+" " +
//                        "and "+Constants.PaymentStatusID+" eq '"+status+"' and "+Constants.InvoiceDate+" ge datetime'" + Constants.getLastMonthDate() + "' ",getActivity(),status,mStrBundleCPGUID);
                alInvoiceBean = OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES+"?$filter="+ Constants.SoldToID+" eq '"+mStrBundleRetID+"' " +
                        "and "+Constants.PaymentStatusID+" eq '"+status+"'",getActivity(),status,mStrBundleCPGUID);


            }

            invoiceHisListAdapter = new InvoiceHisListAdapter( getActivity(), R.layout.activity_invoice_history_list,alInvoiceBean,bundleExtras);
            lv_inv_his_list.setEmptyView(myInflatedView.findViewById(R.id.tv_empty_lay) );
            lv_inv_his_list.setAdapter(invoiceHisListAdapter);
            invoiceHisListAdapter.notifyDataSetChanged();

            if(alInvoiceBean.size()>0){
                tvEmptyLay.setVisibility(View.GONE);
            } else
                tvEmptyLay.setVisibility(View.VISIBLE);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /*get different status for invoices*/
    public void getInvStatus(){
        try{
            String mStrConfigQry = Constants.ValueHelps + "?$filter="+Constants.PropName+" eq '"+Constants.PaymentStatusID+"' and " +Constants.EntityType+" eq '"+Constants.SSInvoice+"' &$orderby="+Constants.ID+"%20asc";

            arrayInvStatusVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
        }
    }
}
