package com.arteriatech.emami.orginal;

import android.content.Context;

import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.scroll.BaseTableAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 12/02/2016.
 *
 */
public class OriginalTableFixHeader {
    private Context context;
    private ArrayList<SKUGroupBean> alCRSSKUList = null;
    private TableFixHeaderAdapter.ClickListener clickListener = null;
    private TableFixHeaderAdapter.TextTypeListener textTypeListener=null;
    private String typeValue="";

    public OriginalTableFixHeader(Context context, ArrayList<SKUGroupBean> alCRSSKUList, TableFixHeaderAdapter.ClickListener clickListener, TableFixHeaderAdapter.TextTypeListener textTypeListener,String typevalue) {
        this.context = context;
        this.alCRSSKUList = alCRSSKUList;
        this.clickListener =clickListener;
        this.textTypeListener=textTypeListener;
        this.typeValue=typevalue;
    }

    public BaseTableAdapter getInstance() {
        OriginalTableFixHeaderAdapter adapter = new OriginalTableFixHeaderAdapter(context);
        List<SKUGroupBean> body = alCRSSKUList;

        adapter.setFirstHeader(typeValue);
        adapter.setHeader(getHeader());
        adapter.setFirstBody(body);
        adapter.setBody(alCRSSKUList);
        adapter.setEditTextBody(alCRSSKUList);
        adapter.setClickListenerFirstBody(clickListener);
        adapter.setTextChangeListener(textTypeListener);
        return adapter;
    }


    private List<String> getHeader() {
        final String headers[] = {
                "SOQ",
                "QTY",
                "MRP",
                "DB Stock",
                "RL Stock",
        };

        return Arrays.asList(headers);
    }

}
