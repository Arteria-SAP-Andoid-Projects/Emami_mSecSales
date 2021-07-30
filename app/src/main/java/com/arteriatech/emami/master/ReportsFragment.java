package com.arteriatech.emami.master;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.customerComplaints.CustomerListActivity;
import com.arteriatech.emami.feedback.FeedbackListActivity;
import com.arteriatech.emami.finance.CompInfoListActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.reports.CollectionHistoryActivity;
import com.arteriatech.emami.reports.FOSRetailerTrendsActivity;
import com.arteriatech.emami.reports.InvoiceHistoryActivity;
import com.arteriatech.emami.reports.OutstandingHistoryActivity;
import com.arteriatech.emami.reports.RetailerStockActivity;
import com.arteriatech.emami.returnOrder.ReturnOrderTabActivity;
import com.arteriatech.emami.visit.MerchindisingListActivity;
import com.arteriatech.emami.visit.NewProductListActivity;


public class ReportsFragment extends Fragment {

    private final String[] mArrStrIconNames = Constants.reportsArray;
    public int[] mArrIntIconPosition = Constants.IconPositionReportFragment;
    String mStrCPGUID = "", mStrRetID = "", mStrRetName = "", mStrCPGUID36 = "", mStrRetUID = "";
    private GridView gvRetailerDetails;
    private int[] mArrIntMinVisibility;

    public ReportsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStrCPGUID = getArguments().getString(Constants.CPGUID32);
        mStrRetID = getArguments().getString(Constants.CPNo);
        mStrRetName = getArguments().getString(Constants.RetailerName);
        mStrCPGUID36 = getArguments().getString(Constants.CPGUID);
        mStrRetUID = getArguments().getString(Constants.CPUID);

        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_ret_reports, container, false);


        onInitUI(myInflatedView);
        setIconVisibility();
        setValuesToUI(myInflatedView);


        return myInflatedView;
    }

    /*
     * TODO This method initialize UI
     */
    private void onInitUI(View myInflatedView) {
        gvRetailerDetails = (GridView) myInflatedView.findViewById(R.id.gv_retailer_details);
    }

    /*
        TODO This method set values to UI
        */
    private void setValuesToUI(View myInflatedView) {
        gvRetailerDetails.setAdapter(new ReportsAdapter(myInflatedView.getContext()));
    }

    /*
    ToDo navigate to OutstandingHistory activity
   */
    private void onOutHistory() {
        Intent intentVisit = new Intent(getActivity(), OutstandingHistoryActivity.class);
        intentVisit.putExtra(Constants.CPNo, mStrRetID);
        intentVisit.putExtra(Constants.RetailerName, mStrRetName);
        intentVisit.putExtra(Constants.CPGUID, mStrCPGUID);
        intentVisit.putExtra(Constants.CPUID, mStrRetUID);
        intentVisit.putExtra(Constants.comingFrom, Constants.RetDetails);
        startActivity(intentVisit);
    }

    /*
    ToDo navigate to CollectionHistory activity
   */
    private void onCollectionHistory() {
        Intent intentCollHisActivity = new Intent(getActivity(),
                CollectionHistoryActivity.class);
        intentCollHisActivity.putExtra(Constants.CPNo, mStrRetID);
        intentCollHisActivity.putExtra(Constants.RetailerName, mStrRetName);
        intentCollHisActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentCollHisActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentCollHisActivity);
    }

    /*
    ToDo navigate to BillHistory activity
   */
    private void onInvoiceHistory() {
        Intent intentInvoiceHistoryActivity = new Intent(getActivity(),
                InvoiceHistoryActivity.class);
//        Intent intentInvoiceHistoryActivity = new Intent(getActivity(),
//                InvoiceHistoryListActivity.class);
        intentInvoiceHistoryActivity.putExtra(Constants.CPNo, mStrRetID);
        intentInvoiceHistoryActivity.putExtra(Constants.RetailerName, mStrRetName);
        intentInvoiceHistoryActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentInvoiceHistoryActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentInvoiceHistoryActivity);
    }

    /*
  ToDo navigate to Must sell ,New Product ,Focused Product
 */
    private void onSegmentedMaterials(String segmentedType) {
        Intent intentNewProdListActivity = new Intent(getActivity(),
                NewProductListActivity.class);

        intentNewProdListActivity.putExtra(Constants.CPNo, mStrRetID);
        intentNewProdListActivity.putExtra(Constants.RetailerName, mStrRetName);
        intentNewProdListActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentNewProdListActivity.putExtra(Constants.CPUID, mStrRetUID);
        if (segmentedType.equalsIgnoreCase("01")) {
            intentNewProdListActivity.putExtra(Constants.ID, segmentedType);
            intentNewProdListActivity.putExtra(Constants.Description, Constants.MustSellProduct);
        } else if (segmentedType.equalsIgnoreCase("02")) {
            intentNewProdListActivity.putExtra(Constants.ID, segmentedType);
            intentNewProdListActivity.putExtra(Constants.Description, Constants.FocusedProduct);
        } else if (segmentedType.equalsIgnoreCase("03")) {
            intentNewProdListActivity.putExtra(Constants.ID, segmentedType);
            intentNewProdListActivity.putExtra(Constants.Description, Constants.NewLaunchedProduct);
        }

        startActivity(intentNewProdListActivity);
    }

    /*
   ToDo navigate to Merchandising List activity
  */
    private void onMerchindisingList() {
        Intent intentMerchListActivity = new Intent(getActivity(),
                MerchindisingListActivity.class);
        intentMerchListActivity.putExtra(Constants.CPNo, mStrRetID);
        intentMerchListActivity.putExtra(Constants.RetailerName, mStrRetName);
        intentMerchListActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentMerchListActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentMerchListActivity);
    }

    /*navigates to Merchandising List activity
     */
    private void onRetailerTrend() {
//        Intent intentRtTrendsActivity = new Intent(getActivity(),
//                RetailerTrendsActivity.class);
        Intent intentRtTrendsActivity = new Intent(getActivity(),
                FOSRetailerTrendsActivity.class);
        intentRtTrendsActivity.putExtra(Constants.CPNo, mStrRetID);
        intentRtTrendsActivity.putExtra(Constants.RetailerName, mStrRetName);
        intentRtTrendsActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentRtTrendsActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentRtTrendsActivity);
    }

    /*navigates to Merchandising List activity
     */
    private void onRetailerStock() {
        Intent intentRetStockListActivity = new Intent(getActivity(),
                RetailerStockActivity.class);
        intentRetStockListActivity.putExtra(Constants.CPNo, mStrRetID);
        intentRetStockListActivity.putExtra(Constants.RetailerName, mStrRetName);
        intentRetStockListActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentRetStockListActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentRetStockListActivity);
    }

    /*navigates to feedback List activity
     */
    private void onFeedback() {
        Intent intentFeedbackListActivity = new Intent(getActivity(),
                FeedbackListActivity.class);
        intentFeedbackListActivity.putExtra(Constants.CPNo, mStrRetID);
        intentFeedbackListActivity.putExtra(Constants.RetailerName, mStrRetName);
        intentFeedbackListActivity.putExtra(Constants.CPGUID, mStrCPGUID);
        intentFeedbackListActivity.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentFeedbackListActivity);
    }

    /*navigates to CompInfo List activity
     */
    private void onCompInfo() {
        Intent intentCompInfo = new Intent(getActivity(),
                CompInfoListActivity.class);
        intentCompInfo.putExtra(Constants.CPNo, mStrRetID);
        intentCompInfo.putExtra(Constants.RetailerName, mStrRetName);
        intentCompInfo.putExtra(Constants.CPGUID, mStrCPGUID);
        intentCompInfo.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intentCompInfo);
    }

    private void onReturnOrderList(int comingFrom) {
        Intent intentCompInfo = new Intent(getActivity(),
                ReturnOrderTabActivity.class);
        intentCompInfo.putExtra(Constants.CPNo, mStrRetID);
        intentCompInfo.putExtra(Constants.RetailerName, mStrRetName);
        intentCompInfo.putExtra(Constants.CPGUID, mStrCPGUID);
        intentCompInfo.putExtra(Constants.CPUID, mStrRetUID);
        intentCompInfo.putExtra(Constants.comingFrom, comingFrom);
        startActivity(intentCompInfo);
    }

    private void onCustomerComplaintsList(int comingFrom) {
        Intent intentCompInfo = new Intent(getActivity(),
                CustomerListActivity.class);
        intentCompInfo.putExtra(Constants.CPNo, mStrRetID);
        intentCompInfo.putExtra(Constants.RetailerName, mStrRetName);
        intentCompInfo.putExtra(Constants.CPGUID, mStrCPGUID);
        intentCompInfo.putExtra(Constants.CPUID, mStrRetUID);
        intentCompInfo.putExtra(Constants.comingFrom, comingFrom);
        startActivity(intentCompInfo);
    }

    /*
    ToDo enable icons based on authorization tcodes
     */
    private void setIconVisibility() {
        mArrIntMinVisibility = Constants.IconVisibiltyReportFragment;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        Constants.setIconVisibiltyReports(sharedPreferences, mArrIntMinVisibility);
        int iconCount = 0;
        for (int iconVisibleCount = 0; iconVisibleCount < mArrIntMinVisibility.length; iconVisibleCount++) {
            if (mArrIntMinVisibility[iconVisibleCount] == 1) {
                mArrIntIconPosition[iconCount] = iconVisibleCount;
                iconCount++;
            }
        }

    }

    class ReportsAdapter extends BaseAdapter {


        private Context mContext;

        ReportsAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            int count = 0;
            for (int aMinVisibility : mArrIntMinVisibility) {
                if (aMinVisibility == 1) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int mIntIconPos = mArrIntIconPosition[position];
            View view;
            if (convertView == null) {
                LayoutInflater liRelatedLinks = getActivity().getLayoutInflater();
                view = liRelatedLinks.inflate(R.layout.retailer_menu_inside, parent, false);
                view.requestFocus();
            } else {
                view = convertView;
            }
            final TextView tvIconName = (TextView) view
                    .findViewById(R.id.icon_text);
            tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
            tvIconName.setText(mArrStrIconNames[mIntIconPos]);
            final ImageView ivIconId = (ImageView) view
                    .findViewById(R.id.ib_must_sell);
            final LinearLayout ll_icon_area_sel = (LinearLayout) view
                    .findViewById(R.id.ll_main_menu_icon_sel);
            if (mIntIconPos == 0) {
                ivIconId.setImageResource(R.drawable.ic_invoices);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInvoiceHistory();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInvoiceHistory();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInvoiceHistory();
                    }
                });
            } else if (mIntIconPos == 1) {
                ivIconId.setImageResource(R.drawable.collection_history);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onCollectionHistory();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCollectionHistory();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCollectionHistory();
                    }
                });
            } else if (mIntIconPos == 2) {
                ivIconId.setImageResource(R.drawable.ic_outstanding);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onOutHistory();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onOutHistory();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onOutHistory();
                    }
                });
            } else if (mIntIconPos == 3) {
                ivIconId.setImageResource(R.drawable.must_sell);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onSegmentedMaterials("01");
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSegmentedMaterials("01");
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSegmentedMaterials("01");
                    }
                });
            } else if (mIntIconPos == 4) {
                ivIconId.setImageResource(R.drawable.focused_products);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onSegmentedMaterials("02");
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSegmentedMaterials("02");
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSegmentedMaterials("02");
                    }
                });
            } else if (mIntIconPos == 5) {
                ivIconId.setImageResource(R.drawable.new_products);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onSegmentedMaterials("03");
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSegmentedMaterials("03");
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSegmentedMaterials("03");
                    }
                });
            } else if (mIntIconPos == 6) {
                ivIconId.setImageResource(R.drawable.ic_snap_icon);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onMerchindisingList();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMerchindisingList();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMerchindisingList();
                    }
                });
            } else if (mIntIconPos == 7) {
                ivIconId.setImageResource(R.drawable.trends);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onRetailerTrend();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRetailerTrend();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRetailerTrend();
                    }
                });
            } else if (mIntIconPos == 8) {
                ivIconId.setImageResource(R.drawable.my_stock_new);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onRetailerStock();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRetailerStock();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRetailerStock();
                    }
                });
            } else if (mIntIconPos == 9) {
                ivIconId.setImageResource(R.drawable.feedback);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onFeedback();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFeedback();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFeedback();
                    }
                });
            } else if (mIntIconPos == 10) {
                ivIconId.setImageResource(R.drawable.comp_info);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onCompInfo();
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCompInfo();
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCompInfo();
                    }
                });
            } else if (mIntIconPos == 11) {
                ivIconId.setImageResource(R.drawable.ic_return_order);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onReturnOrderList(Constants.RETURN_ORDER_POS);
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReturnOrderList(Constants.RETURN_ORDER_POS);
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReturnOrderList(Constants.RETURN_ORDER_POS);
                    }
                });
            } else if (mIntIconPos == 12) {
                ivIconId.setImageResource(R.drawable.ic_return_order);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onReturnOrderList(Constants.SSS_ORDER_POS);
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReturnOrderList(Constants.SSS_ORDER_POS);
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReturnOrderList(Constants.SSS_ORDER_POS);
                    }
                });
            } else if (mIntIconPos == 13) {
                ivIconId.setImageResource(R.drawable.ic_return_order);
                ivIconId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onCustomerComplaintsList(Constants.COMPLAINTS_ORDER_POS);
                    }
                });
                tvIconName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCustomerComplaintsList(Constants.COMPLAINTS_ORDER_POS);
                    }
                });
                ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCustomerComplaintsList(Constants.COMPLAINTS_ORDER_POS);
                    }
                });
            }

            view.setId(position);
            return view;
        }

    }

}
