package com.arteriatech.emami.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arteriatech.emami.mbo.Config;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10763 on 12/19/2016.
 *
 */

public class NewLaunchedProductAdapter  extends ArrayAdapter<Config> {
    private Context context;
    private ArrayList<Config> visitDisplayValues;
    private ArrayList<Config> visitOriginalValues;

    // TODO Below line will use to while selecting specific record and navigating to NewProductListActivity.java class.Future may be useful
    private ArrayList<Config> alConfigBean;
    private NewLaunchProductListFilter filter;
    public NewLaunchedProductAdapter(Context context, ArrayList<Config> items) {
        super(context, R.layout.focused_product_list_adapter, items);
        this.visitDisplayValues= items;
        this.visitOriginalValues = items;
        alConfigBean = items;
        this.context = context;
    }
    @Override
    public int getCount() {
        return this.visitDisplayValues!=null ? this.visitDisplayValues.size() : 0;
    }
    @Override
    public Config getItem(int item) {
        Config visitListBean;
        visitListBean = this.visitDisplayValues!=null ? this.visitDisplayValues.get(item): null;
        return visitListBean;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(convertView==null){
            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.focused_product_list_adapter, parent,false);
        }
        Config beanFocusedProduct = visitDisplayValues.get(position);
        TextView tvMaterialDesc=(TextView)view.findViewById(R.id.tv_material_desc);
        TextView tvMaterial=(TextView)view.findViewById(R.id.tv_material_code);
        tvMaterial.setText(beanFocusedProduct.getValue());
        tvMaterialDesc.setText(beanFocusedProduct.getDescription());
        view.setId(position);
        return view;
    }

    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new NewLaunchedProductAdapter.NewLaunchProductListFilter();
        }
        return filter;
    }

    /**
     * This class search material based on material name from list.
     */
    private class NewLaunchProductListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (visitOriginalValues == null) {
                if(visitDisplayValues==null){
                    visitDisplayValues=new ArrayList<>();
                }
                visitOriginalValues = new ArrayList<>(visitDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = visitOriginalValues;
                results.count = visitOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<Config> filteredItems = new ArrayList<>();
                int count = visitOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    Config item = visitOriginalValues.get(i);
                    String mSirSchemeDescription = item.getDescription().toLowerCase();
                    if (mSirSchemeDescription.contains(prefixString)) {
                        filteredItems.add(item);
                    }
                }
                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            visitDisplayValues = (ArrayList<Config>) results.values; // has the filtered values
            notifyDataSetChanged();
            alConfigBean = visitDisplayValues;
        }
    }
}
