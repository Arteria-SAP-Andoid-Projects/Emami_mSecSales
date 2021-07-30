package com.arteriatech.emami.orginal;

import android.content.Context;

import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.msecsales.R;

import java.util.Arrays;
import java.util.List;


/**
 * Created by miguel on 11/02/2016.
 */
public class OriginalTableFixHeaderAdapter extends TableFixHeaderAdapter<
        String, FirstBodyHeaderViewGroup,
        String, OriginalCellViewGroup,
        SKUGroupBean,
        FirstBodyHeaderViewGroup,
        OriginalCellViewGroup,
        OriginalCellViewGroup,
        OriginalCellViewGroup> {
    private Context context;

    public OriginalTableFixHeaderAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected FirstBodyHeaderViewGroup inflateFirstHeader() {
        return new FirstBodyHeaderViewGroup(context);
    }

    @Override
    protected OriginalCellViewGroup inflateHeader() {
        return new OriginalCellViewGroup(context);
    }

    @Override
    protected FirstBodyHeaderViewGroup inflateFirstBody() {
        return new FirstBodyHeaderViewGroup(context);
    }

    @Override
    protected OriginalCellViewGroup inflateBody() {
        return new OriginalCellViewGroup(context);
    }

    @Override
    protected OriginalCellViewGroup inflateSection() {
        return new OriginalCellViewGroup(context);
    }

    @Override
    protected OriginalCellViewGroup bindEditTextBody() {
        return new OriginalCellViewGroup(context,1);
    }

    @Override
    protected List<Integer> getHeaderWidths() {
        Integer[] witdhs = {
                (int) context.getResources().getDimension(R.dimen._165sdp),
                (int) context.getResources().getDimension(R.dimen._70sdp),
                (int) context.getResources().getDimension(R.dimen._55sdp),
                (int) context.getResources().getDimension(R.dimen._80sdp),
                (int) context.getResources().getDimension(R.dimen._80sdp),
                (int) context.getResources().getDimension(R.dimen._80sdp),
                (int) context.getResources().getDimension(R.dimen._80sdp)
        };

        return Arrays.asList(witdhs);
    }

    @Override
    protected int getHeaderHeight() {
        return (int) context.getResources().getDimension(R.dimen._35sdp);
    }

    @Override
    protected int getSectionHeight() {
        return (int) context.getResources().getDimension(R.dimen._25sdp);
    }

    @Override
    protected int getBodyHeight() {
        return (int) context.getResources().getDimension(R.dimen._40sdp);
    }

    @Override
    protected int getEditTextBodyHeight() {
        return (int) context.getResources().getDimension(R.dimen._40sdp);
    }

    @Override
    protected boolean isSection(List<SKUGroupBean> items, int row) {
        return items.get(row).isMustSell();
    }
}
