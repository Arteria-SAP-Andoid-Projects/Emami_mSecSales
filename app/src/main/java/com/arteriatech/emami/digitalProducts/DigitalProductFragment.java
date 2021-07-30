package com.arteriatech.emami.digitalProducts;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.DocumentsBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DigitalProductFragment extends Fragment {


    private RecyclerView recyclerView;
    private View noRecordFound;
//    private ArrayList<DocumentsBean> documentsBeanList=new ArrayList<>() ;

    public DigitalProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_product, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_digital);
        noRecordFound = (View)view.findViewById(R.id.noRecordFound);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        getDatafromOfflineDB();
    }
    private void getDatafromOfflineDB(){
        try {
            ArrayList<DocumentsBean> documentsBeanList;
            documentsBeanList = OfflineManager.getDocuments(Constants.Documents + "?$filter=DocumentTypeID eq 'ZDMS_DGPRD'");
            if(!documentsBeanList.isEmpty()){
                DigitalProductAdapter digitalProductAdapter = new DigitalProductAdapter(getContext(),documentsBeanList);
                recyclerView.setAdapter(digitalProductAdapter);
                noRecordFound.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else {
                noRecordFound.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
}
