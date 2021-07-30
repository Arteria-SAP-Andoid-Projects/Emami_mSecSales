package com.arteriatech.emami.visualaid;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.arteriatech.emami.adapter.BrouchersAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.DocumentsBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10763 on 2/27/2017.
 *
 */
public class BrouchersFragment extends Fragment {
    ArrayList<DocumentsBean> allDocumentList;
    int[] imageId = {
            R.drawable.ic_npdf,
            R.drawable.ic_ndoc,
            R.drawable.ic_nppt,
            R.drawable.ic_njpg,
            R.drawable.ic_npng,
    };
    private View myInflatedView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myInflatedView = inflater.inflate(R.layout.fragment_brouchers, container, false);
        //   onLoadDialog();
        getDocumentDetails();

        return myInflatedView;
    }

    private void getDocumentDetails() {

        try {
            allDocumentList = OfflineManager.getDocuments(Constants.Documents + "?$filter=("+Constants.DocumentMimeType+"  eq '"+Constants.MimeTypePDF+"' " +
                    "or "+Constants.DocumentMimeType+" eq '"+Constants.MimeTypeDocx+"' " +
                    "or "+Constants.DocumentMimeType+" eq '"+Constants.MimeTypeMsword+"' " +
                    "or "+Constants.DocumentMimeType+" eq '"+Constants.MimeTypePPT+"' " +
                    "or "+Constants.DocumentMimeType+" eq '"+Constants.MimeTypevndmspowerpoint+"') and DocumentTypeID eq 'ZDMS_VAID'");
            BrouchersAdapter brouchersAdapter = new BrouchersAdapter(getActivity(), allDocumentList, imageId);
            GridView gridview = (GridView) myInflatedView.findViewById(R.id.gridview);
            View noDataFound = (View) myInflatedView.findViewById(R.id.noDataFound);
            gridview.setAdapter(brouchersAdapter);
            brouchersAdapter.notifyDataSetChanged();
            if (allDocumentList.isEmpty()){
                noDataFound.setVisibility(View.VISIBLE);
                gridview.setVisibility(View.GONE);
            }else {
                noDataFound.setVisibility(View.GONE);
                gridview.setVisibility(View.VISIBLE);
            }
        } catch (Exception linex) {
            linex.printStackTrace();
        }
    }


}
