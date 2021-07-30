package com.arteriatech.emami.invoicecreate.invoicecreatesteptwo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.invoicecreate.invoicereview.InvoiceReviewActivity;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.mbo.InvoiceCreateBean;
import com.arteriatech.emami.mbo.ValueHelpBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;


/**
 * Created by e10526 on 07-07-2018.
 */

public class MaterialSelectionActivity extends AppCompatActivity implements MaterialSelView , KeyboardView.OnKeyboardActionListener{
    private InvoiceCreateBean invCreateBean;
    private MaterialSelectionPresenterImpl presenter;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewTypeAdapter<CustomerBean> recyclerViewAdapter = null;
    HorizontalScrollView svHeader = null, svItem = null;
    LinearLayout llDelStockLayout=null;
    private int incrementVal =0;
    private String matQry = "";
    private String mStrComingFrom="";
    TextView tv_RetailerName, tv_RetailerID;

    KeyboardView keyboardView;
    Keyboard keyboard;

    KeyboardView keyboardViewWithDot;
    Keyboard keyboardWithDot;

    private static int lastSelectedEditText = 0;
    private static int lastSelectedMrpEditText = 0;
    private boolean isFirstTime =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtras = getIntent().getExtras();
        setContentView(R.layout.activity_mat_sel_screen);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (bundleExtras != null) {
            invCreateBean = (InvoiceCreateBean) bundleExtras.getSerializable(Constants.EXTRA_SO_DETAIL);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        if(invCreateBean==null){
            invCreateBean =new InvoiceCreateBean();
        }
        matQry = invCreateBean.getMatQry();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarView.initActionBarView(this, true,getString(R.string.title_invoice_Create));

        presenter = new MaterialSelectionPresenterImpl(MaterialSelectionActivity.this, this, true, MaterialSelectionActivity.this, invCreateBean);
        if (!Constants.restartApp(MaterialSelectionActivity.this)) {
            initUI();
//            presenter.onStart();
            new GetRetailerList(false).execute();
        }

    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(MaterialSelectionActivity.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);

        keyboardViewWithDot = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboardWithDot = new Keyboard(MaterialSelectionActivity.this, R.xml.ll_plus_minuus_updown_keyboard);
        keyboardViewWithDot.setKeyboard(keyboardWithDot);
        keyboardViewWithDot.setPreviewEnabled(false);
        keyboardViewWithDot.setOnKeyboardActionListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void showProgressDialog(String message) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void displayByCollectionData(ArrayList<ValueHelpBean> alPaymentMode) {

    }

    @Override
    public void displayInvoiceData(ArrayList<InvoiceBean> alInvList) {

    }

    @Override
    public void errorPaymentMode(String message) {

    }

    @Override
    public void showMessage(String message, boolean isSimpleDialog) {

    }

    private void initUI(){
        initializeKeyboardDependencies();
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        llDelStockLayout = (LinearLayout) findViewById(R.id.llDealerStockCreate);

        tv_RetailerID.setText(invCreateBean.getCPNo());
        tv_RetailerName.setText(invCreateBean.getCPName());
    }


    EditText[] edEnterQty;
    EditText[] edCashDisc;
    private Spinner[] spReason;
    private void displayStock() {

//        if (!flag) {
            llDelStockLayout.removeAllViews();
//        }
//        flag = false;

        final TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.retailer_stock_table_view, null);


        int cursorLength = filteredArrayList.size();
        edEnterQty = new EditText[cursorLength];
        edCashDisc = new EditText[cursorLength];
        TextView[] tvMaterialDesc = new TextView[cursorLength];
        TextView[] tvStockQTY = new TextView[cursorLength];
        TextView[] tvMRP = new TextView[cursorLength];
        spReason = new Spinner[cursorLength];
        ImageButton[] ibDeleteItem = new ImageButton[cursorLength];

        if (cursorLength > 0) {

            for (int i = 0; i < cursorLength; i++) {
                final int selvalue = i;
                final LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(R.layout.ll_invoice_scroll_item, null);

                incrementVal = i;
                tableHeading.setTag(i);
                rowRelativeLayout.setTag(i);
                edEnterQty[i] = (EditText) rowRelativeLayout.findViewById(R.id.edit_quantity);


                edCashDisc[i] = (EditText) rowRelativeLayout.findViewById(R.id.edit_mrp);
                tvMaterialDesc[i] = (TextView) rowRelativeLayout.findViewById(R.id.item_dbstk_sku_desc);
                tvStockQTY[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_stk_qty_value);
                tvMRP[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_unit_price);
                spReason[i] = (Spinner) rowRelativeLayout.findViewById(R.id.sp_select_reason);

                final ArrayList<BatchBean> alBatchNo = filteredArrayList.get(incrementVal).getArrayListBatchItem();

                ArrayAdapter<BatchBean> spOrderReasonAdapter = new ArrayAdapter<>(MaterialSelectionActivity.this,
                        R.layout.custom_textview, alBatchNo);
                spOrderReasonAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spReason[i].setAdapter(spOrderReasonAdapter);
                spReason[i].setTag(i);

                edEnterQty[i].setTag(i);
                edCashDisc[i].setTag(i);

                edEnterQty[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                edEnterQty[i].setCursorVisible(true);

                edEnterQty[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastSelectedEditText = selvalue;
                        Constants.setCursorPosition(edEnterQty[selvalue]);
                        v.requestFocus();
                        Constants.showCustomKeyboard(v, keyboardViewWithDot, MaterialSelectionActivity.this);
                        Constants.setCursorPostion(edEnterQty[selvalue],v,event);
                        return true;
                    }
                });
                edEnterQty[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            Constants.setCursorPosition(edEnterQty[selvalue]);
                            lastSelectedEditText = selvalue;
                            Constants.showCustomKeyboard(view, keyboardViewWithDot, MaterialSelectionActivity.this);
                            edEnterQty[positionTable].setHint("");
                        } else {
                            lastSelectedEditText = selvalue;
                            Constants.hideCustomKeyboard(keyboardViewWithDot);
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            edEnterQty[positionTable].setHint(getString(R.string.qty));
                        }


                    }
                });
                UtilConstants.editTextDecimalFormat(edCashDisc[i], 3, 2);
                edCashDisc[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastSelectedMrpEditText = selvalue;
                        Constants.setCursorPosition(edCashDisc[selvalue]);
                        v.requestFocus();
                        Constants.showCustomKeyboard(v, keyboardViewWithDot, MaterialSelectionActivity.this);
                        Constants.setCursorPostion(edCashDisc[selvalue],v,event);
                        return true;
                    }
                });
                edCashDisc[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            Constants.setCursorPosition(edCashDisc[selvalue]);
                            lastSelectedMrpEditText = selvalue;
                            Constants.showCustomKeyboard(view, keyboardViewWithDot, MaterialSelectionActivity.this);
                            edCashDisc[positionTable].setHint("");
                        } else {
                            lastSelectedMrpEditText = selvalue;
                            Constants.hideCustomKeyboard(keyboardViewWithDot);
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            edCashDisc[positionTable].setHint(getString(R.string.lbl_cash_disc));
                        }


                    }
                });


                filteredArrayList.get(incrementVal).setBeanPosition(incrementVal);

                if (filteredArrayList.get(incrementVal).getSelectedBatchNo() != null) {
                    int spPosition = spOrderReasonAdapter.getPosition(filteredArrayList.get(incrementVal).getSelctedBatch());
                    spReason[i].setSelection(spPosition);
                }
                if (filteredArrayList.get(incrementVal).getCashDisc() != null)
                    edCashDisc[i].setText(filteredArrayList.get(incrementVal).getCashDisc());
                if (filteredArrayList.get(incrementVal).getEnterdQTY() != null)
                    edEnterQty[i].setText(filteredArrayList.get(incrementVal).getEnterdQTY());
                spReason[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View spView, int i, long l) {
                        int positionTable = Integer.parseInt(adapterView.getTag().toString());
                        BatchBean seleBatch = alBatchNo.get(i);
                        filteredArrayList.get(positionTable).setSelctedBatch(seleBatch);
                        filteredArrayList.get(positionTable).setSelectedBatchNo(alBatchNo.get(i).getBatchNo());
                        spReason[positionTable].setBackgroundResource(R.drawable.spinner_bg);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                edCashDisc[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        View viewFocus = MaterialSelectionActivity.this.getCurrentFocus();

                        int positionTable = Integer.parseInt(viewFocus.getTag().toString());
                        filteredArrayList.get(positionTable).setCashDisc(editable.toString());
                        if (!editable.toString().isEmpty()) {
                            edCashDisc[positionTable].setBackgroundResource(R.drawable.edittext);
                        }
                    }
                });
                edEnterQty[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        View viewFocus = MaterialSelectionActivity.this.getCurrentFocus();

                        int positionTable = Integer.parseInt(viewFocus.getTag().toString());
                        filteredArrayList.get(positionTable).setEnterdQTY(editable.toString());
                        if (!editable.toString().isEmpty()) {
                            edEnterQty[positionTable].setBackgroundResource(R.drawable.edittext);
                        }
                    }
                });
                tvMaterialDesc[i].setText(filteredArrayList.get(i).getMaterialDESC());
                tvStockQTY[i].setText(filteredArrayList.get(i).getStockQTY());
                tvMRP[i].setText(filteredArrayList.get(i).getMRP());

                ibDeleteItem[i] = (ImageButton) rowRelativeLayout.findViewById(R.id.ib_delete_item);
                ibDeleteItem[i].setTag(i);
                ibDeleteItem[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(v.getTag() + "");

                    }
                });

                rowRelativeLayout.setId(i);
//                filteredArrayList.get(i).setDisplayed(true);
                tableHeading.addView(rowRelativeLayout);
            }

//            mBoolFirstTime = true;

            llDelStockLayout.addView(tableHeading);

        } else {
            String mStrEamptyValue = "";
            if(mStrComingFrom.equalsIgnoreCase("SOList")){
                mStrEamptyValue = getString(R.string.no_data_found);
            }else{
                mStrEamptyValue = getString(R.string.add_product_hint);
            }
            View llEmptyLayout = (View) LayoutInflater.from(this)
                    .inflate(R.layout.empty_layout, null);
            TextView tvNoRecord = (TextView)llEmptyLayout.findViewById(R.id.tv_empty_lay);
            tvNoRecord.setText(mStrEamptyValue);
            llDelStockLayout.addView(llEmptyLayout);
        }
    }
    private ProgressDialog pdLoadDialog;
    private class GetRetailerList extends AsyncTask<String, Void, Void> {
        private boolean reloadView = false;

        private GetRetailerList(boolean reloadView) {
            this.reloadView = reloadView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(MaterialSelectionActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String query = "";
            if(matQry!=null && !matQry.equalsIgnoreCase("")){
                query = Constants.CPStockItemSnos + "?$orderby="+ Constants.Material_Desc+" &$filter=StockTypeID eq '1' and " +  matQry+ "  and "+Constants.ExpiryDate+" ge datetime'" + UtilConstants.getNewDate() + "'";

            }else{
                query = Constants.CPStockItems + "?$orderby="+ Constants.Material_Desc+" &$filter=" + Constants.StockOwner + " eq '01' and " + Constants.MaterialNo + " ne '' ";
            }

            getDistributorStock(query,invCreateBean,matQry);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (menuItem != null) {
                if(!mStrComingFrom.equalsIgnoreCase("SOList")){
                menuItem.setVisible(true);
            }else{
                menuItem.setVisible(false);
                    displayStock();
            }
            }

        }
    }
    private MenuItem menuItem = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_return_order_create, menu);
        menuItem = menu.findItem(R.id.menu_return_add);
        if(!mStrComingFrom.equalsIgnoreCase("SOList")){
            menuItem.setVisible(true);
        }else{
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_return_add:
                stockBeanTemp = new StockBean();
                stockBeanTemp.setAlMaterals(filteredTempArrayList);
                Intent intent = new Intent(MaterialSelectionActivity.this, AddInvMaterialsActivity.class);
                intent.putExtra(Constants.StockOwner, "01");
//                intent.putExtra(Constants.EXTRA_ARRAY_LIST, stockBeanTemp);
                startActivityForResult(intent, AddInvMaterialsActivity.RETURN_ORDER_RESULT_ID);
                break;
            case R.id.menu_return_review:
                onReviewScreen();
                break;
        }
        return false;
    }
    ArrayList<StockBean> filteredArrayList = new ArrayList<>();
    public static StockBean stockBeanTemp = new StockBean();
    ArrayList<StockBean> filteredTempArrayList = new ArrayList<>();
    private void getDistributorStock(String querys,InvoiceCreateBean invCreateBean,String matQry) {
        try {
            filteredArrayList.clear();
            filteredTempArrayList.clear();
            filteredArrayList = OfflineManager.getInvStockList(querys, filteredArrayList,invCreateBean,matQry);
            filteredTempArrayList.addAll(filteredArrayList);
            stockBeanTemp.setAlMaterals(filteredTempArrayList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddInvMaterialsActivity.RETURN_ORDER_RESULT_ID) {
            if (data != null) {
               ArrayList<StockBean> returnOrderBeanArrayList = data.getParcelableArrayListExtra(ConstantsUtils.EXTRA_ARRAY_LIST);
               if(!returnOrderBeanArrayList.isEmpty()){
                    if(isFirstTime){
                        isFirstTime = false;
                        filteredArrayList.clear();
                    }
                    for(StockBean stockBean:returnOrderBeanArrayList){
                        filteredArrayList.add(stockBean);
                    }
                    addStockToEntry();
                }
            }
        }
    }

    private void addStockToEntry() {
        displayStock();
    }
    private void deleteItem(String itemId) {
        final int selectedID = Integer.parseInt(itemId);
        final String selectedStockName = filteredArrayList.get(selectedID).getMaterialDESC();
        final String selectedStockID = filteredArrayList.get(selectedID).getMaterialNo();
        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialSelectionActivity.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.do_want_to_delete_return_order, selectedStockName)).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        filteredArrayList.remove(selectedID);
                        displayStock();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }

                });
        builder.show();

    }

    private void onReviewScreen(){
        Constants.selectedStockItems.clear();
        Constants.selectedStockItems.addAll(filteredArrayList);

        boolean isvalidStock = isValidBatch();
        if(isvalidStock){
            Intent intent = new Intent(MaterialSelectionActivity.this, InvoiceReviewActivity.class);
            intent.putExtra(Constants.EXTRA_ARRAY_LIST, invCreateBean);
            intent.putExtra(Constants.comingFrom, mStrComingFrom);
            startActivity(intent);
        }else{
            UtilConstants.showAlert(getString(R.string.alert_select_valid_batch),MaterialSelectionActivity.this);
        }

    }

    private boolean isValidBatch(){
        boolean isValid = true;
        if(filteredArrayList!=null && filteredArrayList.size()>0){
            for(StockBean stockBean:filteredArrayList ){
                for(StockBean tempBean:Constants.selectedStockItems){
                    if(stockBean.getBeanPosition()!=tempBean.getBeanPosition()){
                        if(stockBean.getMaterialNo().equalsIgnoreCase(tempBean.getMaterialNo())){
                            if(stockBean.getSelectedBatchNo().equalsIgnoreCase(tempBean.getSelectedBatchNo())){
                                isValid = false;
                                break;
                            }
                        }

                    }
                }
                if(isValid){
                    break;
                }
            }
        }
        return isValid;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        return super.onKeyLongPress(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //Handle what you want in long press.
            super.onKeyLongPress(keyCode, event);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }


    /*custom keyboard*/
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    Constants.incrementTextValues(edEnterQty[lastSelectedEditText], Constants.N);
                } else {
                    Constants.incrementTextValues(edCashDisc[lastSelectedMrpEditText], Constants.Y);
                }
                break;
            case 69:
                //Minus
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    Constants.decrementEditTextVal(edEnterQty[lastSelectedEditText], Constants.N);
                } else {
                    Constants.decrementEditTextVal(edCashDisc[lastSelectedMrpEditText], Constants.Y);
                }
                break;
            case 1:
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    changeEditTextFocus(0);
                } else {
                    changeMRPEditTextFocus(0);
                }
                break;
            case 2:
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    changeEditTextFocus(1);
                } else {
                    changeMRPEditTextFocus(1);
                }
                break;
            case 56:
                KeyEvent event2 = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event2);
                break;

            default:
                //default numbers
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;
        }


    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public void changeEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = filteredArrayList.size() - 1;
            if (lastSelectedEditText != ListSize) {
                if (edEnterQty[lastSelectedEditText] != null)
                    edEnterQty[lastSelectedEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedEditText != 0) {
                if (edEnterQty[lastSelectedEditText - 1] != null)
                    edEnterQty[lastSelectedEditText - 1].requestFocus();
            }

        }

    }

    public void changeMRPEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = filteredArrayList.size() - 1;
            if (lastSelectedMrpEditText != ListSize) {
                if (edCashDisc[lastSelectedMrpEditText] != null)
                    edCashDisc[lastSelectedMrpEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedMrpEditText != 0) {
                if (edCashDisc[lastSelectedMrpEditText - 1] != null)
                    edCashDisc[lastSelectedMrpEditText - 1].requestFocus();
            }

        }

    }
}
