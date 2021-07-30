package com.arteriatech.emami.visualaid;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.arteriatech.emami.adapter.VideosAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.DocumentsBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10763 on 2/27/2017.
 */
public class VideosFragment extends Fragment {

    private View myInflatedView = null;
    ArrayList<DocumentsBean> allVideoList;
    int[] videoId = {
            R.drawable.ic_nvideo
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_videos, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        getVideosList();

        return myInflatedView;
    }

    private void getVideosList() {
        try {
            allVideoList = OfflineManager.getDocuments(Constants.Documents + "?$filter=" + Constants.DocumentMimeType + " eq '" + Constants.MimeTypeMP4 + "' and DocumentTypeID eq 'ZDMS_VAID'");

            VideosAdapter videosAdapter = new VideosAdapter(getActivity(), allVideoList, videoId);
            GridView gridview = (GridView) myInflatedView.findViewById(R.id.gridview);
            View noDataFound = (View) myInflatedView.findViewById(R.id.noDataFound);
            gridview.setAdapter(videosAdapter);
            videosAdapter.notifyDataSetChanged();
            if (allVideoList.isEmpty()) {
                noDataFound.setVisibility(View.VISIBLE);
                gridview.setVisibility(View.GONE);
            } else {
                noDataFound.setVisibility(View.GONE);
                gridview.setVisibility(View.VISIBLE);
            }

        } catch (Exception ex) {

        }
    }


}
