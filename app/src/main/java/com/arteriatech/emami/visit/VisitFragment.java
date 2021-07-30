package com.arteriatech.emami.visit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.UpdateListener;
import com.arteriatech.emami.competitorInfo.CompetitorInformation;
import com.arteriatech.emami.customerComplaints.CustomerComplaintsAct;
import com.arteriatech.emami.feedback.FeedBackActivity;
import com.arteriatech.emami.finance.CollectionCreateActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.outletsurvey.OutletSurveyActivity;
import com.arteriatech.emami.retailerStock.RetailerStockEntry;
import com.arteriatech.emami.returnOrder.ReturnOrderCreate;
import com.arteriatech.emami.sampleDisbursement.SampleDisbursementActivity;
import com.arteriatech.emami.socreate.SalesOrderCreateActivity1;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.windowdisplay.WindowDisplayListActivity;

import java.util.HashSet;

/**
 * Created by e10742 on 02-12-2016.
 */
public class VisitFragment extends Fragment implements View.OnClickListener, UpdateListener {

    public static UpdateListener visitUpdateListener = null;
    View myInflatedView = null;
    ImageButton ib_collection_create, ib_merchndising, ib_return_order_create;
    ImageButton ibFeedbackCreate, ibInvoiceCreate, ibCompInfoCreate, ib_ret_stock, ib_so_create, ib_customer_create, ib_outlet_survey;
    ImageButton ib_feed_back_create_selection, ib_competitor_info_create_selection;
    String mStrVisitStartedOrNotQuery = "";
    HashSet<String> mSetVisitKeys = new HashSet<>();
    private String mStrBundleRetailerNo = "";
    private String mStrBundleRetailerName = "";
    private String mUID = "";
    private String mComingFrom = "";
    private String mStrBundleCPGUID32 = "";
    private String mStrBundleCPGUID = "";
    private ImageButton btSampleCollection, btnWinDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mStrBundleCPGUID32 = getArguments().getString(Constants.CPGUID32);
        mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
        mStrBundleRetailerName = getArguments().getString(Constants.RetailerName);
        mUID = getArguments().getString(Constants.CPUID);
        mComingFrom = getArguments().getString(Constants.comingFrom);
        mStrBundleRetailerNo = getArguments().getString(Constants.CPNo);
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.activity_visit_view, container, false);

        initUI();
        return myInflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        visitUpdateListener = this;
    }

    void initUI() {
        ImageView ivMustSell = (ImageView) myInflatedView.findViewById(R.id.ib_must_sell_selection);
        ivMustSell.setOnClickListener(this);

        LinearLayout ll_must_sell = (LinearLayout) myInflatedView.findViewById(R.id.ll_must_sell);
        LinearLayout ll_must_sell_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_must_sell_line);
        ll_must_sell.setOnClickListener(this);

        LinearLayout llSampleCollection = (LinearLayout) myInflatedView.findViewById(R.id.ll_sample_collection_master);
        LinearLayout llSampleCollectionLine = (LinearLayout) myInflatedView.findViewById(R.id.ll_sample_collection_line);
        llSampleCollection.setOnClickListener(this);

        LinearLayout ll_so_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_create);
        LinearLayout ll_so_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_line);
        ll_so_create.setOnClickListener(this);

        LinearLayout ll_invoice_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_invoice_create);
        LinearLayout ll_invoice_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_invoice_line);
        ll_invoice_create.setOnClickListener(this);

        LinearLayout ll_collection_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_collection_create);
        LinearLayout ll_collection_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_collection_line);
        ll_collection_create.setOnClickListener(this);

        LinearLayout ll_competitor_info_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitor_info_create);
        LinearLayout ll_competitor_info_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitor_info_line);
        ll_competitor_info_create.setOnClickListener(this);

        LinearLayout ll_mer_snap_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_mer_snap_line);
        LinearLayout ll_snap_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_snap_create);
        ll_snap_create.setOnClickListener(this);

        LinearLayout ll_feed_back_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_feed_back_create);
        LinearLayout ll_feed_back_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_feed_back_line);
        ll_feed_back_create.setOnClickListener(this);

        LinearLayout ll_return_order_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_return_order_create);
        LinearLayout ll_return_order_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_return_order_line);
        ll_return_order_create.setOnClickListener(this);

        LinearLayout ll_window_display_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_window_disply);
        LinearLayout ll_window_display_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_window_display_line);
        ll_window_display_create.setOnClickListener(this);

        LinearLayout ll_mer_det = (LinearLayout) myInflatedView.findViewById(R.id.ll_mer_det);
        LinearLayout ll_mer_details_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_mer_details_line);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String sharedVal = sharedPreferences.getString(Constants.isCollCreateEnabledKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCollCreateTcode)) {
            ll_collection_create.setVisibility(View.VISIBLE);
            ll_collection_line.setVisibility(View.VISIBLE);
        } else {
            ll_collection_create.setVisibility(View.GONE);
            ll_collection_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isMerchReviewKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isMerchReviewTcode)) {
            ll_snap_create.setVisibility(View.VISIBLE);
            ll_mer_snap_line.setVisibility(View.VISIBLE);
        } else {
            ll_snap_create.setVisibility(View.GONE);
            ll_mer_snap_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isSOCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSOCreateTcode)) {
            ll_so_create.setVisibility(View.VISIBLE);
            ll_so_line.setVisibility(View.VISIBLE);
        } else {
            ll_so_create.setVisibility(View.GONE);
            ll_so_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isInvoiceCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isInvoiceTcode)) {
            ll_invoice_create.setVisibility(View.VISIBLE);
            ll_invoice_line.setVisibility(View.VISIBLE);
        } else {
            ll_invoice_create.setVisibility(View.GONE);
            ll_invoice_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isSampleDisbursmentEnabledKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSampleDisbursmentCreateTcode)) {
            llSampleCollection.setVisibility(View.VISIBLE);
            llSampleCollectionLine.setVisibility(View.VISIBLE);
        } else {
            llSampleCollection.setVisibility(View.GONE);
            llSampleCollectionLine.setVisibility(View.GONE);
        }

        ll_must_sell.setVisibility(View.GONE);
        ll_must_sell_line.setVisibility(View.GONE);

        sharedVal = sharedPreferences.getString(Constants.isCompInfoEnabled, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCompInfoTcode)) {
            ll_competitor_info_create.setVisibility(View.VISIBLE);
            ll_competitor_info_line.setVisibility(View.VISIBLE);
        } else {
            ll_competitor_info_create.setVisibility(View.GONE);
            ll_competitor_info_line.setVisibility(View.GONE);
        }


        ll_mer_det.setVisibility(View.GONE);
        ll_mer_details_line.setVisibility(View.GONE);

        sharedVal = sharedPreferences.getString(Constants.isFeedbackCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isFeedbackTcode)) {
            ll_feed_back_create.setVisibility(View.VISIBLE);
            ll_feed_back_line.setVisibility(View.VISIBLE);
        } else {
            ll_feed_back_create.setVisibility(View.GONE);
            ll_feed_back_line.setVisibility(View.GONE);
        }
        sharedVal = sharedPreferences.getString(Constants.isReturnOrderCreateEnabled, "");
        if (sharedVal.equalsIgnoreCase(Constants.isReturnOrderTcode)) {
            ll_return_order_create.setVisibility(View.VISIBLE);
            ll_return_order_line.setVisibility(View.VISIBLE);
        } else {
            ll_return_order_create.setVisibility(View.GONE);
            ll_return_order_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isWindowDisplayKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isWindowDisplayTcode)) {
            ll_window_display_create.setVisibility(View.VISIBLE);
            ll_window_display_line.setVisibility(View.VISIBLE);
        } else {
            ll_window_display_create.setVisibility(View.GONE);
            ll_window_display_line.setVisibility(View.GONE);
        }

        LinearLayout ll_visit_invoice_His = (LinearLayout) myInflatedView.findViewById(R.id.ll_visit_invoice_His);
        LinearLayout ll_inv_his_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_inv_his_line);

        LinearLayout ll_visit_retailer_stock = (LinearLayout) myInflatedView.findViewById(R.id.ll_visit_retailer_stock);
        LinearLayout ll_ret_stock_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_ret_stock_line);
        ll_visit_retailer_stock.setOnClickListener(this);

        LinearLayout ll_visit_outlet_survey = (LinearLayout) myInflatedView.findViewById(R.id.ll_visit_outlet_survey);
        LinearLayout ll_outlet_survey_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_outlet_survey_line);
        ll_visit_outlet_survey.setOnClickListener(this);


        LinearLayout ll_trends = (LinearLayout) myInflatedView.findViewById(R.id.ll_trends);
        LinearLayout ll_trends_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_trends_line);

        LinearLayout ll_act_status = (LinearLayout) myInflatedView.findViewById(R.id.ll_act_status);
        LinearLayout ll_act_status_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_act_status_line);

        LinearLayout ll_coll_his = (LinearLayout) myInflatedView.findViewById(R.id.ll_coll_his);
        LinearLayout ll_coll_his_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_coll_his_line);

        LinearLayout ll_new_product = (LinearLayout) myInflatedView.findViewById(R.id.ll_new_product);
        LinearLayout ll_new_product_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_new_product_line);

        LinearLayout ll_focused_prd_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_focused_prd_line);
        LinearLayout ll_focused_prd = (LinearLayout) myInflatedView.findViewById(R.id.ll_focused_prd);

        LinearLayout ll_customer_comp_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_customer_complaints_line);
        LinearLayout ll_customer_comp = (LinearLayout) myInflatedView.findViewById(R.id.ll_customer_complaints_create);
        ll_customer_comp.setOnClickListener(this);

        ll_focused_prd.setVisibility(View.GONE);
        ll_focused_prd_line.setVisibility(View.GONE);
        ll_new_product.setVisibility(View.GONE);
        ll_new_product_line.setVisibility(View.GONE);
        ll_visit_invoice_His.setVisibility(View.GONE);
        ll_inv_his_line.setVisibility(View.GONE);
        ll_trends.setVisibility(View.GONE);
        ll_trends_line.setVisibility(View.GONE);
        ll_act_status.setVisibility(View.GONE);
        ll_act_status_line.setVisibility(View.GONE);
        ll_coll_his.setVisibility(View.GONE);
        ll_coll_his_line.setVisibility(View.GONE);

        sharedVal = sharedPreferences.getString(Constants.isRetailerStockKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isRetailerStockTcode)) {
            ll_visit_retailer_stock.setVisibility(View.VISIBLE);
            ll_ret_stock_line.setVisibility(View.VISIBLE);
        } else {
            ll_visit_retailer_stock.setVisibility(View.GONE);
            ll_ret_stock_line.setVisibility(View.GONE);
        }


        sharedVal = sharedPreferences.getString(Constants.isCustomerComplaintEnabledKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCustomerComplaintCreateTcode)) {
            ll_customer_comp.setVisibility(View.VISIBLE);
            ll_customer_comp_line.setVisibility(View.VISIBLE);
        } else {
            ll_customer_comp.setVisibility(View.GONE);
            ll_customer_comp_line.setVisibility(View.GONE);
        }

        ib_merchndising = (ImageButton) myInflatedView.findViewById(R.id.ib_merchndising_selection);
        ib_merchndising.setOnClickListener(this);

        ibInvoiceCreate = (ImageButton) myInflatedView.findViewById(R.id.ib_invoice_create_selection);
        ibInvoiceCreate.setOnClickListener(this);

        ImageButton ib_SO_create = (ImageButton) myInflatedView.findViewById(R.id.ib_so_create_selection);
        ib_SO_create.setOnClickListener(this);

        ib_collection_create = (ImageButton) myInflatedView.findViewById(R.id.ib_collection_create_selection);
        ib_collection_create.setOnClickListener(this);

        ibFeedbackCreate = (ImageButton) myInflatedView.findViewById(R.id.ib_feed_back_create_selection);
        ibFeedbackCreate.setOnClickListener(this);

        ibCompInfoCreate = (ImageButton) myInflatedView.findViewById(R.id.ib_competitor_info_create_selection);
        ibCompInfoCreate.setOnClickListener(this);

        btnWinDisplay = (ImageButton) myInflatedView.findViewById(R.id.right_ib_window_display);
        btnWinDisplay.setOnClickListener(this);

        ImageButton ib_inv_his = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_invoice_his_next);
        ib_inv_his.setOnClickListener(this);

        ImageButton ib_trends = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_trends_next);
        ib_trends.setOnClickListener(this);

        ImageButton ib_act_status = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_act_status_next);
        ib_act_status.setOnClickListener(this);

        ImageButton ib_coll_his = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_coll_his_next);
        ib_coll_his.setOnClickListener(this);


        ImageButton ib_visit_focused_prd_next = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_focused_prd_next);
        ib_visit_focused_prd_next.setOnClickListener(this);

        ImageButton ib_visit_new_product_next = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_new_product_next);
        ib_visit_new_product_next.setOnClickListener(this);

        btSampleCollection = (ImageButton) myInflatedView.findViewById(R.id.right_ib_sample_collection);
        btSampleCollection.setOnClickListener(this);

        ib_customer_create = (ImageButton) myInflatedView.findViewById(R.id.ib_cuustomer_complaint_selection);
        ib_customer_create.setOnClickListener(this);

        ImageView ivAddressCollapse = (ImageView) myInflatedView.findViewById(R.id.iv_visit_address_collapse);
        ivAddressCollapse.setOnClickListener(this);

        ImageView ib_mer_details_next = (ImageView) myInflatedView.findViewById(R.id.ib_mer_details_next);
        ib_mer_details_next.setOnClickListener(this);

        ib_ret_stock = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_retailer_stock_next);
        ib_ret_stock.setOnClickListener(this);

        ib_outlet_survey = (ImageButton) myInflatedView.findViewById(R.id.ib_visit_outlet_survey_next);
        ib_outlet_survey.setOnClickListener(this);

        ib_so_create = (ImageButton) myInflatedView.findViewById(R.id.ib_so_create_selection);
        ib_so_create.setOnClickListener(this);

        ib_return_order_create = (ImageButton) myInflatedView.findViewById(R.id.ib_return_order_create_selection);
        ib_return_order_create.setOnClickListener(this);
        refreshUI();

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        String mStrVisitQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() +
                "' and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "'";

        mStrVisitStartedOrNotQuery = Constants.Visits + "?$top=1 &$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";

        try {
            mSetVisitKeys = OfflineManager.getVisitKeysForCustomer(mStrVisitQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        checkTodayCollCreateOrNot(mStrVisitQry);
        checkTodayMerchReviewCreateOrNot(mStrVisitQry);
        checkTodayCompInfoCreateOrNot(mStrVisitQry);
        checkTodayFeedbackCreateOrNot(mStrVisitQry);
        checkTodayInvoiceCreateOrNot(mStrVisitQry);
        checkTodayRetailerStockCreateOrNot(mStrVisitQry);
        checkTodaySOCreateOrNot(mStrVisitQry);
        checkSampleDisbursement(mStrVisitQry);
        checkTodayROCreateOrNot(mStrVisitQry);
        checkTodayCustomerComplaintsCreateOrNOt(mStrVisitQry);
        checkWindowDisp(mStrVisitQry);
    }

    private void checkTodaySOCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.SOCreateID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.SOCreateID)) {
                ib_so_create.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayROCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.ROCreateID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.ROCreateID)) {
                ib_return_order_create.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayCustomerComplaintsCreateOrNOt(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.CustomerCompCreateID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.CustomerCompCreateID)) {
                ib_customer_create.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayRetailerStockCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.RetailerStockID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.RetailerStockID)) {
                ib_ret_stock.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayCollCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.CollCreateID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.CollCreateID)) {
                ib_collection_create.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayMerchReviewCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.MerchReviewCreateID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.MerchReviewCreateID)) {
                ib_merchndising.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayInvoiceCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.Secondary_Invoice_Type)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.Secondary_Invoice_Type)) {
                ibInvoiceCreate.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayFeedbackCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.FeedbackID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.FeedbackID)) {
                ibFeedbackCreate.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayCompInfoCreateOrNot(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.CompInfoCreateID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.CompInfoCreateID)) {
                ibCompInfoCreate.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkSampleDisbursement(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.SampleDisbursementID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.SampleDisbursementID)) {
                btSampleCollection.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkWindowDisp(String mStrVisitQry) {
        try {
//            if (OfflineManager.getVisitActivityStatusForCustomer(mStrVisitQry, Constants.WindowDisplayID)) {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.WindowDisplayID)) {
                btnWinDisplay.setImageResource(R.drawable.ic_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
//        if (Constants.onGpsCheck(getActivity())) {
//            if (UtilConstants.getLocation(getActivity())) {
        switch (v.getId()) {
            case R.id.ib_collection_create_selection:
                onNavToCollectionCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_collection_create:
                onNavToCollectionCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ib_merchndising_selection:
                onNavToMerchReviewCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_snap_create:
                onNavToMerchReviewCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ib_feed_back_create_selection:
                onNavToFeedbackCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_feed_back_create:
                onNavToFeedbackCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ib_competitor_info_create_selection:
                onNavToCompetitorInfoCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_competitor_info_create:
                onNavToCompetitorInfoCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ib_invoice_create_selection:
                onNavToInvoiceCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_invoice_create:
                onNavToInvoiceCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ib_visit_retailer_stock_next:
                onRetailerStockEntry();
                break;
            case R.id.ll_visit_retailer_stock:
                onRetailerStockEntry();
                break;
            case R.id.ll_visit_outlet_survey:
                onOutletSurvey();
                break;
            case R.id.ib_visit_outlet_survey_next:
                onOutletSurvey();
                break;
            case R.id.ib_so_create_selection:
                onNavToSOCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_so_create:
                onNavToSOCreateActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ib_return_order_create_selection:
                onNavToReturnOrderActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_return_order_create:
                onNavToReturnOrderActivity(mStrVisitStartedOrNotQuery);
                break;
            case R.id.right_ib_sample_collection:
                onNavToSampleCollection(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_sample_collection_master:
                onNavToSampleCollection(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ib_cuustomer_complaint_selection:
                onNavToCustomerComplaints(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_customer_complaints_create:
                onNavToCustomerComplaints(mStrVisitStartedOrNotQuery);
                break;
            case R.id.right_ib_window_display:
                onNavToWinDisplay(mStrVisitStartedOrNotQuery);
                break;
            case R.id.ll_window_disply:
                onNavToWinDisplay(mStrVisitStartedOrNotQuery);
                break;
        }
//            }
//        }
    }

    private void onOutletSurvey() {
        Intent intentOutletSurveyActivity = new Intent(getActivity(),
                OutletSurveyActivity.class);
        intentOutletSurveyActivity.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentOutletSurveyActivity.putExtra(Constants.CPUID, mUID);
        intentOutletSurveyActivity.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentOutletSurveyActivity.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentOutletSurveyActivity.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentOutletSurveyActivity.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentOutletSurveyActivity);
    }

    private void onNavToCustomerComplaints(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent intentFeedBack = new Intent(getActivity(), CustomerComplaintsAct.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {

        }
    }

    private void onNavToReturnOrderActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent intentFeedBack = new Intent(getActivity(), ReturnOrderCreate.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {

        }
    }

    //TODO Navigating to sales order create screen
    private void onNavToSOCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Constants.MAPORDQtyByCrsSkuGrp.clear();
                Constants.MAPSCHGuidByCrsSkuGrp.clear();
//                Intent intentFeedBack = new Intent(getActivity(), SalesOrderCreateActivity.class);
                Intent intentFeedBack = new Intent(getActivity(), SalesOrderCreateActivity1.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }


    private void onRetailerStockEntry() {
        Intent intentFeedBack = new Intent(getActivity(), RetailerStockEntry.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentFeedBack);
    }

    private void onNavToCollectionCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent intentFeedBack = new Intent(getActivity(), CollectionCreateActivity.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onNavToMerchReviewCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent intentFeedBack = new Intent(getActivity(), MerchndisingActivity.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onNavToCompetitorInfoCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
//                Intent intentFeedBack = new Intent(getActivity(), CompetitorInfoActivity.class);
                Intent intentFeedBack = new Intent(getActivity(), CompetitorInformation.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onNavToInvoiceCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
//                Intent intentFeedBack = new Intent(getActivity(), InvoiceCreateActivity.class);
                Intent intentFeedBack = new Intent(getActivity(), com.arteriatech.emami.invoicecreate.InvoiceCreateActivity.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onNavToFeedbackCreateActivity(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent intentFeedBack = new Intent(getActivity(), FeedBackActivity.class);
                intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                intentFeedBack.putExtra(Constants.CPUID, mUID);
                intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(intentFeedBack);

            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onNavToSampleCollection(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent sampleCollection = new Intent(getContext(), SampleDisbursementActivity.class);
                sampleCollection.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                sampleCollection.putExtra(Constants.CPUID, mUID);
                sampleCollection.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                sampleCollection.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                sampleCollection.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                sampleCollection.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(sampleCollection);
            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onNavToWinDisplay(String mStrVisitQry) {
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitQry)) {
                Intent sampleCollection = new Intent(getContext(), WindowDisplayListActivity.class);
                sampleCollection.putExtra(Constants.CPNo, mStrBundleRetailerNo);
                sampleCollection.putExtra(Constants.CPUID, mUID);
                sampleCollection.putExtra(Constants.RetailerName, mStrBundleRetailerName);
                sampleCollection.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                sampleCollection.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                sampleCollection.putExtra(Constants.comingFrom, mComingFrom);
                startActivity(sampleCollection);
            } else {
                UtilConstants.showAlert(getString(R.string.alert_please_start_visit), getActivity());
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate() {
        refreshUI();
    }
}
