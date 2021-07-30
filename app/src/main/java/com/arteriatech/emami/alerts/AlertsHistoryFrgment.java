package com.arteriatech.emami.alerts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.adapter.BirthdayListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.DividerItemDecoration;
import com.arteriatech.emami.mbo.BirthdaysBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10526 on 5/20/2017.
 */

public class AlertsHistoryFrgment extends Fragment {

    View myInflatedView;


    String[][] oneWeekDay;
    TextView tvEmptyListLay = null;
    String splitDayMonth[] = null;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    BirthdayListAdapter adapter;


    ArrayList<BirthdaysBean> alertsOrderBeanList = new ArrayList<>();

    public AlertsHistoryFrgment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myInflatedView = inflater.inflate(R.layout.activity_alerts, container, false);
        oneWeekDay = UtilConstants.getOneweekValues(1);
        splitDayMonth = oneWeekDay[0][0].split("-");
        onInitUI(myInflatedView);
        getAlerts();
        setValuesToUI();

        return myInflatedView;
    }


    /*
     * TODO This method initialize UI
     */
    private void onInitUI(View myInflatedView) {
        tvEmptyListLay = (TextView) myInflatedView.findViewById(R.id.tv_empty_lay);
        recyclerView = (RecyclerView) myInflatedView.findViewById(R.id.card_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
    }


    private void getAlerts() {
        alertsOrderBeanList.clear();
        alertsOrderBeanList.addAll(Constants.getAlertsValuesFromDataVault());
        Constants.BoolAlertsHistoryLoaded = true;
    }

    /*
    TODO This method set values to UI
   */
    private void setValuesToUI() {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }

        if (alertsOrderBeanList != null && alertsOrderBeanList.size() > 0) {
            adapter = new BirthdayListAdapter(alertsOrderBeanList, getActivity(), splitDayMonth, recyclerView, tvEmptyListLay);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

            recyclerView.setAdapter(adapter);

            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyListLay.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvEmptyListLay.setVisibility(View.VISIBLE);
        }

    }

    public void updateFragment() {
        onInitUI(myInflatedView);
        getAlerts();
        setValuesToUI();
    }
}
