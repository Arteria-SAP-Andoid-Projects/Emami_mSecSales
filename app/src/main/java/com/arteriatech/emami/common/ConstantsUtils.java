package com.arteriatech.emami.common;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.emami.interfaces.PasswordDialogCallbackInterface;
import com.arteriatech.emami.mbo.RemarkReasonBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by e10769 on 24-03-2017.
 */

public class ConstantsUtils {
    /*session type*/
    public static final int NO_SESSION = 0;// session passing only  app header
    public static final int SESSION_HEADER = 1;// session passing only  app header
    public static final int SESSION_QRY = 2;// session passing only qry
    public static final int SESSION_QRY_HEADER = 3;// session passing both app header and qry

    public static final String STORE_DATA_INTO_TECHNICAL_CACHE = "storeDataIntoTechnicalCache";
    public static final String Brand = "Brand";
    public static final String Banner = "Banner";
    public static final String MaterialNo = "MaterialNo";
    public static final String SKUGroup = "SKUGroup";
    public static final String SKUGroupDesc = "SKUGroupDesc";
    public static final String RegistrationTypeID = "RegistrationTypeID";
    public static final String RegistrationTypeDesc = "RegistrationTypeDesc";
    public static final String OnSaleOfCatDesc = "OnSaleOfCatDesc";
    public static final String OnSaleOfCatID = "OnSaleOfCatID";
    public static final String FreeMatCritria = "FreeMatCritria";
    public static final String BannerDesc = "BannerDesc";
    public static final String ProductCatDesc = "ProductCatDesc";
    public static final String MaterialGroupDesc = "MaterialGroupDesc";
    public static final String ItemMin = "ItemMin";
    public static final String ClaimGUID = "ClaimGUID";
    public static final String ClaimDate = "ClaimDate";
    public static final String ClaimItemGUID = "ClaimItemGUID";
    public static final String RegistrationType = "RegistrationType";
    public static final String ClaimDocumentID = "ClaimDocumentID";
    public static final String RegistrationDate = "RegistrationDate";
    public static final String EnrollmentDate = "EnrollmentDate";
    public static final String SchemeNo = "SchemeNo";
    public static final String ProductCategoryID = "ProductCategoryID";
    public static final String ProductCategoryDesc = "ProductCategoryDesc";
    public static final String ProductCatID = "ProductCatID";
    public static final String HierarchicalRefGUID = "HierarchicalRefGUID";
    public static final int ITEM_MAX_LENGTH = 6;
    public static final String DISC_AMOUNT = "Disc Amount";


    public static final String EXTRA_ARRAY_LIST = "arrayList";
    public static final String MAXCLMDOC = "MAXCLMDOC";
    public static final String MAXREGDOC = "MAXREGDOC";
    public static final String ZDMS_SCCLM = "ZDMS_SCCLM";
    public static final String EXTRA_FROM = "comingFrom";
    public static final String DISC_PERCENTAGE = "Disc %";
    public static final String FREE_QTY = "Free Qty";
    public static final String TargetBasedID = "TargetBasedID";
    public static final String Training = "Training";
    public static final String Meeting = "Meeting";
    private static final String MC = "MC";
    public static final String B = "B";
    public static final String C = "C";
    private static final String DAYEND = "DAYEND";
    public static String ApprovalStatusID = "ApprovalStatusID";

    public static String convertDateFromString(String date) {
        String dateFinal = "";
        try {
            String[] splited = date.split("/");
            String d = splited[0];
            String m = splited[1];
            String y = splited[2];
            return y + "-" + m + "-" + d + "T00:00:00";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFinal;
    }

    public static boolean getNoItemZero() {
        String query = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                Constants.SS + "' and " + Constants.Types + " eq '" + Constants.NOITMZEROS + "' &$top=1";
        try {
            String typeValues = OfflineManager.getValueByColumnName(query, Constants.TypeValue);
            if (typeValues.equalsIgnoreCase(Constants.X)) {
                return true;
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String addZeroBeforeValue(int values, int minLenght) {
        String finalValues = "";
        try {
            if (values == 0) {
                values = values + 1;
            }
            String stringValues = values + "";
            int currentLength = stringValues.length();
            String typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SS + "' and " + Constants.Types + " eq '" + Constants.SMINVITMNO + "' &$top=1", Constants.TypeValue);
            if (typeValues.equalsIgnoreCase("10")) {
                finalValues = values + "0";
            } else if ((typeValues.equalsIgnoreCase("1")) && !getNoItemZero()) {
                if (minLenght == 6) {
                    if (currentLength == 1) {
                        finalValues = "00000" + values;
                    } else if (currentLength == 2) {
                        finalValues = "0000" + values;
                    } else if (currentLength == 3) {
                        finalValues = "000" + values;
                    } else if (currentLength == 4) {
                        finalValues = "00" + values;
                    } else if (currentLength == 5) {
                        finalValues = "0" + values;
                    } else if (currentLength == 6) {
                        finalValues = "" + values;
                    } else {
                        finalValues = values + "";
                    }
                } else {
                    finalValues = values + "";
                }
            } else {
                finalValues = values + "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalValues;
    }

    public static int getMaxImagesforWindowDis(String types) {
        int maxImage = 0;
        try {
            String stMaxValue = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SC + "' and " + Constants.Types + " eq '" + types + "' &$top=1", Constants.TypeValue);

            if (!TextUtils.isEmpty(stMaxValue))
                maxImage = Integer.parseInt(stMaxValue);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxImage;
    }

    public static void openImageInDialogBox(Context context, byte[] imageByteArray) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.img_expand);
        // set the custom dialog components - text, image and
        // button
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);

        image.setImageBitmap(BitmapFactory.decodeByteArray(imageByteArray, 0,
                imageByteArray.length));
        dialog.show();
    }

    public static String getDayConfigs() {
        String configs = "";
        String query = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                MC + "' and " + Constants.Types + " eq '" + DAYEND + "' ";
        try {
            configs = OfflineManager.getValueByColumnName(query, Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return configs;
    }

    public static boolean showImage(String pastDate) {
        try {
            if (!TextUtils.isEmpty(pastDate)) {
                String[] pastDateSplit = pastDate.split("/");
                String oldD = pastDateSplit[0];
                String oldM = pastDateSplit[1];

                Calendar oldCalender = Calendar.getInstance();
                Calendar currentCalender = Calendar.getInstance();
                oldCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(oldD));
                oldCalender.set(Calendar.MONTH, Integer.parseInt(oldM) - 1);
                Date oldDate = oldCalender.getTime();
                Date currentDate = currentCalender.getTime();
                long numberOfDays = getUnitBetweenDates(oldDate, currentDate, TimeUnit.DAYS);
                if (numberOfDays >= 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    public static int getSelectionPosition(String[][] dropDownList, String defaultValue, int pos) {
        if (dropDownList != null) {
            int dftPos = 0;
            if (dropDownList[pos].length > 0) {
                for (String values : dropDownList[pos]) {
                    if (values.equalsIgnoreCase(defaultValue)) {
                        return dftPos;
                    }
                    dftPos++;
                }
            }
        }
        return -1;
    }

    public static int getAreaOutletPosition(ArrayList<RemarkReasonBean> remarkReasonBeanArrayList, String defaultValue) {
        if (remarkReasonBeanArrayList != null) {
            int dftPos = 0;
            if (remarkReasonBeanArrayList.size() > 0) {
                for (RemarkReasonBean values : remarkReasonBeanArrayList) {
                    if (values.getReasonCode().equalsIgnoreCase(defaultValue)) {
                        return dftPos;
                    }
                    dftPos++;
                }
            }
        }
        return -1;
    }

    public static int getStringPosition(ArrayList<String> stringArrayList, String defaultValue) {
        if (stringArrayList != null) {
            int dftPos = 0;
            if (stringArrayList.size() > 0) {
                for (String values : stringArrayList) {
                    if (values.equalsIgnoreCase(defaultValue)) {
                        return dftPos;
                    }
                    dftPos++;
                }
            }
        }
        return -1;
    }

    public static void initActionBarView(AppCompatActivity mActivity, Toolbar toolbar, boolean homeUpEnabled, String title, int appIcon) {
        com.arteriatech.mutils.actionbar.ActionBarView.initActionBarView(mActivity, toolbar, homeUpEnabled, title, appIcon, 0);
    }

    public static ProgressDialog showProgressDialog(Context mContext, String message) {
        ProgressDialog pdLoadDialog = null;
        try {
            pdLoadDialog = new ProgressDialog(mContext, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(message);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdLoadDialog;
    }

    public static void displayLongToast(Context mContext, String message) {
        try {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAutomaticTimeZone(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) == 1;
//                return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static final int DATE_SETTINGS_REQUEST_CODE = 998;

    public static void showAutoDateSetDialog(final Activity mContext) {
        UtilConstants.dialogBoxWithCallBack(mContext, "", mContext.getString(R.string.autodate_change_msg), mContext.getString(R.string.autodate_change_btn), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                mContext.startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), DATE_SETTINGS_REQUEST_CODE);
            }
        });
    }

    public static String convertDateIntoDisplayFormat(Context mContext, String dateString) {
        String stringDateReturns = "";
        /*Date date = null;
        try {
            date = (new SimpleDateFormat("dd/MM/yyyy")).parse(dateString);
            stringDateReturns = (new SimpleDateFormat("dd-MMM-yyyy")).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        stringDateReturns = convertDateIntoDeviceFormat(mContext, dateString, ConstantsUtils.getConfigTypeDateFormat(mContext));
        return stringDateReturns;
    }

    public static String getConfigTypeDateFormat(Context mContext) {
        try {
            return OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" + Constants.SS + "' and " + Constants.Types + " eq '" + Constants.DATEFORMAT + "' &$top=1", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDateIntoDeviceFormat(Context context, String dateString, String configDateFormat) {
        String pattern = getDeviceDateFormat(context);
        if (!TextUtils.isEmpty(configDateFormat)) {
            pattern = configDateFormat;
        }

        SimpleDateFormat dateFormats = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date convertedDate = new Date();

        try {
            convertedDate = dateFormats.parse(dateString);
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        String dateInDeviceFormat = (String) DateFormat.format(pattern, convertedDate);
        return dateInDeviceFormat;
    }

    public static String getDeviceDateFormat(Context context) {
        java.text.DateFormat dateFormat1 = DateFormat.getDateFormat(context);
        return ((SimpleDateFormat) dateFormat1).toLocalizedPattern();
    }

    public static void onlineRequest(final Context mContext, String query, boolean isSessionRequired, int requestId, int sessionType, final OnlineODataInterface onlineODataInterface, final boolean isReqOnline) {
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, query);
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, isSessionRequired);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, requestId);
        bundle.putInt(Constants.BUNDLE_SESSION_TYPE, sessionType);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isReqOnline) {
//                    OnlineManager.requestQuery(onlineODataInterface, bundle, mContext);
                } else {
                    OfflineManager.requestQueryOffline(onlineODataInterface, bundle, mContext);
                }
            }
        }).start();
    }

    public static void selectedView(final View v, final Spinner spinner, final int position, final Context mContext) {
        v.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ((TextView) v.findViewById(R.id.tvItemValue)).setSingleLine(false);
                    if (position == spinner.getSelectedItemPosition())
                        ((TextView) v.findViewById(R.id.tvItemValue)).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showPasswordRemarksDialog(final Activity activity, final PasswordDialogCallbackInterface customDialogCallBack, String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ll_pwd_edit);

        final EditText newPassword = (EditText) dialog.findViewById(R.id.etNewPsw);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        newPassword.setFilters(new InputFilter[]{filter});
        final TextInputLayout tilRemarks = (TextInputLayout) dialog.findViewById(R.id.tilRemarks);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);
        cancleButton.setVisibility(View.GONE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(newPassword.getText().toString())) {
//                    System.out.println("Not Valid");
                    tilRemarks.setErrorEnabled(true);
                    tilRemarks.setError("Please enter password");
                } else if (!isValidPassword(newPassword.getText().toString())) {
//                    System.out.println("Not Valid");
                    tilRemarks.setErrorEnabled(true);
                    tilRemarks.setError("Password must contain mix of upper and lower case letters as well as digits and one special character(8-20)");
                } else {
                    System.out.println("Valid");
                    dialog.dismiss();
                    if (customDialogCallBack != null) {
                        customDialogCallBack.clickedStatus(true, newPassword.getText().toString());
                    }
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.clickedStatus(false, "");
                }
            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilRemarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();

    }

    public static void showPasswordRemarksDialogSetting(final Activity activity, final PasswordDialogCallbackInterface customDialogCallBack, String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ll_pwd_edit);

        final EditText newPassword = (EditText) dialog.findViewById(R.id.etNewPsw);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        newPassword.setFilters(new InputFilter[]{filter});
        final TextInputLayout tilRemarks = (TextInputLayout) dialog.findViewById(R.id.tilRemarks);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(newPassword.getText().toString())) {
//                    System.out.println("Not Valid");
                    tilRemarks.setErrorEnabled(true);
                    tilRemarks.setError("Please enter password");
                } else if (!isValidPassword(newPassword.getText().toString())) {
//                    System.out.println("Not Valid");
                    tilRemarks.setErrorEnabled(true);
                    tilRemarks.setError("Password must contain mix of upper and lower case letters as well as digits and one special character(8-20)");
                } else {
                    System.out.println("Valid");
                    dialog.dismiss();
                    if (customDialogCallBack != null) {
                        customDialogCallBack.clickedStatus(true, newPassword.getText().toString());
                    }
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.clickedStatus(false, "");
                }
            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilRemarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();

    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
        ;

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static String getPuserIdUtilsReponse(URL url, String userName, String psw) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(1000 * 30);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(1000 * 30);
            String userCredentials = userName + ":" + psw;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestProperty("Content-Type", "application/scim+json");
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readResponse(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String getPswResetUtilsReponse(URL url, String userName, String psw, String body) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        int responseCode=0;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(1000 * 30);
            connection.setConnectTimeout(1000 * 30);
            String userCredentials = userName + ":" + psw;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestProperty("Content-Type", "application/scim+json");
            connection.setRequestMethod("PUT");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.close();
            responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_BAD_REQUEST) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
                if (stream != null) {
                    result = readResponse(stream);
                }
            } else {
                stream = connection.getErrorStream();
                if (stream != null) {
                    result = readResponse(stream);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private static String readResponse(InputStream stream)
            throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder buffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }

    public static String removeZero(String str)
    {
// Count leading zeros
        int i = 0;
        while (i < str.length() && str.charAt(i) == '0')
            i++;

// Convert str into StringBuffer as Strings
// are immutable.
        StringBuffer sb = new StringBuffer(str);

// The StringBuffer replace function removes
// i characters from given index (0 here)
        sb.replace(0, i, "");

        return sb.toString(); // return in String
    }
}
