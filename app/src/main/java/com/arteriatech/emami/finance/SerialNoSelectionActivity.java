package com.arteriatech.emami.finance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.xscript.core.GUID;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressLint("NewApi")
public class SerialNoSelectionActivity extends AppCompatActivity {
    private String mStrSPStockItemGUID = "", mStrMatNo = "", mStrInvoiceQty = "", mStrMatDesc = "", mStrUnitPrice = "";
    private ArrayList<InvoiceBean> selectedInvoiceCheckBoxOption = null;
    private ArrayList<InvoiceBean> selectedInvoiceFromTo = null, alDeleteSnoList = null;

    private ArrayList<InvoiceBean> algetInvoiceList = null;

    private ArrayList<InvoiceBean> alSingleSerialNoList = null;

    private ArrayList<InvoiceBean> alGetSnosFromHashTable = null;

    private int passedValues;
    private String mStrQty = "", mStrFromRangeQty = "", mStrToRangeQty = "";
    private ArrayList<InvoiceBean> alTempAvalibleSnoList = null;
    private ArrayList<InvoiceBean> alAvalibleSnoList = null;
    private ArrayList<InvoiceBean> alAllocatedSnoList = null;
    private ArrayList<InvoiceBean> alTempAllocatedSnoList = null;
    private ArrayList<InvoiceBean> alTestAllocatedSnoList = null;
    private boolean mBooleanSnoAvalible = false;
    private InvoiceBean tempInvoiceBean;
    private double mDoubleTotalAllocatedQty = 0.0, mDoubleValAllocatedQty = 0.0;
    TextView tv_allocated_qty;
    private boolean mBooleanIsDataAvalible = false, mBooleanFullQtyDeletion = false;
    private InvoiceBean testAllocatedSnosBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_invoiceCreate));

        setContentView(R.layout.activity_serial_no_selection);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(SerialNoSelectionActivity.this)) {
            Bundle bundleExtras = getIntent().getExtras();
            if (bundleExtras != null) {
                mStrSPStockItemGUID = bundleExtras.getString(Constants.SPStockItemGUID) != null ? bundleExtras.getString(Constants.SPStockItemGUID) : "";
                mStrInvoiceQty = bundleExtras.getString(Constants.InvoiceQty).equalsIgnoreCase("") ? "0" : bundleExtras.getString(Constants.InvoiceQty);
                mStrUnitPrice = bundleExtras.getString(Constants.UnitPrice).equalsIgnoreCase("") ? "0" : bundleExtras.getString(Constants.UnitPrice);
                mStrMatNo = bundleExtras.getString(Constants.MatCode);
                mStrMatDesc = bundleExtras.getString(Constants.MatDesc);
                passedValues = bundleExtras.getInt(Constants.PassedFrom);
            }

            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {
        TextView tv_serial_mat_desc = (TextView) findViewById(R.id.tv_serial_mat_desc);
        TextView tv_serial_mat_code = (TextView) findViewById(R.id.tv_serial_mat_code);
        TextView tv_serial_unit_price = (TextView) findViewById(R.id.tv_serial_unit_price);
        TextView tv_serial_inv_qty = (TextView) findViewById(R.id.tv_serial_inv_qty);
        TextView tv_serial_total_price = (TextView) findViewById(R.id.tv_serial_total_price);
        tv_allocated_qty = (TextView) findViewById(R.id.tv_allocated_qty);

        if (!Constants.HashTableSerialNoSelection.isEmpty()) {
            if (Constants.HashTableSerialNoSelection.containsKey(mStrSPStockItemGUID)) {
                alGetSnosFromHashTable = Constants.HashTableSerialNoSelection.get(mStrSPStockItemGUID);
                if (alGetSnosFromHashTable != null && alGetSnosFromHashTable.size() > 0) {
                    mBooleanIsDataAvalible = true;
                }
            }
        }

        tv_serial_mat_desc.setText(mStrMatDesc);
        tv_serial_mat_code.setText(UtilConstants.removeLeadingZeros(mStrMatNo));
        tv_serial_inv_qty.setText(mStrInvoiceQty);
        tv_allocated_qty.setText(UtilConstants.removeLeadingZero(mDoubleTotalAllocatedQty) + "");

        double mDouNetAmount = 0.0;

        try {
            mDouNetAmount = Double.parseDouble(mStrUnitPrice) * Double.parseDouble(mStrInvoiceQty);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        tv_serial_unit_price.setText(getString(R.string.str_rupee_symbol) + " " + Constants.removeLeadingZero(mStrUnitPrice));
        tv_serial_total_price.setText(getString(R.string.str_rupee_symbol) + " " + Constants.removeLeadingZero(mDouNetAmount + ""));


        selectedInvoiceCheckBoxOption = new ArrayList<InvoiceBean>();

        selectedInvoiceFromTo = new ArrayList<InvoiceBean>();

        algetInvoiceList = new ArrayList<InvoiceBean>();


        try {
            alAvalibleSnoList = OfflineManager.getSPStockInvoiceSerialNo(Constants.SPStockItemSNos
                    + "?$filter=" + Constants.SPStockItemGUID + " eq guid'" + mStrSPStockItemGUID.toUpperCase() + "' and  "
                    + Constants.Option + " eq 'BT' and " + Constants.InvoiceStatus + " ne '01' " +
                    "and " + Constants.StockTypeID + " eq '1'", "BT", mStrSPStockItemGUID);
//            or "+ Constants.StockTypeID + " eq '01')"
            alTempAvalibleSnoList = alAvalibleSnoList;
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }


        if (alGetSnosFromHashTable != null && alGetSnosFromHashTable.size() > 0) {
            alAvalibleSnoList = new ArrayList<>();
            alAllocatedSnoList = new ArrayList<>();
            for (int k = 0; k < alGetSnosFromHashTable.size(); k++) {
                InvoiceBean invoiceBean = alGetSnosFromHashTable.get(k);
                if (invoiceBean.getStatus().equalsIgnoreCase("02")) { //-->Available stock serial numbers status
                    alAvalibleSnoList.add(invoiceBean);
                } else if (invoiceBean.getStatus().equalsIgnoreCase("03") && mBooleanIsDataAvalible) {  //-->Allocated stock serial numbers status
                    alAllocatedSnoList.add(invoiceBean);
                } else if (invoiceBean.getStatus().equalsIgnoreCase("01")) { //-->Delete stock serial numbers status
                    // --->here not assigned to list
                } else if (invoiceBean.getStatus().equalsIgnoreCase("04")) {
                    alAvalibleSnoList.add(invoiceBean);
                }
            }
        }

        displayAvalibleSerialNos();
        displayAllocatedSerialNos();
    }

    /*Displays dialog box to remove selection*/
    private void enableDialogBoxForRemoveSelCriteria() {
        mBooleanSnoAvalible = false;
        alTempAllocatedSnoList = new ArrayList<InvoiceBean>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.alert_do_u_want_remove_sno)
                .setCancelable(true)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (alAllocatedSnoList != null) {
                                    if (alAllocatedSnoList.contains(tempInvoiceBean)) {
                                        alAllocatedSnoList.remove(tempInvoiceBean);
                                        if (alAvalibleSnoList != null) {

                                            //---->New remove code. start
                                            mBooleanFullQtyDeletion = false;
                                            if (alAvalibleSnoList != null && alAvalibleSnoList.size() > 0) {
                                                for (int k = 0; k < alAvalibleSnoList.size(); k++) {
                                                    InvoiceBean avalibleInvBean = alAvalibleSnoList.get(k);
                                                    if (tempInvoiceBean.getOldSPSNoGUID().equalsIgnoreCase(avalibleInvBean.getOldSPSNoGUID())) {
                                                        mBooleanFullQtyDeletion = true;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (!mBooleanFullQtyDeletion) {
                                                alAvalibleSnoList.add(tempInvoiceBean);
                                            }

                                            if (alAvalibleSnoList != null && alAvalibleSnoList.size() > 0) {
                                                for (int k = 0; k < alAvalibleSnoList.size(); k++) {

                                                    InvoiceBean avalibleInvBean = alAvalibleSnoList.get(k);
                                                    if (tempInvoiceBean.getOldSPSNoGUID().equalsIgnoreCase(avalibleInvBean.getOldSPSNoGUID())) {

                                                        int prefixLenTemp = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

                                                        int prefixLen = (int) Double.parseDouble(avalibleInvBean.getPrefixLength());

                                                        BigInteger AvalToQty = new BigInteger(UtilConstants.removeAlphanumericText(avalibleInvBean.getSerialNoTo(), prefixLen));

                                                        BigInteger AvalFromQty = new BigInteger(UtilConstants.removeAlphanumericText(avalibleInvBean.getSerialNoFrom(), prefixLen));

                                                        BigInteger AllocatedToQty = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoTo(), prefixLenTemp));

                                                        BigInteger AllocatedFromQty = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoFrom(), prefixLenTemp));


                                                        int firstComparison = AllocatedFromQty.compareTo(AvalFromQty);
                                                        int secondComprison = AllocatedFromQty.compareTo(AvalToQty);

                                                        if (avalibleInvBean.getSerialNoFrom().equalsIgnoreCase(tempInvoiceBean.getSerialNoFrom()) &&
                                                                avalibleInvBean.getSerialNoTo().equalsIgnoreCase(tempInvoiceBean.getSerialNoTo())) {
                                                        } else if (firstComparison == -1) {  // AllocatedFromQty < AvalFromQty
                                                            int compareTo = AllocatedToQty.compareTo(AvalFromQty.subtract(new BigInteger("1")));
                                                            int secondCompareTo = AllocatedToQty.compareTo(AvalFromQty);
                                                            if (compareTo == 0) {
                                                                InvoiceBean avaInvBean = new InvoiceBean();
                                                                avaInvBean.setSPSNoGUID(avalibleInvBean.getSPSNoGUID());
                                                                avaInvBean.setCPStockItemGUID(avalibleInvBean.getCPStockItemGUID());
                                                                avaInvBean.setSerialNoTo(avalibleInvBean.getSerialNoTo());
                                                                avaInvBean.setSerialNoFrom(tempInvoiceBean.getSerialNoFrom());
                                                                avaInvBean.setOldSPSNoGUID(avalibleInvBean.getOldSPSNoGUID());
                                                                avaInvBean.setSelectedSerialNoFrom(avalibleInvBean.getSelectedSerialNoFrom());
                                                                avaInvBean.setSelectedSerialNoTo(avalibleInvBean.getSelectedSerialNoTo());
                                                                avaInvBean.setPrefixLength(avalibleInvBean.getPrefixLength());
                                                                avaInvBean.setOption(avalibleInvBean.getOption());
                                                                avaInvBean.setEtag(avalibleInvBean.getEtag());
                                                                avaInvBean.setTempSpSnoGuid(avalibleInvBean.getTempSpSnoGuid());
                                                                avaInvBean.setSequence(avalibleInvBean.getSequence());
                                                                avaInvBean.setUom(avalibleInvBean.getUom());
                                                                avaInvBean.setStockTypeID(avalibleInvBean.getStockTypeID());
                                                                alTempAllocatedSnoList.add(avaInvBean);
                                                                alAvalibleSnoList.remove(k);
                                                                break;
                                                            } else if (secondCompareTo == -1) {
                                                                alTempAllocatedSnoList.add(tempInvoiceBean);
                                                                break;
                                                            }

                                                        } else if (secondComprison == 1) {// AllocatedFromQty > AvalToQty
                                                            int firstCompareTo = AllocatedFromQty.compareTo(AvalToQty.add(new BigInteger("1")));
                                                            int secondCompareTo = AllocatedFromQty.compareTo(AvalToQty);

                                                            if (firstCompareTo == 0) {
                                                                InvoiceBean avaInvBean = new InvoiceBean();
                                                                avaInvBean.setSPSNoGUID(avalibleInvBean.getSPSNoGUID());
                                                                avaInvBean.setCPStockItemGUID(avalibleInvBean.getCPStockItemGUID());
                                                                avaInvBean.setSerialNoTo(tempInvoiceBean.getSerialNoTo());
                                                                avaInvBean.setSerialNoFrom(avalibleInvBean.getSerialNoFrom());
                                                                avaInvBean.setOldSPSNoGUID(avalibleInvBean.getOldSPSNoGUID());
                                                                avaInvBean.setSelectedSerialNoFrom(avalibleInvBean.getSelectedSerialNoFrom());
                                                                avaInvBean.setSelectedSerialNoTo(avalibleInvBean.getSelectedSerialNoTo());
                                                                avaInvBean.setPrefixLength(avalibleInvBean.getPrefixLength());
                                                                avaInvBean.setOption(avalibleInvBean.getOption());
                                                                avaInvBean.setEtag(avalibleInvBean.getEtag());
                                                                avaInvBean.setTempSpSnoGuid(avalibleInvBean.getTempSpSnoGuid());
                                                                avaInvBean.setSequence(avalibleInvBean.getSequence());
                                                                avaInvBean.setUom(avalibleInvBean.getUom());
                                                                avaInvBean.setStockTypeID(avalibleInvBean.getStockTypeID());
                                                                alTempAllocatedSnoList.add(avaInvBean);
                                                                alAvalibleSnoList.remove(k);
                                                                break;
                                                            } else if (secondCompareTo == 1) {
                                                                alTempAllocatedSnoList.add(tempInvoiceBean);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }


                                            if (alAvalibleSnoList.size() > 0) {
                                                alAvalibleSnoList.addAll(alAvalibleSnoList.size(), alTempAllocatedSnoList);
                                            } else {
                                                alAvalibleSnoList.addAll(0, alTempAllocatedSnoList);
                                            }

                                            Collections.sort(alAvalibleSnoList, new Comparator<InvoiceBean>() {
                                                @Override
                                                public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                                                    return firstBean.getSerialNoFrom().compareTo(seondBean.getSerialNoFrom());
                                                }
                                            });

                                            Collections.sort(alAvalibleSnoList, new Comparator<InvoiceBean>() {
                                                @Override
                                                public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                                                    return firstBean.getSequence().compareTo(seondBean.getSequence());
                                                }
                                            });

                                            alTempAllocatedSnoList.clear();
                                            alTempAvalibleSnoList = new ArrayList<InvoiceBean>();

                                            if (alAvalibleSnoList.size() > 0) {
                                                alTempAvalibleSnoList.addAll(0, alAvalibleSnoList);
                                            }

                                            if (alTempAvalibleSnoList != null && alTempAvalibleSnoList.size() > 0) {
                                                for (int k = 0; k < alTempAvalibleSnoList.size() - 1; k++) {
                                                    InvoiceBean firstBean = alTempAvalibleSnoList.get(k);
                                                    InvoiceBean secondBean = alTempAvalibleSnoList.get(k + 1);

                                                    if (!firstBean.getTempSpSnoGuid().equalsIgnoreCase("") && !secondBean.getTempSpSnoGuid().equalsIgnoreCase("")) {
                                                        if (firstBean.getOldSPSNoGUID().equalsIgnoreCase(secondBean.getOldSPSNoGUID())
                                                                ) {
                                                            int prefixLen = (int) Double.parseDouble(firstBean.getPrefixLength());
                                                            BigInteger AvalToQty = new BigInteger(UtilConstants.removeAlphanumericText(firstBean.getSerialNoTo(), prefixLen));
                                                            BigInteger AvalFromQty = new BigInteger(UtilConstants.removeAlphanumericText(secondBean.getSerialNoFrom(), prefixLen));
                                                            int compareTo = AvalToQty.compareTo(AvalFromQty.subtract(new BigInteger("1")));

                                                            if (compareTo == 0) {
                                                                InvoiceBean avaInvBean = new InvoiceBean();
                                                                avaInvBean.setSPSNoGUID(firstBean.getSPSNoGUID());
                                                                avaInvBean.setCPStockItemGUID(firstBean.getCPStockItemGUID());
                                                                avaInvBean.setSerialNoTo(secondBean.getSerialNoTo());
                                                                avaInvBean.setSerialNoFrom(firstBean.getSerialNoFrom());
                                                                avaInvBean.setOldSPSNoGUID(firstBean.getOldSPSNoGUID());
                                                                avaInvBean.setSelectedSerialNoFrom(firstBean.getSelectedSerialNoFrom());
                                                                avaInvBean.setSelectedSerialNoTo(firstBean.getSelectedSerialNoTo());
                                                                avaInvBean.setPrefixLength(firstBean.getPrefixLength());
                                                                avaInvBean.setOption(firstBean.getOption());
                                                                avaInvBean.setEtag(firstBean.getEtag());
                                                                avaInvBean.setTempSpSnoGuid(firstBean.getTempSpSnoGuid());
                                                                avaInvBean.setSequence(firstBean.getSequence());
                                                                avaInvBean.setUom(firstBean.getUom());
                                                                avaInvBean.setStockTypeID(firstBean.getStockTypeID());
                                                                alTempAllocatedSnoList.add(avaInvBean);
                                                                alAvalibleSnoList.remove(firstBean);
                                                                alAvalibleSnoList.remove(secondBean);
                                                            }

                                                        }
                                                    }

                                                }
                                            }
                                            alTestAllocatedSnoList = new ArrayList<InvoiceBean>();
                                            if (alTempAllocatedSnoList.size() > 1) {
                                                for (int k = 0; k < alTempAllocatedSnoList.size() - 1; k++) {
                                                    InvoiceBean firstBean = alTempAllocatedSnoList.get(k);
                                                    InvoiceBean secondBean = alTempAllocatedSnoList.get(k + 1);
                                                    if (!firstBean.getTempSpSnoGuid().equalsIgnoreCase("") && !secondBean.getTempSpSnoGuid().equalsIgnoreCase("")) {
                                                        if (firstBean.getOldSPSNoGUID().equalsIgnoreCase(secondBean.getOldSPSNoGUID())
                                                                ) {
                                                            int prefixLen = (int) Double.parseDouble(firstBean.getPrefixLength());
                                                            BigInteger AvalToQty = new BigInteger(UtilConstants.removeAlphanumericText(firstBean.getSerialNoTo(), prefixLen));
                                                            BigInteger AvalFromQty = new BigInteger(UtilConstants.removeAlphanumericText(secondBean.getSerialNoFrom(), prefixLen));
                                                            int compareTo = AvalFromQty.compareTo(AvalToQty.subtract(new BigInteger("1")));
                                                            if (compareTo == 0) {
                                                                InvoiceBean avaInvBean = new InvoiceBean();
                                                                avaInvBean.setSPSNoGUID(firstBean.getSPSNoGUID());
                                                                avaInvBean.setCPStockItemGUID(firstBean.getCPStockItemGUID());
                                                                avaInvBean.setSerialNoTo(secondBean.getSerialNoTo());
                                                                avaInvBean.setSerialNoFrom(firstBean.getSerialNoFrom());
                                                                avaInvBean.setOldSPSNoGUID(firstBean.getOldSPSNoGUID());
                                                                avaInvBean.setSelectedSerialNoFrom(firstBean.getSelectedSerialNoFrom());
                                                                avaInvBean.setSelectedSerialNoTo(firstBean.getSelectedSerialNoTo());
                                                                avaInvBean.setPrefixLength(firstBean.getPrefixLength());
                                                                avaInvBean.setOption(firstBean.getOption());
                                                                avaInvBean.setEtag(firstBean.getEtag());
                                                                avaInvBean.setTempSpSnoGuid(firstBean.getTempSpSnoGuid());
                                                                avaInvBean.setSequence(firstBean.getSequence());
                                                                avaInvBean.setUom(firstBean.getUom());
                                                                avaInvBean.setStockTypeID(firstBean.getStockTypeID());
                                                                alTestAllocatedSnoList.add(avaInvBean);
                                                                alTempAllocatedSnoList.remove(firstBean);
                                                                alTempAllocatedSnoList.remove(secondBean);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }

                                                if (alAvalibleSnoList.size() > 0) {
                                                    alAvalibleSnoList.addAll(alAvalibleSnoList.size(), alTestAllocatedSnoList);
                                                } else {
                                                    alAvalibleSnoList.addAll(0, alTestAllocatedSnoList);
                                                }

                                            } else {

                                                if (alAvalibleSnoList.size() > 0) {
                                                    alAvalibleSnoList.addAll(alAvalibleSnoList.size(), alTempAllocatedSnoList);
                                                } else {
                                                    alAvalibleSnoList.addAll(0, alTempAllocatedSnoList);
                                                }
                                            }

                                            Collections.sort(alAvalibleSnoList, new Comparator<InvoiceBean>() {
                                                @Override
                                                public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                                                    return firstBean.getSerialNoFrom().compareTo(seondBean.getSerialNoFrom());
                                                }
                                            });

                                            Collections.sort(alAvalibleSnoList, new Comparator<InvoiceBean>() {
                                                @Override
                                                public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                                                    return firstBean.getSequence().compareTo(seondBean.getSequence());
                                                }
                                            });

                                            alTempAllocatedSnoList.clear();

                                        }
                                    }
                                }

                                displayAvalibleSerialNos();
                                displayAllocatedSerialNos();
                            }
                        }

                ).

                setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }

                );
        builder.show();
    }


    /*Displays Dialog box for select fully or partially*/
    private void enableDialogBoxForSelCriteria(final InvoiceBean invoiceBean) {
        mBooleanSnoAvalible = false;
        mDoubleValAllocatedQty = 0.0;
        alTempAllocatedSnoList = new ArrayList<InvoiceBean>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.do_u_want_select_full_or_partial)
                .setCancelable(true)
                .setPositiveButton(R.string.btn_full,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                int prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());
                                BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoTo(), prefixLen));

                                BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoFrom(), prefixLen));

                                BigInteger mDoubleAvalibleQty = doubAvalTo.subtract(doubAvalFrom).add(new BigInteger("1"))/*doubAvalTo - doubAvalFrom + 1 */;

                                String mDoublePenQty = (Double.parseDouble(mStrInvoiceQty) - mDoubleTotalAllocatedQty + "");

                                BigInteger mDoubleEnterQty = new
                                        BigInteger(UtilConstants.removeLeadingZeroVal(mDoublePenQty));


                                mDoubleValAllocatedQty = mDoubleTotalAllocatedQty + mDoubleEnterQty.doubleValue();


                                if (mDoubleValAllocatedQty <= Double.parseDouble(mStrInvoiceQty) && mDoubleEnterQty.doubleValue() > 0) {
                                    int res;

                                    // compare bi1 with bi2
                                    res = mDoubleAvalibleQty.compareTo(mDoubleEnterQty);


                                    if (res == 1 || res == 0) {

                                        GUID guidSPSno = GUID.newRandom();

                                        alAvalibleSnoList.remove(tempInvoiceBean);

                                        String[] splitString;
                                        String mStrPrefixStr = "";
                                        int numberLengthWithLeadingZeroes;

                                        prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

                                        if (prefixLen > 0) {
                                            mStrPrefixStr = tempInvoiceBean.getSerialNoFrom().substring(0, prefixLen);
                                            numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().substring(prefixLen, tempInvoiceBean.getSerialNoFrom().length()).length();
                                            System.out.println("  " + tempInvoiceBean.getSerialNoFrom().substring(prefixLen, tempInvoiceBean.getSerialNoFrom().length()));
                                        } else {
                                            numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().length();
                                        }


                                        InvoiceBean invBean = new InvoiceBean();
                                        invBean.setSerialNoFrom(tempInvoiceBean.getSerialNoFrom());
                                        invBean.setSPSNoGUID(guidSPSno.toString36());
                                        invBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());


                                        if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                            invBean.setSerialNoTo(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty).subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                        } else {
                                            invBean.setSerialNoTo(UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty).subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                        }


                                        invBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                        invBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                        invBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                        invBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                        invBean.setOption(tempInvoiceBean.getOption());
                                        invBean.setEtag(tempInvoiceBean.getEtag());
                                        invBean.setTempSpSnoGuid(guidSPSno.toString36());
                                        invBean.setSequence(tempInvoiceBean.getSequence());
                                        invBean.setUom(tempInvoiceBean.getUom());
                                        invBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                        testAllocatedSnosBean = invBean;
                                        alTempAllocatedSnoList.add(invBean);

                                        InvoiceBean avaInvBean = new InvoiceBean();
                                        avaInvBean.setTempSpSnoGuid(guidSPSno.toString36());
                                        guidSPSno = GUID.newRandom();
                                        avaInvBean.setSPSNoGUID(guidSPSno.toString36());
                                        avaInvBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                        avaInvBean.setSerialNoTo(tempInvoiceBean.getSerialNoTo());

                                        if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                            avaInvBean.setSerialNoFrom(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty), numberLengthWithLeadingZeroes));
                                        } else {
                                            avaInvBean.setSerialNoFrom(UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty), numberLengthWithLeadingZeroes));
                                        }

                                        avaInvBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                        avaInvBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                        avaInvBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                        avaInvBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                        avaInvBean.setOption(tempInvoiceBean.getOption());
                                        avaInvBean.setEtag(tempInvoiceBean.getEtag());
                                        avaInvBean.setSequence(tempInvoiceBean.getSequence());
                                        avaInvBean.setUom(tempInvoiceBean.getUom());
                                        avaInvBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                        if (res == 1) {
                                            alAvalibleSnoList.add(avaInvBean);
                                        }

                                        displayAvalibleSerialNos();
                                        displayAllocatedSerialNos();
                                    } else {
                                        GUID guidSPSno = GUID.newRandom();

                                        alAvalibleSnoList.remove(tempInvoiceBean);

                                        String mStrPrefixStr = "";
                                        int numberLengthWithLeadingZeroes;

                                        prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

                                        if (prefixLen > 0) {
                                            mStrPrefixStr = tempInvoiceBean.getSerialNoFrom().substring(0, prefixLen);
                                            numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().substring(prefixLen, tempInvoiceBean.getSerialNoFrom().length()).length();
                                        } else {
                                            numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().length();
                                        }


                                        InvoiceBean invBean = new InvoiceBean();
                                        invBean.setSerialNoFrom(tempInvoiceBean.getSerialNoFrom());
                                        invBean.setSPSNoGUID(guidSPSno.toString36());
                                        invBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());


                                        if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                            invBean.setSerialNoTo(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleAvalibleQty).subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                        } else {
                                            invBean.setSerialNoTo(UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleAvalibleQty).subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                        }


                                        invBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                        invBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                        invBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                        invBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                        invBean.setOption(tempInvoiceBean.getOption());
                                        invBean.setEtag(tempInvoiceBean.getEtag());
                                        invBean.setTempSpSnoGuid(guidSPSno.toString36());
                                        invBean.setSequence(tempInvoiceBean.getSequence());
                                        invBean.setUom(tempInvoiceBean.getUom());
                                        invBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                        testAllocatedSnosBean = invBean;
                                        alTempAllocatedSnoList.add(invBean);

                                        displayAvalibleSerialNos();
                                        displayAllocatedSerialNos();
                                    }
                                } else {
                                    displayError(getString(R.string.alert_allocated_qty_less_than_invoice_qty));
                                }
                            }
                        }

                ).setNegativeButton(R.string.btn_partial,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        enableDialogBoxForEnterPartialQty();
                    }
                }

        );
        builder.show();
    }


    /*Displays dialog box for partially selection of serial numbers*/
    private void enableDialogBoxForEnterPartialQty() {
        mBooleanSnoAvalible = false;
        alTempAllocatedSnoList = new ArrayList<>();

//        firstBean
        int prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

        BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoTo(), prefixLen));

        BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoFrom(), prefixLen));
        if (alAllocatedSnoList != null) {
            for (int k = 0; k < alAllocatedSnoList.size(); k++) {
                InvoiceBean invBean = alAllocatedSnoList.get(k);

                BigInteger doubAllocatedFrom = new BigInteger(UtilConstants.removeAlphanumericText(alAllocatedSnoList.get(k).getSerialNoFrom(), prefixLen));
                if (invBean.getTempSpSnoGuid().equalsIgnoreCase(tempInvoiceBean.getTempSpSnoGuid())) {

                    int firstComprison = doubAvalFrom.compareTo(doubAllocatedFrom);
                    int secondComprison = doubAvalTo.compareTo(doubAllocatedFrom);

                    if ((firstComprison == 0 || firstComprison == -1) && (secondComprison == 0 || secondComprison == 1)) {
                        mBooleanSnoAvalible = true;
                        break;
                    } else if (firstComprison == 0 && firstComprison == 0) {
                        mBooleanSnoAvalible = true;
                        break;
                    }
                }
            }

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.do_u_want_enter_qty_or_range)
                .setCancelable(true)
                .setPositiveButton(R.string.quantity,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (!mBooleanSnoAvalible) {
                                    enterQtyDialogBox();
                                } else {
                                    displayError(getString(R.string.You_have_already_entered_quantity));
                                }
                            }
                        })
                .setNegativeButton(R.string.range,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (!mBooleanSnoAvalible) {
                                    enterRangeDialogBox();
                                } else {
                                    displayError(getString(R.string.You_have_already_entered_quantity));
                                }

                            }
                        });
        builder.show();
    }

    /*Displays dialog box for entering qty*/
    private void enterQtyDialogBox() {
        mDoubleValAllocatedQty = 0.0;
        AlertDialog.Builder alertDialogEnterAttendRemarks = new AlertDialog.Builder(SerialNoSelectionActivity.this, R.style.MyTheme);
        alertDialogEnterAttendRemarks.setMessage(R.string.alert_plz_enter_qty);
        alertDialogEnterAttendRemarks.setCancelable(true);
        int MAX_LENGTH = 32;

        final EditText editQty = new EditText(SerialNoSelectionActivity.this);
        editQty.setBackgroundResource(R.drawable.edittext);
        editQty.setTextColor(Color.BLACK);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH);
        editQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        editQty.setFilters(FilterArray);
        mStrQty = "";
        editQty.setText(mStrQty.equalsIgnoreCase("") ? mStrQty : "");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editQty.setLayoutParams(lp);
        alertDialogEnterAttendRemarks.setView(editQty);
        alertDialogEnterAttendRemarks.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStrQty = editQty.getText().toString().trim();
                        if (mStrQty.equalsIgnoreCase("")) {
                            displayError(getString(R.string.please_enter_valid_quantity));
                        } else {

                            int prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

                            BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoTo(), prefixLen));

                            BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoFrom(), prefixLen));

                            BigInteger mDoubleAvalibleQty = doubAvalTo.subtract(doubAvalFrom).add(new BigInteger("1"))/*doubAvalTo - doubAvalFrom + 1 */;

                            BigInteger mDoubleEnterQty = new BigInteger(mStrQty);

                            mDoubleValAllocatedQty = mDoubleTotalAllocatedQty + mDoubleEnterQty.doubleValue();

                            if (mDoubleValAllocatedQty <= Double.parseDouble(mStrInvoiceQty) && mDoubleEnterQty.doubleValue() > 0) {
                                int res;

                                // compare bi1 with bi2
                                res = mDoubleAvalibleQty.compareTo(mDoubleEnterQty);


                                if (res == 1 || res == 0) {

                                    GUID guidSPSno = GUID.newRandom();

                                    alAvalibleSnoList.remove(tempInvoiceBean);

                                    String mStrPrefixStr = "";
                                    int numberLengthWithLeadingZeroes;

                                    prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

                                    if (prefixLen > 0) {
                                        mStrPrefixStr = tempInvoiceBean.getSerialNoFrom().substring(0, prefixLen);
                                        numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().substring(prefixLen, tempInvoiceBean.getSerialNoFrom().length()).length();
                                    } else {
                                        numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().length();
                                    }

                                    InvoiceBean invBean = new InvoiceBean();
                                    invBean.setSerialNoFrom(tempInvoiceBean.getSerialNoFrom());
                                    invBean.setSPSNoGUID(guidSPSno.toString36());
                                    invBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                    if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                        invBean.setSerialNoTo(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty).subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                    } else {
                                        invBean.setSerialNoTo(UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty).subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                    }

                                    invBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                    invBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                    invBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                    invBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                    invBean.setOption(tempInvoiceBean.getOption());
                                    invBean.setEtag(tempInvoiceBean.getEtag());
                                    invBean.setTempSpSnoGuid(guidSPSno.toString36());
                                    invBean.setSequence(tempInvoiceBean.getSequence());
                                    invBean.setUom(tempInvoiceBean.getUom());
                                    invBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                    testAllocatedSnosBean = invBean;
                                    alTempAllocatedSnoList.add(invBean);

                                    InvoiceBean avaInvBean = new InvoiceBean();
                                    avaInvBean.setTempSpSnoGuid(guidSPSno.toString36());
                                    guidSPSno = GUID.newRandom();
                                    avaInvBean.setSPSNoGUID(guidSPSno.toString36());
                                    avaInvBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                    avaInvBean.setSerialNoTo(tempInvoiceBean.getSerialNoTo());

                                    if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                        avaInvBean.setSerialNoFrom(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty), numberLengthWithLeadingZeroes));
                                    } else {
                                        avaInvBean.setSerialNoFrom(UtilConstants.addZerosBeforeValue(doubAvalFrom.add(mDoubleEnterQty), numberLengthWithLeadingZeroes));
                                    }

                                    avaInvBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                    avaInvBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                    avaInvBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                    avaInvBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                    avaInvBean.setOption(tempInvoiceBean.getOption());
                                    avaInvBean.setEtag(tempInvoiceBean.getEtag());
                                    avaInvBean.setSequence(tempInvoiceBean.getSequence());
                                    avaInvBean.setUom(tempInvoiceBean.getUom());
                                    avaInvBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                    if (res == 1) {
                                        alAvalibleSnoList.add(avaInvBean);
                                    }

                                    displayAvalibleSerialNos();
                                    displayAllocatedSerialNos();
                                } else {
                                    displayError(getString(R.string.please_enter_valid_quantity));
                                }
                            } else {
                                displayError(getString(R.string.alert_allocated_qty_less_than_invoice_qty));
                            }


                        }
                    }
                });

        alertDialogEnterAttendRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogEnterAttendRemarks.create();
        alertDialog.show();
    }

    /*Dialog to enter range*/
    private void enterRangeDialogBox() {
        mStrFromRangeQty = "";
        mStrToRangeQty = "";
        mDoubleValAllocatedQty = 0.0;
        AlertDialog.Builder alertDialogEnterAttendRemarks = new AlertDialog.Builder(SerialNoSelectionActivity.this, R.style.MyTheme);
        alertDialogEnterAttendRemarks.setMessage(R.string.alert_plz_enter_range);
        alertDialogEnterAttendRemarks.setCancelable(true);

        View view = View.inflate(this, R.layout.range_custom_dialog_box, null);

        int prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

        BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoTo(), prefixLen));

        BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoFrom(), prefixLen));

        final EditText edit_from_range = (EditText) view.findViewById(R.id.edit_from_range);
        final EditText edit_to_range = (EditText) view.findViewById(R.id.edit_to_range);
        edit_from_range.setBackgroundResource(R.drawable.edittext);
        edit_to_range.setBackgroundResource(R.drawable.edittext);
        edit_from_range.setTextColor(Color.BLACK);
        edit_to_range.setTextColor(Color.BLACK);
        edit_from_range.setText(doubAvalFrom.toString());
        edit_to_range.setText(doubAvalTo.toString());


        alertDialogEnterAttendRemarks.setView(view);

        alertDialogEnterAttendRemarks.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStrFromRangeQty = edit_from_range.getText().toString().trim();
                        mStrToRangeQty = edit_to_range.getText().toString().trim();

                        if (mStrFromRangeQty.equalsIgnoreCase("") || mStrToRangeQty.equalsIgnoreCase("")) {
                            displayError(getString(R.string.alert_enter_valid_range));
                        } else {
                            int prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());
                            BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoTo(), prefixLen));

                            BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(tempInvoiceBean.getSerialNoFrom(), prefixLen));

                            BigInteger mDoubleEnterFromQty = new BigInteger(mStrFromRangeQty);
                            BigInteger mDoubleEnterToQty = new BigInteger(mStrToRangeQty);

                            int comparison = mDoubleEnterToQty.compareTo(mDoubleEnterFromQty);


                            int firstComprison = doubAvalFrom.compareTo(mDoubleEnterFromQty);
                            int secondComprison = doubAvalTo.compareTo(mDoubleEnterToQty);

                            if (comparison == 0 || comparison == 1) {

                                if ((firstComprison == 0 || firstComprison == -1) && (secondComprison == 0 || secondComprison == 1)) {


                                    BigInteger remaingQty = mDoubleEnterToQty.subtract(mDoubleEnterFromQty);
                                    mDoubleValAllocatedQty = mDoubleTotalAllocatedQty + remaingQty.doubleValue();

                                    if (mDoubleValAllocatedQty <= Double.parseDouble(mStrInvoiceQty)) {
                                        alAvalibleSnoList.remove(tempInvoiceBean);

                                        GUID guidSPSno = GUID.newRandom();
                                        GUID guidSpnoRange = GUID.newRandom();

                                        String[] splitString;
                                        String mStrPrefixStr = "";
                                        int numberLengthWithLeadingZeroes;

                                        prefixLen = (int) Double.parseDouble(tempInvoiceBean.getPrefixLength());

                                        if (prefixLen > 0) {
                                            mStrPrefixStr = tempInvoiceBean.getSerialNoFrom().substring(0, prefixLen);
                                            numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().substring(prefixLen, tempInvoiceBean.getSerialNoFrom().length()).length();
                                        } else {
                                            numberLengthWithLeadingZeroes = tempInvoiceBean.getSerialNoFrom().length();
                                        }

                                        if ((firstComprison == -1) && (secondComprison == 0)) {

                                            InvoiceBean invBean = new InvoiceBean();
                                            invBean.setSPSNoGUID(guidSPSno.toString36());
                                            invBean.setTempSpSnoGuid(guidSPSno.toString36());
                                            invBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                            invBean.setSerialNoTo(tempInvoiceBean.getSerialNoTo());

                                            if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                                invBean.setSerialNoFrom(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty, numberLengthWithLeadingZeroes));
                                            } else {
                                                invBean.setSerialNoFrom(UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty, numberLengthWithLeadingZeroes));
                                            }

                                            invBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                            invBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                            invBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                            invBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                            invBean.setOption(tempInvoiceBean.getOption());
                                            invBean.setEtag(tempInvoiceBean.getEtag());
                                            invBean.setSequence(tempInvoiceBean.getSequence());
                                            invBean.setUom(tempInvoiceBean.getUom());
                                            invBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                            testAllocatedSnosBean = invBean;
                                            alTempAllocatedSnoList.add(invBean);

                                            InvoiceBean avaInvBean = new InvoiceBean();
                                            avaInvBean.setTempSpSnoGuid(guidSPSno.toString36());
                                            guidSPSno = GUID.newRandom();
                                            avaInvBean.setSPSNoGUID(guidSPSno.toString36());
                                            avaInvBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                            if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                                avaInvBean.setSerialNoTo(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty.subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                            } else {
                                                avaInvBean.setSerialNoTo(UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty.subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                            }
                                            avaInvBean.setSerialNoFrom(tempInvoiceBean.getSerialNoFrom());
                                            avaInvBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                            avaInvBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                            avaInvBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                            avaInvBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                            avaInvBean.setOption(tempInvoiceBean.getOption());
                                            avaInvBean.setEtag(tempInvoiceBean.getEtag());
                                            avaInvBean.setSequence(tempInvoiceBean.getSequence());
                                            avaInvBean.setUom(tempInvoiceBean.getUom());
                                            avaInvBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                            alAvalibleSnoList.add(avaInvBean);

                                        } else if ((firstComprison == 0) && (secondComprison == 1)) {
                                            InvoiceBean invBean = new InvoiceBean();
                                            invBean.setSPSNoGUID(guidSPSno.toString36());
                                            invBean.setTempSpSnoGuid(guidSPSno.toString36());
                                            invBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                            if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                                invBean.setSerialNoTo(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterToQty, numberLengthWithLeadingZeroes));
                                            } else {
                                                invBean.setSerialNoTo(UtilConstants.addZerosBeforeValue(mDoubleEnterToQty, numberLengthWithLeadingZeroes));
                                            }

                                            invBean.setSerialNoFrom(tempInvoiceBean.getSerialNoFrom());
                                            invBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                            invBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                            invBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                            invBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                            invBean.setOption(tempInvoiceBean.getOption());
                                            invBean.setEtag(tempInvoiceBean.getEtag());
                                            invBean.setSequence(tempInvoiceBean.getSequence());
                                            invBean.setUom(tempInvoiceBean.getUom());
                                            invBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                            testAllocatedSnosBean = invBean;
                                            alTempAllocatedSnoList.add(invBean);

                                            InvoiceBean avaInvBean = new InvoiceBean();
                                            avaInvBean.setTempSpSnoGuid(guidSPSno.toString36());
                                            guidSPSno = GUID.newRandom();
                                            avaInvBean.setSPSNoGUID(guidSPSno.toString36());
                                            avaInvBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                            avaInvBean.setSerialNoTo(tempInvoiceBean.getSerialNoTo());
                                            if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                                avaInvBean.setSerialNoFrom(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterToQty.add(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                            } else {
                                                avaInvBean.setSerialNoFrom(UtilConstants.addZerosBeforeValue(mDoubleEnterToQty.add(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                            }
                                            avaInvBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                            avaInvBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                            avaInvBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                            avaInvBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                            avaInvBean.setOption(tempInvoiceBean.getOption());
                                            avaInvBean.setEtag(tempInvoiceBean.getEtag());
                                            avaInvBean.setSequence(tempInvoiceBean.getSequence());
                                            avaInvBean.setUom(tempInvoiceBean.getUom());
                                            avaInvBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                            alAvalibleSnoList.add(avaInvBean);
                                        } else if ((firstComprison == -1) && (secondComprison == 1)) {
                                            InvoiceBean invBean = new InvoiceBean();
                                            invBean.setSPSNoGUID(guidSPSno.toString36());
                                            invBean.setTempSpSnoGuid(guidSpnoRange.toString36());
                                            invBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                            if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                                invBean.setSerialNoTo(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterToQty, numberLengthWithLeadingZeroes));
                                                invBean.setSerialNoFrom(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty, numberLengthWithLeadingZeroes));
                                            } else {
                                                invBean.setSerialNoTo(UtilConstants.addZerosBeforeValue(mDoubleEnterToQty, numberLengthWithLeadingZeroes));
                                                invBean.setSerialNoFrom(UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty, numberLengthWithLeadingZeroes));
                                            }
                                            invBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                            invBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                            invBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                            invBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                            invBean.setOption(tempInvoiceBean.getOption());
                                            invBean.setEtag(tempInvoiceBean.getEtag());
                                            invBean.setSequence(tempInvoiceBean.getSequence());
                                            invBean.setUom(tempInvoiceBean.getUom());
                                            invBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                            testAllocatedSnosBean = invBean;
                                            alTempAllocatedSnoList.add(invBean);

                                            // split first serial number
                                            InvoiceBean avaInvBean = new InvoiceBean();
                                            avaInvBean.setTempSpSnoGuid(guidSpnoRange.toString36());
                                            guidSPSno = GUID.newRandom();
                                            avaInvBean.setSPSNoGUID(guidSPSno.toString36());
                                            avaInvBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                            if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                                avaInvBean.setSerialNoTo(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty.subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));

                                            } else {
                                                avaInvBean.setSerialNoTo(UtilConstants.addZerosBeforeValue(mDoubleEnterFromQty.subtract(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                            }
                                            avaInvBean.setSerialNoFrom(tempInvoiceBean.getSerialNoFrom());
                                            avaInvBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                            avaInvBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                            avaInvBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                            avaInvBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                            avaInvBean.setOption(tempInvoiceBean.getOption());
                                            avaInvBean.setEtag(tempInvoiceBean.getEtag());
                                            avaInvBean.setSequence(tempInvoiceBean.getSequence());
                                            avaInvBean.setUom(tempInvoiceBean.getUom());
                                            avaInvBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                            alAvalibleSnoList.add(avaInvBean);
                                            //split second serial number
                                            avaInvBean = new InvoiceBean();
                                            guidSPSno = GUID.newRandom();
                                            avaInvBean.setTempSpSnoGuid(guidSpnoRange.toString36());
                                            avaInvBean.setSPSNoGUID(guidSPSno.toString36());
                                            avaInvBean.setCPStockItemGUID(tempInvoiceBean.getCPStockItemGUID());
                                            avaInvBean.setSerialNoTo(tempInvoiceBean.getSerialNoTo());
                                            if (Double.parseDouble(tempInvoiceBean.getPrefixLength()) > 0) {
                                                avaInvBean.setSerialNoFrom(mStrPrefixStr + "" + UtilConstants.addZerosBeforeValue(mDoubleEnterToQty.add(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                            } else {
                                                avaInvBean.setSerialNoFrom(UtilConstants.addZerosBeforeValue(mDoubleEnterToQty.add(new BigInteger("1")), numberLengthWithLeadingZeroes));
                                            }
                                            avaInvBean.setOldSPSNoGUID(tempInvoiceBean.getOldSPSNoGUID());
                                            avaInvBean.setSelectedSerialNoFrom(tempInvoiceBean.getSelectedSerialNoFrom());
                                            avaInvBean.setSelectedSerialNoTo(tempInvoiceBean.getSelectedSerialNoTo());
                                            avaInvBean.setPrefixLength(tempInvoiceBean.getPrefixLength());
                                            avaInvBean.setOption(tempInvoiceBean.getOption());
                                            avaInvBean.setEtag(tempInvoiceBean.getEtag());
                                            avaInvBean.setSequence(tempInvoiceBean.getSequence());
                                            avaInvBean.setUom(tempInvoiceBean.getUom());
                                            avaInvBean.setStockTypeID(tempInvoiceBean.getStockTypeID());
                                            alAvalibleSnoList.add(avaInvBean);
                                        } else if (firstComprison == 0 && secondComprison == 0) {
                                            testAllocatedSnosBean = tempInvoiceBean;
                                            alTempAllocatedSnoList.add(tempInvoiceBean);
                                        }


                                        displayAvalibleSerialNos();
                                        displayAllocatedSerialNos();
                                    } else {
                                        displayError(getString(R.string.alert_allocated_qty_less_than_invoice_qty));
                                    }


                                } else {
                                    displayError(getString(R.string.alert_enter_qty));
                                }
                            } else {
                                displayError(getString(R.string.alert_enter_qty));
                            }


                        }
                    }
                });

        alertDialogEnterAttendRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogEnterAttendRemarks.create();
        alertDialog.show();
    }

    private void displayAvalibleSerialNos() {

        if (alAvalibleSnoList != null && alAvalibleSnoList.size() > 0) {
            Collections.sort(alAvalibleSnoList, new Comparator<InvoiceBean>() {
                @Override
                public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                    return firstBean.getSerialNoFrom().compareTo(seondBean.getSerialNoFrom());
                }
            });

        }

        LinearLayout ll_avalible_serial_no_list = (LinearLayout) findViewById(R.id.ll_avalible_serial_no_list);

        ll_avalible_serial_no_list.removeAllViews();

        @SuppressLint("InflateParams")
        TableLayout tlSerialNoList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout ll_avaliable_serial_no_heading;
        ll_avaliable_serial_no_heading = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.avaliable_serial_no_heading,
                        null, false);
        tlSerialNoList.addView(ll_avaliable_serial_no_heading);

        if (alAvalibleSnoList != null && alAvalibleSnoList.size() > 0) {
            LinearLayout llSerialNoList;

            for (int i = 0; i < alAvalibleSnoList.size(); i++) {
                final int selvalue = i;

                final InvoiceBean invoiceBean = alAvalibleSnoList.get(i);

                llSerialNoList = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.avalible_serial_no_values,
                                null, false);

                int prefixLen = (int) Double.parseDouble(invoiceBean.getPrefixLength());
                ((TextView) llSerialNoList.findViewById(R.id.tv_available_from_value))
                        .setText(UtilConstants.rearrangeSerialNoText(alAvalibleSnoList.get(i).getSerialNoFrom(), prefixLen));

                ((TextView) llSerialNoList.findViewById(R.id.tv_available_to_value))
                        .setText(UtilConstants.rearrangeSerialNoText(alAvalibleSnoList.get(i).getSerialNoTo(), prefixLen));

                BigInteger avalibleQty = null;
                try {
                    BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(alAvalibleSnoList.get(i).getSerialNoTo(), prefixLen));

                    BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(alAvalibleSnoList.get(i).getSerialNoFrom(), prefixLen));


                    avalibleQty = (doubAvalTo.subtract(doubAvalFrom).add(new BigInteger("1")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((TextView) llSerialNoList.findViewById(R.id.tv_available_qty_value))
                        .setText(avalibleQty + "");

                llSerialNoList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tempInvoiceBean = invoiceBean;
                        enableDialogBoxForSelCriteria(invoiceBean);
                    }
                });

                tlSerialNoList.addView(llSerialNoList);
            }
        }

        ll_avalible_serial_no_list.addView(tlSerialNoList);
        ll_avalible_serial_no_list.requestLayout();
    }

    private void mergeAllocatedSerialNos() {

        alTempAllocatedSnoList.clear();

        if (alAllocatedSnoList != null && alAllocatedSnoList.size() > 0) {
            for (int k = 0; k < alAllocatedSnoList.size() - 1; k++) {

                InvoiceBean avalibleInvBean = alAllocatedSnoList.get(k);
                if (testAllocatedSnosBean.getOldSPSNoGUID().equalsIgnoreCase(avalibleInvBean.getOldSPSNoGUID())) {

                    int prefixLenTemp = (int) Double.parseDouble(testAllocatedSnosBean.getPrefixLength());

                    int prefixLen = (int) Double.parseDouble(avalibleInvBean.getPrefixLength());

                    BigInteger AvalToQty = new BigInteger(UtilConstants.removeAlphanumericText(avalibleInvBean.getSerialNoTo(), prefixLen));

                    BigInteger AvalFromQty = new BigInteger(UtilConstants.removeAlphanumericText(avalibleInvBean.getSerialNoFrom(), prefixLen));

                    BigInteger AllocatedToQty = new BigInteger(UtilConstants.removeAlphanumericText(testAllocatedSnosBean.getSerialNoTo(), prefixLenTemp));

                    BigInteger AllocatedFromQty = new BigInteger(UtilConstants.removeAlphanumericText(testAllocatedSnosBean.getSerialNoFrom(), prefixLenTemp));


                    int firstComparison = AllocatedFromQty.compareTo(AvalFromQty);
                    int secondComprison = AllocatedFromQty.compareTo(AvalToQty);

                    if (avalibleInvBean.getSerialNoFrom().equalsIgnoreCase(testAllocatedSnosBean.getSerialNoFrom()) &&
                            avalibleInvBean.getSerialNoTo().equalsIgnoreCase(testAllocatedSnosBean.getSerialNoTo())) {
                    } else if (firstComparison == -1) {  // AllocatedFromQty < AvalFromQty
                        int compareTo = AllocatedToQty.compareTo(AvalFromQty.subtract(new BigInteger("1")));
                        int secondCompareTo = AllocatedToQty.compareTo(AvalFromQty);
                        if (compareTo == 0) {
                            InvoiceBean avaInvBean = new InvoiceBean();
                            avaInvBean.setSPSNoGUID(avalibleInvBean.getSPSNoGUID());
                            avaInvBean.setCPStockItemGUID(avalibleInvBean.getCPStockItemGUID());
                            avaInvBean.setSerialNoTo(avalibleInvBean.getSerialNoTo());
                            avaInvBean.setSerialNoFrom(testAllocatedSnosBean.getSerialNoFrom());
                            avaInvBean.setOldSPSNoGUID(avalibleInvBean.getOldSPSNoGUID());
                            avaInvBean.setSelectedSerialNoFrom(avalibleInvBean.getSelectedSerialNoFrom());
                            avaInvBean.setSelectedSerialNoTo(avalibleInvBean.getSelectedSerialNoTo());
                            avaInvBean.setPrefixLength(avalibleInvBean.getPrefixLength());
                            avaInvBean.setOption(avalibleInvBean.getOption());
                            avaInvBean.setEtag(avalibleInvBean.getEtag());
                            avaInvBean.setTempSpSnoGuid(avalibleInvBean.getTempSpSnoGuid());
                            avaInvBean.setSequence(avalibleInvBean.getSequence());
                            avaInvBean.setUom(avalibleInvBean.getUom());
                            avaInvBean.setStockTypeID(avalibleInvBean.getStockTypeID());
                            alTempAllocatedSnoList.add(avaInvBean);
                            try {
                                alAllocatedSnoList.remove(k);
                                alAllocatedSnoList.remove(k);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        } else if (secondCompareTo == -1) {
//                            alTempAllocatedSnoList.add(testAllocatedSnosBean);
                            break;
                        }

                    } else if (secondComprison == 1) {// AllocatedFromQty > AvalToQty
                        int firstCompareTo = AllocatedFromQty.compareTo(AvalToQty.add(new BigInteger("1")));
                        int secondCompareTo = AllocatedFromQty.compareTo(AvalToQty);

                        if (firstCompareTo == 0) {
                            InvoiceBean avaInvBean = new InvoiceBean();
                            avaInvBean.setSPSNoGUID(avalibleInvBean.getSPSNoGUID());
                            avaInvBean.setCPStockItemGUID(avalibleInvBean.getCPStockItemGUID());
                            avaInvBean.setSerialNoTo(testAllocatedSnosBean.getSerialNoTo());
                            avaInvBean.setSerialNoFrom(avalibleInvBean.getSerialNoFrom());
                            avaInvBean.setOldSPSNoGUID(avalibleInvBean.getOldSPSNoGUID());
                            avaInvBean.setSelectedSerialNoFrom(avalibleInvBean.getSelectedSerialNoFrom());
                            avaInvBean.setSelectedSerialNoTo(avalibleInvBean.getSelectedSerialNoTo());
                            avaInvBean.setPrefixLength(avalibleInvBean.getPrefixLength());
                            avaInvBean.setOption(avalibleInvBean.getOption());
                            avaInvBean.setEtag(avalibleInvBean.getEtag());
                            avaInvBean.setTempSpSnoGuid(avalibleInvBean.getTempSpSnoGuid());
                            avaInvBean.setSequence(avalibleInvBean.getSequence());
                            avaInvBean.setUom(avalibleInvBean.getUom());
                            avaInvBean.setStockTypeID(avalibleInvBean.getStockTypeID());
                            alTempAllocatedSnoList.add(avaInvBean);
                            try {
                                alAllocatedSnoList.remove(k);
                                alAllocatedSnoList.remove(k);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        } else if (secondCompareTo == 1) {
                            break;
                        }
                    }
                }
            }
        }


        if (alAllocatedSnoList.size() > 0) {
            alAllocatedSnoList.addAll(alAllocatedSnoList.size(), alTempAllocatedSnoList);
        } else {
            alAllocatedSnoList.addAll(0, alTempAllocatedSnoList);
        }

        Collections.sort(alAllocatedSnoList, new Comparator<InvoiceBean>() {
            @Override
            public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                return firstBean.getSerialNoFrom().compareTo(seondBean.getSerialNoFrom());
            }
        });

        Collections.sort(alAllocatedSnoList, new Comparator<InvoiceBean>() {
            @Override
            public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                return firstBean.getSequence().compareTo(seondBean.getSequence());
            }
        });

        alTempAllocatedSnoList.clear();
        alTempAvalibleSnoList = new ArrayList<InvoiceBean>();

        if (alAllocatedSnoList.size() > 0) {
            alTempAvalibleSnoList.addAll(0, alAllocatedSnoList);
        }

        if (alTempAvalibleSnoList != null && alTempAvalibleSnoList.size() > 0) {
            for (int k = 0; k < alTempAvalibleSnoList.size() - 1; k++) {
                InvoiceBean firstBean = alTempAvalibleSnoList.get(k);
                InvoiceBean secondBean = alTempAvalibleSnoList.get(k + 1);

                if (!firstBean.getTempSpSnoGuid().equalsIgnoreCase("") && !secondBean.getTempSpSnoGuid().equalsIgnoreCase("")) {
                    if (firstBean.getOldSPSNoGUID().equalsIgnoreCase(secondBean.getOldSPSNoGUID())
                            ) {
                        int prefixLen = (int) Double.parseDouble(firstBean.getPrefixLength());
                        BigInteger AvalToQty = new BigInteger(UtilConstants.removeAlphanumericText(firstBean.getSerialNoTo(), prefixLen));
                        BigInteger AvalFromQty = new BigInteger(UtilConstants.removeAlphanumericText(secondBean.getSerialNoFrom(), prefixLen));
                        int compareTo = AvalToQty.compareTo(AvalFromQty.subtract(new BigInteger("1")));

                        if (compareTo == 0) {
                            InvoiceBean avaInvBean = new InvoiceBean();
                            avaInvBean.setSPSNoGUID(firstBean.getSPSNoGUID());
                            avaInvBean.setCPStockItemGUID(firstBean.getCPStockItemGUID());
                            avaInvBean.setSerialNoTo(secondBean.getSerialNoTo());
                            avaInvBean.setSerialNoFrom(firstBean.getSerialNoFrom());
                            avaInvBean.setOldSPSNoGUID(firstBean.getOldSPSNoGUID());
                            avaInvBean.setSelectedSerialNoFrom(firstBean.getSelectedSerialNoFrom());
                            avaInvBean.setSelectedSerialNoTo(firstBean.getSelectedSerialNoTo());
                            avaInvBean.setPrefixLength(firstBean.getPrefixLength());
                            avaInvBean.setOption(firstBean.getOption());
                            avaInvBean.setEtag(firstBean.getEtag());
                            avaInvBean.setTempSpSnoGuid(firstBean.getTempSpSnoGuid());
                            avaInvBean.setSequence(firstBean.getSequence());
                            avaInvBean.setUom(firstBean.getUom());
                            avaInvBean.setStockTypeID(firstBean.getStockTypeID());
                            alTempAllocatedSnoList.add(avaInvBean);
                            alAllocatedSnoList.remove(firstBean);
                            alAllocatedSnoList.remove(secondBean);
                        }

                    }
                }

            }
        }
        alTestAllocatedSnoList = new ArrayList<InvoiceBean>();
        if (alTempAllocatedSnoList.size() > 1) {
            for (int k = 0; k < alTempAllocatedSnoList.size() - 1; k++) {
                InvoiceBean firstBean = alTempAllocatedSnoList.get(k);
                InvoiceBean secondBean = alTempAllocatedSnoList.get(k + 1);
                if (!firstBean.getTempSpSnoGuid().equalsIgnoreCase("") && !secondBean.getTempSpSnoGuid().equalsIgnoreCase("")) {
                    if (firstBean.getOldSPSNoGUID().equalsIgnoreCase(secondBean.getOldSPSNoGUID())
                            ) {
                        int prefixLen = (int) Double.parseDouble(firstBean.getPrefixLength());
                        BigInteger AvalToQty = new BigInteger(UtilConstants.removeAlphanumericText(firstBean.getSerialNoTo(), prefixLen));
                        BigInteger AvalFromQty = new BigInteger(UtilConstants.removeAlphanumericText(secondBean.getSerialNoFrom(), prefixLen));
                        int compareTo = AvalFromQty.compareTo(AvalToQty.subtract(new BigInteger("1")));
                        if (compareTo == 0) {
                            InvoiceBean avaInvBean = new InvoiceBean();
                            avaInvBean.setSPSNoGUID(firstBean.getSPSNoGUID());
                            avaInvBean.setCPStockItemGUID(firstBean.getCPStockItemGUID());
                            avaInvBean.setSerialNoTo(secondBean.getSerialNoTo());
                            avaInvBean.setSerialNoFrom(firstBean.getSerialNoFrom());
                            avaInvBean.setOldSPSNoGUID(firstBean.getOldSPSNoGUID());
                            avaInvBean.setSelectedSerialNoFrom(firstBean.getSelectedSerialNoFrom());
                            avaInvBean.setSelectedSerialNoTo(firstBean.getSelectedSerialNoTo());
                            avaInvBean.setPrefixLength(firstBean.getPrefixLength());
                            avaInvBean.setOption(firstBean.getOption());
                            avaInvBean.setEtag(firstBean.getEtag());
                            avaInvBean.setTempSpSnoGuid(firstBean.getTempSpSnoGuid());
                            avaInvBean.setSequence(firstBean.getSequence());
                            avaInvBean.setUom(firstBean.getUom());
                            avaInvBean.setStockTypeID(firstBean.getStockTypeID());
                            alTestAllocatedSnoList.add(avaInvBean);
                            alTempAllocatedSnoList.remove(firstBean);
                            alTempAllocatedSnoList.remove(secondBean);
                            break;
                        }
                    }
                }
            }

            if (alAllocatedSnoList.size() > 0) {
                alAllocatedSnoList.addAll(alAllocatedSnoList.size(), alTestAllocatedSnoList);
            } else {
                alAllocatedSnoList.addAll(0, alTestAllocatedSnoList);
            }

        } else {

            if (alAllocatedSnoList.size() > 0) {
                alAllocatedSnoList.addAll(alAllocatedSnoList.size(), alTempAllocatedSnoList);
            } else {
                alAllocatedSnoList.addAll(0, alTempAllocatedSnoList);
            }
        }

        Collections.sort(alAllocatedSnoList, new Comparator<InvoiceBean>() {
            @Override
            public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                return firstBean.getSerialNoFrom().compareTo(seondBean.getSerialNoFrom());
            }
        });

        Collections.sort(alAllocatedSnoList, new Comparator<InvoiceBean>() {
            @Override
            public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                return firstBean.getSequence().compareTo(seondBean.getSequence());
            }
        });

        alTempAllocatedSnoList.clear();
        alTestAllocatedSnoList.clear();

    }

    private void displayAllocatedSerialNos() {

        mDoubleTotalAllocatedQty = 0.0;
        if (alTempAllocatedSnoList != null) {
            if (alAllocatedSnoList != null) {
                alAllocatedSnoList.addAll(alAllocatedSnoList.size(), alTempAllocatedSnoList);
            } else {
                alAllocatedSnoList = alTempAllocatedSnoList;
            }
        }

        if (alAllocatedSnoList != null && alAllocatedSnoList.size() > 0) {
            Collections.sort(alAllocatedSnoList, new Comparator<InvoiceBean>() {
                @Override
                public int compare(InvoiceBean firstBean, InvoiceBean seondBean) {
                    return firstBean.getSerialNoFrom().compareTo(seondBean.getSerialNoFrom());
                }
            });

            if (alAllocatedSnoList.size() > 1)
                mergeAllocatedSerialNos();
        }

        LinearLayout ll_allocated_serial_no_list = (LinearLayout) findViewById(R.id.ll_allocated_serial_no_list);

        ll_allocated_serial_no_list.removeAllViews();

        @SuppressLint("InflateParams")
        TableLayout tlSerialNoList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout ll_allocated_serial_no_heading;
        ll_allocated_serial_no_heading = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.allocated_serial_no_heading,
                        null, false);

        tlSerialNoList.addView(ll_allocated_serial_no_heading);


        if (alAllocatedSnoList != null && alAllocatedSnoList.size() > 0) {
            LinearLayout llSerialNoList;

            for (int i = 0; i < alAllocatedSnoList.size(); i++) {
                final int selvalue = i;
                final InvoiceBean invoiceBean = alAllocatedSnoList.get(i);

                llSerialNoList = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.allocated_serial_no_values,
                                null, false);

                int prefixLen = (int) Double.parseDouble(invoiceBean.getPrefixLength());

                ((TextView) llSerialNoList.findViewById(R.id.tv_allocated_from_value))
                        .setText(UtilConstants.rearrangeSerialNoText(alAllocatedSnoList.get(i).getSerialNoFrom(), prefixLen));

                ((TextView) llSerialNoList.findViewById(R.id.tv_allocated_to_value))
                        .setText(UtilConstants.rearrangeSerialNoText(alAllocatedSnoList.get(i).getSerialNoTo(), prefixLen));


                BigInteger allocatedQty = null;
                try {
                    BigInteger doubAllocatedTo = new BigInteger(UtilConstants.removeAlphanumericText(alAllocatedSnoList.get(i).getSerialNoTo(), prefixLen));

                    BigInteger doubAllocatedFrom = new BigInteger(UtilConstants.removeAlphanumericText(alAllocatedSnoList.get(i).getSerialNoFrom(), prefixLen));

                    allocatedQty = (doubAllocatedTo.subtract(doubAllocatedFrom).add(new BigInteger("1")));

                    mDoubleTotalAllocatedQty = mDoubleTotalAllocatedQty + allocatedQty.doubleValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((TextView) llSerialNoList.findViewById(R.id.tv_allocated_qty_value))
                        .setText(allocatedQty + "");


                llSerialNoList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tempInvoiceBean = invoiceBean;
                        enableDialogBoxForRemoveSelCriteria();
                    }
                });


                tlSerialNoList.addView(llSerialNoList);
            }
        }
        tv_allocated_qty.setText(UtilConstants.removeLeadingZero(mDoubleTotalAllocatedQty) + "");

        ll_allocated_serial_no_list.addView(tlSerialNoList);
        ll_allocated_serial_no_list.requestLayout();
    }

    @Override
    public void onBackPressed() {
        boolean errorFlag = false;

        if (mDoubleTotalAllocatedQty == 0) {
            algetInvoiceList.clear();
            errorFlag = false;
        } else if (mDoubleTotalAllocatedQty == Double.parseDouble(mStrInvoiceQty)) {
            algetInvoiceList.clear();
            String mStrAvalDeletedSpSno = "";

            if (alAvalibleSnoList != null && alAvalibleSnoList.size() > 0) {
                for (int k = 0; k < alAvalibleSnoList.size(); k++) {
                    InvoiceBean avlInvBean = alAvalibleSnoList.get(k);

                    if (avlInvBean.getSerialNoFrom().equalsIgnoreCase(avlInvBean.getSelectedSerialNoFrom()) && avlInvBean.getSerialNoTo().equalsIgnoreCase(avlInvBean.getSelectedSerialNoTo())) {
                        InvoiceBean invBean = new InvoiceBean();

                        invBean.setSPSNoGUID(avlInvBean.getSPSNoGUID());
                        invBean.setCPStockItemGUID(avlInvBean.getCPStockItemGUID());
                        invBean.setSerialNoTo(avlInvBean.getSerialNoTo());
                        invBean.setSerialNoFrom(avlInvBean.getSerialNoFrom());
                        invBean.setOldSPSNoGUID(avlInvBean.getOldSPSNoGUID());
                        invBean.setSelectedSerialNoTo(avlInvBean.getSelectedSerialNoTo());
                        invBean.setSelectedSerialNoFrom(avlInvBean.getSelectedSerialNoFrom());
                        invBean.setPrefixLength(avlInvBean.getPrefixLength());
                        invBean.setTempSpSnoGuid(avlInvBean.getTempSpSnoGuid());
                        invBean.setOption(avlInvBean.getOption());
                        invBean.setStatus("04");
                        invBean.setSequence(avlInvBean.getSequence());
                        invBean.setUom(avlInvBean.getUom());
                        invBean.setStockTypeID(avlInvBean.getStockTypeID());
                        algetInvoiceList.add(invBean);
                    } else if (!avlInvBean.getSerialNoFrom().equalsIgnoreCase(avlInvBean.getSelectedSerialNoFrom()) || !avlInvBean.getSerialNoTo().equalsIgnoreCase(avlInvBean.getSelectedSerialNoTo())) {
                        InvoiceBean invBean = new InvoiceBean();

                        invBean.setSPSNoGUID(avlInvBean.getSPSNoGUID());
                        invBean.setCPStockItemGUID(avlInvBean.getCPStockItemGUID());
                        invBean.setSerialNoTo(avlInvBean.getSerialNoTo());
                        invBean.setSerialNoFrom(avlInvBean.getSerialNoFrom());
                        invBean.setOldSPSNoGUID(avlInvBean.getOldSPSNoGUID());
                        invBean.setSelectedSerialNoTo(avlInvBean.getSelectedSerialNoTo());
                        invBean.setSelectedSerialNoFrom(avlInvBean.getSelectedSerialNoFrom());
                        invBean.setPrefixLength(avlInvBean.getPrefixLength());
                        invBean.setOption(avlInvBean.getOption());
                        invBean.setEtag(avlInvBean.getEtag());
                        invBean.setStatus("02");
                        invBean.setTempSpSnoGuid(avlInvBean.getTempSpSnoGuid());
                        invBean.setSequence(avlInvBean.getSequence());
                        invBean.setUom(avlInvBean.getUom());
                        invBean.setStockTypeID(avlInvBean.getStockTypeID());
                        algetInvoiceList.add(invBean);
                    }
                }
            }


            if (alAllocatedSnoList != null && alAllocatedSnoList.size() > 0) {
                for (int k = 0; k < alAllocatedSnoList.size(); k++) {
                    InvoiceBean allocatedInvBean = alAllocatedSnoList.get(k);
                    InvoiceBean invBean = new InvoiceBean();

                    invBean.setSPSNoGUID(allocatedInvBean.getSPSNoGUID());
                    invBean.setCPStockItemGUID(allocatedInvBean.getCPStockItemGUID());
                    invBean.setSerialNoTo(allocatedInvBean.getSerialNoTo());
                    invBean.setSerialNoFrom(allocatedInvBean.getSerialNoFrom());
                    invBean.setOldSPSNoGUID(allocatedInvBean.getOldSPSNoGUID());
                    invBean.setSelectedSerialNoTo(allocatedInvBean.getSelectedSerialNoTo());
                    invBean.setSelectedSerialNoFrom(allocatedInvBean.getSelectedSerialNoFrom());
                    invBean.setPrefixLength(allocatedInvBean.getPrefixLength());
                    invBean.setOption(allocatedInvBean.getOption());
                    invBean.setEtag(allocatedInvBean.getEtag());
                    invBean.setTempSpSnoGuid(allocatedInvBean.getTempSpSnoGuid());
                    invBean.setStatus("03");
                    invBean.setSequence(allocatedInvBean.getSequence());
                    invBean.setUom(allocatedInvBean.getUom());
                    invBean.setStockTypeID(allocatedInvBean.getStockTypeID());
                    algetInvoiceList.add(invBean);

                    if (mStrAvalDeletedSpSno.equalsIgnoreCase("") || !mStrAvalDeletedSpSno.equalsIgnoreCase(allocatedInvBean.getOldSPSNoGUID())) {
                        mStrAvalDeletedSpSno = allocatedInvBean.getOldSPSNoGUID();
                        invBean = new InvoiceBean();
                        invBean.setSPSNoGUID(allocatedInvBean.getOldSPSNoGUID());
                        invBean.setCPStockItemGUID(allocatedInvBean.getCPStockItemGUID());
                        invBean.setSerialNoTo(allocatedInvBean.getSelectedSerialNoTo());
                        invBean.setSerialNoFrom(allocatedInvBean.getSelectedSerialNoFrom());
                        invBean.setOldSPSNoGUID(allocatedInvBean.getOldSPSNoGUID());
                        invBean.setSelectedSerialNoTo(allocatedInvBean.getSelectedSerialNoTo());
                        invBean.setSelectedSerialNoFrom(allocatedInvBean.getSelectedSerialNoFrom());
                        invBean.setPrefixLength(allocatedInvBean.getPrefixLength());
                        invBean.setOption(allocatedInvBean.getOption());
                        invBean.setEtag(allocatedInvBean.getEtag());
                        invBean.setTempSpSnoGuid(allocatedInvBean.getTempSpSnoGuid());
                        invBean.setStatus("01");
                        invBean.setSequence(allocatedInvBean.getSequence());
                        invBean.setUom(allocatedInvBean.getUom());
                        invBean.setStockTypeID(allocatedInvBean.getStockTypeID());
                        if (!allocatedInvBean.getOldSPSNoGUID().equalsIgnoreCase("")) {
                            algetInvoiceList.add(invBean);
                        }
                    }
                }
            }


        } else {
            displayError(getString(R.string.alert_allocated_qty_equal_to_invoice_qty));
            errorFlag = true;
        }

        if (!errorFlag) {

            if (selectedInvoiceCheckBoxOption.size() > 0) {
                if (algetInvoiceList.size() > 0) {
                    algetInvoiceList.addAll(algetInvoiceList.size(), selectedInvoiceCheckBoxOption);
                } else {
                    algetInvoiceList.addAll(0, selectedInvoiceCheckBoxOption);
                }
            }
            Constants.HashTableSerialNoAllocatedQty.put(mStrSPStockItemGUID, mDoubleTotalAllocatedQty + "");
            Constants.HashTableSerialNoSelection.put(mStrSPStockItemGUID, algetInvoiceList);
            Intent backToInvoiceScreen = new Intent(this, SerialNoSelectionActivity.class);
            setResult(passedValues, backToInvoiceScreen);
            finish();
        }
    }

    public void displayError(String errorMessage) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                SerialNoSelectionActivity.this, R.style.MyTheme);
        dialog.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
