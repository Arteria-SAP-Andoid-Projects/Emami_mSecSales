package com.arteriatech.emami.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.emami.mbo.VisualAidBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10526 on 2/16/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private ArrayList<VisualAidBean> retDisplayValues;
    private final Context context;

    public ImageAdapter(Context localContext, ArrayList<VisualAidBean> items) {
        context = localContext;
        this.retDisplayValues = items;
    }

    @Override
    public int getCount() {
        return this.retDisplayValues != null ? this.retDisplayValues.size() : 0;
    }

    @Override
    public VisualAidBean getItem(int item) {
        VisualAidBean retListBean;
        retListBean = this.retDisplayValues != null ? this.retDisplayValues.get(item) : null;
        return retListBean;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.mainmenu_inside, parent, false);
        } else {
            view = convertView;
        }
        view.requestFocus();
        VisualAidBean visualAidBean = retDisplayValues.get(position);

        final TextView tvIconName = (TextView) view
                .findViewById(R.id.icon_text);
        tvIconName.setText(visualAidBean.getImageName());
        final ImageView ivIcon = (ImageView) view
                .findViewById(R.id.ib_must_sell);


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        Bitmap bitmap = null;
        if (visualAidBean.getImageType().equalsIgnoreCase(".jpg"))
            bitmap = BitmapFactory.decodeFile(visualAidBean.getImagePath(), options);
        else if (visualAidBean.getImageType().equalsIgnoreCase(".mp4"))
            bitmap = ThumbnailUtils.createVideoThumbnail(visualAidBean.getImagePath(), 0);

        ivIcon.setImageBitmap(bitmap);

        view.setId(position);
        return view;
    }
}
