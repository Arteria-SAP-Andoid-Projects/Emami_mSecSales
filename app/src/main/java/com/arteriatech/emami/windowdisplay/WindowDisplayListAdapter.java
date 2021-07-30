package com.arteriatech.emami.windowdisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10769 on 17-03-2017.
 */

public class WindowDisplayListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<SchemeBean> schemeModelList;
    private OnClickInterface onClickInterface = null;

    public WindowDisplayListAdapter(Context mContext, ArrayList<SchemeBean> schemeModelList) {
        this.mContext = mContext;
        this.schemeModelList = schemeModelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.window_display_item, parent, false);
        return new WindowDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SchemeBean schemeModel = schemeModelList.get(position);
        ((WindowDisplayViewHolder) holder).tvSchemeName.setText(schemeModel.getSchemeDesc());
        if (schemeModel.isSecondTime())
            ((WindowDisplayViewHolder) holder).llStatusColor.setBackgroundColor(mContext.getResources().getColor(R.color.YELLOW));
        else
            ((WindowDisplayViewHolder) holder).llStatusColor.setBackgroundColor(mContext.getResources().getColor(R.color.RED));

        ((WindowDisplayViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickInterface != null) {
                    onClickInterface.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return schemeModelList.size();
    }

    public void onItemClickListener(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }
}
