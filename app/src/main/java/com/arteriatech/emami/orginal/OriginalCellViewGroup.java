package com.arteriatech.emami.orginal;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.msecsales.R;

import java.util.List;


/**
 * Created by miguel on 09/02/2016.
 *
 */
public class OriginalCellViewGroup extends FrameLayout
        implements
        TableFixHeaderAdapter.HeaderBinder<String>,
        TableFixHeaderAdapter.BodyBinder<SKUGroupBean>,
        TableFixHeaderAdapter.SectionBinder<SKUGroupBean>,
        TableFixHeaderAdapter.BodyEditTextBinder<SKUGroupBean> {

    private Context context;
    public TextView textView = null;
    public View vg_root = null;
    public EditText hEditText = null;
    private LinearLayout ll_header_body_area=null;
    private boolean isTyping = false;
    private static String TAG = "OriginalCellViewGroup";

    public OriginalCellViewGroup(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public OriginalCellViewGroup(Context context, int type) {
        super(context);
        this.context = context;
        if (type == 1) {
            initEditText();
        }
    }

    public OriginalCellViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.text_view_group, this, true);
        textView = (TextView) findViewById(R.id.tv_text);
        vg_root = findViewById(R.id.vg_root);
        ll_header_body_area = (LinearLayout) findViewById(R.id.ll_header_body_area_right);

    }

    private void initEditText() {
        LayoutInflater.from(context).inflate(R.layout.edit_text_group, this, true);
        hEditText = (EditText) findViewById(R.id.etQty);
        vg_root = findViewById(R.id.vg_root);
        ll_header_body_area = (LinearLayout) findViewById(R.id.ll_header_body_area_right);
       /* hEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: "+hasFocus);
            }
        });*/
    }
    @Override
    public void bindHeader(String headerName, int column) {
        if (textView != null) {
            textView.setText(headerName.toUpperCase());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);
        }
        if (vg_root != null) {
            vg_root.setBackgroundResource(R.drawable.cell_header_border_bottom_right_gray);
        }
    }

    @Override
    public void bindBody(SKUGroupBean item, int row, int column) {
        if (textView != null) {
            textView.setText(item.getSKUGroupDesc());
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setGravity(Gravity.RIGHT);
            if (item.isHeader()){
                textView.setText((column==0)?item.getSOQ()+" "+ item.getUOM():(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==3)?item.getDBSTK()+" "+ item.getUOM():(column==4)?item.getRETSTK()+" "+ item.getUOM():"");
            }
            else {
//                textView.setText((column==0)?"":(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==3)?"":"");
                textView.setText((column==0)?"":(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==3)?item.getDBSTK()+" "+ item.getUOM():(column==4)?item.getRETSTK()+" "+ item.getUOM():"");
            }
        }
//        if (vg_root != null) {
//            vg_root.setBackgroundResource(row % 2 == 0 ? R.drawable.cell_lightgray_border_bottom_right_gray : R.drawable.cell_white_border_bottom_right_gray);
//        }

        if(ll_header_body_area!=null){
            if (item.isHeader()){

                if (item.getMatTypeVal().equalsIgnoreCase(Constants.DR)){
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_green));
                }else if(item.getMatTypeVal().equalsIgnoreCase(Constants.US)){ // Orange Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                } else if(item.getMatTypeVal().equalsIgnoreCase(Constants.CS)){ // Blue Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.HeaderTileBackground));
                }

               /* // Must sell and focused products are orange color
                if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_01) || item.getMatTypeVal().equalsIgnoreCase(Constants.str_02)) {
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                }else

                    //  new launched products are blue color
                    if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_03)) {
                        ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
                    }*/
            }

        }
    }

    @Override
    public void bindSection(SKUGroupBean item, int row, int column) {
        if (textView != null) {
            textView.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public void bindEditTextBody(final SKUGroupBean item, final int row, int column, final TableFixHeaderAdapter tableFixHeaderAdapter, final List editextBody,TableFixHeaderAdapter.TextTypeListener textTypeListener,OriginalCellViewGroup viewGroup) {
        if (hEditText!=null) {
            textTypeListener.onTextChangeItem(item,row,column,tableFixHeaderAdapter,hEditText,viewGroup);
        }

        if(ll_header_body_area!=null){
            if (item.isHeader()){
                if (item.getMatTypeVal().equalsIgnoreCase(Constants.DR)){
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_green));
                }else if(item.getMatTypeVal().equalsIgnoreCase(Constants.US)){ // Orange Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                } else if(item.getMatTypeVal().equalsIgnoreCase(Constants.CS)){ // Blue Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.HeaderTileBackground));
                }
               /* // Must sell and focused products are orange color
                if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_01) || item.getMatTypeVal().equalsIgnoreCase(Constants.str_02)) {
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                }else

                    //  new launched products are blue color
                    if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_03)) {
                        ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
                    }*/
            }

        }
    }

    /*@Override
    public void bindEditTextBody(Object item, int row, int column, TableFixHeaderAdapter tableFixHeaderAdapter, List editextBody, TableFixHeaderAdapter.TextTypeListener textTypeListener, Object vbody) {

    }*/
}
