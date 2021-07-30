package com.arteriatech.emami.multipane;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.MSFAApplication;
import com.arteriatech.emami.master.AddressFragment;
import com.arteriatech.emami.master.ReportsFragment;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.visit.VisitFragment;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by e10763 on 3/7/2017.
 */
public class RetailDetailsFragment extends Fragment implements View.OnClickListener, UIListener {


    private String mStrCustomerName = "";
    private String mStrUID = "";
    private String mStrCustomerId = "";
    private String mStrBundleCpGuid = "";
    private String mStrComingFrom = "";
    private String mStrRouteGuid = "";
    private String mStrRouteName = "";

    String mStrCPTypeId = "";
    private ODataGuid mCpGuid;
    private String mStrPopUpText = "";
    private String mStrCustNo = "";

    private String mStrOtherRetailerGuid = "";

    //new
    private String mStrVisitEndRemarks = "";


    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;

    private ODataGuid mStrVisitId = null;
    private boolean mBooleanVisitStarted = false;

    private ProgressDialog pdLoadDialog;
    private boolean mBooleanNavPrvVisitClosed = false;
    private boolean mBooleanSaveStart = false;


    private boolean mBooleanVisitStartDialog = false, mBooleanVisitEndDialog = false;

    ImageView iv_visit_status;
    private String mStrVisitCatId = "";
    Map<String, String> startParameterMap;


    //This is our viewPager
    private ViewPager viewPager;

    TabLayout tabLayout;

    private boolean mBoolMsg = false;

    View detailsView = null;

    static int i = 0;

    public static RetailDetailsFragment newInstance(Bundle bundle) {

        RetailDetailsFragment retailDetailsFragment = new RetailDetailsFragment();


        retailDetailsFragment.setArguments(bundle);
        return retailDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStrCustomerName = getArguments().getString(Constants.RetailerName);
        mStrUID = getArguments().getString(Constants.CPUID);
        mStrCustomerId = getArguments().getString(Constants.CPNo);
        mStrComingFrom = getArguments().getString(Constants.comingFrom);
        mStrBundleCpGuid = getArguments().getString(Constants.CPGUID) != null ? getArguments().getString(Constants.CPGUID) : "";
        mStrRouteName = getArguments().getString(Constants.OtherRouteName) != null ? getArguments().getString(Constants.OtherRouteName) : "";
        mStrRouteGuid = getArguments().getString(Constants.OtherRouteGUID) != null ? getArguments().getString(Constants.OtherRouteGUID) : "";
        mStrVisitCatId = getArguments().getString(Constants.VisitCatID);
        Constants.VisitNavigationFrom = mStrComingFrom;
        mStrCustNo = mStrCustomerId;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Constants.mApplication = (MSFAApplication) getActivity().getApplication();
        Constants.parser = Constants.mApplication.getParser();

        detailsView = getActivity().getLayoutInflater().inflate(
                R.layout.details_layout, null);


        TextView tvCustomerID = (TextView) detailsView.findViewById(R.id.tv_RetailerID);
        TextView tvCustomerName = (TextView) detailsView.findViewById(R.id.tv_RetailerName);
        startParameterMap = new HashMap<String, String>();
        iv_visit_status = (ImageView) detailsView.findViewById(R.id.iv_visit_status);
        iv_visit_status.setOnClickListener(this);

        tvCustomerName.setText(mStrCustomerName);

        tvCustomerID.setText(mStrUID);
        mCpGuid = getCPGUID();
        displayVisitIcon();

        tabIntilize();
        return detailsView;

    }


    /*
           TODO Display Visit Status Icon
        */
    private void displayVisitIcon() {
        if (!mStrComingFrom.equalsIgnoreCase(Constants.RouteList)) {
            Constants.Route_Plan_Key = "";
        }
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList)
                || mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {
            iv_visit_status.setVisibility(View.VISIBLE);
        } else {
            iv_visit_status.setVisibility(View.GONE);
        }
        mBooleanVisitStartDialog = false;
        mBooleanVisitEndDialog = false;
        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "'" +
                "and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' and " + Constants.StatusID + " eq '01'";


        String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                "and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "'and " + Constants.StatusID + " eq '01'";
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                iv_visit_status.setImageResource(R.drawable.stop);
                mBooleanVisitStarted = true;
            } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done);
                mBooleanVisitStarted = false;
            } else {
                Constants.MapEntityVal.clear();

                String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01'";

                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
//                        e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    private void tabIntilize() {

        viewPager = (ViewPager) detailsView.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout = (TabLayout) detailsView.findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {

        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getActivity().getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CPGUID32, mCpGuid.guidAsString32().toUpperCase());
        bundle.putString(Constants.RetailerName, mStrCustomerName);
        bundle.putString(Constants.CPNo, mStrCustomerId);
        bundle.putString(Constants.CPUID, mStrUID);
        bundle.putString(Constants.CPGUID, mCpGuid.guidAsString36().toUpperCase());
        AddressFragment addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid);
        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(bundle);

        adapter.addFrag(addressFragment, Constants.Address);

        if (mBooleanVisitStarted) {

            if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) ||
                    mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {

                VisitFragment visitFragment = new VisitFragment();

                Bundle bundleVisit = new Bundle();
                bundleVisit.putString(Constants.CPGUID32, mCpGuid.guidAsString32().toUpperCase());
                bundleVisit.putString(Constants.RetailerName, mStrCustomerName);
                bundleVisit.putString(Constants.CPNo, mStrCustomerId);
                bundleVisit.putString(Constants.CPUID, mStrUID);
                bundleVisit.putString(Constants.CPGUID, mCpGuid.guidAsString36().toUpperCase());
                bundleVisit.putString(Constants.comingFrom, mStrComingFrom);
                visitFragment.setArguments(bundleVisit);
                adapter.addFrag(visitFragment, Constants.Visit);
            }

        }
        adapter.addFrag(reportsFragment, Constants.Reports);
        viewPager.setAdapter(adapter);
        if (Constants.ComingFromCreateSenarios.equalsIgnoreCase(Constants.X))
            viewPager.setCurrentItem(1);
        Constants.ComingFromCreateSenarios = "";
    }

    /*
        TODO Get Retailer CpGuiD Value
     */
    private ODataGuid getCPGUID() {
        String cpGuidQry = Constants.ChannelPartners + "(guid'" + mStrBundleCpGuid.toUpperCase() + "') ";
        ODataGuid cpGuid = null;

        try {
            ODataEntity retilerEntity = OfflineManager.getRetDetails(cpGuidQry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.CPGUID);
            cpGuid = (ODataGuid) oDataProperty.getValue();
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        return cpGuid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_visit_status:
                onVisitAction();
                break;
        }
    }

    private void onVisitAction() {
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) ||
                mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {
            Constants.MapEntityVal.clear();

            String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01'";
            try {
                mStrVisitId = OfflineManager.getVisitDetails(qry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if (!Constants.MapEntityVal.isEmpty()) {
                mBooleanVisitStarted = true;
            } else {
                mBooleanVisitStarted = false;
            }

            mBooleanSaveStart = false;
            mBooleanNavPrvVisitClosed = false;
            String message = "";
            if (mBooleanVisitStarted) {
                message = getString(R.string.alert_end_visit_gps);
            } else {
                message = getString(R.string.alert_start_visit_gps);
            }
            if (Constants.onGpsCheckCustomMessage(getActivity(), message)) {
                UtilConstants.getLocation(getActivity());
                Constants.MapEntityVal.clear();

                String attdIdStr = "";
                String attnQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
                try {
                    attdIdStr = OfflineManager.getAttendance(attnQry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (!attdIdStr.equalsIgnoreCase("")) {
                    if (!mBooleanVisitStarted) {
                        mStrOtherRetailerGuid = "";
                        //new 28112016
                        String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null and CPGUID ne '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                                "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01'";
                        String[] otherRetDetails = new String[2];
                        try {
                            otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                        if (otherRetDetails[0] == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);

                            builder.setMessage(R.string.alert_start_visit)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                    mBooleanSaveStart = true;
                                                    mBooleanVisitStartDialog = true;
                                                    mBooleanVisitEndDialog = false;
                                                    onSaveStart();
                                                    iv_visit_status.setImageResource(R.drawable.stop);

                                                }
                                            });
                            builder.setNegativeButton(R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                            onRefreshVisitIcon();
                                        }

                                    });

                            builder.show();
                        }
                    }
                }
            }
        }
    }

    private void onRefreshVisitIcon() {

        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                "and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' and " + Constants.StatusID + " eq '01'";
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done);
                mBooleanVisitStarted = false;

            } else {
                Constants.MapEntityVal.clear();
                String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' ";
                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onSaveStart() {
        mStrPopUpText = getString(R.string.marking_visit_start_plz_wait);
        try {
            String cpId = UtilConstants.removeLeadingZeros(mStrCustomerId);
            startParameterMap.put(Constants.CPNo, cpId);
            startParameterMap.put(Constants.CPName, mStrCustomerName);
            startParameterMap.put(Constants.CPTypeID, mStrCPTypeId);
            startParameterMap.put(Constants.VisitCatID, mStrVisitCatId);
            startParameterMap.put(Constants.StatusID, "01");
            startParameterMap.put(Constants.PlannedDate, null);
            startParameterMap.put(Constants.PlannedStartTime, null);
            startParameterMap.put(Constants.PlannedEndTime, null);
            startParameterMap.put(Constants.VisitTypeID, "");
            startParameterMap.put(Constants.VisitTypeDesc, "");
            startParameterMap.put(Constants.Remarks, "");


            Constants.createVisit(startParameterMap, mCpGuid, getActivity(), this);
        } catch (Exception ex) {
            ex.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + ex.getMessage());
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

    }
}
