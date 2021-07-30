package com.arteriatech.emami.common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.arteriatech.emami.mbo.RemarkReasonBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by V1k on 08-Sep-17.
 */

public class MultipleSelectionSpinner extends AppCompatSpinner implements AdapterView.OnItemSelectedListener {

    String[] _items = null;
    boolean[] mSelection = null;

    ArrayAdapter<String> simple_adapter;
    ArrayList<RemarkReasonBean> list;
    ArrayList<MultiSelectBean> multiSelectBeanArrayList = new ArrayList<>();
    private int sbLength;

    public MultipleSelectionSpinner(Context context) {
        super(context);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setOnItemSelectedListener(this);
        super.setAdapter(simple_adapter);

    }

    public MultipleSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setOnItemSelectedListener(this);
        super.setAdapter(simple_adapter);

    }

   /* public void onClick(DialogInterface dialog, int which, boolean isChecked) {

    }*/

    @Override
    public boolean performClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String des = list.get(which).getReasonDesc();
                String id = list.get(which).getReasonCode();
                if (isChecked) {
                    multiSelectBeanArrayList.add(new MultiSelectBean(des, id, isChecked));
                } else {
                    multiSelectBeanArrayList.remove(which);
                }


                if (mSelection != null && which < mSelection.length) {
                    mSelection[which] = isChecked;
                    simple_adapter.clear();
                    if (buildSelectedItemString().length() > 0) {
                        simple_adapter.add(buildSelectedItemString());
                    } else {
                        simple_adapter.add("Tap to select");
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Argument 'which' is out of bounds");
                }
            }
        });
        builder.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "Position :" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(getContext(), multiSelectBeanArrayList.toString(), Toast.LENGTH_LONG).show();
//                System.out.println(multiSelectBeanArrayList.toString());
            }

        });
        /*if (mSelection.length > 3){
            Toast.makeText(getContext(), "Cannot select more than 3", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add("Tap to select");
        ///simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }

    public void getList(ArrayList<RemarkReasonBean> list) {
        this.list = new ArrayList<>();
        this.list = list;
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                }
            }
        }
    }

    public void setDefaultSelection(List<String> selection) {
        if (multiSelectBeanArrayList != null) multiSelectBeanArrayList.clear();
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                String des = list.get(j).getReasonDesc();
                String id = list.get(j).getReasonCode();
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                    multiSelectBeanArrayList.add(new MultiSelectBean(des, id, true));
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setDefaultSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
        /*if (sbLength>0){
            Toast.makeText(getContext(), "Length greater than zero", Toast.LENGTH_SHORT).show();
            simple_adapter.add(buildSelectedItemString());
        }else{
            Toast.makeText(getContext(), "Length shorter", Toast.LENGTH_SHORT).show();
            simple_adapter.add("Tap to select");
        }*/
    }

    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {

                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }

        //Log.e("sb length",""+sb.length());
        sbLength = sb.length();
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        /*String sbCheck;
        if (sb.length()>0){
           sbCheck=sb.toString();
        }else{
            sbCheck="Tap to select";
        }*/
        return sb.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class MultiSelectBean implements Serializable {
        private String description = "";
        private String id = "";
        private boolean isChecked;

        public MultiSelectBean() {
        }

        public MultiSelectBean(String description, String id) {
            this.description = description;
            this.id = id;
        }

        public MultiSelectBean(String description, String id, boolean isChecked) {
            this.description = description;
            this.id = id;
            this.isChecked = isChecked;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
