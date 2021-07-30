package com.arteriatech.emami.expense;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.msecsales.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class AddExpenseActivity extends AppCompatActivity implements OnClickInterface {

    public static final String EXTRA_EXPENSE_TYPE_ID = "expenseTypeId";
    public static final String EXTRA_EXPENSE_IS_ADD_NEW = "expenseAddNew";
    public static final String EXTRA_EXPENSE_BEAN = "expenseBean";
    AddExpenseImageAdapter addExpenseImageAdapter;
    private RecyclerView recyclerView;
    private ArrayList<ExpenseImageBean> imageBeanList = new ArrayList<>();
    private ArrayList<ExpenseImageBean> finalImageBeanList = new ArrayList<>();
    private EditText edExpenseItemRemarks;
    private TextView edExpenseItemAmount;
    private LinearLayout llAttachment;
    private ExpenseBean extraExpenseBean = null;
    private TextView tvExpenseType;
    private TextView tvExpenseBeat;
    private TextView tvExpenseLocation;
    private TextView tvExpenseMode;
    private TextView tvExpenseDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_expense_attachment_entry));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (!Constants.restartApp(AddExpenseActivity.this)) {
            Bundle extra = getIntent().getExtras();
            if (extra != null) {
                extraExpenseBean = (ExpenseBean) extra.getSerializable(AddExpenseActivity.EXTRA_EXPENSE_BEAN);
            }
            initUI();
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            addExpenseImageAdapter = new AddExpenseImageAdapter(AddExpenseActivity.this, imageBeanList);
            addExpenseImageAdapter.onImageAddClick(this);
            recyclerView.setAdapter(addExpenseImageAdapter);
            refreshRecyclerView("", "", "", "", 0);
        }
    }
    /*UI initialization */
    private void initUI() {
        tvExpenseType = (TextView) findViewById(R.id.tv_expense_item_type);
        tvExpenseBeat = (TextView) findViewById(R.id.tv_expense_item_beat);
        tvExpenseLocation = (TextView) findViewById(R.id.tv_expense_item_location);
        tvExpenseMode = (TextView) findViewById(R.id.tv_expense_item_mode);
        tvExpenseDistance = (TextView) findViewById(R.id.tv_expense_item_distance);
        edExpenseItemAmount = (TextView) findViewById(R.id.tv_expense_item_amount);
        edExpenseItemRemarks = (EditText) findViewById(R.id.ed_expense_item_remarks);
        llAttachment = (LinearLayout) findViewById(R.id.ll_attachment);
        edExpenseItemRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                extraExpenseBean.setRemarks(s.toString());
                edExpenseItemRemarks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (extraExpenseBean != null) {
            tvExpenseType.setText(extraExpenseBean.getExpenseItemTypeDesc());
            tvExpenseBeat.setText(extraExpenseBean.getBeatDesc());
            tvExpenseLocation.setText(extraExpenseBean.getLocationDesc());
            tvExpenseMode.setText(extraExpenseBean.getConvenyanceModeDs());
            tvExpenseDistance.setText(extraExpenseBean.getBeatDistance());
            edExpenseItemAmount.setText(extraExpenseBean.getAmount());
            edExpenseItemRemarks.setText(extraExpenseBean.getRemarks());

            if (extraExpenseBean.getIsSupportDocReq().equalsIgnoreCase("")) {
                llAttachment.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            } else {
                llAttachment.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            if (extraExpenseBean.getExpenseImageBeanArrayList() != null)
                imageBeanList.addAll(extraExpenseBean.getExpenseImageBeanArrayList());
        }

    }
    /*send result*/
    private void onAdd() {
        if (itemValidate()) {
            if (extraExpenseBean != null) {
                if (!finalImageBeanList.isEmpty())
                    extraExpenseBean.setExpenseImageBeanArrayList(finalImageBeanList);
                Intent intent = new Intent(this, ExpenseEntryActivity.class);
                intent.putExtra(EXTRA_EXPENSE_BEAN, extraExpenseBean);
                intent.putExtra(EXTRA_EXPENSE_IS_ADD_NEW, false);
                setResult(ExpenseDailyFragment.addExpenseReqCode, intent);
                finish();
            } else {
                finish();
            }
        }
    }
    /*item validation*/
    private boolean itemValidate() {
        boolean validationStatus = true;
        finalImageBeanList.clear();
        String remarks = edExpenseItemRemarks.getText().toString();
        if (!extraExpenseBean.getIsRemarksReq().equalsIgnoreCase("")) {
            if (remarks.equalsIgnoreCase("")) {
                validationStatus = false;
                edExpenseItemRemarks.setBackgroundResource(R.drawable.edittext_border);
            }
        }
        if (!extraExpenseBean.getIsSupportDocReq().equalsIgnoreCase("")) {
            if (imageBeanList.size() < 2) {
                validationStatus = false;
                Constants.dialogBoxWithButton(this, "", getString(R.string.validation_plz_enter_mandatory_flds), getString(R.string.ok), "", null);
            } else {
                for (ExpenseImageBean expenseImageBean : imageBeanList) {
                    if (!expenseImageBean.getImagePath().equals(""))
                        finalImageBeanList.add(expenseImageBean);
                }
            }
        }
        return validationStatus;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.TAKE_PICTURE && resultCode == RESULT_OK && data != null) {
            Bundle bundleExtrasResult = data.getExtras();
            final Bitmap bitMap = (Bitmap) bundleExtrasResult.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            assert bitMap != null;
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            int mLongBitmapSize = imageInByte.length;

            String filename = (System.currentTimeMillis() + "");
            File fileName = Constants.SaveImageInDevice(filename, bitMap);
            String strMimeType = MimeTypeMap.getFileExtensionFromUrl(fileName.getPath());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    strMimeType);


            refreshRecyclerView(fileName.getPath(), filename, strMimeType, mimeType, mLongBitmapSize);
        }

    }

    @Override
    public void onBackPressed() {
        onAdd();
    }
    /*Refresh recyclerview and add image into list*/
    private void refreshRecyclerView(String path, String filename, String strMimeType, String mimeType, int mLongBitmapSize) {
        int totalSize = imageBeanList.size();
        if (checkEmptyImage(totalSize)) {
            ExpenseImageBean expenseImageBean = new ExpenseImageBean();
            expenseImageBean.setImagePath(path);
            expenseImageBean.setDocumentMimeType(mimeType);
            expenseImageBean.setDocumentSize(mLongBitmapSize + "");
            expenseImageBean.setImageExtensions(strMimeType + "");
            expenseImageBean.setFileName(filename + "");
            imageBeanList.add(totalSize - 1, expenseImageBean);
            addExpenseImageAdapter.notifyItemInserted(totalSize - 1);
        } else {
            imageBeanList.add(totalSize, getEmptyImage());
            if (!path.isEmpty()) {
                totalSize = imageBeanList.size();
                ExpenseImageBean expenseImageBeans = new ExpenseImageBean();
                expenseImageBeans.setImagePath(path);
                expenseImageBeans.setDocumentMimeType(mimeType);
                expenseImageBeans.setDocumentSize(mLongBitmapSize + "");
                expenseImageBeans.setImageExtensions(strMimeType + "");
                expenseImageBeans.setFileName(filename + "");
                imageBeanList.add(totalSize - 1, expenseImageBeans);
            }
            addExpenseImageAdapter.notifyDataSetChanged();
        }

    }
    /*set empty image path*/
    private ExpenseImageBean getEmptyImage() {
        ExpenseImageBean expenseImageBean = new ExpenseImageBean();
        expenseImageBean.setImagePath("");
        return expenseImageBean;
    }
    /*check image is present or not*/
    private boolean checkEmptyImage(int size) {
        boolean emptyImageNotFound = false;
        if (size > 0) {
            ExpenseImageBean expenseImageBean = imageBeanList.get(size - 1);
            if (expenseImageBean.getImagePath().equalsIgnoreCase("")) {
                emptyImageNotFound = true;
            }
        }
        return emptyImageNotFound;
    }

    @Override
    public void onItemClick(View view, int position) {
        Constants.openCameraWindow(AddExpenseActivity.this);
    }
}
