package com.arteriatech.emami.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.system.ErrnoException;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.gps.GpsTracker;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.store.OnlineRequestListeners;
import com.arteriatech.emami.alerts.AlertsListFragment;
import com.arteriatech.emami.database.EventDataSqlHelper;
import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.invoicecreate.invoicecreatesteptwo.StockBean;
import com.arteriatech.emami.mbo.BirthdaysBean;
import com.arteriatech.emami.mbo.Config;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.mbo.DmsDivQryBean;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.mbo.MyTargetsBean;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.mbo.SOTempBean;
import com.arteriatech.emami.mbo.SSOItemBean;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.notification.NotificationSetClass;
import com.arteriatech.emami.registration.RegistrationActivity;
import com.arteriatech.emami.reports.MyStockBean;
import com.arteriatech.emami.socreate.DaySummaryActivity;
import com.arteriatech.emami.socreate.SchemeCalcuBean;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.store.OnlineStoreCacheListner;
import com.arteriatech.emami.sync.SyncHist;
import com.arteriatech.emami.visit.VisitActivityBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.mobile.lib.parser.IODataEntry;
import com.sap.mobile.lib.parser.IODataSchema;
import com.sap.mobile.lib.parser.IODataServiceDocument;
import com.sap.mobile.lib.parser.Parser;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.exception.ODataContractViolationException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataNetworkException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.metadata.ODataMetadata;
import com.sap.smp.client.odata.offline.ODataOfflineException;
import com.sap.smp.client.odata.online.OnlineODataStore;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataRequestListener;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.impl.ODataRequestParamSingleDefaultImpl;
import com.sap.xscript.core.CharBuffer;
import com.sap.xscript.core.GUID;
import com.sap.xscript.core.StringFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;
import static com.arteriatech.emami.store.OfflineManager.offlineStore;

public class Constants {
    public static final String EXTRA_SO_DETAIL = "openSODetails";
    public static final String DATEFORMAT = "DATEFORMAT";
    public static final String TRIGGER_TIME = "TRIGGER_TIME";
    public static final String MESC ="MESC" ;
    public static boolean isFirstTime = false;
    public static long VALIDATE_TIME = 0;
    public static int PENDING_INTENT_SYNC_ID = 5;
    public static int SyncFreqency = 1000 * 60 * 5;
    public static int SyncStartFreqency = 1000 * 60 * 15;
    public static String USER_NAME = "";
    public static String USER_ID = "";
    public static String USER_MOBILE_NUMBER = "";
    public static String GUID_SYNC_LOG = "";
    public static String TOLLFREE_NO = "18002586789";

    public static final String isFirstTimeValidation = "isFirstTimeValidation";

    /* Map */
    public static String UserProfiles = "UserProfiles(Application='MSEC')";

    /* Odata Queries */

    public static String actualSeq = "";
    public static String QUICK_PIN = "quickpin";
    public static String ENABLE_ACCESS = "enableaccess";
    public static String QUICK_PIN_ACCESS = "pinaccess";

    public static String Last_Relese_Date = "05-07-2017 13:40";
    public static String About_Version = "3.0.0.2";
    public static EventDataSqlHelper events;

    public static String CUSTOMERNUMBER = "";
    public static String CUSTOMERNAME = "";
    public static boolean isInvoicesCountDone = false;
    public static boolean isInvoicesItemsCountDone = false;
    public static boolean isAuthDone = false;
    public static String ComingFromCreateSenarios = "";
    public static boolean isSync = false;
    public static final String LOG_TABLE = "log";
    public static final String SYNC_TABLE = "SyncTable";
    public static final String STATRTEND_TABLE = "StartEnd";
    public static final String STOCKLIST = "StockList";
    public static final String PriceList = "PriceList";
    public static final String FocusedProducts = "FocusedProducts";
    public static final String SegmentedMaterials = "SegmentedMaterials";


    public static String CollDate = "CollDate";
    public static String FISDocNo = "FISDocNo";


    public static final String CompStocks = "CompStocks";
    public static final String CompStockItemDetails = "CompStockItemDetails";
    public static final String CompMasters = "CompMasters";
    public static final ArrayList<String> matGrpArrList = new ArrayList<String>();
    public static String TotalRetalierCount = "TotalRetalierCount";


    public static SQLiteDatabase EventUserHandler;
    public static boolean devicelogflag = false;
    public static boolean Exportdbflag = false;
    public static boolean importdbflag = false;
    public static boolean FlagForUpdate = false;
    public static boolean FlagForSecurConnection = false;
    public static MSFAApplication mApplication = null;
    public static boolean FlagForSyncAllUpdate = false;
    public static boolean FlagErrorLogAllSync = false;

    public static boolean ExportDataVaultflag = false;
    public static boolean ImportDataVaultflag = false;

    //------>This id our testing purpose added based on route plan approval all levels(12-08-2015)
    public static final String LOGIN_ID_NAME = "userLevel";
    public static final String PREFS_NAME = "mSFAPreference";
    public static final String AUTH_NAME = "Auth";
    public static String DATABASE_NAME = "mSFASecSales.db";
    public static String DATABASE_REGISTRATION_TABLE = "registrationtable";
    public static String APPS_NAME = "mSFASecSales";
    public static String AppName_Key = "AppName";
    public static String UserName_Key = "username";
    public static String Password_Key = "password";


    public static String serverHost_key = "serverHost";
    public static String serverPort_key = "serverPort";
    public static String serverClient_key = "serverClient";
    public static String companyid_key = "companyid";
    public static String securityConfig_key = "securityConfig";
    public static String appConnID_key = "appConnID";
    public static String appID_key = "appID";
    public static String YES = "YES";
    public static String SegmentId = "SegmentId";
    public static String SegmentDesc = "SegmentDesc";
    public static String MustSellId = "01";


    public static String Others = "Others";
    public static String EncryptKey = "welcome1";
    public static String Conv_Mode_Type_Other = "0000000001";

    public static String collections[] = null;
    public static Parser parser = null;
    public static IODataServiceDocument serviceDocument = null;
    public static IODataSchema schema = null;
    public static List<IODataEntry> entries = null;
    public static String Table[] = null;
    public static String clumsName[] = null;
    public static String serviceDoc = null;
    public static final String SO_ORDER_HEADER = "SalesOrders";
    public static String cookies = "";
    public static String metaDoc = null;
    public static String x_csrf_token = "";
    public static final String COMPETITORSTOCK = "CompetitorStock";
    public static final String CompetitorStocks = "CompetitorStocks";
    public static final String PRICINGLISTTABLE = "PricingList";
    public static final String INCENTIVETRACKINGTABLE = "IncentiveTracking";
    public static final String MaterialRgb = "RegularShades";
    public static final String upcomingShades = "upcomingShades";
    // public static int INDEX_TEMP_NEW[] = null;
    public final static String PROPERTY_APPLICATION_ID = "d:ApplicationConnectionId";
    public static String ABOUTVERSION = "3.0";
    public static String ABOUTDATE = "Nov 13,2015, 23:59:00";
    public static int autoduration = 2;
    public static String USERTYPE = "T";
    public static String CollAmount = "";
    public static String SyncTime = "11";
    public static boolean iSAutoSync = false;
    public static int autoSyncDur = 60;
    public static boolean crashlogflag = false;
    public static double MaterialUnitPrice = 0.0, MaterialNetAmount = 0.0, InvoiceTotalAmount = 0.0, InvoiceUnitPrice = 0.0;
    public static String Customers = "Customers";
    public static ArrayList<InvoiceBean> alTempInvoiceList = new ArrayList<>();

    public static Hashtable<String, ArrayList<InvoiceBean>> HashTableSerialNoSelection = new Hashtable<String, ArrayList<InvoiceBean>>();
    public static String InvoicePaymentModeID = "InvoicePaymentModeID";
    public static String ReferenceTypeID = "ReferenceTypeID";
    public static String ReferenceTypeDesc = "ReferenceTypeDesc";
    public static String Name = "Name";
    public static String CPUID = "CPUID";
    public static String BankName = "BankName";
    public static String Fresh = "Fresh";
    public static String ReturnOrders = "ReturnOrders";
    public static String SoldToTypDesc = "SoldToTypDesc";
    public static String EXTRA_SSRO_GUID = "extraSSROguid";
    public static String EXTRA_TAB_POS = "extraTabPos";
    public static String EXTRA_ORDER_DATE = "extraDate";
    public static String EXTRA_ORDER_IDS = "extraIDS";
    public static String EXTRA_ORDER_AMOUNT = "extraAmount";
    public static String EXTRA_ORDER_SATUS = "extraStatus";
    public static String EXTRA_ORDER_CURRENCY = "extraCurrency";
    public static String EXTRA_SCHEME_GUID = "extraSchemeGUID";
    public static String EXTRA_SCHEME_NAME = "extraSchemeName";
    public static String EXTRA_SCHEME_IS_SECONDTIME = "isSecondTime";
    public static String EXTRA_SCHEME_TYPE_ID = "extraSchemeTypeIds";
    public static String EXTRA_INVOICE_DATE = "invoiceDate";
    public static String EXTRA_SCHEME_ID = "schemeIds";
    public static String EXTRA_ARRAY_LIST = "arrayList";
    public static String EXTRA_TEMP_STATUS = "TempStatus";

    public static int RETURN_ORDER_POS = 1;
    public static int SSS_ORDER_POS = 2;
    public static int COMPLAINTS_ORDER_POS = 3;
    public static int TAB_POS_1 = 1;
    public static int TAB_POS_2 = 2;
    public static final int TAKE_PICTURE = 190;
    public static String TotalQty = "TotalQty";
    public static String HLPLNE = "HLPLNE";
    public static String MSEC = "MSEC";
    public static String SC = "SC";
    public static String SS = "SS";
    public static String SSCP = "SSCP";
    public static String SSROUT = "SSROUT";
    public static String MAXEXPALWD = "MAXEXPALWD";
    public static String MAXEXPALWM = "MAXEXPALWM";
    public static String SF = "SF";
    public static String HLPLNEPHN = "HLPLNEPHN";
    public static String WDSPINVDTR = "WDSPINVDTR";
    public static String NOITMZEROS = "NOITMZEROS";
    public static String SMINVITMNO = "SMINVITMNO";
    public static String CPDOBUPD = "CPDOBUPD";
    public static String CPANNVUPD = "CPANNVUPD";
    public static String GEOLOCUPD = "GEOLOCUPD";
    public static String DISPGEO = "DISPGEO";
    public static String Evaluation = "Evaluation";
    public static String Desc = "Desc";
    public static String TotalValue = "TotalValue";
    public static String ITEMSSerialNo = "ITEMSSerialNo";
    public static String Status = "Status";
    public static String InvoiceItemGUID = "InvoiceItemGUID";
    public static String LoginID = "LoginID";
    public static String ShipToID = "ShipToID";
    public static String InvoiceQty = "InvoiceQty";
    public static String Feedback = "Feedback";
    public static String FeedBackGuid = "FeedBackGuid";
    public static String FeedbackDesc = "FeedbackDesc";
    public static String BtsID = "BtsID";
    public static String Competitor_Information = "Competitor Information";
    public static String SubmittingCompetitorInfosmsg = "Submitting Competitor Info, please wait";
    public static String CurrentInvoice = "CurrentInvoice";
    public static String GA = "GA";
    public static String H_FRC = "H.FRC";
    public static String H_SRC = "H.SRC";
    public static String Earning_per_Month = "Earning per Month";
    public static String Territory = "Territory";
    public static String strITEMS = "ITEMS";
    public static String UpdateList = "UpdateList";
    public static String ResourcePath = "ResourcePath";
    public static String Invoices = "Invoices";
    public static String DeviceInvoices = "Device Invoices";
    public static String Error_in_Competitor_information = "Error in Competitor information : ";
    public static String Secondary_Invoice = "Secondary Invoice";
    public static String Secondary_Invoice_Type = "05";
    public static String Code = "Code";
    public static String ROList = "ROList";
    public static String BatchOrSerial = "BatchOrSerial";
    public static String EXTRA_OPEN_NOTIFICATION = "openNotification";
    public static String RatioSchNum = "RatioSchNum";
    public static String RatioSchDen = "RatioSchDen";
    public static String FreeMaterialNo = "FreeMaterialNo";
   /* public static String TypeSetCPOLSZ="CPOLSZ";
    public static String TypeSetCPOLSP="CPOLSP";
    public static String TypeSetCPOLLC="CPOLLC";*/


    public static String BeatGuid = "BeatGuid";
    public static String RschGUID = "RschGUID";
    public static String RouteGUID = "RouteGUID";
    public static String RschDesc = "RschDesc";
    public static String InvoiceItemList = "InvoiceItemList";
    public static String SyncLogInsert = "SyncLogInsert";
    public static String SyncLogInserts = "SyncLogInserts";
    public static String Guid = "Guid";
    public static String Userid = "Userid";
    public static String SyncType = "SyncType";
    public static String SyncTypeID = "";
    public static String AlertAt = "AlertAt";
    public static String AlertAtID = "";
    public static Calendar TimeDifference1 = null;
    public static Calendar TimeDifference2 = null;
    public static final String CURRENT_VERSION_CODE = "currentVersionCode";

    public static int NewDefingRequestVersion = 95;

    public static String[] getDefinigReq(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginId = sharedPreferences.getString("username", "");
        if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) <= 93)
        {
            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", Customers,
                    "SSInvoices",
                    "SSInvoiceItemDetails",
                    //"MustSells",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems,
                    CompetitorMasters, CompetitorInfos, TEXT_CATEGORY_SET,
                    "Visits", "Attendances", "VisitActivities",
                    OutstandingInvoices, OutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",


                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages",
//                "CPPartnerFunctions",
                    "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
//               "Alerts?$filter=Application eq 'MSEC'",
                            "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                    Claims,
                    ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27",
                    "SSSOs", "SSSOItemDetails",
                    "SSROs", "SSROItemDetails",
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'",
                     "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                   // "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO'",
                    //adding DISTNC typeset,for geofencing only in quality not in production
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'DISTNC' or Typeset eq 'NEWPRD'",

                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
//                      ")"};
                            ") and LoginID eq '" + loginId.toUpperCase() + "'"
            };


            return DEFINGREQARRAY;
        }
        else if(sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) <= 94)
        {
            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", Customers,
                    "SSInvoices", "CPMarketSet", "CPBusinessSet", CompetitorSales,
                    "SSInvoiceItemDetails",
                    //"MustSells",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems,
                    CompetitorMasters, CompetitorInfos, TEXT_CATEGORY_SET,
                    "Visits", "Attendances", "VisitActivities",
                    OutstandingInvoices, OutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages",
//                "CPPartnerFunctions",
                    "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
//               "Alerts?$filter=Application eq 'MSEC'",
                            "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                    Claims,
                    ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27", "UserProfiles(Application='MSEC')",
                    "SSSOs", "SSSOItemDetails",
                    "SSROs", "SSROItemDetails",
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'",
                    "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                   // "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'DISTNC'",
                    //adding DISTNC typeset,for geofencing only in quality not in production.
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'DISTNC' or Typeset eq 'NEWPRD'",
                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0' or Typeset eq 'SEQDEV'  or Typeset eq 'CPOLSZ' or Typeset eq 'CPOLSP' or Typeset eq 'CPOLLC' or Typeset eq 'ALLBSN'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
//                      ")"};
                            ") and LoginID eq '" + loginId.toUpperCase() + "'"};


            return DEFINGREQARRAY;
        }
        else/* if(sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) <= 94)*/
        {

            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", Customers,
                    "SSInvoices", "CPMarketSet", "CPBusinessSet", CompetitorSales,
                    "SSInvoiceItemDetails",
                    //"MustSells",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules",
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems,
                    CompetitorMasters, CompetitorInfos, TEXT_CATEGORY_SET,
                    "Visits", "Attendances", "VisitActivities",
                    OutstandingInvoices, OutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages",
//                "CPPartnerFunctions",
                    "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
//               "Alerts?$filter=Application eq 'MSEC'",
                            "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                  //  Claims,
                  //  ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27", "UserProfiles(Application='MSEC')",
                    "SSSOs", "SSSOItemDetails",
                    "SSROs", "SSROItemDetails",
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'",
                    "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                   // "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO'",
                    //adding DISTNC typeset,for geofencing only in quality not in production.
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'DISTNC' or Typeset eq 'NEWPRD'",
                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0' or Typeset eq 'SEQDEV'  or Typeset eq 'CPOLSZ' or Typeset eq 'CPOLSP' or Typeset eq 'CPOLLC' or Typeset eq 'ALLBSN'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
//                      ")"};
                            ") and LoginID eq '" + loginId.toUpperCase() + "'"};


            return DEFINGREQARRAY;

        }
    }

//    ----->Emami smp DEV,QAS and PRD
//	public static String APP_ID = "com.arteriatech.mSecSales";

    //for ID4/DEV
    public static final String OutstandingInvoices = "SSOutstandingInvoices";
    public static final String OutstandingInvoiceItemDetails = "SSOutstandingInvoiceItemDetails";
    public static final String OutstandingInvoiceItems = "SSOutstandingInvoiceItems";

    //for emami DEV
//	public static final String OutstandingInvoices = "OutstandingInvoices";
//	public static final String OutstandingInvoiceItemDetails = "OutstandingInvoiceItemDetails";
//	public static final String OutstandingInvoiceItems = "OutstandingInvoiceItems";

    //ID4`
    public static String FeedbackEntity = ".Feedback";
    public static String VISITACTIVITYENTITY = ".VisitActivity";
    public static String FeedbackItemDetailEntity = ".FeedbackItemDetail";
    public static String VISITENTITY = ".Visit";
    public static String ATTENDANCEENTITY = ".Attendance";
    public static String MERCHINDISINGENTITY = ".MerchReview";
    public static String MERCHINDISINGITEMENTITY = ".MerchReviewImage";
    public static String ChannelPartnerEntity = ".ChannelPartner";
    public static String CustomerEntity = ".Customer";
    public static String SyncLogInsertEntity = ".SyncLogInsert";
    public static String CPDMSDivisionEntity = ".CPDMSDivision";
    public static String InvoiceEntity = ".SSInvoice";
    public static String InvoiceItemEntity = ".SSInvoiceItemDetail";
    public static String InvoiceSerialNoEntity = ".SSInvoiceItemSerialNo";
    public static String FinancialPostingsEntity = ".FinancialPosting";
    public static String CPMarketEntity = ".CPMarket";
    public static String FinancialPostingsItemEntity = ".FinancialPostingItemDetail";
    public static String CPBusinessItemEntity = ".CPBusiness";
    public static String CompetitorSaleEntity = ".CompetitorSale";
    public static String CompetitorInfoEntity = ".CompetitorInfo";
    public static String SPStockSNosEntity = ".SPStockItemSNo";
    public static String CPStockItemEntity = ".CPStockItem";
    public static String ComplaintEntity = ".Complaint";
    public static String AlertEntity = ".Alert";
    public static String SchemeCPsEntity = ".SchemeCP";
    public static String ClaimsEntity = ".Claim";
    public static String ClaimItemDetailsEntity = ".ClaimItemDetail";
    public static String ReturnOrderEntity = ".SSRO";
    public static String ReturnOrderItemEntity = ".SSROItemDetail";
    public static String SalesOrderEntity = ".SSSO";
    public static String SalesOrderItemEntity = ".SSSOItemDetail";
    public static String ExpenseEntity = ".Expense";
    public static String ExpenseItemEntity = ".ExpenseItemDetail";
    public static String ExpenseItemDocumentEntity = ".ExpenseItemDetail_ExpenseDocuments";

    public static String NO_OF_DAYS = "0";
    public static final String STORE_NAME = "mSecSales_Offline";
    public static final String STORE_NAME_MustSell = "mSecSales_OfflineMustSell";

    public static final String backupDBPath = "mSecSales_Offline.udb";

    public static final String backuprqDBPath = "mSecSales_Offline.rq.udb";
    public static boolean mBoolIsReqResAval = false;
    public static boolean mBoolIsNetWorkNotAval = false;

    public static String SalesPersons = "SalesPersons";
    public static String CPSPRelations = "CPSPRelations";
    public static final String CustomerNo = "CustomerNo";
    public static final String CustomerName = "CustomerName";
    public static final String Street = "Street";
    public static final String City = "City";
    public static final String MobileNumber = "MobileNumber";
    public static final String MailId = "MailId";
    public static final String CustDOB = "CustDOB";
    //	public static final String Anniversary = "Anniversary";
    public static final String SpouseDOB = "SpouseDOB";
    public static final String Child1DOB = "Child1DOB";
    public static final String Child2DOB = "Child2DOB";
    public static final String Child3DOB = "Child3DOB";
    public static final String Child1Name = "Child1Name";
    public static final String Child2Name = "Child2Name";
    public static final String Child3Name = "Child3Name";


    public static final String DbStock = "DbStock";
    public static final String ItemCatID = "ItemCatID";
    public static final String MaterialNo = "MaterialNo";
    public static final String MatGrpDesc = "MatGrpDesc";
    public static final String UspMustSell = "UspMustSell";
    public static final String UspFocused = "UspFocused";
    public static final String UspNew = "UspNew";
    public static final String UspDesc = "UspDesc";
    public static final String MaterialDesc = "MaterialDesc";
    public static final String MaterialGroup = "MaterialGroup";
    public static final String MaterialGrpDesc = "MaterialGrpDesc";
    public static final String OrdMatGrp = "OrdMatGrp";
    public static final String DevCollAmount = "DevCollAmount";
    public static String InvoiceNo = "InvoiceNo";
    public static String InvoiceTypeID = "InvoiceTypeID";
    public static String UnitPrice = "UnitPrice";
    public static String IntermUnitPrice = "IntermUnitPrice";
    public static String NetAmount = "NetAmount";
    public static String CollectionAmount = "CollectionAmount";
    public static String ShipToName = "ShipToName";
    public static String GrossAmount = "GrossAmount";
    public static String InvoiceTypeDesc = "InvoiceTypeDesc";
    public static String Tax = "Tax";
    public static String DiscountPer = "DiscountPer";
    public static String A = "A";
    public static String TransRefTypeID = "TransRefTypeID";
    public static String TransRefNo = "TransRefNo";
    public static String TransRefItemNo = "TransRefItemNo";
    public static String AlternativeUOM1Num = "AlternativeUOM1Num";
    public static String AlternativeUOM1Den = "AlternativeUOM1Den";

    public static final String RouteSchedules = "RouteSchedules";
    public static final String RouteSchedulePlans = "RouteSchedulePlans";

    public static String InvoiceNumber = "";
    public static String FIPDocumentNumber = "";
    public static ODataGuid VisitActivityRefID = null;

    public static String CompetitorName = "CompName";
    public static String CompetitorGUID = "CompGUID";
    public static String PerformanceTypeID = "PerformanceTypeID";

    public static String AttendanceTypeH1 = "AttendanceTypeH1";
    public static String AttendanceTypeH2 = "AttendanceTypeH2";
    public static String AutoClosed = "AutoClosed";


    public static String Material_No = "MaterialNo";
    public static String Material_Desc = "MaterialDesc";
    public static String BaseUom = "BaseUom";

    public static String SPStockItemGUID = "SPStockItemGUID";
    public static String SPSNoGUID = "SPSNoGUID";
    public static String SerialNoTo = "SerialNoTo";
    public static String SerialNoFrom = "SerialNoFrom";
    public static String Option = "Option";
    public static String StockTypeID = "StockTypeID";
    public static String QAQty = "QAQty";
    public static String UnrestrictedQty = "UnrestrictedQty";
    public static String BlockedQty = "BlockedQty";
    public static String PrefixLength = "PrefixLength";
    public static String Zzindicator = "Zzindicator";

    public static String EvaluationTypeID = "EvaluationTypeID";
    public static String ReportOnID = "ReportOnID";

    public static String QtyTarget = "QtyTarget";
    public static String QtyLMTD = "QtyLMTD";
    public static String QtyMTD = "QtyMTD";
    public static String QtyMonthlyGrowth = "QtyMonthlyGrowth";
    public static String QtyMonth1PrevPerf = "QtyMonth1PrevPerf";
    public static String QtyMonth2PrevPerf = "QtyMonth2PrevPerf";
    public static String QtyMonth3PrevPerf = "QtyMonth3PrevPerf";
    public static final String OtherRouteList = "OtherRouteList";

    public static String AmtTarget = "AmtTarget";
    public static String AmtLMTD = "AmtLMTD";
    public static String AmtMTD = "AmtMTD";
    public static String AmtMonth1PrevPerf = "AmtMonth1PrevPerf";
    public static String AmtMonthlyGrowth = "AmtMonthlyGrowth";
    public static String AmtMonth2PrevPerf = "AmtMonth2PrevPerf";
    public static String AmtMonth3PrevPerf = "AmtMonth3PrevPerf";
    public static String PerformanceOnID = "PerformanceOnID";
    public static String PerformanceGUID = "PerformanceGUID";
    public static String ComplaintCategory = "ComplaintCategory";
    public static String ComplaintType = "ComplaintType";
    public static String AmtLastYearMTD = "AmtLastYearMTD";
    public static String QtyLastYearMTD = "QtyLastYearMTD";

    public static double RCVStockValueDouble = 0.0;
    public static double SIMStockValue = 0.0;

    public static String StockValue = "StockValue";
    public static String CPStockItemGUID = "CPStockItemGUID";
    public static String CPSnoGUID = "CPSnoGUID";

    public static final String Feature = "Feature";
    public static final String TYPE = "Type";
    public static final String VALUE = "Value";
    public static final String DESCRIPTION = "Description";
    public static final String EntityType = "EntityType";
    public static final String IsDefault = "IsDefault";
    public static final String CPGroup4 = "CPGroup4";
    public static final String SchemeSalesArea = "SchemeSalesArea";
    public static final String WeeklyOff = "WeeklyOff";
    public static final String ChannelPartner = "ChannelPartner";
    public static final String TaxRegStatus = "TaxRegStatus";


    public static final String AppntRmnDur = "AppntRmnDur";
    public static final String PropName = "PropName";
    public static final String ID = "ID";
    public static final String ExpenseFreq = "ExpenseFreq";
    public static final String ExpenseDaily = "000010";
    public static final String ExpenseMonthly = "000030";
    public static final String ExpenseType = "ExpenseType";
    public static final String ExpenseTypeDesc = "ExpenseTypeDesc";
    public static final String ExpenseItemType = "ExpenseItemType";
    public static final String ExpenseItemTypeDesc = "ExpenseItemTypeDesc";
    public static final String ExpenseFreqDesc = "ExpenseFreqDesc";
    public static final String ExpenseItemCat = "ExpenseItemCat";
    public static final String ExpenseItemCatDesc = "ExpenseItemCatDesc";
    public static final String DefaultItemCat = "DefaultItemCat";
    public static final String DefaultItemCatDesc = "DefaultItemCatDesc";
    public static final String AmountCategory = "AmountCategory";
    public static final String AmountCategoryDesc = "AmountCategoryDesc";
    public static final String MaxAllowancePer = "MaxAllowancePer";
    public static final String ExpenseQuantityUom = "ExpenseQuantityUom";
    public static final String ItemFieldSet = "ItemFieldSet";
    public static final String ItemFieldSetDesc = "ItemFieldSetDesc";
    public static final String Allowance = "Allowance";
    public static final String IsSupportDocReq = "IsSupportDocReq";
    public static final String IsRemarksReq = "IsRemarksReq";
    public static final String ExpenseGUID = "ExpenseGUID";
    public static final String FiscalYear = "FiscalYear";
    public static final String ExpenseNo = "ExpenseNo";
    public static final String ExpenseDate = "ExpenseDate";
    public static final String ExpenseItemGUID = "ExpenseItemGUID";
    public static final String ExpeseItemNo = "ExpeseItemNo";
    public static final String BeatGUID = "BeatGUID";
    public static final String ConvenyanceMode = "ConvenyanceMode";
    public static final String ConvenyanceModeDs = "ConvenyanceModeDs";
    public static final String Distance = "Distance";
    public static final String BeatDistance = "BeatDistance";
    public static final String ConveyanceAmt = "ConveyanceAmt";
    public static final String ExpenseDocumentID = "ExpenseDocumentID";
    public static final String DocumentTypeID = "DocumentTypeID";
    public static final String DocumentTypeDesc = "DocumentTypeDesc";
    public static final String DocumentStatusID = "DocumentStatusID";
    public static final String DocumentStatusDesc = "DocumentStatusDesc";
    public static final String DocumentMimeType = "DocumentMimeType";
    public static final String DocumentSize = "DocumentSize";
    public static final String RefDocGUID = "RefDocGUID";
    public static final String RefDocItmGUID = "RefDocItmGUID";

    public static final String MimeTypePDF = "application/pdf";
    public static final String MimeTypeMP4 = "application/octet-stream";
    public static final String MimeTypePPT = "application/ppt";
    public static final String MimeTypeDocx = "application/docx";
    public static final String MimeTypevndmspowerpoint = "application/vnd.ms-powerpoint";
    public static final String MimeTypeMsword = "application/msword";
    public static final String MimeTypePng = "image/png";
    public static final String MimeTypeJpg = "image/jpg";
    public static final String MimeTypeJpeg = "image/jpeg";


    public static final String TypeValue = "TypeValue";
    public static final String Typeset = "Typeset";
    public static final String Types = "Types";
    public static final String Type = "Type";
    public static final String Typesname = "Typesname";
    public static final String PROP_ATTTYP = "ATTTYP";
    public static final String PROP_ACTTYP = "ACTTYP";
    public static final String SCHEMECP_DOCUMENT_TYPE_ID = "ZDMS_SCREG";

    public static String RschGuid = "RschGuid";
    public static String RouteSchGUID = "RouteSchGUID";
    public static String VisitCPGUID = "VisitCPGUID";
    public static String SalesPersonID = "SalesPersonID";
    public static String ShortName = "ShortName";
    public static String RoutId = "RoutId";
    public static String SequenceNo = "SequenceNo";
    public static String DayOfWeek = "DayOfWeek";
    public static String DayOfMonth = "DayOfMonth";


    public static String MSTSELREQ = "MSTSELREQ";
    public static String SSSO = "SSSO";


    public static String DOW = "DOW";
    public static String DOM = "DOM";
    public static String RoutSchScope = "RoutSchScope";
    public static String RoutSchScopeRetailer = "000001";
    public static final String SSSOs = "SSSOs";
    public static final String SSROs = "SSROs";
    public static final String ALERTS = "Alerts";

    public static final String SSROItemDetails = "SSROItemDetails";
    public static final String SSSoItemDetails = "SSSOItemDetails";
    public static final String Complaints = "Complaints";
    public static final String ConfigTypsetTypeValues = "ConfigTypsetTypeValues";
    public static final String ConfigTypesetTypes = "ConfigTypesetTypes";
    public static final String RoutePlans = "RoutePlans";
    public static final String TypeSetSEQDEV = "ConfigTypesetTypes?$filter=Typeset eq 'SEQDEV'";
    public static final String TypeSetCPOLSZ = "ConfigTypesetTypes?$filter=Typeset eq 'CPOLSZ'";
    public static final String TypeSetCPOLSP = "ConfigTypesetTypes?$filter=Typeset eq 'CPOLSP'";
    public static final String TypeSetCPOLLC = "ConfigTypesetTypes?$filter=Typeset eq 'CPOLLC'";
    public static final String TypeSetALLBSN = "ConfigTypesetTypes?$filter=Typeset eq 'ALLBSN'";

    public static int selectedIndex;
    public static String customerName = "";

    public static OnlineODataStore onlineStore = null;
    public static OnlineODataStore onlineStoreSyncLog = null;
    public static OnlineODataStore onlineStoreMustCell = null;


    public static ArrayList<String> selectedPositionsDemon = new ArrayList<String>();
    public static ArrayList<String> selectedPositionsProm = new ArrayList<String>();
    public static HashMap<String, String> selectedMatGrpStatusDemon = new HashMap<String, String>();
    public static HashMap<String, String> selectedMatGrpStatusPrompt = new HashMap<String, String>();

    public static String collectionName[] = null;
    public static boolean isCustContactLists;
    public static boolean isCustomerLists;
    public static boolean FlagForSyncError = false;
    public static String resSO = "";
    public static String reqSO = "";


    public static Hashtable<String, ArrayList<InvoiceBean>> convertToMapArryList(String jsonString) {
        Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos = null;
        try {
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<Hashtable<String, ArrayList<InvoiceBean>>>() {
            }.getType();
            hashTableItemSerialNos = gson.fromJson(jsonString, stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashTableItemSerialNos;
    }

    public static ArrayList<BirthdaysBean> convertToBirthDayArryList(String jsonString) {
        ArrayList<BirthdaysBean> alBirthDayList = null;
        try {
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<ArrayList<BirthdaysBean>>() {
            }.getType();
            alBirthDayList = gson.fromJson(jsonString, stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alBirthDayList;
    }

    public static void customAlertMessage(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyTheme);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();


                    }
                });

        builder.show();
    }

    public static String removeLeadingZero(String value) {
        String textReturn = "";
        try {
            if (!value.equalsIgnoreCase("") && value != null) {
                textReturn = removeLeadingZero1(new BigDecimal(value));

            } else {
                textReturn = "0.00";
            }
        } catch (Exception e) {
            textReturn = "0.00";
            e.printStackTrace();
        }
        return textReturn;
    }

    public static String removeLeadingZero1(BigDecimal number) {
        // for your case use this pattern -> #,##0.00
        DecimalFormat df = new DecimalFormat("#####0.00");
        return df.format(number);
    }

    public static HashMap<String, Object> MapEntityVal = new HashMap<String, Object>();
    public static HashMap<String, Object> HashMapEntityVal = new HashMap<String, Object>();

    public static String COLLECTIONHDRS = "CollectionHdrs";
    public static String COLLECTIONITEMS = "CollectionItems";
    public static String OPEN_INVOICE_LIST = "OpenInvList";
    public static String INVOICES = "Invoices";
    public static String VISITACTIVITIES = "VisitActivities";
    public static String INVOICESSERIALNUMS = "InvoiceItmSerNumList";


    public static String ENDLONGITUDE = "EndLongitude";
    public static String REMARKS = "Remarks";
    public static String VISITKEY = "VisitGUID";
    public static String ROUTEPLANKEY = "RoutePlanGUID";
    public static String LOGINID = "LoginID";
    public static String DATE = "Date";
    public static String VISITTYPE = "VisitType";
    public static String CUSTOMERNO = "CustomerNo";
    public static String STATUS = "Status";
    public static String REASON = "Reason";
    public static String STARTDATE = "StartDate";
    public static String STARTTIME = "StartTime";
    public static String STARTLATITUDE = "StartLatitude";
    public static String STARTLONGITUDE = "StartLongitude";
    public static String ENDTIME = "EndTime";
    public static String ENDDATE = "EndDate";
    public static String ENDLATITUDE = "EndLatitude";
    public static String ETAG = "ETAG";

    public static String VisitActivityGUID = "VisitActivityGUID";
    public static String VisitGUID = "VisitGUID";
    public static String ActivityType = "ActivityType";
    public static String ActivityTypeDesc = "ActivityTypeDesc";
    public static String ActivityRefID = "ActivityRefID";


    public static String Validity = "Validity";
    public static String Benefits = "Benefits";
    public static String Price = "Price";

    public static String ItemNo = "ItemNo";
    public static String SchemeDesc = "SchemeDesc";
    public static String SchemeGuid = "SchemeGuid";

    public static String ReviewDate = "ReviewDate";

    public static String FromCPTypeID = "FromCPTypeID";
    public static String CPTypeID = "CPTypeID";
    public static String SPGuid = "SPGuid";

    public static final String VisitCatID = "VisitCatID";
    public static final String NoOfOutlet = "NoOfOutlet";
    public static final String AdhocVisitCatID = "02";
    public static final String BeatVisitCatID = "01";
    public static final String OtherBeatVisitCatID = "02";

    public static String StockOwner = "StockOwner";
    public static String ParentType = "ParentType";

    public static String SoldToCPGUID = "SoldToCPGUID";
    public static String ShipToCPGUID = "ShipToCPGUID";
    public static String SoldToTypeID = "SoldToTypeID";
    public static String SoldToTypeDesc = "SoldToTypeDesc";
    public static String ShipToTypeID = "ShipToTypeID";
    public static String spName = "spName";
    public static String spNo = "spNo";
    public static String BillTo = "BillTo";

    public static String BillToID = "BillToID";
    public static String BillToCPGUID = "BillToCPGUID";
    public static String BillToType = "BillToType";

    public static String CPName = "CPName";
    public static String Address1 = "Address1";
    public static String CountryID = "CountryID";
    //	public static String Country = "Country";
    public static String BTSCircle = "BTSCircle";


//	public static String ParentTypeID = "ParentTypeID";


    public static String DistrictDesc = "DistrictDesc";
    public static String CityDesc = "CityDesc";
    public static String CityID = "CityID";
    public static String DistrictID = "DistrictID";
    public static String VisitNavigationFrom = "";
    public static String Time_Value = "T00:00:00";

    public static String BirthDayAlertsTempKey = "BirthDayAlertsTempKey";
    public static String AlertsTempKey = "AlertsTempKey";


    public static String BirthDayAlertsKey = "BirthDayAlertsKey";
    public static String BirthDayAlertsDate = "BirthDayAlertsDate";
    public static String AlertsDataKey = "AlertsDataKey";

    public static String DBStockKey = "DBStockKey";
    public static String DBStockKeyDate = "DBStockKeyDate";
    public static String District = "District";
    public static String CountryDesc = "CountryDesc";
    public static String StateID = "StateID";
    public static String Landmark = "Landmark";
    public static String PostalCode = "PostalCode";
    public static String SalesPersonMobileNo = "MobileNo";
    public static String MobileNo = "Mobile1";
    public static String GSTIN = "GSTIN";
    public static String CPMobileNo = "CPMobileNo";
    public static String EmailID = "EmailID";
    public static String Fax = "Fax";
    public static String DOB = "DOB";
    public static String PAN = "PAN";
    public static String PANRefNo = "PANRefNo";
    public static String ServiceTaxRegNo = "ServiceTaxRegNo";
    public static String VATNo = "VATNo";
    public static String TIN = "TIN";
    public static String OwnerName = "OwnerName";
    public static String OutletName = "Name";
    public static String TitleID = "TitleID";
    public static String TitleDesc = "TitleDesc";
    public static String RetailerProfile = "Group1";
    public static String Group2 = "Group2";
    public static String Group1 = "Group1";
    public static String Latitude = "Latitude";
    public static String Longitude = "Longitude";
    public static String OutletSizeID = "OutletSizeId";
    public static String OutletSizeDesc = "OutletSizeDesc";
    public static String OutletShapeId = "OutletShapeId";
    public static String OutletShapeDesc = "OutletShapeDesc";
    public static String NoOfEmployee = "NoOfEmployee";
    public static String IsHomeDeliveryAvl = "IsHomedlvryAvl";
    public static String IsPhOrderAvl = "IsPhOrderAvl";
    public static String IsCompBillAvl = "IsCompBillAvl";
    public static String NoOfCounters = "NoOfCounters";
    public static String OutletLocId = "OutletLocId";
    public static String OutletLocDesc = "OutletLocDesc";
    public static String IsEduInstNrby = "IsEduInstNrby";
    public static String IsHsptlNearBy = "IsHsptlNearBy";
    public static String NoOfWindowDisp = "NoOfWindowDisp";
    public static String IsSmartPhAvl = "IsSmartPhAvl";
    public static String OpeningTime = "OpeningTime";
    public static String ClosingTime = "ClosingTime";
    public static String LunchTime = "LunchTime";
    public static String SetResourcePath = "SetResourcePath";
    public static String PartnerMgrGUID = "PartnerMgrGUID";
    public static String OtherCustGuid = "OtherCustGuid";
    public static String IsLatLongUpdate = "IsLatLongUpdate";
    public static String CPGUID32 = "CPGUID32";
    public static String CPGUID = "CPGUID";
    public static String CPSPGUID = "CPSPGUID";
    public static String CPGuid = "CPGuid";
    public static String SalesArea = "SalesArea";
    public static String ECCNo = "ECCNo";
    public static String CSTNo = "CSTNo";
    public static String LSTNo = "LSTNo";
    public static String ExciseRegNo = "ExciseRegNo";
    public static String CP1GUID = "CP1GUID";
    public static String Pref1 = "Pref1";
    public static String Pref2 = "Pref2";
    public static String Pref3 = "Pref3";

    public static String GoodsIssueFromID = "GoodsIssueFromID";
    public static String AccountGrp = "AccountGrp";
    public static String Anniversary = "Anniversary";
    public static String ApprovedAt = "ApprovedAt";
    public static String ApprovedBy = "ApprovedBy";
    public static String ApprovedOn = "ApprovedOn";
    public static String ApprvlStatusDesc = "ApprvlStatusDesc";
    public static String ApprvlStatusID = "ApprvlStatusID";
    public static String ApprovalStatus = "ApprovalStatus";
    public static String ApprovalStatusID = "ApprovalStatus";
    public static String ChangedAt = "ChangedAt";
    public static String ChangedOn = "ChangedOn";
    public static String Country = "Country";
    public static String CountryName = "CountryName";
    public static String CPStock = "CPStock";
    public static String CPTypeDesc = "CPTypeDesc";
    public static String EvaluationTypeDesc = "EvaluationTypeDesc";
    public static String CreatedAt = "CreatedAt";
    public static String CreditDays = "CreditDays";
    public static String CreditBills = "CreditBills";
    public static String CreditLimit = "CreditLimit";
    public static String CreditExposure = "CreditExposure";
    public static String CreditLimitUsed = "CreditLimitUsed";
    public static String AnnualSales = "AnnualSales";
    public static String AnnualSalesYear = "AnnualSalesYear";
    public static String Group1Desc = "Group1Desc";
    public static String Group2Desc = "Group2Desc";
    public static String Group3 = "Group3";
    public static String Group3Desc = "Group3Desc";
    public static String Group4 = "Group4";
    public static String Group5 = "Group5";
    public static String Group4Desc = "Group4Desc";
    public static String IsKeyCP = "IsKeyCP";
    public static String Landline = "Landline";
    public static String Mobile2 = "Mobile2";
    public static String ParentTypDesc = "ParentTypDesc";
    public static String ParentTypeID = "ParentTypeID";
    public static String PartnerMgrName = "PartnerMgrName";
    public static String PartnerMgrNo = "PartnerMgrNo";
    public static String SalesGroupID = "SalesGroupID";
    public static String SalesGrpDesc = "SalesGrpDesc";
    public static String SalesOffDesc = "SalesOffDesc";
    public static String SalesOfficeID = "SalesOfficeID";
    public static String SearchTerm = "SearchTerm";
    public static String StateDesc = "StateDesc";
    public static String StatusDesc = "StatusDesc";
    public static String TownID = "TownID";
    public static String UOM = "UOM";
    public static String FreeQtyUOM = "FreeQtyUOM";
    public static String ZoneDesc = "ZoneDesc";
    public static String ZoneID = "ZoneID";
    public static String ParentTypeDesc = "ParentTypeDesc";
    public static String SchemeCPGUID = "SchemeCPGUID";
    public static String SchemeCPDocumentID = "SchemeCPDocumentID";
    public static String WindowHeight = "WindowHeight";
    public static String WindowSizeUOM = "WindowUOM";
    public static String WindowBreadth = "WindowBreadth";
    public static String WindowLength = "WindowLength";

    public static String SIMStockUOM = "";
    public static String Win_Display_Reg_Type_Other = "0000000005";

    public static String str_02 = "02";

    public static String InvoiceHisNo = "InvoiceNo";
    public static String InvoiceDate = "InvoiceDate";
    public static String InvoiceAmount = "InvoiceAmount";
    public static String InvoiceAmount1 = "GrossAmount";
    public static String InvoiceStatus = "StatusID";
    public static String InvoiceGUID = "InvoiceGUID";
    public static String OutAmount = "OutAmount";
    public static String SoldToName = "SoldToName";
    public static String SoldToID = "SoldToID";

    public static String PassedFrom = "PassedFrom";


    public static String ViisitCPNo = "ViisitCPNo";
    public static String VisitCPName = "VisitCPName";
    public static String VisitCPUID = "VisitCPUID";
    public static String CPNo = "CPNo";
    public static String RetailerName = "Name";
    public static String Address2 = "Address2";
    public static String Address3 = "Address3";
    public static String Address4 = "Address4";
    public static String TownDesc = "TownDesc";
    public static String ParentID = "ParentID";
    public static String ParentName = "ParentName";
    public static String StatusID = "StatusID";
    public static String StatusIdRetailer = "01";
    public static String VisitSeq = "VisitSeq";
    public static String lRetailerList = "VisitSeq";
    public static String ActualSeq = "ActualSeq";
    public static String DeviationReasonID = "DeviationReasonID";
    public static String DeviationRemarks = "DeviationRemarks";
    public static String Description = "Description";
    public static String RoutePlanGUID = "RoutePlanGUID";
    public static String Visit_Summary = "Visit_Summary";
    public static String Source = "Source";
    public static String Source_SFA = "SFA";

    /*
    constants for getting documents
     */

    public static String DocumentID = "DocumentID";
    public static String DocumentSt = "DocumentStore";
    public static String Application = "Application";
    public static String AuthOrgTypeID = "AuthOrgTypeID";
    public static String DocumentLink = "DocumentLink";
    public static String DocumentName = "FileName";
    public static String FolderName = "VisualVid";
    /*
    end for the constants for getting documents
     */

    public static String CategoryId = "CategoryId";

    public static String VoiceBalance = "VoiceBalance";
    public static String DataBalance = "DataBalance";
    public static String Last111Date = "Last111Date";
    public static String OutstandingAmt = "OutstandingAmt";
    public static String LastInvAmt = "LastInvAmt";
    public static String NewLaunchedProduct = "New Launched Product";
    public static String MustSellProduct = "Must Sell Product";
    public static String FocusedProduct = "Focused Product";
    public static String SalesOrderCreate = "Sales Order Create";

    public static String StockGuid = "StockGuid";
    public static String MerchReviewGUID = "MerchReviewGUID";
    public static String SPNo = "SPNo";
    public static String SPName = "SPName";
    public static String SPGUID = "SPGUID";
    public static String MerchReviewType = "MerchReviewType";
    public static String MerchReviewTypeDesc = "MerchReviewTypeDesc";
    public static String MerchReviewTime = "MerchReviewTime";
    public static String CreatedBy = "CreatedBy";
    public static String CreatedOn = "CreatedOn";
    public static String ChangedBy = "ChangedBy";
    public static String TestRun = "TestRun";
    public static String ReceivePoint = "ReceivePoint";
    public static String UnloadPoint = "UnloadPoint";
    public static String Surname = "Surname";
    public static String CustAccGrp = "CustAccGrp";
    public static String CompanyName = "CompanyName";
    public static String LegalStatus = "LegalStatus";
    public static String LegalStatusDesc = "LegalStatusDesc";
    public static String CustomerClass = "CustomerClass";
    public static String CustomerClassDesc = "CustomerClassDesc";
    public static String Industry = "Industry";
    public static String IndustryDesc = "IndustryDesc";
    public static String CustomerGroup = "CustomerGroup";
    public static String CustomerGroupDesc = "CustomerGroupDesc";
    public static String Street2 = "Street2";
    public static String Street3 = "Street3";
    public static String Street4 = "Street4";
    public static String DOA = "DOA";
    public static String TrustedStatusID = "TrustedStatusID";
    public static String TrustedStatusDesc = "TrustedStatusDesc";
    public static String TrustedFrom = "TrustedFrom";
    public static String SubDistrictID = "SubDistrictID";
    public static String SubDistrictDesc = "SubDistrictDesc";
    public static String TownOrVillageID = "TownOrVillageID";
    public static String TownOrVillageDesc = "TownOrVillageDesc";
    public static String WardID = "WardID";
    public static String WardDesc = "WardDesc";
    public static String DestinationID = "DestinationID";
    public static String DestinationDesc = "DestinationDesc";
    public static String Landline2 = "Landline2";
    public static String EmailID2 = "EmailID2";
    public static String CustomerCatgID = "CustomerCatgID";
    public static String CustomerDesc = "CustomerDesc";
    public static String CustomerCatgDate = "CustomerCatgDate";
    public static String Name2 = "Name2";
    public static String SPCategoryDesc = "SPCategoryDesc";
    public static String ComplaintTypeDesc = "ComplaintTypeDesc";
    public static String ComplaintTypeID = "ComplaintTypeID";
    public static String ComplainCategoryDesc = "ComplainCategoryDesc";
    public static String ComplaintCategoryID = "ComplaintCategoryID";
    public static String ComplaintNo = "ComplaintNo";
    public static String ComplaintGUID = "ComplaintGUID";
    public static String ComplaintPriorityID = "ComplaintPriorityID";
    public static String ComplaintPriorityDesc = "ComplaintPriorityDesc";
    public static String MaterialGrp = "MaterialGrp";
    public static String Material = "Material";
    public static String ComplaintDate = "ComplaintDate";
    public static String ComplaintStatusID = "ComplaintStatusID";
    public static String ComplaintStatusDesc = "ComplaintStatusDesc";
    public static String MFD = "MFD";
    public static String ExpiryDate = "ExpiryDate";
    public static String SchFreeMatGrpGUID = "SchFreeMatGrpGUID";
    public static String StockRefGUID = "StockRefGUID";


    /*
    for Alerts   AlertGUID
     */
    public static String AlertGUID = "AlertGUID";
    public static String AlertText = "AlertText";
    public static String PartnerType = "PartnerType";
    public static String PartnerID = "PartnerID";
    public static String AlertType = "AlertType";
    public static String AlertTypeDesc = "AlertTypeDesc";
    public static String ObjectType = "ObjectType";
    public static String ObjectID = "ObjectID";
    public static String ObjectSubID = "ObjectSubID";

    public static String FeedbackNo = "FeedbackNo";
    public static String FeebackGUID = "FeebackGUID";
    public static String FeedbackType = "FeedbackType";
    public static String RegSchemeCat = "RegSchemeCat";
    public static String SchemeCPDocType = "SchemeCPDocType";
    public static String FeedbackTypeDesc = "FeedbackTypeDesc";
    public static String SPCategoryID = "SPCategoryID";
    public static String Location = "Location";
    public static String Location1 = "Location1";
    public static String BTSID = "BTSID";
    public static String Testrun = "Testrun";
    public static String FeebackItemGUID = "FeebackItemGUID";


    public static String PlannedDate = "PlannedDate";
    public static String PlannedStartTime = "PlannedStartTime";
    public static String PlannedEndTime = "PlannedEndTime";

    public static String VisitTypeID = "VisitTypeID";
    public static String VisitTypeDesc = "VisitTypeDesc";

    public static String VisitDate = "VisitDate";
    public static String ProposedRoute = "ProposedRoute";
    public static String ApprovedRoute = "ApprovedRoute";
    public static String RouteID = "RouteID";
    public static String RouteDesc = "RouteDesc";
    public static String RoutePlanKey = "RoutePlanKey";


    public static String PaymentStatusID = "PaymentStatusID";
    public static String PaymentModeID = "PaymentModeID";
    public static String PaymentMode = "PaymentMode";
    public static String PaymentModeDesc = "PaymentModeDesc";
    public static String PaymetModeDesc = "PaymetModeDesc";
    public static String BranchName = "BranchName";
    public static String InstrumentNo = "InstrumentNo";
    public static String InstrumentDate = "InstrumentDate";
    public static String BankID = "BankID";
    public static String Remarks = "Remarks";
    public static String Currency = "Currency";
    public static String Geo1 = "Geo1";
    public static String Geo1Desc = "Geo1Desc";
    public static String Geo2 = "Geo2";
    public static String Geo2Desc = "Geo2Desc";
    public static String Geo3 = "Geo3";
    public static String Geo3Desc = "Geo3Desc";
    public static String Geo4 = "Geo4";
    public static String Geo4Desc = "Geo4Desc";
    public static String Geo5 = "Geo5";
    public static String Geo5Desc = "Geo5Desc";
    public static String Geo6 = "Geo6";
    public static String Geo6Desc = "Geo6Desc";
    public static String Geo7 = "Geo7";
    public static String Geo7Desc = "Geo7Desc";
    public static String Geo8 = "Geo8";
    public static String Geo8Desc = "Geo8Desc";
    public static String Geo9 = "Geo9";
    public static String Geo9Desc = "Geo9Desc";
    public static String Geo10 = "Geo10";
    public static String Geo10Desc = "Geo10Desc";
    public static String Amount = "Amount";
    public static String FIPGUID = "FIPGUID";
    public static String RefFIPDocGUID = "RefFIPDocGUID";
    public static String ExtRefID = "ExtRefID";
    public static String FIPDocType = "FIPDocType";
    public static String FIPDate = "FIPDate";
    public static String FIPDocNo = "FIPDocNo";
    public static String FIPAmount = "FIPAmount";
    public static String DebitCredit = "DebitCredit";
    public static String ParentNo = "ParentNo";
    public static String SPFirstName = "SPFirstName";
    public static String DeletionInd = "DeletionInd";


    public static String FIPItemGUID = "FIPItemGUID";
    public static String ReferenceID = "ReferenceID";
    public static String ReferenceDate = "ReferenceDate";
    public static String BalanceAmount = "BalanceAmount";
    public static String ClearedAmount = "ClearedAmount";
    public static String FIPItemNo = "FIPItemNo";


    public static String FirstName = "FirstName";
    public static String SalesOffice = "SalesOffice";


    public static String LastName = "LastName";


    public static String AttendanceGUID = "AttendanceGUID";
    public static String StartDate = "StartDate";
    public static String StartTime = "StartTime";
    public static String EndTime = "EndTime";
    public static String StartLat = "StartLat";
    public static String StartLong = "StartLong";
    public static String EndDate = "EndDate";
    public static String EndLat = "EndLat";
    public static String EndLong = "EndLong";
    public static String Etag = "Etag";


    public static String TextCategoryID = "TextCategoryID";
    public static String TextCategoryTypeID = "TextCategoryTypeID";
    public static String TextCategoryDesc = "TextCategoryDesc";
    public static String TextCategoryTypeDesc = "TextCategoryTypeDesc";
    public static String Text = "Text";


    public static String InvoiceHisMatNo = "MaterialNo";
    public static String InvoiceHisMatDesc = "MaterialDesc";
    public static String InvoiceHisAmount = "GrossAmount";
    public static String InvoiceHisQty = "Quantity";


    public static String CompName = "CompName";
    public static String CompGUID = "CompGUID";

    public static String CompInfoGUID = "CompInfoGUID";
    public static String Earnings = "Earnings";
    public static String SchemeAmount = "SchemeAmount";
    public static String SchemeName = "SchemeName";

    public static String MatGrp1Amount = "MatGrp1Amount";
    public static String MatGrp2Amount = "MatGrp2Amount";
    public static String MatGrp3Amount = "MatGrp3Amount";
    public static String MatGrp4Amount = "MatGrp4Amount";
    public static String UpdatedOn = "UpdatedOn";
    public static String PurchaseQty = "PurchaseQty";
    public static String PurchaseAmount = "PurchaseAmount";


    public static boolean collCreate = false;
    public static boolean CEFCreate = false;
    public static boolean returnOrdeCreate = false;
    public static boolean beatlist = false;
    public static HashMap<String, Integer> mapCount = new HashMap<String, Integer>();
    public static HashMap<String, String> MapSpinnerSelectedValue = new HashMap<String, String>();
    public static HashMap<String, String> MapRejectionReason = new HashMap<String, String>();
    public static HashMap<String, String> MapRoutePlanReason = new HashMap<String, String>();
    public static HashMap<String, String> MapApprovalReason = new HashMap<String, String>();
    public static HashMap<String, Integer> MapApprovalStatusIndexValue = new HashMap<String, Integer>();
    public static String ReturnDealer = "";
    public static int count = 0;
    public static String RETURNORDERMATERIAL = "MDStockSerList";
    public static String[] retailer_names = null;
    public static String[] retailer_codes = null;
    public static final String ALLOCSTOCKLIST = "AllocStockList";
    public static final String ORG_MONTHS[] = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String NEW_MONTHSCODE[] = {"11", "12", "01", "02",
            "03", "04", "05", "06", "07", "08", "09", "10"};
    public static Map<String, List<String>> matSer = new HashMap<String, List<String>>();
    public static String selOutletCode = null;
    public static String selOutletName = null;
    public static String RegError = null;
    public static String uniqueId = "";
    public static String UserID = "";
    public static String APP_PKG_NME = "";
    public static String host = null;
    public static String port = null;
    public static String CLIENT = "";
    public static String customerCodeName = null;
    public static String customerCode = null;
    public static String outletCode = null;
    public static String outletCodeName = null;
    public static String Dealer = "DEALER";
    public static String Dealer_Synckey = "";
    public static String Dealer_Name = "";
    public static String Dealer_date = "";
    public static String Dealer_Id = "";
    public static int PLANNED_VISIT = 0;
    public static int ACT_VISIT = 6;
    public static double TODAY_ACH_TARG = 0;
    public static int MONTH_ACH = 0;
    public static double GrosPrice = 0.0;
    public static double Vat = 0.0;
    // sync time
    public static String SYNC_START_TIME = "";
    public static String SYNC_END_TIME = "";
    public static String CREDIT_LIMIT_SYNC_TIME = "";
    public static String STOCKOVERVIEW_SYNC_TIME = "";

    // public static boolean isTerminateSync = false;
    public static boolean flagUpt = false;
    public static boolean chkdata = true;
    public static boolean isReg = false;
    public static boolean isRegError = false;
    public static boolean isSavePassChk = false;
    public static boolean iSAutoSyncStarted1 = false;
    public static boolean iSInsideCreate = false;
    public static boolean iSAttendancesync = false;
    public static boolean iSStartsync = false;
    public static boolean iSClosesync = false;
    public static boolean iSDealersync = false;
    public static boolean iSOutletsync = false;
    public static boolean iSFjpsync = false;
    public static boolean iSSignboardsync = false;
    public static boolean iSVisitsync = false;
    public static boolean iSDealerVisitsync = false;
    public static boolean iSSOCreatesync = false;
    public static boolean isSOCreateTstSync = false;
    public static boolean isSOCreateTstSync1 = false;
    public static boolean iSoutletCreatesync = false;
    public static boolean iSstartCreatesync = false;
    public static boolean iSCloseCreatesync = false;
    public static boolean iSServiceCreated = false;
    public static boolean iSMetaDocCreated = false;
    public static boolean iSClose = false;
    public static boolean iSStart = false;
    public static boolean iSfirstStart = true;
    public static boolean iSfirstStarted = false;
    public static boolean iSfirstclosed = false;
    public static boolean isCollectionsync = false;
    public static boolean isBatchmatstocksync = false;
    public static boolean iSfjpvisit = false;
    public static boolean iSclosevisit = false;
    public static boolean iSclose = false;
    public static boolean isupdatespinnervisit = false;
    public static boolean isupdateclosevisit = false;
    public static boolean iSfirstclose = false;
    public static boolean issolist = false;
    public static boolean isoutletlist = false;
    public static boolean isactivitylist = false;
    public static double latitude = 0.0, longitude = 0.0;
    public static boolean isInvoiceVisit = false;

    public static SQLiteDatabase dbCnt;
    public static int beforPendingcount = 0;
    public static int afterPendingcount = 0;
    public static Hashtable<String, String> hashtable;
    public static Hashtable<String, String> headerValues;
    public static Hashtable<String, String> itemValues;
    public static Hashtable[] itemValues_Responce = null;
    public static Cursor cursor;
    public static String DATABASE_PATH = "";
    public static Context ctx;
    public static String UserNameSyc = "";
    public static int NoOfItems = 0;
    public static int lastQty = 0;
    public static double totalUnitPrice = 0.0;
    public static String matNo = null;
    public static Hashtable INVOICEITEM = null;
    public static boolean iSItemview = false;
    public static Vector checkedSerialNo = new Vector();
    public static Vector beatRetailor = new Vector();
    public static ArrayList<String> list1 = new ArrayList<String>();
    public static ArrayList<String> enterEditTextValList = new ArrayList<String>();
    public static Hashtable<String, String> mapEnteredTextsHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredPricesHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredMaterialDescHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredMaterialGroupHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredBrandHashTable = new Hashtable<String, String>();
    public static Hashtable<String, String> mapEnteredMatrialUOMHashTable = new Hashtable<String, String>();

    public static HashMap<String, String> mapCheckedStateHashMap = new HashMap<String, String>();
    public static HashMap<String, String> mapEnteredTextsHashMap = new HashMap<String, String>();
    public static HashMap<String, String> mapEnteredPricesHashMap = new HashMap<String, String>();


    public static HashMap<String, String> dealerStockEnteredQtyHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockMatAndDescHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockMatAndBrandHashMap = new HashMap<String, String>();

    public static HashMap<String, String> dealerStockEnteredPurchasedQtyHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockVerfiedQtyHashMap = new HashMap<String, String>();
    public static HashMap<String, String> dealerStockUOMHashMap = new HashMap<String, String>();

    public static ArrayList<String> serialnumlist = new ArrayList<String>();
    public static HashMap<String, Double> InvoiceCreateVat = new HashMap<String, Double>();
    public static HashMap<String, Double> InvoiceCreateGross = new HashMap<String, Double>();
    public static HashMap<String, Double> MapVat = new HashMap<String, Double>();
    public static HashMap<String, Double> EXTRAVat = new HashMap<String, Double>();

    public static final String CONFIG_TABLE = "Config2";
    public static String DeviceTble = "Devicecollection";
    public static final String AUTHORIZATION_TABLE = "Authorizations";
    public static int congSel = 0;
    public static int congList = 0;
    // public static LiteMessagingClient lm = null;
    // public static LiteUserManager lurm = null;

    public static Hashtable SALESORDER_COMMENTS = null;
    public static Hashtable SALESORDER_HEADER = null;
    public static Hashtable[] SALESORDER_ITEMS = null;
    public static Hashtable SALESORDER_RESPONCE = null;
    public static Hashtable[] SALESORDER__HEADER_RESPONCE = null;
    public static Hashtable[] SALESORDER__ITEM_RESPONCE = null;
    public static Hashtable[] SALESORDER_SCHEMES = null;
    public static Hashtable[] ITEMS = null;
    public static Hashtable[] BATCH = null;
    public static Hashtable[] BATCH_COMMENTS = null;
    public static Hashtable fjpVlaues = null;

    public static ArrayList<String> matList = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> batchList = new ArrayList<ArrayList<String>>();
    public static ArrayList<String> focusMatBean = new ArrayList<String>();
    public static ArrayList<String> withoutSelMat = new ArrayList<String>();
    public static ArrayList<String> list = new ArrayList<String>();
    public static ArrayList<String> matCodeDecList = new ArrayList<String>();
    public static ArrayList<String> matDesclist = new ArrayList<String>();
    public static ArrayList<String> selectmatlist = new ArrayList<String>();
    public static ArrayList<String> selectbatchlist = new ArrayList<String>();

    // for temarary storage
    public static ArrayList SALESORDER_CHECK_TEMP = null;
    public static int INDEX_TEMP[] = null;
    public static int INDEX_TEMP1[] = null;
    public static int INDEX_TEMP_NEW[] = null;
    public static int INDEX_TEMP_IN[] = null;
    public static int FOCUS_MATERIAL[] = null;
    public static boolean closeFlag = false;
    public static boolean issaveclose = false;
    public static Boolean isChequeRequired = false;
    public static final String SPACE = "%20";
    public static String[][] matDesc = null;
    public static String FROM_PER = "";
    public static String FROM_PER1 = "";
    public static String TO_PER = "";

    // for visit storage
    public static Hashtable VISIT_HEADER = null;
    public static Hashtable[] VISIT_ITEMS = null;
    public static Hashtable[][] VISIT2_ITEMS = null;
    public static Hashtable INVERTER_QUANTITY = null;
    public static Hashtable[] DISTRIBUTOR_ITEMS = null;
    public static Hashtable[] COMPENTITOR_ITEMS = null;

    public static Hashtable[] MATERIALBATCHITEMS = null;

    public static int lengthofdealer = 0;
    public static int lengthofproducts = 0;
    public static int lengthofdistributor = 0;

    // for Dealer visit storage
    public static Hashtable DEALERVISIT_HEADER = null;
    public static Hashtable[] DEALER_ITEMS = null;
    public static int lengthofdealeritems = 0;
    public static Hashtable DEALERVISIT_POP = null;
    public static Hashtable[] DEALERCOMPENTITOR_ITEMS = null;

    public static int lengthofdealercomp = 0;
    public static boolean isSalesTargetSync = false;
    public static boolean iscollTargetSync = false;
    public static boolean isdlrofftakeSync = false;
    public static boolean isdlrprefSync = false;
    public static boolean issoitemSync = false;
    // for star image
    public static boolean is_accounts = false;
    public static boolean is_product_price = false;
    public static boolean isstock = false;
    public static boolean is_sales_order = false;
    public static boolean is_invoice = false;
    public static boolean is_collections = false;
    public static boolean is_activity = false;
    public static boolean is_target = false;

    public static final String ERROR_ARCHIVE_COLLECTION = "ErrorArchive";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_METHOD = "RequestMethod";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_BODY = "RequestBody";
    public static final String ERROR_ARCHIVE_ENTRY_HTTP_CODE = "HTTPStatusCode";
    public static final String ERROR_ARCHIVE_ENTRY_MESSAGE = "Message";
    public static final String ERROR_ARCHIVE_ENTRY_CUSTOM_TAG = "CustomTag";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_URL = "RequestURL";

    public static Hashtable<String, String> HashTableSerialNoAllocatedQty = new Hashtable<String, String>();

    public static final String PERSISTEDMETADATA = "metadata";
    public static final String PERSISTEDSERVICEDOC = "servicedoc";
    public static final String PERSISTEDFEEDS = "feeds";
    public static final String TOWN = "TownDistributorList";
    public static final String TRADES = "Trades";
    public static final String FJPLIST = "FJPList";
    public static final String ROUTES = "Routes";
    public static final String OUTLETS = "Outlets";
    public static final String VILLAGELIST = "VillageList";
    public static final String COLLECTIONS = "Collections";
    public static final String COMPETITOR = "CompetitorStocks";
    public static final String COMPETITORITEMS = "CompetitorStockItems";

    public static final String SECONDARYSALES = "SecondarySales";
    public static final String COUNTERSALES = "CounterSales";
    public static final String TERTIARYSALES = "TertiarySales";
    public static final String TERTIARYCOMPETITORS = "TertiaryCompetitors";
    public static final String PRODUCTGROUPS = "ProductGroups";
    public static final String COMPITITORPRODUCTGROUP = "CompProductGrps";
    public static final String RECEIPT_TABLE = "ReceiptTable";
    public static final String SALESORDTYPES = "OrderTypes";
    public static final String SALESAREAS = "SaleAreas";
    public static final String PAYMENTTERMS = "PaymentTerms";
    public static final String CREDITLIMIT = "CreditLimits";
    public static final String CustomerLatLong = "CustomerLatLong";
    public static final String ChangePassword = "ChangePassword";


    public static final String CompetitorMasters = "CompetitorMasters";
    public static final String TEXT_CATEGORY_SET = "TextCategorySet";
    public static final String CONFIGURATION = "Configurations";
    public static final String ValueHelps = "ValueHelps";
    public static final String SALES_ORDER_DELIVERIES = "SalesOrderDeliveries";
    public static final String PRODUCTPRICES = "ProductPrices";
    public static final String SECONDARYCOMPETITORS = "SecondaryCompetitors";
    public static final String SCHEMESMATERIALS = "SchemeMaterials";
    public static final String POPORDERLISTS = "PopOrderLists";
    public static final String SIGN_BOARD_REQUESTS = "SignBoardRequests";
    public static final String MATERIALS = "Materials";
    public static final String OUTLETFLIST = "OutletF4List";
    public static final String CUSTOMER_MATERIALS = "CustomerMaterials";
    public static final String ACTVITYF4 = "ActivityF4List";
    public static final String CustomerPerformances = "CustomerPerformances";
    public static final String MATERIALLIST = "MaterialList";
    public static final String INVOICE_HEADER = "InvoiceHeaders";
    public static final String INVOICE_ITEM = "InvoiceItems";
    public static final String INVOICEDELIVERIES = "InvoiceDeliveries";
    public static final String ACTIVITY_HDR = "JourneyCycles";
    public static final String REPORTDEALER = "ReportDealerTable";
    public static final String REPORTDEALER_ITEM = "ReportDealeritemTable";
    public static final String REPORTDEALER_COMMENTS = "ReportDealerCommentTable";
    public static final String SO_TEST = "SalesOrders";
    public static final String SO_ITEM_TEST = "Test";
    public static final String MEETINGS = "Meetings";
    public static final String DEALERMEETINGS = "DealerMeetings";
    public static final String POPMATERIALS = "PopMaterials";
    public static final String SOSIMULATELIST = "SoSimulateList";
    public static final String SO_ORDER_SCHEMES = "SalesOrderSchemes";
    public static final String COLL_TARGETS = "CollectionTargets";
    public static final String SALES_TARGETS = "SalesTargets";
    public static final String DLR_OFFTAKE = "DealerOfftakes";
    public static final String DLR_PREFS = "DealerPerfs";
    public static final String CONTACTPERSON = "ContactPersons";
    public static final String CUSTSALESAREAS = "CustSalesAreas";
    public static final String SALESAREAORDTYPES = "SalesAreaOrdTypes";
    public static final String Attendances = "Attendances";
    public static final String Visits = "Visits";
    public static final String ChannelPartners = "ChannelPartners";
    public static final String CPDMSDivisions = "CPDMSDivisions";
    public static final String FinancialPostings = "FinancialPostings";
    public static final String FinancialPostingItemDetails = "FinancialPostingItemDetails";
    public static final String FinancialPostingItems = "FinancialPostingItems";
    public static final String RetailerSummarySet = "RetailerSummarySet";
    public static final String SPChannelEvaluationList = "SPChannelEvaluationList";
    public static final String Documents = "Documents";
    public static final String ExpenseConfigs = "ExpenseConfigs";
    public static final String ExpenseAllowances = "ExpenseAllowances";
    public static final String Alerts = "Alerts";
    public static final String SSInvoiceTypes = "SSInvoiceTypes";
    public static final String RequestID = "RequestID";
    public static final String RepeatabilityCreation = "RepeatabilityCreation";


    public static final String AttributeTypesetTypes = "AttributeTypesetTypes";
    public static final String SSINVOICES = "SSInvoices";
    public static final String SSInvoiceItemDetails = "SSInvoiceItemDetails";
    public static final String CPMarketSet = "CPMarketSet";
    public static final String CPBusinessSet = "CPBusinessSet";
    public static final String CompetitorSales = "CompetitorSales";
    public static final String SSInvoiceItemSerials = "SSInvoiceItemSerialNos";
    public static final String CompetitorInfos = "CompetitorInfos";
    public static final String SPStockItemDetails = "SPStockItemDetails";
    public static final String SPStockItemSNos = "SPStockItemSNos";
    public static final String SPStockItems = "SPStockItems";
    public static final String UserProfileAuthSet = "UserProfileAuthSet";
    public static final String Performances = "Performances";
    public static final String RetailerActivationStatusSet = "RetailerActivationStatusSet";

    public static final String CEFStatusID = "CEFStatusID";
    public static final String Status111BID = "Status111BID";
    public static final String Status222ID = "Status222ID";
    public static final String SubsMSIDN = "SubsMSIDN";

    public static final String Targets = "Targets";
    public static final String KPISet = "KPISet";
    public static final String KPIItems = "KPIItems";
    public static final String TargetItemDetails = "TargetItemDetails";
    public static final String TargetItems = "TargetItems";


    public static final String Month = "Month";
    public static final String Year = "Year";
    public static final String Period = "Period";
    public static final String KPIGUID = "KPIGUID";
    public static final String KPICode = "KPICode";
    public static final String KPIName = "Name";
    public static final String TargetQty = "TargetQty";
    public static final String ActualQty = "ActualQty";
    public static final String TargetValue = "TargetValue";
    public static final String ActualValue = "ActualValue";
    public static final String TargetGUID = "TargetGUID";
    public static final String CalculationBase = "CalculationBase";
    public static final String CalculationSource = "CalculationSource";
    public static final String KPIFor = "KPIFor";
    public static final String RollUpTo = "RollUpTo";
    public static final String RollupStatus = "RollupStatus";
    public static final String RollupStatusDesc = "RollupStatusDesc";
    public static final String CvgValue = "CvgValue";


    public static final String CEFStatusDesc = "CEFStatusDesc";
    public static final String Status111BDesc = "Status111BDesc";
    public static final String Status222Desc = "Status222Desc";

    public static final String PartnerGUID = "PartnerGUID";
    public static final String PartnerNo = "PartnerNo";


    public static final String Refersh = "Attendances,SSInvoices,SSInvoiceItemDetails,FinancialPostings,FinancialPostingItemDetails";


    public static final String CPStockItemDetails = "CPStockItemDetails";
    public static final String CPStockItemSnos = "CPStockItemSnos";
    public static final String CPStockItems = "CPStockItems";


    public static String AuthOrgValue = "AuthOrgValue";
    public static String AuthOrgTypeDesc = "AuthOrgTypeDesc";


    public static final String DAYTARGETS = "DayTarget";
    public static final String MONTHTARGETS = "MonthlyTarget";
    public static final String CHEQUESUMMARY = "ChequeSummary";

    public static final String PAINTERVISIT = "PainterVisit";
    public static final String QUARTERTARGETS = "QuarterlyTarget";
    public static final String DEALERWISETARGETS = "DealerWiseTarget";
    public static final String DEALERWISETARGETSVALUE = "DealerWiseTargetValue";
    public static final String STOCKOVERVIEWS = "StockOverviews";
    public static final String AUTHORIZATIONS = "Authorizations";
    public static final String DEALERREQUEST = "DealerRequests";
    public static final String CITYCODES = "CityCodes";
    public static final String SALESRETURNINVOICES = "SalesReturnInvoices";
    public static final String SALESCOLLECTIONDATA = "SaleCollectionDatas";
    public static final String STOCKVALUEDATA = "StockValueDatas";
    public static final String PRODUCTDESKDATA = "PrdDeskDatas";
    public static final String ACTIVITYS = "Activities";
    public static final String OUTLETTYPES = "OutletTypes";
    public static final String OUTLETCATEGORIES = "OutletCategories";
    public static final String OUTLETCLASSES = "OutletClasses";
    public static final String BATCHBLOCKLIST = "BatchBlockList";
    public static final String EXCLUDEDMATERIALLIST = "ExcludedMaterialList";
    public static final String FOCUSPRODUCTLIST = "FocusProductList";
    public static final String VISITLIST = "VisitList";
    public static final String FOCUSPRODREASONLIST = "FocusProdReasons";
    public static final String MATERIALSTOCK = "MaterialStocks";
    public static String SO_RESPONCE_ORDNO = "";
    public static String SO_RESQUEST_ORDNO = "";
    public static String OUTLET_RESPONCENO = "";
    public static String OUTLET_RESQUESTNO = "";

    public static Map<String, List<String>> focusmaterials = new HashMap<String, List<String>>();
    public static Hashtable[] SALESORDER_FOCUSMATERIALS = null;
    public static String selectedOutletCode = "";
    public static String selectedOutletDesc = "";

    public static boolean isMaterDataSyncEnable, isFocuPrdSyncEnable,
            isCollectionSyncEnable, isFJPSyncEnable, isActSyncEnable,
            isBatchBlockSyncEnable, isExcMaterialSyncEnable,
            isMatStockSyncEnable, isOutstandSyncEnable, isSOSyncEnable,
            isStartCloseSyncEnable, isAuthSyncEnable, isVisitSyncEnable, isMaterialSyncEnable,
            isSTOSyncEnable, isSalesOrderSyncEnable, isDeliverySyncEnable,
            isInvoiceSync, isStockSyncEnable,
            isCollSyncEnable, isVisitStartSyncEnable;
    ;
    public static Hashtable[] SERIALNUMS;
    public static final String RECEIPT = "Receipt";
    public static final String VISITTYPECONFIG = "VisitTypeConfig";
    public static final String CREDITLIMITTABLE = "CustomerCreditLimits";

    public static final String MerchandisingReviews = "MerchandisingReviews";
    public static final String RETILERIMGTABLE = "RetailerImgTable";
    public static final String AGEINGREPORT = "AgeingReport";
    public static final String CREDITNOTE = "CreditNote";
    public static final String Leads = "Leads";


    public static final String Surveys = "Surveys";
    public static final String SurveyQuestions = "SurveyQuestions";
    public static final String SurveyQuestionOptions = "SurveyQuestionOptions";


    public static final String MATERIALSTOCKQTY = "MaterialStock";
    public static final String DEALERWISESECSALES = "DealerWiseSecSales";
    public static final String COMPLAINTSTRACKING = "ComplaintsTracking";
    public static final String SALESORDER = "SalesOrders";
    public static final String SALESORDERITEMS = "SalesOrderItems";
    public static final String SALESORDERITEMSDETAILS = "SalesOrderItemDetails";
    public static final String BrandPerforms = "BrandPerforms";
    public static final String Trends = "Trends";

    public static final String InvoiceItemDetails = "InvoiceItemDetails";


    public static final String PlantStocks = "PlantStocks";


    public static final String CustomerComplaints = "CustomerComplaints";

    public static final String Feedbacks = "Feedbacks";
    public static final String FeedbackItemDetails = "FeedbackItemDetails";

    public static final String MerchReviews = "MerchReviews";
    public static final String MerchReviewImages = "MerchReviewImages";
    public static final String SchemeCPDocuments = "SchemeCPDocuments";
    public static final String MerchReviewsAssociativeType = "MerchReview_MerchReviewImage";

    public static final String VisitSurveys = "VisitSurveys";
    public static final String VisitSurveyResults = "VisitSurveyResults";


    public static final String CollectionLists = "CollectionLists";


    public static final String CPPartnerFunctions = "CPPartnerFunctions";
    public static final String Schemes = "Schemes";
    public static final String Claims = "Claims";
    public static final String ClaimItemDetails = "ClaimItemDetails";
    public static final String ClaimDocuments = "ClaimDocuments";
    public static final String Tariffs = "Tariffs";


    public static final String ExpenseEntryItemDetails = "ExpenseEntryItemDetails";
    public static final String ExpenseEntryImages = "ExpenseEntryImages";
    public static final String LeadItemDetails = "LeadItemDetails";
    public static String SubOrdinates = "SubOrdinates";
    public static final String ShadeCards = "ShadeCards";


    public static final String TargetMatGrpCustomers = "TargetMatGrpCustomers";


    public static final String ShadeCardFeedbacks = "ShadeCardFeedbacks";


    public static final String DishonourChqs = "DishonourChqs";

    public static final String DepotTargets = "DepotTargets";
    public static final String DishonourChqItemDetails = "DishonourChqItemDetails";


    public static final String DishonourCheques = "DishonourCheques";


    public static final String SalesOrderSummary = "SalesOrderSummary";
    public static final String CollectionAmtSummary = "CollectionAmtSummary";
    public static final String InvoiceSummary = "InvoiceSummary";


    public static final String VisitActivities = "VisitActivities";


    public static final String ActivitySummarys = "ActivitySummarys";


    public static final String SalesHierarchies = "SalesHierarchies";


    public static final String PasswordChanges = "PasswordChanges";


    public static String CustomerComplaintTxts = "CustomerComplaintTxts";
    public static String RoutePlanApprovals = "RoutePlanApprovals";
    public static String BUSINESSCALKEYNO = "";

    public static Date dateFrom;
    public static Date dateTo;
    public static boolean OrderCreated = false;
    public static boolean ReturnOrderCreated = false;
    public static boolean collectionUpdated = false;
    public static boolean snapshotTaken = false;
    public static boolean bussinessCallSavedSucessfully = false;
    public static boolean relationshipCallSavedSuccessfully = false;
    public static boolean ShadeCardSuccessfully = false;
    public static boolean CustomerComplaintsSavedSuccessfully = false;
    public static boolean DealerStockEnteredSuccessfully = false;
    public static boolean CompetitorStockSuccessfully = false;
    public static HashMap<String, Boolean> mapAllDone = new HashMap<String, Boolean>();
    public static String retailerIDSelected = "";
    public static String retailerNameSelected = "";
    public static String mobileNo = "";
    public static String address1 = "";
    public static String address2 = "";
    public static int selectednumber = 0;
    public static boolean newProduct = false;
    public static boolean FocusProduct = false;
    public static boolean MustSell = false;
    public static String RRETAILERMOBILENO = "";
    public static String RRETAILERFITSTADDRESS = "";
    public static String RRETAILERSECONDADDRESS = "";
    public static String MaterialGrpAndCode = "MaterialGrpAndCode";
    public static String BalanceConfirmationHeader = "BalanceConfirmationHeader";
    public static String BalanceConfirmationItems = "BalanceConfirmationItems";

    public static String BalanceConfirmations = "BalanceConfirmations";
    public static String BalConfirmItemDetaills = "BalConfirmItemDetails";


    public static boolean isCreateFlag = false;
    public static boolean isSOCountDone = false;
    public static boolean isAppEndPointDone = false;
    public static boolean isMetaDataDone = false;
    public static boolean isSOItmCountDone = false;
    public static boolean isMaterialsCountDone = false;
    public static boolean isPriceListCountDone = false;
    public static String OutstandingSummary = "OutstandingSummary";
    public static String CustomerwiseOSs = "CustomerwiseOSs";

    public static String Promotion = "Promotion";
    public static String BrandPerformanc = "BrandPerformance";
    public static String ErrorMsg = "";
    public static String SalesOrderNo = "";
    public static String newMPNo = "";


    public static String MerchndisingKeyNo = "";
    public static String VisitKeyNo = "";
    public static String VisitTypeNo = "";
    public static String VisitStartKeyNo = "";
    public static String PartnerFunction = "PartnerFunction";
    public static String PFGUID = "PFGUID";
    public static String PartnarCPGUID = "PartnarCPGUID";
    public static String PartnerFunctionDesc = "PartnerFunctionDesc";
    public static String PartnerCPNo = "PartnerCPNo";
    public static String PartnarName = "PartnarName";
    public static String PartnerMobileNo = "PartnerMobileNo";
    public static String VisitStartKeyNoCurrentDealerNo = "";


    public static String Collections = "Collections";
    public static String CollectionItemDetails = "CollectionItemDetails";


    public static String SyncGroup = "SyncGroup";

    public static String MasterPainter = "MasterPainter";
    public static String Expenses = "Expenses";
    public static String ExpenseItemDetails = "ExpenseItemDetails";
    public static String ExpenseDocuments = "ExpenseDocuments";
    public static String reqExpensesNo;
    public static String resExpensesNo;
    public static String resLeadProjectNo;
    public static String reqLeadProjectNo;
    public static String MerchandisingReview = "MerchandisingReview";
    public static String reqMerchandisingNo;
    public static String resMerchandisingNo;
    public static String resBusinessCallNo;
    public static String reqBusinessCallNo;
    public static String reqRelationShipCallNo;
    public static String resRelationShipCallNo;
    public static String reqCustomerComplaintNo;
    public static String resCustomerComplaintNo;


    public static String collectionresDocNo = "";
    public static String collectionreqDocNo = "";
    public static String reqMasterPainterNo = "";
    public static String resMasterPainterNo = "";


    public static String reqAttendanceID = "";


    public static String resAttendanceID = "";


    public static boolean SoCreateSeaniro = false;


    public static double latitudeValue;


    public static double longitudeValue;


    public static boolean isCollListsAuthEnabled;


    public static String CustomerComplaintNo = "";
    public static boolean isMerchSyncEnable;


    public static String PreviousMaterialGrp = "";


    public static String ExpenseEntrys = "ExpenseEntrys";


    public static boolean isCustPerFormances, isAttencesSync;


    public static boolean isShadeCardEnabled;


    public static boolean isTargetsEnabled;


    public static String reqShadeCardFeedBackNo;


    public static String resShadeCardFeedBackNo;


    public static boolean isDishonourSyncEnable;


    public static String reqLeadChangeProjectNo;


    public static String resLeadChangeProjectNo;


    public static String resMatSO;


    public static String reqMatSO;


    public static ArrayList<HashMap> soItem = new ArrayList<HashMap>();
    public static HashMap selBrand = new HashMap();
    public static ArrayList<HashMap> soDaySummary = new ArrayList<HashMap>();
    public static ArrayList<HashMap> collDaySummary = new ArrayList<HashMap>();
    public static ArrayList<HashMap> invDaySummary = new ArrayList<HashMap>();

    public static String VisitSurveyNo;


    public static String FocusedCustomers = "FocusedCustomers";


    public static String LeadNo = "";


    public static String NewPwd = "";


    public static String getLoginName() {

        String loginQry = Constants.SalesPersons;
        String mStrLoginName = "";
        try {
            mStrLoginName = OfflineManager.getLoginName(loginQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return mStrLoginName;
    }

    public static String getSalesPersonName() {

        String loginQry = Constants.SalesPersons + "?$select=" + Constants.FirstName + " ";
        String mStrMobNo = "";
        try {
            mStrMobNo = OfflineManager.getSalePersonName(loginQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return mStrMobNo;
    }

    public static String getCurrency() {

        String currecyQry = Constants.SalesPersons + "?$top=1 &$select=" + Constants.Currency + " ";
        String mStrCurrency = "";
        try {
            mStrCurrency = OfflineManager.getCurrency(currecyQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return mStrCurrency;
    }


    public static String getName(String collName, String columnName, String whereColumnn, String whereColval) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static String getConfigTypeIndicator(String collName, String columnName, String whereColumnn, String whereColval, String propertyColumn, String propVal) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "' and " + propertyColumn + " eq '" + propVal + "' ", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static String getSPGUID(String collName, String columnName) {
        String mStrSPGUID = "";
        try {
            mStrSPGUID = OfflineManager.getGuidValueByColumnName(collName + "?$select=" + columnName, columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mStrSPGUID;
    }

    public static JSONObject prepareInvoiceJsonObject(Hashtable<String, String> dbHeadTable, ArrayList<HashMap<String, String>> arrtable,
                                                      Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos) {
        JSONObject invoiceHeader = new JSONObject();

        JSONObject invoiceItemDetails;

        JSONObject invoiceSerilaItemDetails;

        JSONArray invoiceItemDetailsArray = new JSONArray();

        JSONArray invoiceSerialItemDetailsArray;

        try {

            invoiceHeader.put("InvoiceGUID", dbHeadTable.get(Constants.InvoiceGUID));
            invoiceHeader.put("LoginID", dbHeadTable.get(Constants.LOGINID));
            invoiceHeader.put("InvoiceTypeID", dbHeadTable.get(Constants.InvoiceTypeID));
            invoiceHeader.put("InvoiceDate", dbHeadTable.get(Constants.InvoiceDate));
            invoiceHeader.put("CPNo", dbHeadTable.get(Constants.CPNo));
            invoiceHeader.put(Constants.CPGUID, dbHeadTable.get(Constants.CPGUID));
            invoiceHeader.put("SoldToID", dbHeadTable.get(Constants.SoldToID));
            invoiceHeader.put("ShipToID", dbHeadTable.get(Constants.SoldToID));
            invoiceHeader.put(Constants.CPTypeID, dbHeadTable.get(Constants.CPTypeID));
            invoiceHeader.put(Constants.SPGUID, dbHeadTable.get(Constants.SPGuid));
            invoiceHeader.put(Constants.NetAmount, dbHeadTable.get(Constants.NetAmount));
            invoiceHeader.put(Constants.TestRun, dbHeadTable.get(Constants.TestRun));
            invoiceHeader.put(Constants.Currency, dbHeadTable.get(Constants.Currency));

            invoiceHeader.put(Constants.SoldToCPGUID, dbHeadTable.get(Constants.SoldToCPGUID));
            invoiceHeader.put(Constants.SoldToTypeID, dbHeadTable.get(Constants.SoldToTypeID));
            invoiceHeader.put(Constants.ShipToCPGUID, dbHeadTable.get(Constants.SoldToCPGUID));
            invoiceHeader.put(Constants.ShipToTypeID, dbHeadTable.get(Constants.SoldToTypeID));
            invoiceHeader.put(Constants.SPNo, dbHeadTable.get(Constants.SPNo));


            for (int i = 0; i < arrtable.size(); i++) {

                invoiceItemDetails = new JSONObject();
                HashMap<String, String> singleRow = arrtable.get(i);
                invoiceItemDetails.put("ItemNo", ((i + 1)) + "");
                invoiceItemDetails.put("MaterialNo", singleRow.get("MatCode"));
                invoiceItemDetails.put("MaterialDesc", singleRow.get("MatDesc"));
                invoiceItemDetails.put("Quantity", singleRow.get("Qty"));
                invoiceItemDetails.put("InvoiceItemGUID", singleRow.get("InvoiceItemGUID"));
                invoiceItemDetails.put("InvoiceGUID", dbHeadTable.get("InvoiceGUID"));
                invoiceItemDetails.put("StockGuid", singleRow.get("StockGuid"));
                invoiceItemDetails.put("UOM", singleRow.get("UOM"));
                invoiceItemDetails.put(Constants.UnitPrice, singleRow.get(Constants.UnitPrice));


                invoiceItemDetails.put(Constants.NetAmount, singleRow.get(Constants.NetAmount));

                ArrayList<InvoiceBean> alItemSerialNo = hashTableItemSerialNos.get(singleRow.get("StockGuid"));

                int incementsize = 0;
                invoiceSerialItemDetailsArray = new JSONArray();
                if (alItemSerialNo != null && alItemSerialNo.size() > 0) {
                    for (int j = 0; j < alItemSerialNo.size(); j++) {
                        InvoiceBean serialNoInvoiceBean = alItemSerialNo.get(j);
                        if (!serialNoInvoiceBean.getStatus().equalsIgnoreCase("04")) {
                            invoiceSerilaItemDetails = new JSONObject();
                            invoiceSerilaItemDetails.put("ItemNo", ((incementsize + 1)) + "");
                            invoiceSerilaItemDetails.put("SerialNoFrom", serialNoInvoiceBean.getSerialNoFrom());
                            invoiceSerilaItemDetails.put("SerialNoTo", serialNoInvoiceBean.getSerialNoTo());
                            invoiceSerilaItemDetails.put("PrefixLength", serialNoInvoiceBean.getPrefixLength());
                            invoiceSerilaItemDetails.put("InvoiceItemSNoGUID", serialNoInvoiceBean.getSPSNoGUID());
                            invoiceSerilaItemDetails.put("InvoiceItemGUID", singleRow.get("InvoiceItemGUID"));
                            invoiceSerilaItemDetails.put("StatusID", serialNoInvoiceBean.getStatus());
                            invoiceSerilaItemDetails.put("Option", serialNoInvoiceBean.getOption());
                            invoiceSerilaItemDetails.put(Constants.UOM, serialNoInvoiceBean.getUom());
                            BigInteger qty = null;
                            try {
                                int prefixLen = (int) Double.parseDouble(serialNoInvoiceBean.getPrefixLength());
                                BigInteger doubAvalTo = new BigInteger(UtilConstants.removeAlphanumericText(serialNoInvoiceBean.getSerialNoTo(), prefixLen));

                                BigInteger doubAvalFrom = new BigInteger(UtilConstants.removeAlphanumericText(serialNoInvoiceBean.getSerialNoFrom(), prefixLen));

                                qty = (doubAvalTo.subtract(doubAvalFrom).add(new BigInteger("1")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (qty != null) {
                                invoiceSerilaItemDetails.put("Quantity", qty.toString());
                            }
                            invoiceSerialItemDetailsArray.put(invoiceSerilaItemDetails);
                            incementsize++;
                        }
                    }
                }
                invoiceItemDetails.put("SSInvoiceItemSerialNos", invoiceSerialItemDetailsArray);
                invoiceItemDetailsArray.put(invoiceItemDetails);

            }

            invoiceHeader.put("SSInvoiceItemDetails", invoiceItemDetailsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return invoiceHeader;
    }

    @SuppressLint("NewApi")
    public static void deletePendingReqFromDataVault(Context context, String tempNo) {
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet("InvList", null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }

        setTemp.remove(tempNo);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("InvList", setTemp);
        editor.commit();

        try {
            LogonCore.getInstance().addObjectToStore(tempNo, "");
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }


    public static void deletePendingVisitActivity(String visitActRefID) {

        ArrayList<InvoiceBean> alDeleteSnoList = null;
        VisitActivityBean visitActivityBean = null;
        try {
            visitActivityBean = OfflineManager.getVisitActivityGuid(Constants.VisitActivities + "?$filter=" + Constants.ActivityRefID + " eq guid'" + visitActRefID + "' ");

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        try {
            OfflineManager.deleteVisitActivity(visitActivityBean);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    public static String CreateOperation = "Create";
    public static String ReadOperation = "Read";
    public static String UpdateOperation = "Update";
    public static String DeleteOperation = "Delete";
    public static String QueryOperation = "Query";

    @SuppressLint("NewApi")
    public static String getSyncType(Context context, String collectionName, String operation) {
        String mStrSyncType = "4";
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String sharedVal = sharedPreferences.getString(collectionName, "");
        if (!sharedVal.equalsIgnoreCase("")) {
            if (operation.equalsIgnoreCase(CreateOperation)) {
                if (sharedVal.substring(0, 1).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(ReadOperation)) {
                if (sharedVal.substring(1, 2).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }

            } else if (operation.equalsIgnoreCase(UpdateOperation)) {
                if (sharedVal.substring(2, 3).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(DeleteOperation)) {
                if (sharedVal.substring(3, 4).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(QueryOperation)) {
                if (sharedVal.substring(4, 5).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            }
        } else {
            mStrSyncType = "4";
        }


        return mStrSyncType;
    }


    public static class DecimalFilter implements InputFilter {
        EditText editText;
        int beforeDecimal, afterDecimal;

        public DecimalFilter(EditText editText, int beforeDecimal, int afterDecimal) {
            this.editText = editText;
            this.afterDecimal = afterDecimal;
            this.beforeDecimal = beforeDecimal;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            StringBuilder sbText = new StringBuilder(source);
            String text = sbText.toString();
            if (dstart == 0) {
                if (text.contains("0")) {
                    return "";
                } else {
                    return source;
                }
            }
            String etText = editText.getText().toString();
            if (etText.isEmpty()) {
                return null;
            }
            String temp = editText.getText() + source.toString();

            if (temp.equals(".")) {
                return "0.";
            } else if (temp.toString().indexOf(".") == -1) {
                // no decimal point placed yet
                if (temp.length() > beforeDecimal) {
                    return "";
                }
            } else {
                int dotPosition;
                int cursorPositon = editText.getSelectionStart();
                if (etText.indexOf(".") == -1) {
                    Log.i("First time Dot", etText.toString().indexOf(".") + " " + etText);
                    dotPosition = temp.indexOf(".");
                } else {
                    dotPosition = etText.indexOf(".");
                }
                if (cursorPositon <= dotPosition) {
                    String beforeDot = etText.substring(0, dotPosition);
                    if (beforeDot.length() < beforeDecimal) {
                        return source;
                    } else {
                        if (source.toString().equalsIgnoreCase(".")) {
                            return source;
                        } else {
                            return "";
                        }

                    }
                } else {
                    temp = temp.substring(temp.indexOf(".") + 1);
                    if (temp.length() > afterDecimal) {
                        return "";
                    }
                }
            }
            return null;
        }


    }

    public static void setAmountPattern(EditText editTxt, final int beforeDecimal, final int afterDecimal) {

        try {
            editTxt.setFilters(new InputFilter[]{new DecimalFilter(editTxt, beforeDecimal, afterDecimal)});

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    //new 28112016 Ramu
    public static String Route_Plan_No = "";
    public static String Route_Plan_Desc = "";
    public static String Route_Plan_Key = "";
    public static String Route_Schudle_GUID = "";
    public static String Visit_Type = "";
    public static String VISIT_TYPE = "VISIT_TYPE";
    public static String PlannedRoute = "PlannedRoute";
    public static String PlannedRouteName = "PlannedRouteName";
    public static String PlanedCustomerName = "CustomerName";


    public static String getCPGUID(String collName, String columnName, String whereColumn, String whereColumnValue) {
        String getGuid = "";
        try {
            getGuid = OfflineManager.getGuidValueByColumnName(collName +
                    "?$select=" + columnName + " &$filter = " + whereColumn + " eq '" + whereColumnValue + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getGuid;
    }


    public static String convertStrGUID32to36(String strGUID32) {
        try {
            return CharBuffer.join9(StringFunction.substring(strGUID32, 0, 8), "-", StringFunction.substring(strGUID32, 8, 12), "-", StringFunction.substring(strGUID32, 12, 16), "-", StringFunction.substring(strGUID32, 16, 20), "-", StringFunction.substring(strGUID32, 20, 32));
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFirstDateOfCurrentMonth() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static String getLastMonthDate() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
//		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static long getFirstDateOfCurrentMonthInMiliseconds() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }


    /*Checks for GPS*/
    public static boolean onGpsCheck(final Context context) {
        if (!UtilConstants.isGPSEnabled(context)) {
            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(context, R.style.MyTheme);
            gpsEnableDlg
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");
            gpsEnableDlg.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
            // on pressing cancel button
            gpsEnableDlg.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            gpsEnableDlg.show();
        }
        return UtilConstants.isGPSEnabled(context);
    }

    /*  *//*Checks for GPS*//*
    public static boolean onGpsCheck(final Context context) {
        UtilConstants.canGetLocation(context);
        if(!UtilConstants.isGPSEnabled(context)){
            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(context, R.style.MyTheme);
            gpsEnableDlg
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");
            gpsEnableDlg.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
            // on pressing cancel button
            gpsEnableDlg.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            gpsEnableDlg.show();
        }else{
            if(UtilConstants.longitude ==0.0 && UtilConstants.latitude ==0.0) {
                UtilConstants.canGetLocation(context);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme);
                builder.setMessage(R.string.gps_location_empty_try_again)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @SuppressLint("NewApi")
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Intent intent = new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        context.startActivity(intent);

                                    }
                                });
                // on pressing cancel button
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                // Showing Alert Message
                builder.show();
            }

        }

        if(GpsTracker.isGPSEnabled){
            if(UtilConstants.longitude ==0.0 && UtilConstants.latitude ==0.0) {
                return false;
            }else{
                return GpsTracker.isGPSEnabled;
            }
        }else{
            return false;
        }


    }*/

    public static Hashtable getCollHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {

        Hashtable dbHeadTable = new Hashtable();

        try {

            dbHeadTable.put(Constants.BeatGUID, fetchJsonHeaderObject.getString(Constants.BeatGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.BankID, fetchJsonHeaderObject.getString(Constants.BankID));
            dbHeadTable.put(Constants.BankName, fetchJsonHeaderObject.getString(Constants.BankName));
            dbHeadTable.put(Constants.BranchName, fetchJsonHeaderObject.getString(Constants.BranchName));
            dbHeadTable.put(Constants.InstrumentNo, fetchJsonHeaderObject.getString(Constants.InstrumentNo));
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            dbHeadTable.put(Constants.FIPDocType, fetchJsonHeaderObject.getString(Constants.FIPDocType));
            dbHeadTable.put(Constants.PaymentModeID, fetchJsonHeaderObject.getString(Constants.PaymentModeID));
            dbHeadTable.put(Constants.FIPDate, fetchJsonHeaderObject.getString(Constants.FIPDate));
            dbHeadTable.put(Constants.InstrumentDate, fetchJsonHeaderObject.getString(Constants.InstrumentDate));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.FIPGUID, fetchJsonHeaderObject.getString(Constants.FIPGUID));
            dbHeadTable.put(Constants.SPGuid, fetchJsonHeaderObject.getString(Constants.SPGuid));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.ParentNo, fetchJsonHeaderObject.getString(Constants.ParentNo));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPFirstName, fetchJsonHeaderObject.getString(Constants.SPFirstName));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.Source, fetchJsonHeaderObject.getString(Constants.Source));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getSOHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            try {
                dbHeadTable.put(Constants.BeatGuid, fetchJsonHeaderObject.getString(Constants.BeatGuid));
            } catch (JSONException e) {
                dbHeadTable.put(Constants.BeatGuid, "");
                e.printStackTrace();
            }
            dbHeadTable.put(Constants.SSSOGuid, fetchJsonHeaderObject.getString(Constants.SSSOGuid));
            dbHeadTable.put(Constants.OrderNo, fetchJsonHeaderObject.getString(Constants.OrderNo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));
            dbHeadTable.put(Constants.PONo, fetchJsonHeaderObject.getString(Constants.PONo));
            dbHeadTable.put(Constants.PODate, fetchJsonHeaderObject.getString(Constants.PODate));
            dbHeadTable.put(Constants.FromCPGUID, fetchJsonHeaderObject.getString(Constants.FromCPGUID));
            dbHeadTable.put(Constants.FromCPNo, fetchJsonHeaderObject.getString(Constants.FromCPNo));
            dbHeadTable.put(Constants.FromCPName, fetchJsonHeaderObject.getString(Constants.FromCPName));
            dbHeadTable.put(Constants.FromCPTypId, fetchJsonHeaderObject.getString(Constants.FromCPTypId));
            dbHeadTable.put(Constants.FromCPTypDs, fetchJsonHeaderObject.getString(Constants.FromCPTypDs));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToId, fetchJsonHeaderObject.getString(Constants.SoldToId));
            dbHeadTable.put(Constants.SoldToUID, fetchJsonHeaderObject.getString(Constants.SoldToUID));
            dbHeadTable.put(Constants.SoldToDesc, fetchJsonHeaderObject.getString(Constants.SoldToDesc));
            dbHeadTable.put(Constants.SoldToType, fetchJsonHeaderObject.getString(Constants.SoldToType));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.FirstName, fetchJsonHeaderObject.getString(Constants.FirstName));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));
            dbHeadTable.put(Constants.GrossAmt, fetchJsonHeaderObject.getString(Constants.GrossAmt));
            dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.getString(Constants.NetPrice));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));

            //geofencing...
            dbHeadTable.put(Constants.Distance, fetchJsonHeaderObject.getString(Constants.Distance));
            dbHeadTable.put(Constants.Longitude, fetchJsonHeaderObject.getString(Constants.Longitude));
            dbHeadTable.put(Constants.Latitude, fetchJsonHeaderObject.getString(Constants.Latitude));
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));



            //............//.............//..........................
            try {
                dbHeadTable.put(Constants.BillToCPGUID, fetchJsonHeaderObject.getString(Constants.BillToCPGUID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getROHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            dbHeadTable.put(Constants.SSROGUID, fetchJsonHeaderObject.getString(Constants.SSROGUID));
            dbHeadTable.put(Constants.OrderNo, fetchJsonHeaderObject.getString(Constants.OrderNo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));
            dbHeadTable.put(Constants.FromCPGUID, fetchJsonHeaderObject.getString(Constants.FromCPGUID));
            dbHeadTable.put(Constants.FromCPNo, fetchJsonHeaderObject.getString(Constants.FromCPNo));
            dbHeadTable.put(Constants.FromCPName, fetchJsonHeaderObject.getString(Constants.FromCPName));
            dbHeadTable.put(Constants.FromCPTypId, fetchJsonHeaderObject.getString(Constants.FromCPTypId));
            dbHeadTable.put(Constants.FromCPTypDs, fetchJsonHeaderObject.getString(Constants.FromCPTypDs));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToId, fetchJsonHeaderObject.getString(Constants.SoldToId));
            dbHeadTable.put(Constants.SoldToUID, fetchJsonHeaderObject.getString(Constants.SoldToUID));
            dbHeadTable.put(Constants.SoldToDesc, fetchJsonHeaderObject.getString(Constants.SoldToDesc));
            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
            dbHeadTable.put(Constants.SoldToTypDesc, fetchJsonHeaderObject.getString(Constants.SoldToTypDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.FirstName, fetchJsonHeaderObject.getString(Constants.FirstName));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.StatusID, fetchJsonHeaderObject.getString(Constants.StatusID));
            dbHeadTable.put(Constants.ApprovalStatusID, fetchJsonHeaderObject.getString(Constants.ApprovalStatusID));
            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getFeedbackHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            //noinspection unchecked
            dbHeadTable.put(Constants.FeebackGUID, fetchJsonHeaderObject.getString(Constants.FeebackGUID));
            //noinspection unchecked
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            //noinspection unchecked
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            //noinspection unchecked
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));

            //noinspection unchecked
            dbHeadTable.put(Constants.FeedbackType, fetchJsonHeaderObject.getString(Constants.FeedbackType));
            dbHeadTable.put(Constants.FeedbackTypeDesc, fetchJsonHeaderObject.getString(Constants.FeedbackTypeDesc));

            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));


            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));

            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));

            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));

            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));

            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            dbHeadTable.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));
            dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            dbHeadTable.put(Constants.ParentTypDesc, fetchJsonHeaderObject.getString(Constants.ParentTypDesc));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getSSInvoiceHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {
            try {
                dbHeadTable.put(Constants.BeatGUID, fetchJsonHeaderObject.getString(Constants.BeatGUID));
            } catch (JSONException e) {
                dbHeadTable.put(Constants.BeatGUID, "");
            }
            dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.InvoiceNo, fetchJsonHeaderObject.getString(Constants.InvoiceNo));
            dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
            dbHeadTable.put(Constants.InvoiceTypeDesc, fetchJsonHeaderObject.getString(Constants.InvoiceTypeDesc));
            dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
            dbHeadTable.put(Constants.PONo, fetchJsonHeaderObject.getString(Constants.PONo));
            dbHeadTable.put(Constants.PODate, fetchJsonHeaderObject.getString(Constants.PODate));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
            dbHeadTable.put(Constants.SoldToName, fetchJsonHeaderObject.getString(Constants.SoldToName));
            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
            dbHeadTable.put(Constants.SoldToTypeDesc, fetchJsonHeaderObject.getString(Constants.SoldToTypeDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getExpenseHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            dbHeadTable.put(Constants.ExpenseGUID, fetchJsonHeaderObject.getString(Constants.ExpenseGUID));
            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.ExpenseNo, fetchJsonHeaderObject.getString(Constants.ExpenseNo));
            dbHeadTable.put(Constants.FiscalYear, fetchJsonHeaderObject.getString(Constants.FiscalYear));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.ExpenseType, fetchJsonHeaderObject.getString(Constants.ExpenseType));
            dbHeadTable.put(Constants.ExpenseTypeDesc, fetchJsonHeaderObject.getString(Constants.ExpenseTypeDesc));
            dbHeadTable.put(Constants.ExpenseDate, fetchJsonHeaderObject.getString(Constants.ExpenseDate));
            dbHeadTable.put(Constants.Status, fetchJsonHeaderObject.getString(Constants.Status));
            dbHeadTable.put(Constants.StatusDesc, fetchJsonHeaderObject.getString(Constants.StatusDesc));
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getSecondaryInvHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
            dbHeadTable.put(Constants.InvoiceNo, fetchJsonHeaderObject.getString(Constants.InvoiceNo));
            dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
            dbHeadTable.put(Constants.InvoiceTypeDesc, fetchJsonHeaderObject.getString(Constants.InvoiceTypeDesc));
            dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));
            dbHeadTable.put(Constants.PONo, fetchJsonHeaderObject.getString(Constants.PONo));
            dbHeadTable.put(Constants.PODate, fetchJsonHeaderObject.getString(Constants.PODate));
            dbHeadTable.put(Constants.DeliveryDate, fetchJsonHeaderObject.getString(Constants.DeliveryDate));
            dbHeadTable.put(Constants.DeliveryPerson, fetchJsonHeaderObject.getString(Constants.DeliveryPerson));
            dbHeadTable.put(Constants.DriverName, fetchJsonHeaderObject.getString(Constants.DriverName));
            dbHeadTable.put(Constants.DriverMobile, fetchJsonHeaderObject.getString(Constants.DriverMobile));
            dbHeadTable.put(Constants.TransVhclId, fetchJsonHeaderObject.getString(Constants.TransVhclId));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.FromCPTypDs, fetchJsonHeaderObject.getString(Constants.FromCPTypDs));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
            dbHeadTable.put(Constants.SoldToUID, fetchJsonHeaderObject.getString(Constants.SoldToUID));
            dbHeadTable.put(Constants.SoldToName, fetchJsonHeaderObject.getString(Constants.SoldToName));
            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.RefDocGUID, fetchJsonHeaderObject.getString(Constants.RefDocGUID));
            dbHeadTable.put(Constants.BeatGUID, fetchJsonHeaderObject.getString(Constants.BeatGUID));
            dbHeadTable.put(Constants.PaymentModeID, fetchJsonHeaderObject.getString(Constants.PaymentModeID));
            dbHeadTable.put(Constants.PaymentModeDesc, fetchJsonHeaderObject.getString(Constants.PaymentModeDesc));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));
            dbHeadTable.put(Constants.GrossAmount, fetchJsonHeaderObject.getString(Constants.GrossAmount));
            dbHeadTable.put(Constants.NetAmount, fetchJsonHeaderObject.getString(Constants.NetAmount));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static int MAX_LENGTH = 100;
    public static final int DATE_DIALOG_ID = 0;
    public static String WeeklyOffDesc = "WeeklyOffDesc";

    //Anns : Constants
    public static final String ErrorInParser = "Error in initializing the parser!";
    public static final String ODATA_METADATA_COMMAND = "$metadata";

    public static final String ATOM_CONTENT_TYPE = "application/atom+xml";

    public static final String HTTP_CONTENT_TYPE = "content-type";
    public static final String ODATA_TOP_FILTER = "$top=";
    public static final String ODATA_FILTER = "$filter=";
    public static final String RequestFlushResponse = "requestFlushResponse - status code ";
    public static final String OfflineStoreRequestFailed = "offlineStoreRequestFailed";
    public static final String PostedSuccessfully = "posted successfully";
    public static final String SynchronizationCompletedSuccessfully = "Synchronization completed successfully";
    public static final String OfflineStoreFlushStarted = "offlineStoreFlushStarted";
    public static final String OfflineStoreFlushFinished = "offlineStoreFlushFinished";
    public static final String OfflineStoreFlushSucceeded = "offlineStoreFlushSucceeded";
    public static final String OfflineStoreFlushFailed = "offlineStoreFlushFailed";
    public static final String FlushListenerNotifyError = "FlushListener::notifyError";

    public static final String OfflineStoreRefreshStarted = "OfflineStoreRefreshStarted";
    public static final String OfflineStoreRefreshSucceeded = "OfflineStoreRefreshSucceeded";
    public static final String OfflineStoreRefreshFailed = "OfflineStoreRefreshFailed";
    public static final String ALL = "ALL";

    public static final String MerchandisingSnapshot = "Merchandising Snapshot";


    public static final String RequestCacheResponse = "requestCacheResponse";
    public static final String RequestFailed = "requestFailed";
    public static final String Status_message = "status message";
    public static final String Status_code = "status code";
    public static final String RequestFinished = "requestFinished";
    public static final String RequestServerResponse = "requestServerResponse";
    public static final String BeforeReadRequestServerResponse = "Before Read requestServerResponse";
    public static final String BeforeReadentity = "Before Read entity";
    public static final String AfterReadentity = "After Read entity";
    public static final String RequestStarted = "requestStarted";
    public static final String Product = "Product";


    public static final String OfflineRequestListenerNotifyError = "OfflineRequestListener::notifyError";
    public static final String ErrorWhileRequest = "Error while request";

    public static final String TimeStamp = "TimeStamp";
    public static final String SyncTableHistory = "Sync table(History)";
    public static final String CollList = "CollList";
    public static final String SyncOnRequestSuccess = "Sync::onRequestSuccess";
    public static final String SubmittingDeviceCollectionsPleaseWait = "Submitting device collections, please wait";
    public static final String SubmittingDeviceInvoicesPleaseWait = "Submitting device invoices, please wait";

    public static final String Collection = "Collection";
    public static final String Merchendising_Snap = "Merchendising Snapshot";
    public static final String IMGTYPE = "JPEG";


    public static final String OfflineStoreOpenFailed = "offlineStoreOpenFailed";
    public static final String OfflineStoreOpenedFailed = "Offline store opened failed";
    public static final String OfflineStoreStateChanged = "offlineStoreStateChanged";

    public static final String OfflineStoreOpenFinished = "offlineStoreOpenFinished";

    public static final String Requestsuccess_status_message_key = "requestsuccess - status message key";
    public static final String RequestFailed_status_message = "requestFailed - status message ";
    public static final String RequestServerResponseStatusCode = "requestServerResponse - status code";
    public static final String FeedbackCreated = "Feedback created";
    public static final String RequestsuccessStatusMessageBeforeSuccess = "requestsuccess - status message before success";

    public static final String OnlineRequestListenerNotifyError = "OnlineRequestListener::notifyError";
    public static final String HTTP_HEADER_SUP_APPCID = "X-SUP-APPCID";

    public static final String HTTP_HEADER_SMP_APPCID = "X-SMP-APPCID";
    public static final String[][] billAges = {{"00", "01", "02", "03", "04"}, {"All", "0 - 30 Days", "31 - 60 Days", "61 - 90 Days", "> 90 Days"}};
    public static final String SalesPersonName = "SalesPersonName";


    public static final String DeviceCollectionsText = "Device Collections";
    public static final String ItemsText = "ITEMS";

    public static final String H = "H";

    public static final String All = "All";

    public static final String SSInvoices = "SSInvoices";
    public static final String MatCode = "MatCode";
    public static final String MatDesc = "MatDesc";

    public static final String Qty = "Qty";
    public static final String SSInvoice = "SSInvoice";
    public static final String InvList = "InvList";
    public static final String SnapshotList = "Snapshot List";


    public static final String plain_text = "plain/text";

    public static final String send_email = "Send your email in:";

    public static final String error_txt = "Error :";

    public static final String whatsapp_packagename = "com.whatsapp";

    public static final String whatsapp_conv_packagename = "com.whatsapp.Conversation";

    public static final String whatsapp_domainname = "@s.whatsapp.net";

    public static final String jid = "jid";

    public static final String sms_txt = "sms:";

    public static final String tel_txt = "tel:";


    public static final String[] beatsArray = {"All"};

    public static final String AdhocList = "AdhocList";

    public static final String comingFrom = "ComingFrom";
    public static final String RetailerChange = "RetailerChange";


    public static final String red_hex_color_code = "#D32F2F";

    public static final String salesPersonName = "SalesPersonName";

    public static final String salesPersonMobileNo = "SalesPersonMobileNo";

    public static final String statusID_03 = "03";

    public static final String dtFormat_ddMMyyyywithslash = "dd/MM/yyyy";

    public static final String X = "X";

    public static final String offlineStoreRequestFailed = "offlineStoreRequestFailed";

    public static final String[] reportsArray = {"Bill History",
            "Collection History", "Outstanding", MustSellProduct, FocusedProduct,
            NewLaunchedProduct, "Merchandising List", "Retailer Trends", "Stock", "Feedback", "Competitor List",
            "Return Order History", "Secondary Sales Order", "Customer Complaints"};

    public static final String isPasswordSaved = "isPasswordSaved";

    public static final String isDeviceRegistered = "isDeviceRegistered";

    public static final String appEndPoint_Key = "appEndPoint";

    public static final String pushEndPoint_Key = "pushEndPoint";

    public static final String RetDetails = "RetDetails";

    public static final String RetailerList = "RetailerList";

    public static final String Retailer = "Retailer";

    public static final String NAVFROM = "NAVFROM";

    public static final String getSyncHistory = "getSyncHistory: ";

    public static final String time_stamp = "Time Stamp";

    public static final String[] syncMenu = {"All", "Download", "Upload", "Sync History"};

    public static final String isLocalFilterQry = "?$filter= sap.islocal() ";
    public static final String isNonLocalFilterQry = "?$filter= not sap.islocal() ";
    public static final String device_reg_failed_txt = "Device registration failed";

    public static final String SHOWNOTIFICATION = "SHOWNOTIFICATION";

    public static final String timeStamp = "TimeStamp";

    public static final String sync_table_history_txt = "Sync table(History)";

    public static final String getLastSyncTimeStamp(String tableName, String columnName, String columnValue) {
        return "select *  from  " + tableName + " Where " + columnName + "='" + columnValue + "'  ;";

    }

    public static final String ITEM_TXT = "ITEMS";
    public static final String ITEM_TXT1 = "ITEMS1";
    public static final String SecondarySOCreate = "Secondary SO Create";
    public static final String OutletSurveyCreate = "Outlet Survey Create";
    public static final String OutletSurveyUpdate = "OutletSurveyUpdate";
    public static final String SampleDisbursement = "SampleDisbursement";
    public static final String SampleDisbursementDesc = "Sample Disbursement";
    public static final String ReturnOrderCreate = "Return Order Create";
    public static final String CustomerComplaintsCreate = "Consumer Complaints Create";
    public static final String TestRun_Text = "M";
    public static final String arteria_dayfilter = "x-arteria-daysfilter";
    public static final String arteria_mobile_scheme = "x-mobile";
    public static final String RouteType = "RouteType";
    public static final String CPCreateKey = "CPCreateKey";
    public static final String BeatPlan = "BeatPlan";

    public static final String NonFieldWork = "NonFieldWork";

    public static final String sync_req_sucess_txt = "Sync::onRequestSuccess";

    public static final String collection = "Collection";

    public static final String entityType = "EntityType";

    public static final String savePass = "savePass";

    public static final String offlineDBPath = "/data/com.arteriatech.ss.msecsales/files/mSecSales_Offline.udb";

    public static final String offlineReqDBPath = "/data/com.arteriatech.ss.msecsales/files/mSecSales_Offline.rq.udb";
    public static final String isFirstTimeReg = "isFirstTimeReg";
    public static final String isReIntilizeDB = "isReIntilizeDB";


    public static final String[] todayIconArray = {"Start", "Beat Work", "Alerts",
            "Adhoc Visit", "Create Retailer", "Update Retailer",
            "My Stock", "My Targets", "DB Stock",
            "Help Line", "Behaviour",
            "Schemes", "My Performance", "Day Summary",
            "Visit & MTD Summary", "Visual Aid", "Expense Entry", "Digital Product", "Schemes", "Distributor", "Outlet Survey"};
    /*{ "Start", "Beat Work",  "My Targets",
            "Schemes","DB Stock&Price","Visit Summary",
			"Expense Entry",
			"Visual Aid","Adhoc Visit","Alerts"};*/

    public static final String[] reportIconArray = {"Retailers", "My Targets", "My Performance"};
    /*{ "Retailer List","Behaviour","Appointment"};*/

    public static final String[] admintIconArray = {"Sync", "Log", ""};

    public static final String BeatType = "BeatType";

    public static final String RouteList = "RouteList";

    public static final String VisitType = "VisitType";


    public static final String CustomerList = "CustomerList";


    public static final String Address = "Address";
    public static final String Visit = "Visit";
    public static final String Reports = "Reports";
    public static final String Summary = "Summary";

    public static final String default_txt = "default";

    public static final String logon_finished_appcid = "onLogonFinished: appcid:";

    public static final String logon_finished_aendpointurl = "onLogonFinished: endpointurl:";


    public static final String isFromNotification = "isFromNotification";

    public static final String username = "username";

    public static final String VisitSeqId = "VisitSeqId";
    public static final String BirthdayAlertsCount = "BirthdayAlertsCount";
    public static final String TextAlertsCount = "TextAlertsCount";
    public static final String AppointmentAlertsCount = "AppointmentAlertsCount";

    public static final String RouteBased = "RouteBased";

    public static final String full_Day = "Full Day";


    public static final String first_half = "1st Half";

    public static final String second_half = "2nd Half";

    public static final String[][] arrWorkType = {{"01", "02"}, {"Full Day", "Split"}};

    public static int[] IconVisibiltyReportFragment = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public static int[] IconPositionReportFragment = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    public static final String DeviceStatus = "DeviceStatus";
    public static final String InvDate = "InvDate";
    public static final String InvAmount = "InvAmount";
    public static final String DeviceNo = "DeviceNo";

    public static final String RetailerNo = "RetailerNo";

    public static final String FFDA33 = "#FFDA33";

    public static final String EntitySet = "EntitySet";

    public static final String T = "T";

    public static final String getCollectionSuccessMsg(String fipDocNo) {
        return "Collection # " + fipDocNo + " created";
    }

    public static final String getSOSuccessMsg(String fipDocNo) {
        return "SO # " + fipDocNo + " created";
    }

    public static final String getROSuccessMsg(String fipDocNo) {
        return "RO # " + fipDocNo + " created";
    }

    public static String isSOCreateKey = "isSOCreate";
    public static String isSOCreateTcode = "/ARTEC/SS_SOCRT";

    public static String isCollCreateEnabledKey = "isCollCreateEnabled";
    public static String isCollCreateTcode = "/ARTEC/SS_FICRT";

    public static String isSampleDisbursmentEnabledKey = "isSampleDisbursmentCreateEnabled";
    public static String isSampleDisbursmentCreateTcode = "/ARTEC/SS_SAMPCRT";

    public static String isCustomerComplaintEnabledKey = "isCustomerComplaintCreateEnabled";
    public static String isCustomerComplaintCreateTcode = "/ARTEC/SF_CUSTCOMPCRT";

    public static String isMerchReviewKey = "isMerCreateEnabled";
    public static String isMerchReviewTcode = "/ARTEC/SS_MERRVW";

    public static String isMerchReviewListKey = "isMerCreateListEnabled";
    public static String isMerchReviewListTcode = "/ARTEC/SS_MERRVWLST";

    private static String isStockListKey = "isStocksListEnabled";
    private static String isStockListTcode = "/ARTEC/SS_CPSTKLIST";

    private static String isFeedBackListKey = "isFeedBackListEnabled";
    private static String isFeedBackListTcode = "/ARTEC/SF_FDBKLIST";

    private static String isCompetitorListKey = "isCompetitorListEnabled";
    private static String isCompetitorListTcode = "/ARTEC/SF_COMPINFOLST";

    public static String isComplintsListKey = "isComplintsListEnabled";
    public static String isComplintsListTcode = "/ARTEC/SF_CUSTCOMPLST";

    private static String isReturnOrderListKey = "isReturnOrderListEnabled";
    private static String isReturnOrderListTcode = "/ARTEC/SS_ROLIST";

    public static String isSecondarySalesListKey = "isSecondarySalesListEnabled";
    public static String isSecondarySalesListTcode = "/ARTEC/SS_SOLIST";

    private static String isSampleDisbursmentListKey = "isSampleDisbursmentListEnabled";
    private static String isSampleDisbursmentListTcode = "/ARTEC/SS_SMPLST";

    private static String isCustomerComplaintListKey = "isCustomerComplaintListEnabled";
    private static String isCustomerComplaintListTcode = "/ARTEC/SF_CUSTCOMPLST";

    public static String isMustSellKey = "isMustSellEnabled";
    public static String isMustSellTcode = "/ARTEC/MC_MSTSELL";

    public static String isFocusedProductKey = "isFocusedProductEnabled";
    public static String isFocusedProductTcode = "/ARTEC/SS_FOCPROD";

    public static String isNewProductKey = "isNewProductEnabled";
    public static String isNewProductTcode = "/ARTEC/SS_NEWPROD";

    public static String isDBStockKey = "isDBStockEnabled";
    public static String isDBStockTcode = "/ARTEC/SS_DBSTK";

    public static String isCompInfoEnabled = "isCompInfoEnabled";
    public static String isCompInfoTcode = "/ARTEC/SS_COMPINFO";

    public static String isFeedbackCreateKey = "isFeedbackCreateEnabled";
    public static String isFeedbackTcode = "/ARTEC/SS_FDBKCRT";

    public static String isInvoiceCreateKey = "isCreateInvoiceEnabled";
    public static String isInvoiceTcode = "/ARTEC/SS_INVCRT";

    public static String isReturnOrderCreateEnabled = "isReturnOrderCreateEnabled";
    public static String isReturnOrderTcode = "/ARTEC/SS_RETURNORDER";

    public static String isDaySummaryKey = "isDaySummaryEnabled";
    public static String isDaySummaryTcode = "/ARTEC/SS_DAYSMRY";

    public static String isDlrBehaviourKey = "isBehaviourEnabled";
    public static String isDlrBehaviourTcode = "/ARTEC/SS_SPCP_EVAL";

    public static String isRetailerStockKey = "isRetailerStock";
    public static String isRetailerStockTcode = "/ARTEC/SS_CPSTK";

    public static String isVisitSummaryKey = "isVisitSummaryEnabled";
    public static String isVisitSummaryTcode = "/ARTEC/SS_VISTSMRY";

    public static String isVisualAidKey = "isVisualAidEnabled";
    public static String isVisualAidTcode = "/ARTEC/SS_VSLADS";

    public static String isWindowDisplayKey = "isWindowDisplayEnabled";
    public static String isWindowDisplayTcode = "/ARTEC/SS_WIN_DISPLAY";

    public static String isExpenseEntryKey = "isExpenseEntryEnabled";
    public static String isExpenseEntryTcode = "/ARTEC/SS_EXP_ENTRY";

    public static String isDigitalProductEntryKey = "isDigitalProductEntryEnabled";
    public static String isDigitalProductEntryTcode = "/ARTEC/SS_DGTPRD";

    public static String isSchemeKey = "isSchemeEnabled";
    public static String isSchemeTcode = "/ARTEC/SS_SCHEMES";

    public static String isDistributorKey = "isDistributorEnabled";
    public static String isDistributorTcode = "/ARTEC/SS_DIST_UPD";

    public static String isBeatOptmKey = "isBeatOptmEnabled";
    public static String isBeatOptmTcode = "/ARTEC/SS_BEAT_OPTM";

    public static final void updateTCodetoSharedPreference(SharedPreferences sharedPreferences, SharedPreferences.Editor editor, ArrayList<Config> authList) {
        if (authList != null && authList.size() > 0) {
            if (sharedPreferences.contains("isStartCloseEnabled")) {
                editor.remove("isStartCloseEnabled");
            }
            if (sharedPreferences.contains("isRetailerListEnabled")) {
                editor.remove("isRetailerListEnabled");
            }
            if (sharedPreferences.contains("isRetailerUpdate")) {
                editor.remove("isRetailerUpdate");
            }
            if (sharedPreferences.contains("isRetailerCreate")) {
                editor.remove("isRetailerCreate");
            }
            if (sharedPreferences.contains("isHelpLine")) {
                editor.remove("isHelpLine");
            }
            if (sharedPreferences.contains("isMyStock")) {
                editor.remove("isMyStock");
            }
            if (sharedPreferences.contains("isVisitCreate")) {
                editor.remove("isVisitCreate");
            }
            if (sharedPreferences.contains("isTrends")) {
                editor.remove("isTrends");
            }
            if (sharedPreferences.contains("isRetailerStock")) {
                editor.remove("isRetailerStock");
            }
            if (sharedPreferences.contains("isCollHistory")) {
                editor.remove("isCollHistory");
            }
            if (sharedPreferences.contains("isInvHistory")) {
                editor.remove("isInvHistory");
            }
            if (sharedPreferences.contains("isRouteEnabled")) {
                editor.remove("isRouteEnabled");
            }
            if (sharedPreferences.contains("isAdhocVisitEnabled")) {
                editor.remove("isAdhocVisitEnabled");
            }
            if (sharedPreferences.contains("isTariffEnabled")) {
                editor.remove("isTariffEnabled");
            }
            if (sharedPreferences.contains("isSchemeEnabled")) {
                editor.remove("isSchemeEnabled");
            }
            if (sharedPreferences.contains("isBehaviourEnabled")) {
                editor.remove("isBehaviourEnabled");
            }
            if (sharedPreferences.contains("isMyTargetsEnabled")) {
                editor.remove("isMyTargetsEnabled");
            }
            if (sharedPreferences.contains("isMyPerformanceEnabled")) {
                editor.remove("isMyPerformanceEnabled");
            }
            if (sharedPreferences.contains(isFocusedProductKey)) {
                editor.remove(isFocusedProductKey);
            }
            if (sharedPreferences.contains(isNewProductKey)) {
                editor.remove(isNewProductKey);
            }
            if (sharedPreferences.contains(isFocusedProductKey)) {
                editor.remove(isFocusedProductKey);
            }
            if (sharedPreferences.contains("isOutstandingHistory")) {
                editor.remove("isOutstandingHistory");
            }
            if (sharedPreferences.contains(isDBStockKey)) {
                editor.remove(isDBStockKey);
            }
            if (sharedPreferences.contains(isVisualAidKey)) {
                editor.remove(isVisualAidKey);
            }
            if (sharedPreferences.contains(isRetailerStockKey)) {
                editor.remove(isRetailerStockKey);
            }
            if (sharedPreferences.contains(isDaySummaryKey)) {
                editor.remove(isDaySummaryKey);
            }
            if (sharedPreferences.contains(isDlrBehaviourKey)) {
                editor.remove(isDlrBehaviourKey);
            }

            if (sharedPreferences.contains(isSchemeKey)) {
                editor.remove(isSchemeKey);
            }

            if (sharedPreferences.contains("isActStatusEnabled")) {
                editor.remove("isActStatusEnabled");
            }
            if (sharedPreferences.contains(isMerchReviewKey)) {
                editor.remove(isMerchReviewKey);
            }
            if (sharedPreferences.contains(isMerchReviewListKey)) {
                editor.remove(isMerchReviewListKey);
            }
            if (sharedPreferences.contains(isSOCreateKey)) {
                editor.remove(isSOCreateKey);
            }
            if (sharedPreferences.contains(isVisitSummaryKey)) {
                editor.remove(isVisitSummaryKey);
            }
            if (sharedPreferences.contains(isInvoiceCreateKey)) {
                editor.remove(isInvoiceCreateKey);
            }
            if (sharedPreferences.contains(isCollCreateEnabledKey)) {
                editor.remove(isCollCreateEnabledKey);
            }
            if (sharedPreferences.contains("isFeedbackCreateEnabled")) {
                editor.remove("isFeedbackCreateEnabled");
            }
            if (sharedPreferences.contains("isCompInfoEnabled")) {
                editor.remove("isCompInfoEnabled");
            }
            if (sharedPreferences.contains(isStockListKey)) {
                editor.remove(isStockListKey);
            }
            if (sharedPreferences.contains(isFeedBackListKey)) {
                editor.remove(isFeedBackListKey);
            }
            if (sharedPreferences.contains(isCompetitorListKey)) {
                editor.remove(isCompetitorListKey);
            }
            if (sharedPreferences.contains(isReturnOrderListKey)) {
                editor.remove(isReturnOrderListKey);
            }
            if (sharedPreferences.contains(isSecondarySalesListKey)) {
                editor.remove(isSecondarySalesListKey);
            }
            if (sharedPreferences.contains(isSampleDisbursmentEnabledKey)) {
                editor.remove(isSampleDisbursmentEnabledKey);
            }
            if (sharedPreferences.contains(isCustomerComplaintEnabledKey)) {
                editor.remove(isCustomerComplaintEnabledKey);
            }
            if (sharedPreferences.contains(isReturnOrderCreateEnabled)) {
                editor.remove(isReturnOrderCreateEnabled);
            }
            if (sharedPreferences.contains(isFeedbackCreateKey)) {
                editor.remove(isFeedbackCreateKey);
            }
            if (sharedPreferences.contains(isFeedbackCreateKey)) {
                editor.remove(isFeedbackCreateKey);
            }
            if (sharedPreferences.contains(isWindowDisplayKey)) {
                editor.remove(isWindowDisplayKey);
            }
            if (sharedPreferences.contains(isSampleDisbursmentListKey)) {
                editor.remove(isSampleDisbursmentListKey);
            }
            if (sharedPreferences.contains(isCustomerComplaintListKey)) {
                editor.remove(isCustomerComplaintListKey);
            }
            if (sharedPreferences.contains(isExpenseEntryKey)) {
                editor.remove(isExpenseEntryKey);
            }
            if (sharedPreferences.contains(isDigitalProductEntryKey)) {
                editor.remove(isDigitalProductEntryKey);
            }

            if (sharedPreferences.contains(isDistributorKey)) {
                editor.remove(isDistributorKey);
            }

            if (sharedPreferences.contains(isBeatOptmKey)) {
                editor.remove(isBeatOptmKey);
            }

            editor.commit();

            for (int incVal = 0; incVal < authList.size(); incVal++) {
                if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/MC_ATTND")) {
                    editor.putString("isStartCloseEnabled", "/ARTEC/MC_ATTND");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_CP_GETLST")) {
                    editor.putString("isRetailerListEnabled", "/ARTEC/SS_CP_GETLST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_CP_CHG")) {
                    editor.putString("isRetailerUpdate", "/ARTEC/SS_CP_CHG");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_CP_CRT")) {
                    editor.putString("isRetailerCreate", "/ARTEC/SS_CP_CRT");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_HELPLINE")) {
                    editor.putString("isHelpLine", "/ARTEC/SF_HELPLINE");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_MYSTK")) {
                    editor.putString("isMyStock", "/ARTEC/SF_MYSTK");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_VST")) {
                    editor.putString("isVisitCreate", "/ARTEC/SF_VST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_TRENDS")) {
                    editor.putString("isTrends", "/ARTEC/SF_TRENDS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_CPSTK")) {
                    editor.putString(isRetailerStockKey, "/ARTEC/SS_CPSTK");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_COLLHIS")) {
                    editor.putString("isCollHistory", "/ARTEC/SS_COLLHIS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_INVHIS")) {
                    editor.putString("isInvHistory", "/ARTEC/SS_INVHIS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_ROUTPLAN")) {
                    editor.putString("isRouteEnabled", "/ARTEC/SS_ROUTPLAN");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/ADHOC_VST")) {
                    editor.putString("isAdhocVisitEnabled", "/ARTEC/ADHOC_VST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_TARIFF")) {
                    editor.putString("isTariffEnabled", "/ARTEC/SS_TARIFF");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_SCHEME")) {
                    editor.putString("isSchemeEnabled", "/ARTEC/SS_SCHEME");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_SPCP_EVAL")) {
                    editor.putString("isBehaviourEnabled", "/ARTEC/SS_SPCP_EVAL");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_MYTRGTS")) {
                    editor.putString("isMyTargetsEnabled", "/ARTEC/SS_MYTRGTS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_MYPERF")) {
                    editor.putString("isMyPerformanceEnabled", "/ARTEC/SS_MYPERF");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isFocusedProductTcode)) {
                    editor.putString(isFocusedProductKey, isFocusedProductTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isNewProductTcode)) {
                    editor.putString(isNewProductKey, isNewProductTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMustSellTcode)) {
                    editor.putString(isMustSellKey, isMustSellTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_ACTSTS")) {
                    editor.putString("isActStatusEnabled", "/ARTEC/SS_ACTSTS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMerchReviewTcode)) {
                    editor.putString(isMerchReviewKey, isMerchReviewTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isInvoiceTcode)) {
                    editor.putString(isInvoiceCreateKey, isInvoiceTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCollCreateTcode)) {
                    editor.putString(isCollCreateEnabledKey, isCollCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_FDBKCRT")) {
                    editor.putString("isFeedbackCreateEnabled", "/ARTEC/SS_FDBKCRT");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_COMPINFO")) {
                    editor.putString("isCompInfoEnabled", "/ARTEC/SS_COMPINFO");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_OUTSTND")) {
                    editor.putString("isOutstandingHistory", "/ARTEC/SS_OUTSTND");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMerchReviewListTcode)) {
                    editor.putString(isMerchReviewListKey, isMerchReviewListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSOCreateTcode)) {
                    editor.putString(isSOCreateKey, isSOCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDBStockTcode)) {
                    editor.putString(isDBStockKey, isDBStockTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDaySummaryTcode)) {
                    editor.putString(isDaySummaryKey, isDaySummaryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDlrBehaviourTcode)) {
                    editor.putString(isDlrBehaviourKey, isDlrBehaviourTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRetailerStockTcode)) {
                    editor.putString(isRetailerStockKey, isRetailerStockTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isVisitSummaryTcode)) {
                    editor.putString(isVisitSummaryKey, isVisitSummaryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isVisualAidTcode)) {
                    editor.putString(isVisualAidKey, isVisualAidTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isStockListTcode)) {
                    editor.putString(isStockListKey, isStockListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isFeedBackListTcode)) {
                    editor.putString(isFeedBackListKey, isFeedBackListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCompetitorListTcode)) {
                    editor.putString(isCompetitorListKey, isCompetitorListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isReturnOrderListTcode)) {
                    editor.putString(isReturnOrderListKey, isReturnOrderListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSecondarySalesListTcode)) {
                    editor.putString(isSecondarySalesListKey, isSecondarySalesListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSampleDisbursmentCreateTcode)) {
                    editor.putString(isSampleDisbursmentEnabledKey, isSampleDisbursmentCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCustomerComplaintCreateTcode)) {
                    editor.putString(isCustomerComplaintEnabledKey, isCustomerComplaintCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isReturnOrderTcode)) {
                    editor.putString(isReturnOrderCreateEnabled, isReturnOrderTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isWindowDisplayTcode)) {
                    editor.putString(isWindowDisplayKey, isWindowDisplayTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSampleDisbursmentListTcode)) {
                    editor.putString(isSampleDisbursmentListKey, isSampleDisbursmentListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCustomerComplaintListTcode)) {
                    editor.putString(isCustomerComplaintListKey, isCustomerComplaintListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isExpenseEntryTcode)) {
                    editor.putString(isExpenseEntryKey, isExpenseEntryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDigitalProductEntryTcode)) {
                    editor.putString(isDigitalProductEntryKey, isDigitalProductEntryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSchemeTcode)) {
                    editor.putString(isSchemeKey, isSchemeTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDistributorTcode)) {
                    editor.putString(isDistributorKey, isDistributorTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isBeatOptmTcode)) {
                    editor.putString(isBeatOptmKey, isBeatOptmTcode);
                }


                editor.commit();
            }
        }
    }

    public static final void setIconVisibiltyReports(SharedPreferences sharedPreferences, int[] mArrIntReportsOriginalStatus) {
        String sharedVal = sharedPreferences.getString("isInvHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_INVHIS")) {
            mArrIntReportsOriginalStatus[0] = 1;
        } else {
            mArrIntReportsOriginalStatus[0] = 0;
        }

        sharedVal = sharedPreferences.getString("isCollHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_COLLHIS")) {
            mArrIntReportsOriginalStatus[1] = 1;
        } else {
            mArrIntReportsOriginalStatus[1] = 0;
        }

        sharedVal = sharedPreferences.getString("isOutstandingHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_OUTSTND")) {
            mArrIntReportsOriginalStatus[2] = 1;
        } else {
            mArrIntReportsOriginalStatus[2] = 0;
        }

        sharedVal = sharedPreferences.getString(isMustSellKey, "");
        if (sharedVal.equalsIgnoreCase(isMustSellTcode)) {
            mArrIntReportsOriginalStatus[3] = 1;
        } else {
            mArrIntReportsOriginalStatus[3] = 0;
        }
        sharedVal = sharedPreferences.getString(isFocusedProductKey, "");
        if (sharedVal.equalsIgnoreCase(isFocusedProductTcode)) {
            mArrIntReportsOriginalStatus[4] = 1;
        } else {
            mArrIntReportsOriginalStatus[4] = 0;
        }

        sharedVal = sharedPreferences.getString(isNewProductKey, "");
        if (sharedVal.equalsIgnoreCase(isNewProductTcode)) {
            mArrIntReportsOriginalStatus[5] = 1;
        } else {
            mArrIntReportsOriginalStatus[5] = 0;
        }

        sharedVal = sharedPreferences.getString(isMerchReviewListKey, "");
        if (sharedVal.equalsIgnoreCase(isMerchReviewListTcode)) {
            mArrIntReportsOriginalStatus[6] = 1;
        } else {
            mArrIntReportsOriginalStatus[6] = 0;
        }


        sharedVal = sharedPreferences.getString("isTrends", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_TRENDS")) {
            mArrIntReportsOriginalStatus[7] = 1;
        } else {
            mArrIntReportsOriginalStatus[7] = 0;
        }
        sharedVal = sharedPreferences.getString(isStockListKey, "");
        if (sharedVal.equalsIgnoreCase(isStockListTcode)) {
            mArrIntReportsOriginalStatus[8] = 1;
        } else {
            mArrIntReportsOriginalStatus[8] = 0;
        }
        sharedVal = sharedPreferences.getString(isFeedBackListKey, "");
        if (sharedVal.equalsIgnoreCase(isFeedBackListTcode)) {
            mArrIntReportsOriginalStatus[9] = 1;
        } else {
            mArrIntReportsOriginalStatus[9] = 0;
        }
        sharedVal = sharedPreferences.getString(isCompetitorListKey, "");
        if (sharedVal.equalsIgnoreCase(isCompetitorListTcode)) {
            mArrIntReportsOriginalStatus[10] = 1;
        } else {
            mArrIntReportsOriginalStatus[10] = 0;
        }
        sharedVal = sharedPreferences.getString(isReturnOrderListKey, "");
        if (sharedVal.equalsIgnoreCase(isReturnOrderListTcode)) {
            mArrIntReportsOriginalStatus[11] = 1;
        } else {
            mArrIntReportsOriginalStatus[11] = 0;
        }
        sharedVal = sharedPreferences.getString(isSecondarySalesListKey, "");
        if (sharedVal.equalsIgnoreCase(isSecondarySalesListTcode)) {
            mArrIntReportsOriginalStatus[12] = 1;
        } else {
            mArrIntReportsOriginalStatus[12] = 0;
        }
        sharedVal = sharedPreferences.getString(isCustomerComplaintListKey, "");
        if (sharedVal.equalsIgnoreCase(isCustomerComplaintListTcode)) {
            mArrIntReportsOriginalStatus[13] = 1;
        } else {
            mArrIntReportsOriginalStatus[13] = 0;
        }

        /*sharedVal =sharedPreferences.getString(isSampleDisbursmentListKey, "");
        if(sharedVal.equalsIgnoreCase(isSampleDisbursmentListTcode)) {
			mArrIntReportsOriginalStatus[14] = 1;
		}else{
			mArrIntReportsOriginalStatus[14] = 0;
		}*/
    }

    public static final String offline_store_not_closed = "Offline store not closed: ";


    public static final String invalid_payload_entityset_expected = "Invalid payload:EntitySet expected but got ";

    public static final String None = "None";
    public static final String str_00 = "00";

    public static final String str_01 = "01";
    public static final String str_2 = "2";

    public static final String str_04 = "04";
    public static final String str_03 = "03";
    public static final String str_3 = "3";
    public static final String str_1 = "1";
    public static final String str_06 = "06";
    public static final String str_05 = "05";
    public static final String str_20 = "20";

    public static final String str_false = "false";

    public static final String str_0 = "0";

    public static final String error_txt1 = "Error";
    public static final String error_archive_called_txt = "Error Arcive is called";


    public static final String error = "error";

    public static final String message = "message";

    public static final String CollectionHeaderTable = "CollectionHeaderTable";

    public static final String value = "value";

    public static Boolean isAlertRecordsAvailable = true;

    public static final void setIconVisibilty(SharedPreferences sharedPreferences, int[] mArrIntMainMenuOriginalStatus, int[] mArrIntMainMenuReportsOriginalStatus) {
        String sharedVal = sharedPreferences.getString("isStartCloseEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/MC_ATTND")) {
            mArrIntMainMenuOriginalStatus[0] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[0] = 0;
        }
        sharedVal = sharedPreferences.getString("isRouteEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_ROUTPLAN")) {
            mArrIntMainMenuOriginalStatus[1] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[1] = 0;
        }
//        if (Constants.isAlertRecordsAvailable) {
        mArrIntMainMenuOriginalStatus[2] = 1;
//        } else {
//            mArrIntMainMenuOriginalStatus[2] = 0;
//        }


        sharedVal = sharedPreferences.getString("isAdhocVisitEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/ADHOC_VST")) {
            mArrIntMainMenuOriginalStatus[3] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[3] = 0;
        }
        sharedVal = sharedPreferences.getString("isRetailerCreate", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_CP_CRT")) {
            mArrIntMainMenuOriginalStatus[4] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[4] = 0;
        }
        sharedVal = sharedPreferences.getString("isRetailerUpdate", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_CP_CHG")) {
            mArrIntMainMenuOriginalStatus[5] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[5] = 0;
        }
        sharedVal = sharedPreferences.getString("isMyStock", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_MYSTK")) {
            mArrIntMainMenuOriginalStatus[6] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[6] = 0;
        }
        sharedVal = sharedPreferences.getString("isMyTargetsEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_MYTRGTS")) {
            mArrIntMainMenuOriginalStatus[7] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[7] = 0;
        }
        sharedVal = sharedPreferences.getString(isDBStockKey, "");
        if (sharedVal.equalsIgnoreCase(isDBStockTcode)) {
            mArrIntMainMenuOriginalStatus[8] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[8] = 0;
        }


        sharedVal = sharedPreferences.getString("isHelpLine", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_HELPLINE")) {
            mArrIntMainMenuOriginalStatus[9] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[9] = 0;
        }

        sharedVal = sharedPreferences.getString(isDlrBehaviourKey, "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_SPCP_EVAL")) {
            mArrIntMainMenuOriginalStatus[10] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[10] = 0;
        }


        mArrIntMainMenuOriginalStatus[11] = 0;

        sharedVal = sharedPreferences.getString("isMyPerformanceEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_MYPERF")) {
            mArrIntMainMenuOriginalStatus[12] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[12] = 0;
        }

        sharedVal = sharedPreferences.getString(isDaySummaryKey, "");
        if (sharedVal.equalsIgnoreCase(isDaySummaryTcode)) {
            mArrIntMainMenuOriginalStatus[13] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[13] = 0;
        }

        sharedVal = sharedPreferences.getString(isVisitSummaryKey, "");
        if (sharedVal.equalsIgnoreCase(isVisitSummaryTcode)) {
            mArrIntMainMenuOriginalStatus[14] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[14] = 0;
        }

        sharedVal = sharedPreferences.getString(isVisualAidKey, "");
        if (sharedVal.equalsIgnoreCase(isVisualAidTcode)) {
            mArrIntMainMenuOriginalStatus[15] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[15] = 0;
        }

        sharedVal = sharedPreferences.getString(isExpenseEntryKey, "");
        if (sharedVal.equalsIgnoreCase(isExpenseEntryTcode)) {
            mArrIntMainMenuOriginalStatus[16] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[16] = 0;
        }

        sharedVal = sharedPreferences.getString(isDigitalProductEntryKey, "");
        if (sharedVal.equalsIgnoreCase(isDigitalProductEntryTcode)) {
            mArrIntMainMenuOriginalStatus[17] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[17] = 0;
        }

        sharedVal = sharedPreferences.getString(isSchemeKey, "");
        if (sharedVal.equalsIgnoreCase(isSchemeTcode)) {
            mArrIntMainMenuOriginalStatus[18] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[18] = 0;
        }


        sharedVal = sharedPreferences.getString("isRetailerListEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_CP_GETLST")) {
            mArrIntMainMenuReportsOriginalStatus[0] = 1;
        } else {
            mArrIntMainMenuReportsOriginalStatus[0] = 0;
        }

        sharedVal = sharedPreferences.getString(isDistributorKey, "");
        if (sharedVal.equalsIgnoreCase(isDistributorTcode)) {
            mArrIntMainMenuOriginalStatus[19] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[19] = 0;
        }
        mArrIntMainMenuOriginalStatus[20] = 0;
        /*if (sharedVal.equalsIgnoreCase(isDistributorTcode)) {
            mArrIntMainMenuOriginalStatus[20] = 1;
        } else {
            mArrIntMainMenuOriginalStatus[20] = 0;
        }*/

    }


    public static final String error_during_offline_close = "Error during store close: ";


    public static final String icurrentDBPath = "/data/com.arteriatech.ss.msecsales/files/mSecSales_Offline.udb";
    public static final String ibackupDBPath = "mSecSales_Offline.udb";


    public static final String icurrentRqDBPath = "/data/com.arteriatech.ss.msecsales/files/mSecSales_Offline.rq.udb";
    public static final String ibackupRqDBPath = "mSecSales_Offline.rq.udb";


    public static final String error_creating_sync_db = "Registration:createSyncDatabase Error while creating sync database";

    public static final String error_in_collection = "Error in Collection :";

    public static final String RetName = "RetName";

    public static final String RetID = "RetID";

    public static final void createDB(SQLiteDatabase db) {
        String sql = "create table if not exists "
                + Constants.DATABASE_REGISTRATION_TABLE
                + "( username  text, password   text,repassword text,themeId text,mainView text);";
        Log.d("EventsData", "onCreate: " + sql);
        db.execSQL(sql);
    }

    public static final void insertHistoryDB(SQLiteDatabase db, String tblName, String clmname, String value) {
        String sql = "INSERT INTO " + tblName + "( " + clmname + ") VALUES('"
                + value + "') ;";
        db.execSQL(sql);
    }

    public static final void updateStatus(SQLiteDatabase db, String tblName, String clmname, String value, String inspectionLot) {
        String sql = "UPDATE " + tblName + " SET  " + clmname + "='" + value
                + "' Where Collections = '" + inspectionLot + "';";
        db.execSQL(sql);
    }


    public static final String delete_from = "DELETE FROM ";

    public static final String create_table = "create table IF NOT EXISTS ";

    public static final String EventsData = "EventsData";

    public static final String on_Create = "onCreate:";

    public static final void createTable(SQLiteDatabase db, String tableName, String clumsname) {
        try {
            String sql = Constants.create_table + tableName
                    + " ( " + clumsname + ", Status text );";
            Log.d(Constants.EventsData, Constants.on_Create + sql);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void deleteTable(SQLiteDatabase db, String tableName) {
        try {
            String delSql = Constants.delete_from + tableName;
            db.execSQL(delSql);

        } catch (Exception e) {
            System.out.println("createTableKey(EventDataSqlHelper): " + e.getMessage());
        }
    }

    public static final String RTGS = "RTGS";

    public static final String NEFT = "NEFT";

    public static final String DD = "DD";

    public static final String Cheque = "Cheque";


    public static boolean BoolTodayBeatLoaded = false;
    public static boolean BoolOtherBeatLoaded = false;
    public static boolean BoolMoreThanOneRoute = false;
    public static String ClosingeDay = "ClosingeDay";
    public static String Today = "Today";
    public static String PreviousDay = "PreviousDay";
    public static String ClosingeDayType = "ClosingeDayType";

    public static boolean BoolAlertsLoaded = false;
    public static boolean BoolAlertsHistoryLoaded = false;

    /*
       TODO Get Current Day Birthdays list
    */
    public static ArrayList<BirthdaysBean> getTodayBirthDayList() {
        ArrayList<BirthdaysBean> alRetBirthDayList = null;
        ArrayList<BirthdaysBean> alAppointmentList = null;
        String[][] oneWeekDay = UtilConstants.getOneweekValues(1);
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {

                String[] splitDayMonth = oneWeekDay[0][i].split("-");

                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String mStrAppointmentListQuery = Constants.Visits + "?$filter=" + Constants.StatusID + " eq '00' and (month%28" + Constants.PlannedDate + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.PlannedDate + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ")";
                try {
                    alAppointmentList = OfflineManager.getAppointmentListForAlert(mStrAppointmentListQuery);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    if (alAppointmentList != null && alAppointmentList.size() > 0) {
                        alRetBirthDayList.addAll(alRetBirthDayList.size(), alAppointmentList);
                    }
                } else {
                    alRetBirthDayList = new ArrayList<>();
                    if (alAppointmentList != null && alAppointmentList.size() > 0) {
                        alRetBirthDayList.addAll(alAppointmentList);
                    }
                }

            }
        }

        return alRetBirthDayList;
    }

    /*
      TODO Get Current Day Birthdays list
   */
    public static ArrayList<BirthdaysBean> getTodayBirthDayListOnly() {
        ArrayList<BirthdaysBean> alRetBirthDayList = null;
        String[][] oneWeekDay = UtilConstants.getOneweekValues(1);
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {
                String[] splitDayMonth = oneWeekDay[0][i].split("-");
                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        }

        return alRetBirthDayList;
    }

    public static void setCurrentDateTOSharedPerf(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.BirthDayAlertsDate, UtilConstants.getDate1());
        editor.commit();

    }

    // TODO add values into data vault
    public static void assignValuesIntoDataVault(ArrayList<BirthdaysBean> alRetBirthDayList) {

        Gson gson = new Gson();
        Hashtable dbHeaderTable = new Hashtable();
        try {
            String jsonFromMap = gson.toJson(alRetBirthDayList);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            LogonCore.getInstance().addObjectToStore(Constants.BirthDayAlertsKey, jsonHeaderObject.toString());
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BirthdaysBean> getBirthdayListFromDataVault(String mStrKeyVal) {

        ArrayList<BirthdaysBean> beanArrayList = null;
        //Fetch object from data vault
        try {

            JSONObject fetchJsonHeaderObject = new JSONObject(mStrKeyVal);

            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

            beanArrayList = convertToBirthDayArryList(itemsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beanArrayList;
    }

    // TODO add  empty values into data vault
    public static void assignEmptyValuesIntoDataVault() {
        try {
            //noinspection deprecation
            LogonCore.getInstance().addObjectToStore(Constants.BirthDayAlertsKey, "");
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }


    public static void setBirthdayListToDataValut(Context context) {
        String[][] oneWeekDay;
        oneWeekDay = UtilConstants.getOneweekValues(1);
        String splitDayMonth[] = oneWeekDay[0][0].split("-");

        ArrayList<BirthdaysBean> alRetBirthDayTempList = new ArrayList<>();
        ArrayList<BirthdaysBean> alDataVaultList = new ArrayList<>();
        ArrayList<BirthdaysBean> alRetBirthDayList = getTodayBirthDayList();

        try {
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                    0);
            String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

            if (mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {

                String store = null;
                try {
                    store = LogonCore.getInstance().getObjectFromStore(Constants.BirthDayAlertsKey);
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }
                if (store != null && !store.equalsIgnoreCase("")) {
                    alDataVaultList = Constants.getBirthdayListFromDataVault(store);


                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            boolean mBoolIsRecordExists = false;

                            if (alDataVaultList != null && alDataVaultList.size() > 0) {

                                // Loop arrayList1 items
                                for (BirthdaysBean secondBeanAL : alDataVaultList) {
                                    if (firstBeanAL.getCPUID().toUpperCase().equalsIgnoreCase(secondBeanAL.getCPUID()) && (!firstBeanAL.getAppointmentAlert()
                                            && !secondBeanAL.getAppointmentAlert())) {

                                        if ((secondBeanAL.getDOB().equalsIgnoreCase(firstBeanAL.getDOB())
                                                || (secondBeanAL.getAnniversary().equalsIgnoreCase(firstBeanAL.getAnniversary())))) {

                                            BirthdaysBean birthdaysBean = new BirthdaysBean();
                                            birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                            if (firstBeanAL.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0]) && secondBeanAL.getDOBStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setDOBStatus("");
                                            else
                                                birthdaysBean.setDOBStatus(Constants.X);

                                            if (firstBeanAL.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0]) && secondBeanAL.getAnniversaryStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setAnniversaryStatus("");
                                            else
                                                birthdaysBean.setAnniversaryStatus(Constants.X);

                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setDOB(firstBeanAL.getDOB());
                                            birthdaysBean.setAnniversary(firstBeanAL.getAnniversary());
                                            birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                            birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            alRetBirthDayTempList.add(birthdaysBean);
                                            mBoolIsRecordExists = true;
                                            break;
                                        }

                                    } else {
                                        if (firstBeanAL.getCPUID().toUpperCase().equalsIgnoreCase(secondBeanAL.getCPUID())
                                                && (firstBeanAL.getAppointmentAlert()
                                                && secondBeanAL.getAppointmentAlert())) {
                                            BirthdaysBean birthdaysBean = new BirthdaysBean();
                                            birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                            if (secondBeanAL.getAppointmentStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setAppointmentStatus("");
                                            else
                                                birthdaysBean.setAppointmentStatus(Constants.X);

                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                            birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setAppointmentTime(firstBeanAL.getAppointmentTime());
                                            birthdaysBean.setAppointmentEndTime(firstBeanAL.getAppointmentEndTime());
                                            birthdaysBean.setAppointmentType(firstBeanAL.getAppointmentType());
                                            birthdaysBean.setAppointmentAlert(true);
                                            alRetBirthDayTempList.add(birthdaysBean);
                                            mBoolIsRecordExists = true;
                                            break;
                                        }
                                    }
                                }

                                if (!mBoolIsRecordExists) {
                                    BirthdaysBean birthdaysBean = new BirthdaysBean();
                                    if (!firstBeanAL.getAppointmentAlert()) {
                                        birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                        birthdaysBean.setDOBStatus(firstBeanAL.getDOBStatus());
                                        birthdaysBean.setAnniversaryStatus(firstBeanAL.getAnniversaryStatus());
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setDOB(firstBeanAL.getDOB());
                                        birthdaysBean.setAnniversary(firstBeanAL.getAnniversary());
                                        birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                        birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                        alRetBirthDayTempList.add(birthdaysBean);
                                    } else {
                                        birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                        birthdaysBean.setAppointmentStatus("");
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                        birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setAppointmentTime(firstBeanAL.getAppointmentTime());
                                        birthdaysBean.setAppointmentEndTime(firstBeanAL.getAppointmentEndTime());
                                        birthdaysBean.setAppointmentType(firstBeanAL.getAppointmentType());
                                        birthdaysBean.setAppointmentAlert(true);
                                        alRetBirthDayTempList.add(birthdaysBean);
                                    }
                                }

                            }


                        }

                    }


                    setCurrentDateTOSharedPerf(context);
                    // TODO add values into data vault
                    if (alRetBirthDayTempList != null && alRetBirthDayTempList.size() > 0) {
                        assignValuesIntoDataVault(alRetBirthDayTempList);
                    } else {
                        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                            assignValuesIntoDataVault(alRetBirthDayList);
                        } else {
                            assignEmptyValuesIntoDataVault();
                        }
                    }

                } else {
                    setCurrentDateTOSharedPerf(context);
                    // TODO add values into data vault
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        assignValuesIntoDataVault(alRetBirthDayList);
                    } else {
                        assignEmptyValuesIntoDataVault();
                    }
                }


            } else {
                assignEmptyValuesIntoDataVault();
                setCurrentDateTOSharedPerf(context);
                // TODO add values into data vault
                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    assignValuesIntoDataVault(alRetBirthDayList);
                } else {
                    assignEmptyValuesIntoDataVault();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static int setBirthDayRecordsToDataValut(Context context) {
        int mIntBirthdaycount = 0;
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        ArrayList<BirthdaysBean> alRetBirthDayList = getTodayBirthDayListOnly();
        try {

            String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

            if (mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {
                String store = null;
                try {
                    store = LogonCore.getInstance().getObjectFromStore(Constants.BirthDayAlertsTempKey);
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }
                if (store != null && !store.equalsIgnoreCase("")) {
                    HashMap<String, String> hashMap = Constants.getBirthdayListFromDataValt(store);
                    if (!hashMap.isEmpty()) {
                        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                            for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                                if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                                    String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                                    if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                                    } else {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                        mIntBirthdaycount++;
                                    }
                                } else {
                                    hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                    mIntBirthdaycount++;
                                }
                            }
                        } else {
                            mIntBirthdaycount = 0;
                        }
                    } else {
                        hashMap = new HashMap<>();
                        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                            for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                                if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                                    String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                                    if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                                    } else {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                        mIntBirthdaycount++;
                                    }
                                } else {
                                    hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                    mIntBirthdaycount++;
                                }
                            }
                        } else {
                            mIntBirthdaycount = 0;
                        }
                    }
                    setCurrentDateTOSharedPerf(context);
                    if (hashMap == null)
                        hashMap = new HashMap<>();
                    setBirthdayToDataVault(hashMap, Constants.BirthDayAlertsTempKey);

                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                                String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                                if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                    hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                                } else {
                                    hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                    mIntBirthdaycount++;
                                }
                            } else {
                                hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                mIntBirthdaycount++;
                            }
                        }
                    } else {
                        mIntBirthdaycount = 0;
                    }

                    setCurrentDateTOSharedPerf(context);
                    setBirthdayToDataVault(hashMap, Constants.BirthDayAlertsTempKey);
                }
            } else {
                HashMap<String, String> hashMap = new HashMap<>();
                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                        if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                            String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                            if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                            } else {
                                hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                mIntBirthdaycount++;
                            }
                        } else {
                            hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                            mIntBirthdaycount++;
                        }
                    }
                } else {
                    mIntBirthdaycount = 0;
                }

                setCurrentDateTOSharedPerf(context);
                setBirthdayToDataVault(hashMap, Constants.BirthDayAlertsTempKey);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.BirthdayAlertsCount, mIntBirthdaycount);
        editor.commit();

        return mIntBirthdaycount;

    }

    public static int setAlertsRecordsToDataValut(Context context) {
        int mIntTextAlertcount = 0;
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);

        ArrayList<BirthdaysBean> arrayList = new ArrayList<>();
        ArrayList<BirthdaysBean> alRetBirthDayList = AlertsListFragment.getAlertsFromLocalDB(arrayList);
        try {

            String store = null;
            try {
                store = LogonCore.getInstance().getObjectFromStore(Constants.AlertsTempKey);
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }
            if (store != null && !store.equalsIgnoreCase("")) {
                HashMap<String, String> hashMap = Constants.getBirthdayListFromDataValt(store);
                if (!hashMap.isEmpty()) {
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            if (hashMap.containsKey(firstBeanAL.getAlertGUID())) {
                                String mStrVal = hashMap.get(firstBeanAL.getAlertGUID());
                                if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.Y);
                                } else {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                    mIntTextAlertcount++;
                                }
                            } else {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                mIntTextAlertcount++;
                            }
                        }
                    } else {
                        mIntTextAlertcount = 0;
                    }
                } else {
                    hashMap = new HashMap<>();
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            if (hashMap.containsKey(firstBeanAL.getAlertGUID())) {
                                String mStrVal = hashMap.get(firstBeanAL.getAlertGUID());
                                if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.Y);
                                } else {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                    mIntTextAlertcount++;
                                }
                            } else {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                mIntTextAlertcount++;
                            }
                        }
                    } else {
                        mIntTextAlertcount = 0;
                    }
                }

                setCurrentDateTOSharedPerf(context);
                setBirthdayToDataVault(hashMap, Constants.AlertsTempKey);

            } else {

                HashMap<String, String> hashMap = new HashMap<>();
                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                        if (hashMap.containsKey(firstBeanAL.getAlertGUID())) {
                            String mStrVal = hashMap.get(firstBeanAL.getAlertGUID());
                            if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.Y);
                            } else {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                mIntTextAlertcount++;
                            }
                        } else {
                            hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                            mIntTextAlertcount++;
                        }
                    }
                } else {
                    mIntTextAlertcount = 0;
                }

                setCurrentDateTOSharedPerf(context);
                setBirthdayToDataVault(hashMap, Constants.AlertsTempKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.TextAlertsCount, mIntTextAlertcount);
        editor.commit();

        return mIntTextAlertcount;

    }


    public static void displayAlertWithBackPressed(final Activity activity, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.MyTheme).create();
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    alertDialog.cancel();
                    activity.onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    public static void updateLastSyncTimeToTable(ArrayList<String> alAssignColl) {
        try {
            String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
            for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                String colName = alAssignColl.get(incReq);
                if (colName.contains("?$")) {
                    String splitCollName[] = colName.split("\\?");
                    colName = splitCollName[0];
                }

                if (colName.contains("(")) {
                    String splitCollName[] = colName.split("\\(");
                    colName = splitCollName[0];
                }

                Constants.events.updateStatus(Constants.SYNC_TABLE,
                        colName, Constants.TimeStamp, syncTime
                );
            }
        } catch (Exception exce) {
            LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
        }
    }

    // TODO make query for current month any Invoices is created or not
    public static String getCurrentMonthInvoiceQry(String cpUID) {
        return Constants.SSInvoices + "?$filter=" + Constants.SoldToID + " eq '" + cpUID + "' " +
                "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "'";
    }

    // TODO make query for current month any collection is created or not
    public static String getCurrentMonthCollHisQry(String CPGUID) {
        return Constants.FinancialPostings + "?$filter=" + Constants.CPGUID + " eq guid'"
                + Constants.convertStrGUID32to36(CPGUID).toUpperCase()
                + "' and " + Constants.FIPDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "'";
    }

    @SuppressLint("NewApi")
    public static ArrayList<MyStockBean> getDevStock(Context context, String cpGuid) throws OfflineODataStoreException {
        ArrayList<MyStockBean> alStockBean = new ArrayList<>();
        try {
            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet("InvList", null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    String store = null, deviceNo = "";
                    try {
                        deviceNo = itr.next().toString();
                        store = LogonCore.getInstance().getObjectFromStore(deviceNo);
                    } catch (LogonCoreException e) {
                        e.printStackTrace();
                    }
                    try {
                        MyStockBean stockBean;

                        JSONObject fetchJsonHeaderObject = new JSONObject(store);
                        ArrayList<HashMap<String, String>> arrtable;
                        if (fetchJsonHeaderObject.getString("EntityType").equalsIgnoreCase("SSInvoice")
                                && cpGuid.equalsIgnoreCase("")) {
                            String itemsString = fetchJsonHeaderObject.getString("ITEMS");
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            for (int i = 0; i < arrtable.size(); i++) {
                                HashMap<String, String> singleRow = arrtable.get(i);
                                stockBean = new MyStockBean();
                                stockBean.setMaterialNo(singleRow.get("MatCode"));
                                stockBean.setUnrestrictedQty(singleRow.get("Qty"));
                                stockBean.setStockValue(singleRow.get(Constants.UnitPrice));
                                alStockBean.add(stockBean);
                            }
                        } else if (fetchJsonHeaderObject.getString("EntityType").equalsIgnoreCase("SSInvoice")
                                && fetchJsonHeaderObject.getString(Constants.CPGUID).equalsIgnoreCase(cpGuid.toUpperCase())) {
                            String itemsString = fetchJsonHeaderObject.getString("ITEMS");
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            for (int i = 0; i < arrtable.size(); i++) {
                                HashMap<String, String> singleRow = arrtable.get(i);
                                stockBean = new MyStockBean();
                                stockBean.setMaterialNo(singleRow.get("MatCode"));
                                stockBean.setUnrestrictedQty(singleRow.get("Qty"));
                                stockBean.setStockValue(singleRow.get(Constants.UnitPrice));
                                alStockBean.add(stockBean);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            throw new OfflineODataStoreException
                    (e)
                    ;
        }
        return alStockBean;

    }

    public static String getRetMobileNo(String mStrRetID) {

        String loginQry = Constants.ChannelPartners + "?$select=" + Constants.MobileNo + " &$filter = " + Constants.CPNo + " eq '" + mStrRetID + "'";
        String mStrMobNo = "";
        try {
            mStrMobNo = OfflineManager.getRetilerMobileNo(loginQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }

        return mStrMobNo;
    }

    public static ArrayList<SchemeBean> convertToArryList(String jsonString) {
        ArrayList<SchemeBean> alSchemeList = null;
        try {
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<ArrayList<SchemeBean>>() {
            }.getType();
            alSchemeList = gson.fromJson(jsonString, stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alSchemeList;
    }

    public static String[] comRetailerTag = {"GA", "H.FRC", "H.SRC", "Earning per month", "Tertiary", "SchemeName"};


    public static final String DeviceMechindising = "DeviceMechindising";
    public static final String NonDeviceMechindising = "NonDeviceMechindising";

    public static File SaveImageInDevice(String filename, Bitmap bitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, filename + ".jpg");
        if (filename.contains(".")) {
            file = new File(extStorageDirectory, filename);
        }
        if (file.exists()) {
            file.delete();
//            file = new File(extStorageDirectory, filename + ".jpg");
        }
        try {
            // make a new bitmap from your file

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    public static File getImageFromDevice(String filename, Bitmap bitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, filename);
        if (file.exists()) {
            file = new File(extStorageDirectory, filename);
        }
        return file;
    }

    public static void saveDeviceDocNoToSharedPref(Context context, String createType, String refDocNo) {
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(createType, null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }
        setTemp.add(refDocNo);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(createType, setTemp);
        editor.commit();
    }

    public static void removeDeviceDocNoFromSharedPref(Context context, String createType, String refDocNo) {
        try {
            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(createType, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }
            setTemp.remove(refDocNo);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(createType, setTemp);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String MerchList = "MerchList";

    public static void storeInDataVault(String docNo, String jsonHeaderObjectAsString) {
        try {
            LogonCore.getInstance().addObjectToStore(docNo, jsonHeaderObjectAsString);
        } catch (LogonCoreException var3) {
            var3.printStackTrace();
        }

    }


    public static ArrayList<String> getPendingMerchList(Context context, String createType) {
        ArrayList<String> devMerList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(createType, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                devMerList.add(itr.next().toString());
            }
        }

        return devMerList;
    }

    public static String getValueFromDataVault(String key) {
        String store = null;
        try {
            store = LogonCore.getInstance().getObjectFromStore(key);
        } catch (LogonCoreException e) {
            store = "";
        }

        return store;
    }

    public static void deleteDeviceMerchansisingFromDataVault(Context context) {
        ArrayList<String> alDeviceMerList = Constants.getPendingMerchList(context, Constants.MerchList);
        if (alDeviceMerList != null && alDeviceMerList.size() > 0) {
            for (int incVal = 0; incVal < alDeviceMerList.size(); incVal++) {
                try {
                    if (!OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews +
                            "?$filter=sap.islocal() and " + Constants.MerchReviewGUID + " eq guid'" + alDeviceMerList.get(incVal) + "'")) {
                        Constants.removeDeviceDocNoFromSharedPref(context, Constants.MerchList, alDeviceMerList.get(incVal));
                        storeInDataVault(alDeviceMerList.get(incVal), "");
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static final String PROP_MER_TYPE = "RVWTYP";

    public static String MerchReviewDate = "MerchReviewDate";
    public static String MerchReviewLat = "MerchReviewLat";
    public static String MerchReviewLong = "MerchReviewLong";
    public static String MerchImageGUID = "MerchImageGUID";
    public static String ImageMimeType = "ImageMimeType";
    public static String ImageSize = "ImageSize";
    public static String Image = "Image";
    public static String ImagePath = "ImagePath";

    public static String ImageByteArray = "ImageByteArray";

    public static String DocumentStore = "DocumentStore";
    public static String FileName = "FileName";

    public static String OtherRouteGUIDVal = "";
    public static String OtherRouteNameVal = "";
    public static String Visit_Cat_ID = "";

    public static final String OtherRouteGUID = "OtherRouteGUID";
    public static final String OtherRouteName = "OtherRouteName";

    public static String BrandsCategories = "BrandsCategories";
    public static String OrderMaterialGroups = "OrderMaterialGroups";
    public static String Brands = "Brands";
    public static String MaterialCategories = "MaterialCategories";
    public static String BrandID = "BrandID";
    public static String BrandDesc = "BrandDesc";
    public static String MaterialCategoryID = "MaterialCategoryID";
    public static String MaterialCategoryDesc = "MaterialCategoryDesc";
    public static String DMSDivision = "DMSDivision";
    public static String DMSDivisionDesc = "DMSDivisionDesc";
    public static String PerformanceOnIDDesc = "PerformanceOnIDDesc";
    public static String Material_Catgeory = "MaterialCategory";
    public static String DbBatch = "Batch";
    public static String ManufacturingDate = "ManufacturingDate";
    public static String Quantity = "Quantity";
    public static String FreeQuantity = "FreeQuantity";
    public static String Category = "Category";
    public static String CRS_SKU_GROUP = "CRS SKU Group";
    public static String OrderMaterialGrp = "OrderMaterialGrp";
    public static String OrderMaterialGrpDesc = "OrderMaterialGrpDesc";
    public static String OrderMaterialGroupDesc = "OrderMaterialGroupDesc";
    public static String OrderMaterialGroupID = "OrderMaterialGroupID";
    public static String BannerID = "BannerID";


    public static String getLastSyncTime(String collName, String whereCol, String whereColVal, String retiveColName, Context context) {
        String lastSyncTime = "";
        try {
            Cursor cursorLastSync = SyncHist.getInstance()
                    .getLastSyncTime(collName, whereCol, whereColVal);

            if (cursorLastSync != null
                    && cursorLastSync.getCount() > 0) {
                while (cursorLastSync.moveToNext()) {
                    lastSyncTime = cursorLastSync
                            .getString(cursorLastSync
                                    .getColumnIndex(retiveColName)) != null ? cursorLastSync
                            .getString(cursorLastSync
                                    .getColumnIndex(retiveColName)) : "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String lastSyncDuration = "";
        try {
            lastSyncDuration = UtilConstants.getTimeAgo(lastSyncTime, context);
        } catch (Exception e) {
            lastSyncDuration = "";
        }


        return lastSyncDuration;
    }


    public static String History = "History";
    public static String PendingSync = "Pending Sync";

    public static String RETURN_ORDER_TAB_TITLE_1 = "History";
    public static String RETURN_ORDER_TAB_TITLE_2 = "Pending Sync";
    public static String SSSO_TAB_TITLE_1 = "History";

    public static String Merchindising = "Merchandising";
    public static String DeviceMerchindising = "Device Merchandising";


    public static String convertDateIntoDeviceFormat(Context context, String dateString) {
        java.text.DateFormat dateFormat1 = DateFormat.getDateFormat(context);
        String pattern = ((SimpleDateFormat) dateFormat1).toLocalizedPattern();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date convertedDate = new Date();

        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException var7) {
            var7.printStackTrace();
        }

        String dateInDeviceFormat = (String) DateFormat.format(pattern, convertedDate);
        return dateInDeviceFormat;
    }

    public static boolean isSpecificCollTodaySyncOrNot(String sleDate) {


        boolean mBoolDBSynced = false;
        if (sleDate != null && !sleDate.equalsIgnoreCase("")) {

            Date date = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                date = null;
                try {
                    date = sdf.parse(sleDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (date == null) {
                date = new Date();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int mYear = cal.get(Calendar.YEAR);
            int mMonth = cal.get(Calendar.MONTH);
            int mDay = cal.get(Calendar.DAY_OF_MONTH);

            Calendar calCurrent = Calendar.getInstance();

            int mYearCurrent = calCurrent.get(Calendar.YEAR);
            int mMonthCurrent = calCurrent.get(Calendar.MONTH);
            int mDayCurrent = calCurrent.get(Calendar.DAY_OF_MONTH);

            if (mYear == mYearCurrent && mMonth == mMonthCurrent && mDay == mDayCurrent) {
                mBoolDBSynced = true;
            } else {
                mBoolDBSynced = false;
            }

        } else {
            mBoolDBSynced = false;
        }
        return mBoolDBSynced;
    }

    public static String getLastSyncDate(String collName, String whereCol, String whereColVal, String retiveColName, Context context) {
        String lastSyncTime = "";
        Cursor cursorLastSync = SyncHist.getInstance()
                .getLastSyncTime(collName, whereCol, whereColVal);

        if (cursorLastSync != null
                && cursorLastSync.getCount() > 0) {
            while (cursorLastSync.moveToNext()) {
                lastSyncTime = cursorLastSync
                        .getString(cursorLastSync
                                .getColumnIndex(retiveColName)) != null ? cursorLastSync
                        .getString(cursorLastSync
                                .getColumnIndex(retiveColName)) : "";
            }
        }
        return lastSyncTime;
    }

    public static String getNameByCPGUID(String collName, String columnName, String whereColumnn, String whereColval) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq guid'" + whereColval + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    //	public static void createVisit(String cpID, String cpName, String cpTypeId, ODataGuid cpGuid, Context context, String visitCatId,String statusID,UIListener listener)
    public static void createVisit(Map<String, String> parameterMap, ODataGuid cpGuid, Context context, UIListener listener) {

        try {
            Thread.sleep(100);

            GUID guid = GUID.newRandom();

            Hashtable table = new Hashtable();
            //noinspection unchecked
            table.put(Constants.CPNo, UtilConstants.removeLeadingZeros(parameterMap.get(Constants.CPNo)));

            table.put(Constants.CPName, parameterMap.get(Constants.CPName));
            //noinspection unchecked
            table.put(Constants.STARTDATE, UtilConstants.getNewDateTimeFormat());

            final Calendar calCurrentTime = Calendar.getInstance();
            int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
            int minute = calCurrentTime.get(Calendar.MINUTE);
            int second = calCurrentTime.get(Calendar.SECOND);
            ODataDuration oDataDuration = null;
            try {
                oDataDuration = new ODataDurationDefaultImpl();
                oDataDuration.setHours(hourOfDay);
                oDataDuration.setMinutes(minute);
                oDataDuration.setSeconds(BigDecimal.valueOf(second));
            } catch (Exception e) {
                e.printStackTrace();
            }

            table.put(Constants.STARTTIME, oDataDuration);
            //noinspection unchecked
            table.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            table.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.longitude));
            //noinspection unchecked
            table.put(Constants.EndLat, "");
            //noinspection unchecked
            table.put(Constants.EndLong, "");
            //noinspection unchecked
            table.put(Constants.ENDDATE, "");
            //noinspection unchecked
            table.put(Constants.ENDTIME, "");
            //noinspection unchecked
            table.put(Constants.VISITKEY, guid.toString().toUpperCase());

            table.put(Constants.StatusID, parameterMap.get(Constants.StatusID));

            table.put(Constants.VisitCatID, parameterMap.get(Constants.VisitCatID));

            table.put(Constants.CPTypeID, parameterMap.get(Constants.CPTypeID));


            if (parameterMap.get(Constants.PlannedDate) != null) {
                table.put(Constants.PlannedDate, parameterMap.get(Constants.PlannedDate));
            } else {
                table.put(Constants.PlannedDate, "");
            }

            if (parameterMap.get(Constants.PlannedStartTime) != null) {
                ODataDuration startDuration = UtilConstants.getTimeAsODataDuration(parameterMap.get(Constants.PlannedStartTime));
                table.put(Constants.PlannedStartTime, startDuration);
            } else {
                table.put(Constants.PlannedStartTime, "");
            }

            if (parameterMap.get(Constants.PlannedEndTime) != null) {
                ODataDuration endDuration = UtilConstants.getTimeAsODataDuration(parameterMap.get(Constants.PlannedEndTime));
                table.put(Constants.PlannedEndTime, endDuration);
            } else {
                table.put(Constants.PlannedEndTime, "");
            }

            //noinspection unchecked
            if (parameterMap.get(Constants.Remarks) != null) {
                table.put(Constants.Remarks, parameterMap.get(Constants.Remarks));
            }

            table.put(Constants.VisitTypeID, parameterMap.get(Constants.VisitTypeID));
            table.put(Constants.VisitTypeDesc, parameterMap.get(Constants.VisitTypeDesc));


            if (parameterMap.get(Constants.VisitDate) != null) {
                table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
            } else {
                table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
            }

            table.put(Constants.CPGUID, cpGuid.guidAsString32().toUpperCase());

            String[][] mArraySPValues = getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(cpGuid.guidAsString36().toUpperCase());

            try {
                table.put(Constants.SPGUID, mArraySPValues[4][0].toUpperCase());
            } catch (Exception e) {
                table.put(Constants.SPGUID, Constants.getSPGUID());
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);

//            int sharedVal = sharedPreferences.getInt("VisitSeqId", 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            table.put(Constants.LOGINID, loginIdVal);

            try {
                table.put(Constants.VisitSeq, parameterMap.get(Constants.VisitSeq));
            } catch (Exception e) {
                table.put(Constants.VisitSeq, "");
                e.printStackTrace();
            }

            //DeviationRemarks
            try {
                table.put(Constants.ActualSeq, parameterMap.get(Constants.ActualSeq));
                table.put(Constants.DeviationReasonID, parameterMap.get(Constants.DeviationReasonID));
                table.put(Constants.DeviationRemarks, parameterMap.get(Constants.DeviationRemarks));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                table.put(Constants.BeatGUID, parameterMap.get(Constants.BeatGUID).toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }


           /* sharedVal++;

            SharedPreferences sharedPreferencesVal = context.getSharFedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferencesVal.edit();
            editor.putInt(Constants.VisitSeqId, sharedVal);
            editor.commit();*/


            String mStrRoutePlanKey = Constants.Route_Plan_Key;
            if (!mStrRoutePlanKey.equalsIgnoreCase("")) {
                String mStrRouteGuidFormat = CharBuffer.join9(StringFunction.substring(mStrRoutePlanKey, 0, 8), "-", StringFunction.substring(mStrRoutePlanKey, 8, 12), "-", StringFunction.substring(mStrRoutePlanKey, 12, 16), "-", StringFunction.substring(mStrRoutePlanKey, 16, 20), "-", StringFunction.substring(mStrRoutePlanKey, 20, 32));
                //noinspection unchecked
                table.put(Constants.ROUTEPLANKEY, mStrRouteGuidFormat.toUpperCase());
            } else {
                String mStrRouteKey = getRouteNo(cpGuid, parameterMap.get(Constants.VisitCatID));
                if (mStrRouteKey.equalsIgnoreCase("")) {
                    table.put(Constants.ROUTEPLANKEY, "");
                } else {
                    String mStrRouteGuidFormat = CharBuffer.join9(StringFunction.substring(mStrRouteKey, 0, 8), "-", StringFunction.substring(mStrRouteKey, 8, 12), "-", StringFunction.substring(mStrRouteKey, 12, 16), "-", StringFunction.substring(mStrRouteKey, 16, 20), "-", StringFunction.substring(mStrRouteKey, 20, 32));
                    table.put(Constants.ROUTEPLANKEY, mStrRouteGuidFormat.toUpperCase());
                }

            }
            try {
                if (parameterMap.get(Constants.NoOfOutlet) != null) {
                    table.put(Constants.NoOfOutlet, parameterMap.get(Constants.NoOfOutlet));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                //noinspection unchecked
                OfflineManager.createVisit(table, listener);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (InterruptedException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }

    private static String getRouteNo(ODataGuid mCpGuid, String mStrVisitCatID) {

        String mStrRouteKey = "";
        String qryStr = Constants.RouteSchedulePlans + "?$filter=" + Constants.VisitCPGUID + " eq '" + mCpGuid.guidAsString32().toUpperCase() + "' ";
        try {
            mStrRouteKey = OfflineManager.getRoutePlanKeyNew(qryStr, mStrVisitCatID);

        } catch (OfflineODataStoreException e) {
            mStrRouteKey = "";
            e.printStackTrace();
        }
        return mStrRouteKey;
    }

    public static boolean onGpsCheckCustomMessage(final Context context, String message) {
        UtilConstants.getLocation(context);
//        if (!UtilConstants.canGetLocation(context)) {
        if (!GpsTracker.isGPSEnabled) {
            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(context, R.style.MyTheme);
            gpsEnableDlg
                    .setMessage(message);
            gpsEnableDlg.setPositiveButton("Enable",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
            // on pressing cancel button
            gpsEnableDlg.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            gpsEnableDlg.show();
        }
        return GpsTracker.isGPSEnabled;
    }

    public static void setAppointmentNotification(Context context) {
        new NotificationSetClass(context);

    }

    public static String MateralQry = "MateralQry";
    public static String SSROGUID = "SSROGUID";
    public static String SSSOGuid = "SSSOGuid";
    public static String CPMKTGUID = "CPMKTGUID";
    public static String AllBusinessDesc = "AllBusinessDesc";
    public static String AllBusinessID = "AllBusinessID";
    public static String AnnualTurnover = "AnnualTurnover";
    public static String CPBUSGUID = "CPBUSGUID";
    public static String CompSalesGUID = "CompSalesGUID";
    public static String OrderNo = "OrderNo";
    public static String OrderType = "OrderType";
    public static String OrderTypeDesc = "OrderTypeDesc";
    public static String OrderDate = "OrderDate";
    public static String DmsDivision = "DmsDivision";
    public static String DmsDivisionDesc = "DmsDivisionDesc";
    public static String PONo = "PONo";
    public static String PODate = "PODate";
    public static String DeliveryDate = "DeliveryDate";
    public static String DeliveryPerson = "DeliveryPerson";
    public static String DriverName = "DriverName";
    public static String DriverMobile = "DriverMobile";
    public static String TransVhclId = "TransVhclId";
    public static String FromCPGUID = "FromCPGUID";
    public static String FromCPNo = "FromCPNo";
    public static String FromCPName = "FromCPName";
    public static String FromCPTypId = "FromCPTypId";
    public static String FromCPTypDs = "FromCPTypID";
    public static String SoldToUID = "SoldToUID";
    public static String SoldToDesc = "SoldToDesc";
    public static String SoldToType = "SoldToType";
    public static String SoldToTypDs = "SoldToTypDs";
    public static String ShipToIdCPGUID = "ShipToIdCPGUID";
    public static String ShipToUID = "ShipToUID";
    public static String ShipToDesc = "ShipToDesc";
    public static String ShipToType = "ShipToType";
    public static String ShipToTypDs = "ShipToTypDs";
    public static String GrossAmt = "GrossAmt";
    public static String NetPrice = "NetPrice";
    public static String Freight = "Freight";
    public static String TAX = "TAX";
    public static String Discount = "Discount";
    public static String CPType = "CPType";
    public static String SoldToId = "SoldToId";


    public static String SSROItemGUID = "SSROItemGUID";


    public static String SSSOItemGUID = "SSSOItemGUID";
    public static String OrderMatGrp = "OrderMatGrp";
    public static String OrderMatGrpDesc = "OrderMatGrpDesc";
    public static String OrdMatGrpDesc = "OrdMatGrpDesc";
    public static String Uom = "Uom";
    public static String HigherLevelItemno = "HigherLevelItemno";
    public static String IsfreeGoodsItem = "IsfreeGoodsItem";
    public static String ISFreeGoodsItem = "ISFreeGoodsItem";
    public static String RefdocItmGUID = "RefdocItmGUID ";
    public static String Batch = "Batch";
    public static String FreeTypeID = "FreeTypeID";
    public static String RejectionReasonID = "RejectionReasonID";
    public static String RejectionReasonDesc = "RejectionReasonDesc";
    public static String MRP = "MRP";
    public static String LandingPrice = "LandingPrice";
    public static String Division = "Division";

    public static final String Margin = "Margin";
    public static final String WholeSalesLandingPrice = "WholeSalesLandingPrice";
    public static final String ConsumerOffer = "ConsumerOffer";
    public static final String TradeOffer = "TradeOffer";
    public static final String ShelfLife = "ShelfLife";

    public static ODataDuration getOdataDuration() {
        final Calendar calCurrentTime = Calendar.getInstance();
        int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
        int minute = calCurrentTime.get(Calendar.MINUTE);
        int second = calCurrentTime.get(Calendar.SECOND);
        ODataDuration oDataDuration = null;
        try {
            oDataDuration = new ODataDurationDefaultImpl();
            oDataDuration.setHours(hourOfDay);
            oDataDuration.setMinutes(minute);
            oDataDuration.setSeconds(BigDecimal.valueOf(second));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return oDataDuration;
    }

    public static String convertArrListToGsonString(ArrayList<HashMap<String, String>> arrtable) {
        String convertGsonString = "";
        Gson gson = new Gson();
        try {
            convertGsonString = gson.toJson(arrtable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertGsonString;
    }

    public static String strErrorWithColon = "Error : ";
    public static String SystemKPI = "SystemKPI";
    public static String SecDiscount = "SecDiscount";
    public static String PriDiscount = "PriDiscount";
    public static String CashDiscount = "CashDiscount";
    public static final String SOList = "SOList";
    public static final String outletSurvery = "outletSurvery";
    public static final String CPList = "CPList";
    public static final String FeedbackList = "FeedbackList";
    public static String LoginId = "LoginId";
    public static String SecondaryTradeDiscAmt = "SecondaryTradeDiscAmt";
    public static String SecondaryTradeDisc = "SecondaryTradeDisc";
    public static String SecondaryDiscountPerc = "SecondaryDiscountPerc";
    public static String PrimaryDiscountPerc = "PrimaryDiscountPerc";
    public static String PrimaryTradeDis = "PrimaryTradeDis";
    public static String CashDiscountPer = "CashDiscountPer";
    public static String PrimaryTradeDisAmt = "PrimaryTradeDisAmt";
    public static String CashDiscountPerc = "CashDiscountPerc";

    public static String getConcatinatinFlushCollectios(ArrayList<String> alFlushColl) {
        String concatFlushCollStr = "";
        for (int incVal = 0; incVal < alFlushColl.size(); incVal++) {
            if (incVal == 0 && incVal == alFlushColl.size() - 1) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
            } else if (incVal == 0) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
            } else if (incVal == alFlushColl.size() - 1) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
            } else {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
            }
        }

        return concatFlushCollStr;
    }

    public static ArrayList<String> getPendingList() {
        ArrayList<String> alFlushColl = new ArrayList<>();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.Visits);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.VisitActivities);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.MerchReviews);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return alFlushColl;
    }

    public static ArrayList<String> getRefreshList() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.Visits);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.VisitActivities);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.MerchReviews);
                alAssignColl.add(Constants.MerchReviewImages);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return alAssignColl;
    }

    public static String SS_INV_RET_QRY = "";
    public static ArrayList<String> alRetailers = new ArrayList<>();
    public static ArrayList<String> alRetailersGuid = new ArrayList<>();

    public static String getTotalOrderValue(Context context, String mStrCurrentDate,
                                            ArrayList<CustomerBean> alTodaysRetailers) {

        String mSOOrderType = getSOOrderType();
        double totalOrderVal = 0.0;

        String mStrRetQry = "", ssINVRetQry = "";

        if (alTodaysRetailers != null && alTodaysRetailers.size() > 0) {
            for (int i = 0; i < alTodaysRetailers.size(); i++) {
                if (i == 0 && i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";

                    ssINVRetQry = ssINVRetQry
                            + "(" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";

                } else if (i == 0) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";

                    ssINVRetQry = ssINVRetQry
                            + "(" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";

                } else if (i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";

                    ssINVRetQry = ssINVRetQry
                            + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";
                } else {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";

                    ssINVRetQry = ssINVRetQry
                            + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";
                }

                if (!alRetailers.contains(alTodaysRetailers.get(i).getCPNo())) {
                    alRetailers.add(alTodaysRetailers.get(i).getCPNo());
                    alRetailersGuid.add(alTodaysRetailers.get(i).getCpGuidStringFormat());
                }
            }

        }
        Constants.SS_INV_RET_QRY = mStrRetQry;
        String mStrOrderVal = "0.0";
        if (alRetailers.size() > 0) {
            try {
                if (!mStrRetQry.equalsIgnoreCase("")) {
                    mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
                            "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' and " + mStrRetQry + " ", Constants.NetPrice);
                } else {
                    mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
                            "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' ", Constants.NetPrice);
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        double mdouDevOrderVal = 0.0;

        if (alRetailers.size() > 0) {
            try {
                mdouDevOrderVal = OfflineManager.getDeviceTotalOrderAmt(Constants.SOList, context, mStrCurrentDate, alRetailers);
            } catch (Exception e) {
                mdouDevOrderVal = 0.0;
            }
        }

        totalOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return totalOrderVal + "";
    }


    public static String getTotalInvValue(Context context, String mStrCurrentDate,
                                          ArrayList<CustomerBean> alTodaysRetailers) {

        String mSOOrderType = getSOOrderType();
        double totalOrderVal = 0.0;

        String mStrRetQry = "", ssINVRetQry = "";

        if (alTodaysRetailers != null && alTodaysRetailers.size() > 0) {
            for (int i = 0; i < alTodaysRetailers.size(); i++) {
                if (i == 0 && i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";

                    ssINVRetQry = ssINVRetQry
                            + "(" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";

                } else if (i == 0) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";

                    ssINVRetQry = ssINVRetQry
                            + "(" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";

                } else if (i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";

                    ssINVRetQry = ssINVRetQry
                            + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "')";
                } else {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";

                    ssINVRetQry = ssINVRetQry
                            + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCPNo() + "'";
                }

                if (!alRetailers.contains(alTodaysRetailers.get(i).getCPNo())) {
                    alRetailers.add(alTodaysRetailers.get(i).getCPNo());
                }
            }

        }
        Constants.SS_INV_RET_QRY = ssINVRetQry;
        String mStrOrderVal = "0.0";
        if (alRetailers.size() > 0) {
            try {
                if (!mStrRetQry.equalsIgnoreCase("")) {
                    mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoices +
                            "?$select=" + Constants.NetAmount + " &$filter=" + Constants.InvoiceDate + " eq datetime'" + mStrCurrentDate + "' and " + mStrRetQry + " ", Constants.NetAmount);
                } else {
                    mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoices +
                            "?$select=" + Constants.NetAmount + " &$filter=" + Constants.InvoiceDate + " eq datetime'" + mStrCurrentDate + "' ", Constants.NetAmount);
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        double mdouDevOrderVal = 0.0;

        if (alRetailers.size() > 0) {
            try {
                mdouDevOrderVal = OfflineManager.getDeviceTotalInvAmt(Constants.InvList, context, mStrCurrentDate, alRetailers);
            } catch (Exception e) {
                mdouDevOrderVal = 0.0;
            }
        }

        totalOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return totalOrderVal + "";
    }

    public static String getTotalOrderValueByCurrentMonth(String mStrFirstDateMonth, String cpQry, String mStrCPDMSDIVQry) {

        double totalOrderVal = 0.0;

        String mStrOrderVal = "0.0";
        try {
            if (cpQry.equalsIgnoreCase("")) {
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoices +
                        "?$select=" + Constants.NetAmount + " &$filter=" + Constants.InvoiceDate + " ge datetime'" + mStrFirstDateMonth + "' and " + Constants.InvoiceDate + " lt datetime'" + UtilConstants.getNewDate() + "' and " + mStrCPDMSDIVQry + " ", Constants.NetAmount);
            } else {
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoices +
                        "?$select=" + Constants.NetAmount + " &$filter=" + Constants.InvoiceDate + " ge datetime'" + mStrFirstDateMonth + "' and " + Constants.InvoiceDate + " lt datetime'" + UtilConstants.getNewDate() + "' and (" + cpQry + ") and " + mStrCPDMSDIVQry + " ", Constants.NetAmount);
            }


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        double mdouDevOrderVal = 0.0;

        totalOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return totalOrderVal + "";
    }

    public static String getLastDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        Date lastDayOfMonth = cal.getTime();
        String currentDateTimeString1 = (String) DateFormat.format("yyyy-MM-dd", lastDayOfMonth);
        return getTimeformat2(currentDateTimeString1, (String) null);
    }

    public static String getTimeformat2(String date, String time) {
        String datefrt = "";
        datefrt = "00:00:00";
        String currentDateTimeString = date + "T" + datefrt;
        return currentDateTimeString;
    }

    /*returns total number of retailers has to visit today(Route plan)*/
    // Changed the below code  in  03052017 5:40
    public static ArrayList<CustomerBean> getTodaysBeatRetailers() {
        ArrayList<CustomerBean> alRetailerList = new ArrayList<>();
        ArrayList<CustomerBean> alRSCHList = getTodayRoutePlan();
        if (alRSCHList != null && alRSCHList.size() > 0) {
            String mCPGuidQry = getCPFromRouteSchPlan(alRSCHList);
            try {
                if (!mCPGuidQry.equalsIgnoreCase("")) {
                    List<CustomerBean> listRetailers = OfflineManager.getTodayBeatRetailer(mCPGuidQry, Constants.mMapCPSeqNo);
                    alRetailerList = (ArrayList<CustomerBean>) listRetailers;
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        return alRetailerList;

    }


    public static int getTodaysBeatRetailersCount() {
        int todayRetCount = 0;
        ArrayList<CustomerBean> alRSCHList = getTodayRoutePlan();
        if (alRSCHList != null && alRSCHList.size() > 0) {
            String mCPGuidQry = getCPFromRouteSchPlan(alRSCHList);
            try {
                if (!mCPGuidQry.equalsIgnoreCase("")) {
                    todayRetCount = OfflineManager.getTodayBeatRetailersCount(Constants.ChannelPartners + "?$filter=(" +
                            Constants.CPGUID + " eq " + mCPGuidQry + ") and " + Constants.StatusID + " eq '01' and " + Constants.ApprvlStatusID + " eq '03' ");
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        return todayRetCount;
    }


    public static ArrayList<CustomerBean> getTodayRoutePlan() {
        String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
        ArrayList<CustomerBean> alRSCHList = null;
        try {
            alRSCHList = OfflineManager.getTodayRoutes1(routeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return alRSCHList;
    }

    public static String getCPFromRouteSchPlan(ArrayList<CustomerBean> alRouteList) {
        String mCPGuidQry = "", qryForTodaysBeat = "";
        if (alRouteList != null && alRouteList.size() > 0) {
            String mRSCHQry = "";
            // Routescope ID will be same for all the routes planned for the day hence first record scope is used to decide
            String routeSchopeVal = alRouteList.get(0).getRoutSchScope();
            if (alRouteList.size() > 1) {

                if (routeSchopeVal.equalsIgnoreCase("000001")) {
                    for (CustomerBean routeList : alRouteList) {
                        if (mRSCHQry.length() == 0)
                            mRSCHQry += " guid'" + routeList.getRschGuid().toUpperCase() + "'";
                        else
                            mRSCHQry += " or " + Constants.RouteSchGUID + " eq guid'" + routeList.getRschGuid().toUpperCase() + "'";

                    }

                } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                    // Get the list of retailers from RoutePlans

                }

            } else {


                if (routeSchopeVal.equalsIgnoreCase("000001")) {

                    mRSCHQry = "guid'" + alRouteList.get(0).getRschGuid().toUpperCase() + "'";


                } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                    // Get the list of retailers from RoutePlans
                }

            }
            qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=(" +
                    Constants.RouteSchGUID + " eq " + mRSCHQry + ") &$orderby=" + Constants.SequenceNo + "";

            try {
                // Prepare Today's beat Retailer Query
                mCPGuidQry = OfflineManager.getBeatList(qryForTodaysBeat);

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        return mCPGuidQry;
    }

    /*returns total number of retailers has to visit today(Route plan)*/
    public static String getVisitTargetForToday() {
        alTodayBeatRet.clear();
        String count = "0";
        ArrayList<CustomerBean> alRetailerList = new ArrayList<>();
        alRetailerList = getTodaysBeatRetailers();
        alTodayBeatRet.addAll(alRetailerList);
        count = (alRetailerList.size() > 0) ? String.valueOf(alRetailerList.size()) : "0";
        return count;
    }

    public static String getOtherBeatRetailerCount() {
        String mOtherBeatVisitCount = "0";
        String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' " +
                "and " + Constants.VisitCatID + " eq '02' &$orderby=" + Constants.EndTime + "%20desc ";


        Set<String> retList = new HashSet<>();
        try {
            retList = OfflineManager.getUniqueOutVisitFromVisit(mVisitQry);
            mOtherBeatVisitCount = retList.size() + "";
        } catch (Exception e) {
            mOtherBeatVisitCount = "0";
        }


       /* try {
            mOtherBeatVisitCount = OfflineManager.getOtherBeatRetailerList(mVisitQry)+"";
        } catch (OfflineODataStoreException e) {
            mOtherBeatVisitCount = "0";
        }*/
        return mOtherBeatVisitCount;
    }


    public static ArrayList<CustomerBean> getVisitedRetFromVisit() {
        String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "' " +
                "and ("
                + Constants.VisitCatID + " eq '" + Constants.str_01 + "' or " + Constants.VisitCatID + " eq '" + Constants.str_02 + "') &$orderby=" + Constants.EndTime + "%20desc ";

        ArrayList<CustomerBean> retList = OfflineManager.getCPListFromVisit(mVisitQry);
        return retList;
    }

    /*returns total number of retailers visited(Route plan)*/
    public static String getVisitedRetailerCount(ArrayList<CustomerBean> alTodayBeatRet) {
        String mTodayBeatVisitCount = "0";
        try {
            if (alTodayBeatRet != null && alTodayBeatRet.size() > 0) {
                String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' " +
                        "and ("
                        + Constants.VisitCatID + " eq '" + Constants.str_01 + "' )";
                Set<String> retList = new HashSet<>();
                try {
                    retList = OfflineManager.getUniqueOutVisitFromVisit(mVisitQry);
                    mTodayBeatVisitCount = retList.size() + "";
                } catch (Exception e) {
                    mTodayBeatVisitCount = "0";
                }



               /* String cpQry = Constants.makeCPQryFromBeanList(alTodayBeatRet,Constants.CPGUID);
                if(!cpQry.equalsIgnoreCase("")) {
                    String mVisitQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate ne null " +
                            "and ("+cpQry+") and " + Constants.StatusID + " eq '01'";
                    try {
                        mTodayBeatVisitCount = OfflineManager.getOtherBeatRetailerList(mVisitQry) + "";
                    } catch (OfflineODataStoreException e) {
                        mTodayBeatVisitCount = "0";
                    }
                }*/
            } else {
                mTodayBeatVisitCount = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTodayBeatVisitCount;
    }


    public static String getTodayActualVisitedRetCount(String mStrDate) {
        int count = 0;
        String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
        ArrayList<CustomerBean> alRSCHList = null;
        ArrayList<String> mStrVisitedRet = null;
        try {
            alRSCHList = OfflineManager.getTodayRoutes1(routeQry);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (alRSCHList != null && alRSCHList.size() > 0) {
            String routeSchopeVal = alRSCHList.get(0).getRoutSchScope();
            if (routeSchopeVal.equalsIgnoreCase("000001")) {
                String mStrRoutePlanKeyQry = getRoutePlanQry(alRSCHList);
                String mStrVisitStartedQry = Constants.Visits + "?$select=" + Constants.CPGUID + " &$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate ne null " +
                        "and  " + Constants.StatusID + " eq '01' and (" + Constants.RoutePlanGUID + " eq " + mStrRoutePlanKeyQry + ") ";
                try {
                    mStrVisitedRet = OfflineManager.getVisitedRetiler(mStrVisitStartedQry, Constants.CPGUID);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                // Get the list of retailers from RoutePlans
            }
        }

        if (mStrVisitedRet != null) {
            count = mStrVisitedRet.size();
        } else {
            count = 0;
        }

        return String.valueOf(count);
    }

    private static String getRoutePlanQry(ArrayList<CustomerBean> alRSCHList) {
        String mRouteQry = "";
        for (CustomerBean routeList : alRSCHList) {
            if (mRouteQry.length() == 0)
                mRouteQry += " guid'" + Constants.convertStrGUID32to36(routeList.getRoutePlanKey().toUpperCase()) + "'";
            else
                mRouteQry += " or " + Constants.RoutePlanGUID + " eq guid'" + Constants.convertStrGUID32to36(routeList.getRoutePlanKey().toUpperCase()) + "'";

        }
        return mRouteQry;
    }


    public static String[][] getDistributors() {
        String[][] mArrayDistributors = null;
//		String qryStr = Constants.SalesPersons + "?$filter=(" + Constants.CPGUID + " ne '' and " + Constants.CPGUID + " ne null) &$apply=groupby((" + Constants.CPGUID + "))";
        String qryStr = Constants.SalesPersons;
        try {
            mArrayDistributors = OfflineManager.getDistributorList(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[10][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
        }

        return mArrayDistributors;
    }


    public static String[][] getDistributorsByCPGUID(String mStrCPGUID) {
        String mDBStkType = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.DSTSTKVIEW);
        String spGuid = "";
        String qryStr = "";
        String[][] mArrayDistributors = null;

        try {
            String mStrConfigTypeQry = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Types + " eq '" + Constants.DSTSTKVIEW + "'";
            if (OfflineManager.getVisitStatusForCustomer(mStrConfigTypeQry)) {
                if (mDBStkType.equalsIgnoreCase(Constants.str_01)) {
                    try {
                        spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                    qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "'";
                } else {
                    qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
                }
            } else {
                mDBStkType = "";
                qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mArrayDistributors = OfflineManager.getDistributorListByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[11][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
            mArrayDistributors[10][0] = "";
        } else {
            try {
                if (mArrayDistributors[4][0] != null) {

                }
            } catch (Exception e) {
                mArrayDistributors = new String[11][1];
                mArrayDistributors[0][0] = "";
                mArrayDistributors[1][0] = "";
                mArrayDistributors[2][0] = "";
                mArrayDistributors[3][0] = "";
                mArrayDistributors[4][0] = "";
                mArrayDistributors[5][0] = "";
                mArrayDistributors[6][0] = "";
                mArrayDistributors[7][0] = "";
                mArrayDistributors[8][0] = "";
                mArrayDistributors[9][0] = "";
                mArrayDistributors[10][0] = "";
            }
        }

        return mArrayDistributors;
    }

    public static String getSPGUID() {
        String spGuid = "";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$top=1 &$select=" + Constants.SPGUID + " ", Constants.SPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return spGuid;
    }

    public static String getSampleInvoiceTypeID() {
        String InvoiceTypeID = "";
        try {
            InvoiceTypeID = OfflineManager.getValueByColumnName(Constants.SSInvoiceTypes + "?$top=1 &$select=" + Constants.InvoiceTypeID + " " +
                    "&$filter=" + Constants.GoodsIssueFromID + " eq '000002' and GoodsIssueCatID eq '000002'", Constants.InvoiceTypeID);
        } catch (OfflineODataStoreException e) {
            InvoiceTypeID = "";
        }
        return InvoiceTypeID;
    }

    public static String[][] getDMSDivisionByCPGUID(String mStrCPGUID) {
        String spGuid = "";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String[][] mArrayCPDMSDivisions = null;
        String qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' ";
        try {
            mArrayCPDMSDivisions = OfflineManager.getDMSDivisionByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayCPDMSDivisions == null) {
            mArrayCPDMSDivisions = new String[2][1];
            mArrayCPDMSDivisions[0][0] = "";
            mArrayCPDMSDivisions[1][0] = "";
        }

        return mArrayCPDMSDivisions;
    }

    public static String[][] getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(String mStrCPGUID) {
        String spGuid = "";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        String selCPDMSDIV = "";
        try {
            selCPDMSDIV = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.DMSDivision + " &$filter="
                    + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' ", Constants.DMSDivision);


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String[][] mArraySPValues = null;
        String qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and "
                + Constants.DMSDivision + " eq '" + selCPDMSDIV + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "'";
        try {
            mArraySPValues = OfflineManager.getSPValuesByCPGUIDAndDMSDivision(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        if (mArraySPValues == null) {
            mArraySPValues = new String[12][1];
            mArraySPValues[0][0] = "";
            mArraySPValues[1][0] = "";
            mArraySPValues[2][0] = "";
            mArraySPValues[3][0] = "";
            mArraySPValues[4][0] = "";
            mArraySPValues[5][0] = "";
            mArraySPValues[6][0] = "";
            mArraySPValues[7][0] = "";
            mArraySPValues[8][0] = "";
            mArraySPValues[9][0] = "";
            mArraySPValues[10][0] = "";
            mArraySPValues[11][0] = "";
        } else {
            try {
                if (mArraySPValues[4][0] != null) {

                }
            } catch (Exception e) {
                mArraySPValues = new String[12][1];
                mArraySPValues[0][0] = "";
                mArraySPValues[1][0] = "";
                mArraySPValues[2][0] = "";
                mArraySPValues[3][0] = "";
                mArraySPValues[4][0] = "";
                mArraySPValues[5][0] = "";
                mArraySPValues[6][0] = "";
                mArraySPValues[7][0] = "";
                mArraySPValues[8][0] = "";
                mArraySPValues[9][0] = "";
                mArraySPValues[10][0] = "";
                mArraySPValues[11][0] = "";
            }
        }

        return mArraySPValues;
    }

    public static HashMap<String, String> MAPQPSSCHGuidByMaterial = new HashMap<>();
    public static HashMap<String, ArrayList<String>> MAPSCHGuidByMaterial = new HashMap<>();//TODO need to change
    public static HashMap<String, String> MAPORDQtyByCrsSkuGrp = new HashMap<>();
    public static HashMap<String, ArrayList<String>> MAPSCHGuidByCrsSkuGrp = new HashMap<>();// TODO need to change
    public static HashMap<String, ArrayList<SKUGroupBean>> HashMapSubMaterials = new HashMap<>();
    public static ArrayList<SKUGroupBean> selectedSOItems = new ArrayList<>();
    public static ArrayList<StockBean> selectedStockItems = new ArrayList<>();

    public static void onVisitActivityUpdate(String mStrBundleCPGUID32, String loginIdVal,
                                             String visitActRefID, String vistActType, String visitActTypeDesc) {
        //========>Start VisitActivity
        try {
            Hashtable visitActivityTable = new Hashtable();
            String getVisitGuidQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
            ODataGuid mGuidVisitId = null;
            try {
                mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            GUID mStrGuide = GUID.newRandom();
            visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
            visitActivityTable.put(Constants.LOGINID, loginIdVal);
            visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
            visitActivityTable.put(Constants.ActivityType, vistActType);
            visitActivityTable.put(Constants.ActivityTypeDesc, visitActTypeDesc);
            visitActivityTable.put(Constants.ActivityRefID, visitActRefID);
            visitActivityTable.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.latitude));
            visitActivityTable.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.longitude));

            visitActivityTable.put(Constants.StartTime, mStartTimeDuration);
            visitActivityTable.put(Constants.EndTime, UtilConstants.getOdataDuration());


            try {
                OfflineManager.createVisitActivity(visitActivityTable);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //========>End VisitActivity
    }

    public static byte imageByteArray[] = null;
    public static byte videoByteArray[] = null;
    public static byte WordByteArray[] = null;
    public static byte pptByteArray[] = null;
    public static byte jpegByteArray[] = null;
    public static byte pngByteArray[] = null;

    public static boolean checkAlertsRecordsAvailable() {
        ArrayList<BirthdaysBean> alRetBirthDayList = null;
        ArrayList<BirthdaysBean> alAppointmentList = null;
        String[][] oneWeekDay;
        String splitDayMonth[] = null;
        oneWeekDay = UtilConstants.getOneweekValues(1);
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {

                splitDayMonth = oneWeekDay[0][i].split("-");

                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

        }

        String mStrAppointmentListQuery = Constants.Visits + "?$filter=" + Constants.StatusID + " eq '00'";
        try {
            alAppointmentList = OfflineManager.getAppointmentListForAlert(mStrAppointmentListQuery);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String mStrAlertQuery = Constants.ALERTS + Constants.isNonLocalFilterQry;
        boolean mAlertAval = false;
        try {
            mAlertAval = OfflineManager.getVisitStatusForCustomer(mStrAlertQuery);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if ((alRetBirthDayList != null && alRetBirthDayList.size() > 0) || (alAppointmentList != null && alAppointmentList.size() > 0) || mAlertAval) {
            return true;
        } else {
            return false;
        }


    }

    public static String RetailerStock = "RetailerStock";
    public static String SOCreateID = "06";
    public static String ROCreateID = "08";
    public static String CustomerCompCreateID = "10";
    public static String RetailerStockID = "07";
    public static String CollCreateID = "02";
    public static String MerchReviewCreateID = "01";
    public static String CompInfoCreateID = "04";
    public static String FeedbackID = "03";
    public static String SampleDisbursementID = "09";
    public static final String SubmittingDeviceFeedbacksPleaseWait = "Submitting device feedbacks, please wait";
    public static String DeviceFeedbacksText = "Device Feedbacks";
    public static String TLSD = "TLSD";
    public static String NotPurchasedType = "000004";

    public static String FeedbackSubTypeID = "FeedbackSubTypeID";
    public static String FeedbackSubTypeDesc = "FeedbackSubTypeDesc";
    public static String FeedbackSubType = "FeedbackSubType";
    public static final String PROP_FIPRTY = "FIPRTY";
    public static final String PROP_SCGOTY = "SCGOTY";
    public static final String TypesName = "TypesName";


    public static String getOrderValByRetiler(String mStrRetNo, String mStrCurrentDate, String mSOOrderType, Context context) {
        String mStrOrderVal = "0.0";
        double orderVal = 0.0;
        try {
            mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
                    "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate +
                    "' and " + Constants.SoldToId + " eq '" + mStrRetNo + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' ", Constants.NetPrice);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        double mdouDevOrderVal = 0.0;
        try {
            mdouDevOrderVal = OfflineManager.getDeviceTotalOrderAmtByRetailer(Constants.SOList, context, mStrCurrentDate, mStrRetNo);
        } catch (Exception e) {
            mdouDevOrderVal = 0.0;
        }

        orderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return orderVal + "";
    }

    public static String getInvoiceValByRetiler(String mStrRetNo, String mStrCurrentDate, String mSOOrderType, Context context) {
        String mStrOrderVal = "0.0";
        double orderVal = 0.0;
        try {
            mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoices +
                    "?$select=" + Constants.NetAmount + " &$filter=" + Constants.InvoiceDate + " eq datetime'" + mStrCurrentDate +
                    "' and " + Constants.SoldToID + " eq '" + mStrRetNo + "' ", Constants.NetAmount);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        double mdouDevOrderVal = 0.0;
        try {
            mdouDevOrderVal = OfflineManager.getDeviceTotalInvoiceAmtByRetailer(Constants.InvList, context, mStrCurrentDate, mStrRetNo);
        } catch (Exception e) {
            mdouDevOrderVal = 0.0;
        }

        orderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return orderVal + "";
    }

    public static String get12HoursFromat(String mStrTime) {
        String mStrTimeFormat = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date dateObj = sdf.parse(mStrTime);
            mStrTimeFormat = new SimpleDateFormat("h:mm aa").format(dateObj);
        } catch (final ParseException e) {
            mStrTimeFormat = "00:00";
        }
        return mStrTimeFormat;
    }

    public static String getDiffTime(String startTime, String endTime) {
        String mStrDiffTime = "";

        long difference = 0;
        try {
            if (!startTime.equalsIgnoreCase("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                try {
                    Date startDate = simpleDateFormat.parse(startTime);
                    Date endDate = simpleDateFormat.parse(endTime);

                    difference = endDate.getTime() - startDate.getTime();
                    if (difference < 0) {
                        Date dateMax = null;

                        dateMax = simpleDateFormat.parse(startTime);

                        Date dateMin = simpleDateFormat.parse(endTime);
                        difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);

            int seconds = (int) (difference / 1000) % 60;


            return (hours < 10 ? "0" + hours : hours) + ":" + (min < 10 ? "0" + min : min) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        } catch (Exception e) {
            return 00 + ":" + 00 + ":" + 00;
        }
    }

    public static final String Periodicity = "Periodicity";
    public static final String KPICategory = "KPICategory";
    public static String AsOnDate = "AsOnDate";

    public static String getGUIDEditResourcePath(String collection, String key) {
        return new String(collection + "(guid'" + key + "')");
    }

    public static void dialogBoxWithButton(Context context, String title, String message, String positiveButton, String negativeButton, final DialogCallBack dialogCallBack) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme);
            if (!title.equalsIgnoreCase("")) {
                builder.setTitle(title);
            }
            builder.setMessage(message).setCancelable(false).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (dialogCallBack != null)
                        dialogCallBack.clickedStatus(true);
                }
            });
            if (!negativeButton.equalsIgnoreCase("")) {
                builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (dialogCallBack != null)
                            dialogCallBack.clickedStatus(false);
                    }
                });
            }
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String pricingCalculation(String mStrTaxVal, String mStrTaxColumn) {
        String mStrCalAmt = "";


        return mStrCalAmt;

    }

    public static String formulaOneCalculation(String taxPer, String applyColumn) {
        Double mCaluclateVal = 0.0;
        try {
            mCaluclateVal = (Double.parseDouble(applyColumn) * Double.parseDouble(taxPer)) / 100;
        } catch (NumberFormatException e) {
            mCaluclateVal = 0.0;
        }

        if (mCaluclateVal.isInfinite() || mCaluclateVal.isNaN()) {
            mCaluclateVal = 0.0;
        }
        return mCaluclateVal + "";

    }

    public static String TaxRegStatusDesc = "TaxRegStatusDesc";
    public static String Tax1 = "Tax1";
    public static String Tax2 = "Tax2";
    public static String Tax3 = "Tax3";
    public static String Tax4 = "Tax4";
    public static String Tax5 = "Tax5";
    public static String Tax6 = "Tax6";
    public static String Tax7 = "Tax7";
    public static String Tax8 = "Tax8";
    public static String Tax9 = "Tax9";
    public static String Tax10 = "Tax10";

    public static String ConditionTypeID = "ConditionTypeID";
    public static String ConditionTypeDesc = "ConditionTypeDesc";
    public static String ReferenceTaxFieldID = "ReferenceTaxFieldID";
    public static String ReferenceTaxFieldDesc = "ReferenceTaxFieldDesc";
    public static String FormulaID = "FormulaID";
    public static String FormulaDesc = "FormulaDesc";
    public static String CalcOnID = "CalcOnID";
    public static String CalcOnDesc = "CalcOnDesc";
    public static String ApplicableOnID = "ApplicableOnID";
    public static String ApplicableOnDesc = "ApplicableOnDesc";
    public static String CalcOnConditionTypeID = "CalcOnConditionTypeID";
    public static String CalcOnConditionTypeDesc = "CalcOnConditionTypeDesc";
    public static String PricingConditions = "PricingConditions";
    public static String MustSells = "MustSels";


    public static String getCalculateColumn(String mStrApplicableOnID) {
        String mStrCalColumn = "";

        if (mStrApplicableOnID.equalsIgnoreCase("01")) {
//            mStrCalColumn = Constants.UnitPrice;
            mStrCalColumn = Constants.IntermUnitPrice;
        } else if (mStrApplicableOnID.equalsIgnoreCase("02")) {
            mStrCalColumn = Constants.LandingPrice;
        } else if (mStrApplicableOnID.equalsIgnoreCase("03")) {
            mStrCalColumn = Constants.MRP;
        } else if (mStrApplicableOnID.equalsIgnoreCase("04")) {
//            mStrCalColumn = Constants.UnitPrice;
            mStrCalColumn = Constants.IntermUnitPrice;
        }

        return mStrCalColumn;
    }

    public static String PC = "PC";

    private static SharedPreferences getAppointmentSharedPrefer(Context context) {
        return context.getSharedPreferences("appointmentPref", Context.MODE_PRIVATE);
    }

    public static void saveSharedPref(Context context, String key, int value) {
        SharedPreferences sharedPreferences = getAppointmentSharedPrefer(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getSharedPref(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = getAppointmentSharedPrefer(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void removeAlarmIdFromSharedPref(Context context, String key) {
        SharedPreferences sharedPreferences = getAppointmentSharedPrefer(context);
        if (sharedPreferences.contains(key)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public static void setCursorPosition(EditText editText) {
        int position = editText.getText().toString().length();
        editText.setSelection(position);
    }

    public static boolean isCustomKeyboardVisible(KeyboardView keyboardView) {
        boolean visibleStatus = false;
        try {
            if (keyboardView != null)
                visibleStatus = keyboardView.getVisibility() == View.VISIBLE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return visibleStatus;
    }

    public static void hideCustomKeyboard(KeyboardView keyboardView) {
        try {
            keyboardView.setVisibility(View.GONE);
            keyboardView.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomKeyboard(View v, KeyboardView keyboardView, Context context) {
        if (v != null) {
            ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);

    }

    public static void decrementEditTextVal(EditText editText, String mStrDotAval) {
        BigDecimal mDouAmountVal = new BigDecimal("0");
        String et_text = editText.getText().toString();
        String total = "0.0";
        if (!et_text.isEmpty()) {
            total = et_text;

        }

        if (total.contains(".")) {
//            double number = Double.parseDouble(total);
            BigDecimal number = new BigDecimal(total);
//            int integer = (int)number;
            BigInteger integer = new BigDecimal(number.doubleValue()).toBigInteger();
            String[] splitNumber = total.split("\\.");
            BigDecimal decimal = new BigDecimal("0.0");
            BigInteger subtactVal = new BigInteger("1");
            if (splitNumber.length > 1) {
                if (!splitNumber[1].equalsIgnoreCase("")) {
                    decimal = BigDecimal.valueOf(Double.parseDouble("." + splitNumber[1]));
                    mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue() + decimal.doubleValue());
                } else {
                    mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue());
                }
            } else {
                mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue());
            }

        } else {
            mDouAmountVal = BigDecimal.valueOf(Double.parseDouble(total) - 1);
        }
        int res = mDouAmountVal.compareTo(new BigDecimal("0"));

        if (res <= 0) {
            if (mStrDotAval.equalsIgnoreCase("Y")) {
                setCursorPos(editText);
                if (et_text.contains(".")) {
                    editText.setText("0.0");
                } else {
                    editText.setText(UtilConstants.removeLeadingZeroVal("0"));
                }
            } else {
                editText.setText("0");
            }
            setCursorPos(editText);
        } else {
            if (mStrDotAval.equalsIgnoreCase("Y")) {
                setCursorPos(editText);
                if (et_text.contains(".")) {
                    editText.setText(mDouAmountVal + "");
                } else {
                    editText.setText(UtilConstants.removeLeadingZeroVal(mDouAmountVal + ""));
                }
            } else {
                editText.setText(UtilConstants.removeLeadingZeroVal(mDouAmountVal + ""));
            }
            setCursorPos(editText);
        }

    }

    public static void incrementTextValues(EditText editText, String mStrDotAval) {
//        double sPrice = 0;
        BigDecimal sPrice = new BigDecimal("0");
        String et_text = editText.getText().toString();

        String total = "0.0";
        if (!et_text.isEmpty()) {
            total = et_text;
        }
//        sPrice = Double.parseDouble(total);
//        sPrice++;

        if (total.contains(".")) {
//            double number = Double.parseDouble(total);
            BigDecimal number = new BigDecimal(total);
//            int integer = (int)number;
            BigInteger integer = new BigDecimal(number.doubleValue()).toBigInteger();
            String[] splitNumber = total.split("\\.");
            BigDecimal decimal = new BigDecimal("0.0");
            BigInteger incrementVal = new BigInteger("1");
            if (splitNumber.length > 1) {
                if (!splitNumber[1].equalsIgnoreCase("")) {
//                    decimal = Double.parseDouble("."+splitNumber[1]);
                    decimal = BigDecimal.valueOf(Double.parseDouble("." + splitNumber[1]));
                    sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue() + decimal.doubleValue());
                } else {
                    sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue());
                }
            } else {
                sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue());
            }

        } else {
            sPrice = BigDecimal.valueOf(Double.parseDouble(total) + 1);
        }
        if (mStrDotAval.equalsIgnoreCase("Y")) {
            setCursorPos(editText);
            if (et_text.contains(".")) {
                editText.setText(sPrice + "");
            } else {
                editText.setText(UtilConstants.removeLeadingZeroVal(sPrice + ""));
            }
        } else {
            editText.setText(UtilConstants.removeLeadingZeroVal(sPrice + ""));
        }
        setCursorPos(editText);

    }

    private static void setCursorPos(EditText editText) {
        int position = 0;
        try {
            position = editText.getText().toString().length();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            editText.setSelection(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String Y = "Y";
    public static String N = "N";
    public static String WindowDisplayID = "11";
    public static String WindowDisplayClaimID = "13";
    public static String WindowDisplayValueHelp = "WindowDisplay";
    public static String CameraPackage = "android.media.action.IMAGE_CAPTURE";

    public static String getCameraPackage(Context context) {
        String defaultCameraPackage = "";
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int n = 0; n < list.size(); n++) {
            if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                    defaultCameraPackage = list.get(n).packageName;
                    break;
                }
            }
        }
        return defaultCameraPackage;
    }

    /**
     * SHOW PROGRESS DIALOG
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = null;
        try {
            progressDialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
            progressDialog.setMessage(message);
//            progressDialog.setTitle(title);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }


    /**
     * HIDE PROGRESS DIALOG
     *
     * @param progressDialog
     */
    public static void hideProgressDialog(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null)
                progressDialog.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * open camera
     */
    public static void openCameraWindow(Activity context) {
        try {
            String defaultCameraPackage = "";
            PackageManager packageManager = context.getPackageManager();
            List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (int n = 0; n < list.size(); n++) {
                if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                        defaultCameraPackage = list.get(n).packageName;
                        break;
                    }
                }
            }

            Intent intentResult = new Intent("android.media.action.IMAGE_CAPTURE");
            intentResult.setPackage(defaultCameraPackage);
            context.startActivityForResult(intentResult, TAKE_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete visual vid product
     */
    public static void deleteFolder() {

        File myDirectory = new File(Environment.getExternalStorageDirectory(), "VisualVid");
        if (myDirectory.exists()) {
            myDirectory.delete();
        }


    }

    public static void openImageInGallery(Context mContext, String file) {
//        String videoResource = file.getPath();
        Uri intentUri = Uri.fromFile(new File(file));
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(intentUri.fromFile(new File(file)), "image/jpeg");
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
            try {
                Toast.makeText(mContext, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String SchemeFreeMatGrpMaterials = "SchemeFreeMatGrpMaterials";
    public static String SchemeItems = "SchemeItems";
    public static String SchemeItemDetails = "SchemeItemDetails";
    public static String SchemeSlabs = "SchemeSlabs";
    public static String SchemeGeographies = "SchemeGeographies";
    public static String SchemeCPs = "SchemeCPs";
    public static String SchemeSalesAreas = "SchemeSalesAreas";
    public static String SchemeCostObjects = "SchemeCostObjects";
    public static String ItemMin = "ItemMin";

    public static Date convertStringToDate(String dates) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.parse(dates);
    }

    public static String SchemeTypeID = "SchemeTypeID";
    public static String SchemeTypeDesc = "SchemeTypeDesc";
    public static String SchemeID = "SchemeID";
    public static String ValidFrom = "ValidFrom";
    public static String ValidTo = "ValidTo";
    public static String SKUGroupID = "SKUGroupID";
    public static String SKUGroupDesc = "SKUGroupDesc";
    public static String SchemeGUID = "SchemeGUID";
    public static String PriDiscountPer = "PriDiscountPer";
    public static String ProductCatID = "ProductCatID";


    public static String DMSDivisionID = "DMSDivisionID";
    public static String DmsDivsionDesc = "DmsDivsionDesc";
    public static String CPGroup2Desc = "CPGroup2Desc";
    public static String SalesAreaGUID = "SalesAreaGUID";
    public static String CPGroup1ID = "CPGroup1ID";
    public static String CPGroup1Desc = "CPGroup1Desc";
    public static String CPGroup3Desc = "CPGroup3Desc";
    public static String DivisionID = "DivisionID";
    public static String CPGroup4Desc = "CPGroup4Desc";
    public static String DistributionChannelID = "DistributionChannelID";
    public static String CPGroup3ID = "CPGroup3ID";
    public static String SalesOrgDesc = "SalesOrgDesc";
    public static String DistributionChannelDesc = "DistributionChannelDesc";
    public static String DivisionDesc = "DivisionDesc";
    public static String CPGroup4ID = "CPGroup4ID";
    public static String SalesOrgID = "SalesOrgID";
    public static String CPGroup2ID = "CPGroup2ID";

    public static String SchemeLevelID = "SchemeLevelID";
    public static String GeographyGUID = "GeographyGUID";
    public static String SchemeValueDesc = "SchemeValueDesc";
    public static String SchemeScopeDesc = "SchemeScopeDesc";
    public static String SchemeLevelDesc = "SchemeLevelDesc";
    public static String SchemeValueID = "SchemeValueID";
    public static String SchemeScopeID = "SchemeScopeID";

    public static String GeographyScopeID = "GeographyScopeID";
    public static String GeographyScopeDesc = "GeographyScopeDesc";
    public static String GeographyLevelID = "GeographyLevelID";
    public static String GeographyLevelDesc = "GeographyLevelDesc";
    public static String GeographyTypeID = "GeographyTypeID";
    public static String GeographyTypeDesc = "GeographyTypeDesc";
    public static String GeographyValueID = "GeographyValueID";
    public static String GeographyValueDesc = "GeographyValueDesc";
    public static String SchemeItemGUID = "SchemeItemGUID";
    public static String FromQty = "FromQty";
    public static String ToQty = "ToQty";
    public static String FromValue = "FromValue";
    public static String ToValue = "ToValue";
    public static String PayoutPerc = "PayoutPerc";
    public static String PayoutAmount = "PayoutAmount";
    public static String SlabRuleID = "SlabRuleID";
    public static String SlabRuleDesc = "SlabRuleDesc";
    public static String SaleUnitID = "SaleUnitID";
    public static String FreeQty = "FreeQty";
    public static String FreeArticle = "FreeArticle";
    public static String NoOfCards = "NoOfCards";
    public static String CardTitle = "CardTitle";
    public static String SalesAreaID = "SalesAreaID";
    public static String SlabGUID = "SlabGUID";
    public static String SaleUnitIDAmountWise = "000002";
    public static String TargetBasedID = "TargetBasedID";

    public static String getActiveSchemeQry() {
        ArrayList<SchemeBean> alScheme = null;
        try {
            alScheme = OfflineManager.getSchemeGrp(Constants.Schemes + "?$filter= " + Constants.StatusID +
                    " eq '01' and ValidTo ge datetime'" + UtilConstants.getNewDate() + "' ");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String mStrSchemeQry = "";

        if (alScheme != null && alScheme.size() > 0) {
            for (int i = 0; i < alScheme.size(); i++) {
                if (i == 0 && i == alScheme.size() - 1) {
                    mStrSchemeQry = mStrSchemeQry
                            + "(" + Constants.SchemeGUID + "%20eq%20guid'"
                            + alScheme.get(i).getSchemeGUID().toUpperCase() + "')";

                } else if (i == 0) {
                    mStrSchemeQry = mStrSchemeQry
                            + "(" + Constants.SchemeGUID + "%20eq%20guid'"
                            + alScheme.get(i).getSchemeGUID().toUpperCase() + "'";

                } else if (i == alScheme.size() - 1) {
                    mStrSchemeQry = mStrSchemeQry
                            + "%20or%20" + Constants.SchemeGUID + "%20eq%20guid'"
                            + alScheme.get(i).getSchemeGUID().toUpperCase() + "')";
                } else {
                    mStrSchemeQry = mStrSchemeQry
                            + "%20or%20" + Constants.SchemeGUID + "%20eq%20guid'"
                            + alScheme.get(i).getSchemeGUID().toUpperCase() + "'";
                }
            }

        }

        if (!mStrSchemeQry.equalsIgnoreCase("")) {
            mStrSchemeQry = Constants.SchemeItemDetails + "?$filter=" + mStrSchemeQry;
        }

        return mStrSchemeQry + "";
    }

    public static String SchemeSaleUnitIDCBB = "000004";
    public static String SchemeFreeProdSeq = "000001";
    public static String SchemeFreeProdLowMRP = "000002";

    public static String SchemeFreeProduct = "000001";
    public static String SchemeFreeSKUGroup = "000007";
    public static String SchemeFreeCRSSKUGroup = "000002";
    public static String SchemeFreeDiscountPercentage = "000003";
    public static String SchemeFreeDiscountAmount = "000004";
    public static String SchemeFreeScratchCard = "000005";
    public static String SchemeFreeFreeArticle = "000006";

    public static String OnSaleOfBanner = "000001";
    public static String OnSaleOfBrand = "000002";
    public static String OnSaleOfProdCat = "000003";
    public static String OnSaleOfOrderMatGrp = "000004";
    public static String OnSaleOfMat = "000005";
    public static String OnSaleOfSchemeMatGrp = "000006";

    public static String Free_Txt = "Free";
    public static String CPGeoClassifications = "CPGeoClassifications";
    public static String GeographyMapping = "GeographyMapping";
    public static String IsHeaderBasedSlab = "IsHeaderBasedSlab";
    public static String OrgScopeID = "OrgScopeID";
    public static String OrgScopeDesc = "OrgScopeDesc";
    public static String SalesAreaWise = "000001";
    public static String DMSDivisionWise = "000002";
    public static String IsExcluded = "IsExcluded";
    public static String Zone = "Zone";
    public static String Region = "Region";
    public static String RegionDesc = "RegionDesc";
    public static String Area = "Area";
    public static String HeadQuarter = "HQ";
    public static String Depot = "Depot";
    public static String OrderMaterialGroup = "OrderMaterialGroup";
    public static String SlabTypeID = "SlabTypeID";
    public static String SlabTypeDesc = "SlabTypeDesc";
    public static String IsIncludingPrimary = "IsIncludingPrimary";
    public static String SchemeCatID = "SchemeCatID";
    public static String SchemeCatDesc = "SchemeCatDesc";
    public static String OnSaleOfCatID = "OnSaleOfCatID";
    public static String OnSaleOfCatIDOrderMatGrp = "000005";
    public static String OnSaleOfCatIDMaterial = "000005";
    public static Boolean BoolMatWiseSchemeAvalible = false;
    public static Boolean BoolMatWiseQPSSchemeAvalible = false;

    public static String getNoOfDaysBefore(int days) {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static double DoubGetRunningSlabPer = 0.0;
    public static String SchemeCatIDInstantScheme = "000002";
    public static String BasketCatID = "000002";
    public static String SchemeCatIDQPSScheme = "000001";
    public static String SchemeTypeIDBasketScheme = "000008";

    public static TextView setFontSizeByMaxText(TextView textView) {
        try {
            int lineCount = textView.getText().length();

            if (lineCount < 20) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else if (lineCount < 35) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            } else if (lineCount < 50) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            } else if (lineCount < 70) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            } else if (lineCount < 85) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            } else {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 6);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        return textView;
    }

    public static int dpToPx(int i, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = i * ((int) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    // TODO Calculate  primary and secondary scheme discount
    public static String calculatePrimaryDiscount(String mStrPriDis, String mStrNetAmt) {
        String mDouPriSchDis = "0.0";

        try {
            if (Double.parseDouble(mStrPriDis) > 0 && Double.parseDouble(mStrNetAmt) > 0) {
                mDouPriSchDis = Constants.formulaOneCalculation(mStrPriDis, mStrNetAmt);
            } else {
                mDouPriSchDis = "0.0";
            }
        } catch (NumberFormatException e) {
            mDouPriSchDis = "0.0";
        }
        return mDouPriSchDis;
    }

    public static final String UOMNO0 = "UOMNO0";

    public static String[][] CheckForOtherInConfigValue(String[][] configValues) {
        for (int i = 0; i < configValues[0].length; i++) {
            if (configValues[1][i].equalsIgnoreCase("Others")) {
                String[] temp = new String[configValues.length];
                for (int k = 0; k < configValues.length; k++) {
                    temp[k] = configValues[k][i];
                }
                for (int j = i; j < configValues[0].length - 1; j++) {
                    for (int k = 0; k < configValues.length; k++) {
                        configValues[k][j] = configValues[k][j + 1];
                    }
                }
                for (int k = 0; k < configValues.length; k++) {
                    configValues[k][configValues[0].length - 1] = temp[k];
                }
                break;
            }
        }
        return configValues;
    }

    public static boolean isSchemeBasketOrNot(String mStrSchemeGuid) {
        boolean mBoolHeadWiseScheme = false;
        String mStrSchemeQry = Constants.Schemes + "?$filter= " + Constants.SchemeGUID +
                " eq guid'" + mStrSchemeGuid + "' and  " + Constants.SchemeTypeID + " eq '" + Constants.SchemeTypeIDBasketScheme + "' ";
        try {
            mBoolHeadWiseScheme = OfflineManager.getVisitStatusForCustomer(mStrSchemeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mBoolHeadWiseScheme;
    }

    public static HashMap<String, SchemeBean> hashMapCpStockItemGuidQtyValByMaterial = new HashMap<>();
    public static HashMap<String, Set> hashMapMaterialValByOrdMatGrp = new HashMap<>();

    public static String SchemeTypeNormal = "NormalScheme";
    public static String SchemeTypeBasket = "BasketScheme";
    public static String EmptyGUIDVal = "00000000-0000-0000-0000-000000000000";
    public static String BasketHeadingName = "Basket(Min)";
    public static Boolean IsOnlineStoreFailed = false;
    public static Boolean IsOnlineStoreSyncLogFailed = false;
    public static Boolean IsOnlineStoreMustSellFailed = false;
    public static HashSet<String> mSetTodayRouteSch = new HashSet<>();
    public static String TodayTargetRetailersCount = "0";
    public static String TodayActualVisitRetailersCount = "0";
    public static String TodayAchivedPer = "0";
    public static ArrayList<CustomerBean> alTodayBeatRet = new ArrayList<>();
    public static ArrayList<CustomerBean> alOtherBeatRet = new ArrayList<>();

    public static final String Error = "Error";
    public static final String Error_code_missing = "";
    public static int ErrorCode = 0;
    public static int ErrorNo = 0;
    public static int ErrorNoSyncLog = 0;
    public static int ErrorNoTechincalCache = 0;
    public static int ErrorNo_Get_Token = 0;
    public static String ErrorName = "";
    public static String NetworkError_Name = "NetworkError";
    public static String Comm_error_name = "Communication error";
    public static String Network_Name = "Network";
    public static String Unothorized_Error_Name = "401";
    public static String Max_restart_reached = "Maximum restarts reached";
    public static int Network_Error_Code = 101;
    public static int Comm_Error_Code = 110;
    public static int UnAuthorized_Error_Code = 401;
    public static int UnAuthorized_Error_Code_Offline = -10207;
    public static int Network_Error_Code_Offline = -10205;
    public static int Unable_to_reach_server_offline = -10208;
    public static int Resource_not_found = -10210;
    public static int Unable_to_reach_server_failed_offline = -10204;
    public static int Build_Database_Failed_Error_Code1 = -100036;
    public static int Build_Database_Failed_Error_Code2 = -100097;
    public static int Build_Database_Failed_Error_Code3 = -10214;
    public static int Database_Transction_Failed_Error_Code = -10104;
    public static String Invalid_input_Error_Code = "-10133";
    public static String Executing_SQL_Commnd_Error = "10001";
    public static int Execu_SQL_Error_Code = -10001;
    public static int Store_Def_Not_matched_Code = -10247;
    public static String Store_Defining_Req_Not_Matched = "10247";
    public static String RFC_ERROR_CODE_100027 = "100027";
    public static String RFC_ERROR_CODE_100029 = "100029";
    public static String Invalid_Store_Option_Value = "InvalidStoreOptionValue";
    static ArrayList<MyTargetsBean> alKpiList = null;
    static Map<String, Double> mapMonthTarget;
    static Map<String, Double> mapMonthAchived;
    static Map<String, MyTargetsBean> mapMyTargetVal;
    static MyTargetsBean salesKpi = null;
    static String mStrTotalOrderVal = "0";
    static int mIntBalVisit = 0;
    static ArrayList<MyTargetsBean> alMyTargets = null;
    static String mStrSpGuid = "";
    static String mStrCPDMSDIV = "";
    static String mStrVisitTargetRetCount = "0";
    static double mDoubInvGrossAmt = 0.0;

    public static void loadingTodayAchived(Context context, ArrayList<CustomerBean> alTodaysRet) {
        DmsDivQryBean dmsDivQryBean = Constants.getDMSDIV("");
        mStrCPDMSDIV = dmsDivQryBean.getCVGValueQry();
        mapMonthTarget = new HashMap<>();
        mapMonthAchived = new HashMap<>();
        mapMyTargetVal = new HashMap<>();
        alKpiList = new ArrayList<>();
        mStrTotalOrderVal = "0";
        mIntBalVisit = 0;
        mStrSpGuid = "";
        mStrVisitTargetRetCount = "0";
        salesKpi = null;
        Constants.TodayAchivedPer = "0";
        mDoubInvGrossAmt = 0.0;
        try {
            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '06' and " + Constants.CalculationBase + " eq '02' ", mStrCPDMSDIV);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (salesKpi != null) {
            alKpiList.add(salesKpi);
        }
        Constants.alRetailers.clear();
        ArrayList<CustomerBean> alTodaysRetailers = null;
        if (alTodaysRet.size() > 0) {
            alTodaysRetailers = alTodaysRet;
        } else {
            alTodaysRetailers = Constants.getTodaysBeatRetailers();
        }

        if (alTodaysRetailers != null && alTodaysRetailers.size() > 0) {
            mStrVisitTargetRetCount = alTodaysRetailers.size() + "";
        } else {
            mStrVisitTargetRetCount = "0";
        }
        mStrTotalOrderVal = Constants.getTotalOrderValue(context, UtilConstants.getNewDate(), alTodaysRetailers);
        mIntBalVisit = DaySummaryActivity.getBalanceVisit(alTodaysRetailers);
        mDoubInvGrossAmt = DaySummaryActivity.getInvoiceAmtByRetailer(Constants.alRetailers, dmsDivQryBean.getDMSDivisionSSInvQry());
        String[][] mArrayDistributors = Constants.getDistributors();
        try {
            mStrSpGuid = mArrayDistributors[8][0];
        } catch (Exception e) {
            mStrSpGuid = "";
        }
        getMyTargetsList();
    }

    private static void getMyTargetsList() {
        try {
            if (alKpiList != null && alKpiList.size() > 0) {
                alMyTargets = OfflineManager.getTargetsDaySummary(alKpiList, mStrSpGuid);
            }
            mapMyTargetVal = getALMyTargetList(alMyTargets);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        achivedPerCal();
    }

    private static void achivedPerCal() {
        double mDoubMonthAchived = 0.0;
        if (!mapMyTargetVal.isEmpty()) {
            Iterator iterator = mapMyTargetVal.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();

                double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), (mDoubInvGrossAmt) + "");

                Double mDoubTargAchi = 0.0;
                try {
                    mDoubTargAchi = BTDPer / mIntBalVisit;
                } catch (Exception e) {
                    mDoubTargAchi = 0.0;
                }

                if (mDoubTargAchi.isNaN() || mDoubTargAchi.isInfinite()) {
                    mDoubTargAchi = 0.0;
                }


                mDoubMonthAchived = mapMonthAchived.get(key) + Double.parseDouble(mStrTotalOrderVal);

                Constants.TodayAchivedPer = OfflineManager.getAchivedPer(mDoubTargAchi + "", mDoubMonthAchived + "") + "";


            }
        }
    }

    //ToDo sum of actual and target quantity/Value based on kpi code and assign to map table
    private static Map<String, MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets) {
        Map<String, MyTargetsBean> mapMyTargetBean = new HashMap<>();
        if (alMyTargets != null && alMyTargets.size() > 0) {
            for (MyTargetsBean bean : alMyTargets)
                if (mapMonthTarget.containsKey(bean.getKPICode())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) + mapMonthTarget.get(bean.getKPICode());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) + mapMonthAchived.get(bean.getKPICode());
                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
                } else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA());
                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
                }
        }
        return mapMyTargetBean;
    }

    public static String OpenAdvanceAmt = "OpenAdvanceAmt";
    public static String ApplicationID = "ApplicationID";
    public static String OTP = "OTP";
    public static String GUIDVal = "GUID";
    public static String OldPassword = "OldPassword";
    public static String NewPassword = "NewPassword";
    public static String isForgotPwdActivated = "isForgotPwdActivated";
    public static String isUserIsLocked = "isUserIsLocked";
    public static String ForgotPwdOTP = "ForgotPwdOTP";
    public static String ForgotPwdGUID = "ForgotPwdGUID";
    public static boolean isBoolPwdgenerated = false;
    public static String IsChange = "IsChange";
    public static String Passwords = "Passwords";
    public static final String PasswordGUID = "PasswordGUID";
    //    public static String PasswordEntity = "/ARTEC/GCGW.Password";
    public static String PasswordEntity = ".Password";

    public static String encodedPwd(String mStrPwd) {
        String encodedPwd = "";
        byte[] data = new byte[0];
        try {
            data = mStrPwd.getBytes("UTF-8");
            encodedPwd = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodedPwd;
    }

    public static void setCursorPostion(EditText editText, View view, MotionEvent motionEvent) {
        EditText edText = (EditText) view;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int touchPosition = editText.getOffsetForPosition(x, y);
        if (touchPosition >= 0) {
            editText.setSelection(touchPosition);
        }
    }

    public static int getCursorPostion(EditText editText, View view, MotionEvent motionEvent) {
        int touchPosition = 0;
        try {
            EditText edText = (EditText) view;
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            touchPosition = editText.getOffsetForPosition(x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return touchPosition;
    }


    public static HashMap<String, String> convertStringToMap(String jsonString) {
        HashMap myHashMap = new HashMap();
        try {
            JSONArray e = new JSONArray("[" + jsonString + "]");
            JSONObject jObject = null;
            String keyString = null;

            for (int i = 0; i < e.length(); ++i) {
                jObject = e.getJSONObject(i);
                for (int k = 0; k < jObject.length(); ++k) {
                    keyString = (String) jObject.names().get(k);
                    myHashMap.put(keyString, jObject.getString(keyString));
                }
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return myHashMap;
    }

    public static HashMap<String, String> getBirthdayListFromDataValt(String mStrKeyVal) {
        HashMap hashMap = new HashMap();
        //Fetch object from data vault
        try {
            JSONObject fetchJsonHeaderObject = new JSONObject(mStrKeyVal);

            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

            hashMap = convertStringToMap(itemsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static int getAlertsCountFromDataVault() {
        int mIntAlertsCount = 0;
        JSONObject fetchJsonHeaderObject = null;
        String store = null;
        try {
            store = LogonCore.getInstance().getObjectFromStore(AlertsDataKey);
        } catch (LogonCoreException e) {
            store = "";
        }

        if (store != null && !store.equalsIgnoreCase("")) {
            try {
                fetchJsonHeaderObject = new JSONObject(store);
                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                ArrayList<BirthdaysBean> alDataValutAlertList = Constants.convertToBirthDayArryList(itemsString);
                if (alDataValutAlertList != null && alDataValutAlertList.size() > 0) {
                    for (BirthdaysBean birthdaysBean : alDataValutAlertList) {
                        if (!birthdaysBean.getStatus().equalsIgnoreCase(Constants.Y)) {
                            mIntAlertsCount++;
                        }
                    }
                } else {
                    mIntAlertsCount = 0;
                }
            } catch (JSONException e) {
                mIntAlertsCount = 0;
            }
        }
        return mIntAlertsCount;
    }

    public static void setAlertsValToDataVault(ArrayList<BirthdaysBean> alAlerts, String alertsKey) {

        Hashtable dbHeaderTable = new Hashtable();
        Gson gson = new Gson();
        try {
            String jsonFromMap = gson.toJson(alAlerts);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            LogonCore.getInstance().addObjectToStore(alertsKey, jsonHeaderObject.toString());
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<BirthdaysBean> getAlertsValuesFromDataVault() {
        ArrayList<BirthdaysBean> alDataValutAlertList = new ArrayList<>();
        ArrayList<BirthdaysBean> alDataValutAlertHistList = new ArrayList<>();
        JSONObject fetchJsonHeaderObject = null;
        String store = null;
        try {
            store = LogonCore.getInstance().getObjectFromStore(AlertsDataKey);
        } catch (LogonCoreException e) {
            store = "";
        }

        if (store != null && !store.equalsIgnoreCase("")) {
            try {
                fetchJsonHeaderObject = new JSONObject(store);
                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                alDataValutAlertList = Constants.convertToBirthDayArryList(itemsString);
                if (alDataValutAlertList != null && alDataValutAlertList.size() > 0) {
                    for (BirthdaysBean birthdaysBean : alDataValutAlertList) {
                        if (birthdaysBean.getStatus().equalsIgnoreCase(Constants.Y)) {
                            alDataValutAlertHistList.add(birthdaysBean);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return alDataValutAlertHistList;
    }

    public static void setBirthdayToDataVault(HashMap<String, String> hashMap, String alertsKey) {

        Hashtable dbHeaderTable = new Hashtable();
        Gson gson = new Gson();
        try {
            String jsonFromMap = gson.toJson(hashMap);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            LogonCore.getInstance().addObjectToStore(alertsKey, jsonHeaderObject.toString());
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }


    public static void updateBirthdayAlertsStatus(String keyVal) {
        String store = null;
        try {
            store = LogonCore.getInstance().getObjectFromStore(keyVal);
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
        if (store != null && !store.equalsIgnoreCase("")) {
            HashMap<String, String> hashMap = Constants.getBirthdayListFromDataValt(store);
            if (!hashMap.isEmpty()) {
                Iterator mapSelctedValues = hashMap.keySet()
                        .iterator();
                while (mapSelctedValues.hasNext()) {
                    String Key = (String) mapSelctedValues.next();
                    hashMap.put(Key, Constants.Y);
                }
            }
            setBirthdayToDataVault(hashMap, keyVal);

        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            setBirthdayToDataVault(hashMap, keyVal);
        }
    }

    public static String getSOOrderType() {
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps + "?$top=1 &$select=" + Constants.ID + " &$filter=" + Constants.EntityType + " eq 'SSSO' and  " +
                    "" + Constants.PropName + " eq 'OrderType' and  " + Constants.ParentID + " eq '000010' ", Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return ordettype;
    }

    public static String getReturnOrderType() {
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps + "?$top=1 &$select=" + Constants.ID + " &$filter=" + Constants.EntityType + " eq 'SSRO' and  " +
                    "" + Constants.PropName + " eq 'OrderType' and  " + Constants.ParentID + " eq '000020' ", Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return ordettype;
    }

    public static String SS_FOS01_USER = "SS_FOS01";

    public static DmsDivQryBean getDMSDIV(String mStrParentID) {
        DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
        try {
            if (mStrParentID.equalsIgnoreCase("")) {
                dmsDivQryBean = OfflineManager.getDMSDIVQry(Constants.CPSPRelations);
            } else {
                dmsDivQryBean = OfflineManager.getDMSDIVQry(Constants.CPSPRelations + "?$filter=" + Constants.CPGUID + " eq '" + mStrParentID + "'");
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return dmsDivQryBean;
    }

    public static String getOrderQtyByRetiler(String mStrRetNo, String mStrCurrentDate, Context context, String ssoQry) {
        String mStrOrderQty = "0.0";
        double orderQty = 0.0;
        try {
            if (!ssoQry.equalsIgnoreCase("")) {
                mStrOrderQty = OfflineManager.getTotalSumByCondition("" + Constants.SSSoItemDetails +
                        "?$select=" + Constants.Quantity + " &$filter= " + ssoQry + " ", Constants.Quantity);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        double mdouDevOrderQty = 0.0;
        try {
            mdouDevOrderQty = OfflineManager.getDeviceTotalOrderQtyByRetailer(Constants.SOList, context, mStrCurrentDate, mStrRetNo, Constants.Quantity);
        } catch (Exception e) {
            mdouDevOrderQty = 0.0;
        }

        orderQty = Double.parseDouble(mStrOrderQty) + mdouDevOrderQty;

        return orderQty + "";
    }

    public static String getInvQtyByInvQry(String invQry) {
        String mStInvQty = "0.0";
        try {
            if (!invQry.equalsIgnoreCase("")) {
                mStInvQty = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoiceItemDetails +
                        "?$select=" + Constants.Quantity + " &$filter= " + invQry + " ", Constants.Quantity);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStInvQty + "";
    }


    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }


    public static String getRouteSchGUID(String collName, String columnName, String whereColumnn, String whereColval, String cpTypeID) {
        String mStrRouteSchGUID = "";
        if (cpTypeID.equalsIgnoreCase(Constants.str_01)) {
            try {
                // mStrRouteSchGUID = OfflineManager.getGuidValueByColumnNameRP(collName + "?$select=" + columnName+ " &$filter = " + whereColumnn + " eq '" + whereColval + "'", columnName);
                mStrRouteSchGUID = OfflineManager.getGuidValueByColumnName(collName + "?$top=1 &$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "' and " + Constants.StatusID + " eq '01'", columnName);

            } catch (Exception e) {
                mStrRouteSchGUID = "";
            }
        } else {
            // future will use ful
        }

        return mStrRouteSchGUID;
    }

    public static String getSourceValFromChannelPartner(String cpGUID) {
        String mStrSource = "";
        try {
            mStrSource = OfflineManager.getValueByColumnName(Constants.ChannelPartners +
                    "?$top=1 &$select=" + Constants.Source + " &$filter = " +
                    Constants.CPGUID + " eq guid'" + cpGUID.toUpperCase() + "'", Constants.Source);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return mStrSource;
    }

    public static ODataDuration mStartTimeDuration = null;

    public static boolean isValidTime(String startTime, String endTime) {
        boolean isValidTime = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            Date startTimeDate = simpleDateFormat.parse(startTime);
            Date endTimeDate = simpleDateFormat.parse(endTime);
            if (endTimeDate.before(startTimeDate)) {
                isValidTime = false;
            } else {
                isValidTime = true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isValidTime;
    }

    public static boolean isValidDate() {
        boolean isValidDate = false;
        String mDayStartDate = "";
        try {
            mDayStartDate = OfflineManager.getAsOnDate(Constants.Attendances +
                    "?$select=" + Constants.StartDate + " &$filter=" + Constants.ENDDATE + " eq null ", Constants.StartDate);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String mStrConvStartDate = ConstantsUtils.convertDateFromString(mDayStartDate);

        String todaysDate = UtilConstants.getNewDateTimeFormat();

        SimpleDateFormat sdf = null;
        Date date1 = null;
        Date date2 = null;
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            date1 = sdf.parse(mStrConvStartDate);
            date2 = sdf.parse(todaysDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1.compareTo(date2) == 0) {
            isValidDate = true;
        } else {
            isValidDate = false;
        }
        return isValidDate;
    }

    public static boolean Is_Return_Order_Tab_Delete = false;
    public static HashMap<String, String> Map_Must_Sell_Mat = new HashMap<>();
    public static String IS_TRUE = "true";
    public static String IS_NEW = "isNew";

    public static double getAvgSumCal(double mNumnitorVal, int mDenmenatorVal) {
        Double mDouAvgVal = 0.0;
        try {
            mDouAvgVal = mNumnitorVal / mDenmenatorVal;
        } catch (Exception e) {
            mDouAvgVal = 0.0;
        }

        if (mDouAvgVal.isInfinite() || mDouAvgVal.isNaN()) {
            mDouAvgVal = 0.0;
        }

        return mDouAvgVal;
    }

    //pretty print a map
    public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey()
                    + " Value : " + entry.getValue());
        }
    }

    /*
     * Java method to sort Map in Java by value e.g. HashMap or Hashtable
     * throw NullPointerException if Map contains null values
     * It also sort values even if they are duplicates
     */
    public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new LinkedList<>(map.entrySet());

        java.util.Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K, V> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static String convertSetToString(Set<String> stringSet, String propertyName) {
        String mStrQry = "";
        if (stringSet != null && !stringSet.isEmpty()) {
            Iterator itr = stringSet.iterator();
            int incVal = 0;
            while (itr.hasNext()) {
                if (incVal == 0 && incVal == stringSet.size() - 1) {
                    mStrQry = mStrQry
                            + "(" + propertyName + "%20eq%20guid'"
                            + itr.next().toString() + "' )";

                } else if (incVal == 0) {
                    mStrQry = mStrQry
                            + "((" + propertyName + "%20eq%20guid'"
                            + itr.next().toString() + "' )";

                } else if (incVal == stringSet.size() - 1) {
                    mStrQry = mStrQry
                            + "%20or%20(" + propertyName + "%20eq%20guid'"
                            + itr.next().toString() + "' ))";
                } else {
                    mStrQry = mStrQry
                            + "%20or%20(" + propertyName + "%20eq%20guid'"
                            + itr.next().toString() + "' ) ";
                }
                incVal++;
            }
        }

        return mStrQry;
    }

    public static String convertHashMapToString(HashMap<String, String> hashMap, String propertyName) {
        String mStrQry = "";
        if (hashMap != null && !hashMap.isEmpty()) {
            Iterator iterator = hashMap.keySet().iterator();
            int incVal = 0;
            while (iterator.hasNext()) {
                if (incVal == 0 && incVal == hashMap.size() - 1) {
                    mStrQry = mStrQry
                            + "(" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "')";

                } else if (incVal == 0) {
                    mStrQry = mStrQry
                            + "((" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "')";

                } else if (incVal == hashMap.size() - 1) {
                    mStrQry = mStrQry
                            + "%20or%20(" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "'))";
                } else {
                    mStrQry = mStrQry
                            + "%20or%20(" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "') ";
                }
                incVal++;
            }
        }

        return mStrQry;
    }

    public static ArrayList<String> getDefinigReqList(Context mContext) {
        ArrayList<String> alAssignColl = new ArrayList<>();
        String[] DEFINGREQARRAY = getDefinigReq(mContext);
        for (String collectionName : DEFINGREQARRAY) {
            if (collectionName.contains("?")) {
                String splitCollName[] = collectionName.split("\\?");
                collectionName = splitCollName[0];
            }
            alAssignColl.add(collectionName);
        }
        return alAssignColl;
    }

    public static HashMap<String, String> mMapCPSeqNo = new HashMap<>();

    public static String makeCPQry(ArrayList<String> alRetailers, String columnName) {
        String mCPQry = "";
        for (String cpNo : alRetailers) {
            if (mCPQry.length() == 0)
                mCPQry += " " + columnName + " eq '" + cpNo + "'";
            else
                mCPQry += " or " + columnName + " eq '" + cpNo + "'";

        }

        return mCPQry;
    }

    public static String makeCPQryFromBeanList(ArrayList<CustomerBean> alRetailers, String columnName) {
        String mCPQry = "";
        for (CustomerBean retBean : alRetailers) {
            if (mCPQry.length() == 0)
                mCPQry += " " + columnName + " eq '" + retBean.getCpGuidStringFormat() + "'";
            else
                mCPQry += " or " + columnName + " eq '" + retBean.getCpGuidStringFormat() + "'";

        }
        return mCPQry;
    }

    public static String makeCPGuidQryFromBeanList(ArrayList<CustomerBean> alRetailers, String columnName) {
        String mCPQry = "";
        for (CustomerBean retBean : alRetailers) {
            if (mCPQry.length() == 0)
                mCPQry += " " + columnName + " eq guid'" + retBean.getCPGUID().toUpperCase() + "'";
            else
                mCPQry += " or " + columnName + " eq guid'" + retBean.getCPGUID().toUpperCase() + "'";

        }
        return mCPQry;
    }


    public static HashMap<String, String> getCPGrp3Desc(ArrayList<CustomerBean> alRetList) {
        HashMap<String, String> CPGrp3Desc = new HashMap<>();
        if (alRetList != null && alRetList.size() > 0) {
            String cpGuidQry = makeCPGuidQryFromBeanList(alRetList, Constants.CPGUID);
            if (!cpGuidQry.equalsIgnoreCase("")) {
                CPGrp3Desc = OfflineManager.getCPGrp3DescListFromCPDMSDiv(Constants.CPDMSDivisions + "?$select=" + Constants.Group3Desc + "," + Constants.CPGUID + " &$filter=("
                        + cpGuidQry + " )");
            }
        }
        return CPGrp3Desc;
    }

    public static boolean ReIntilizeStore = false;
    public static boolean isDayStartSyncEnbled = false;
    public static int mErrorCount = 0;
    public static ArrayList<String> AL_ERROR_MSG = new ArrayList<>();

    public static String convertALBussinessMsgToString(ArrayList<String> arrayList) {
        String mErrorMsg = "";
        if (arrayList != null && arrayList.size() > 0) {
            for (String errMsg : arrayList) {
                if (mErrorMsg.length() == 0) {
                    mErrorMsg = mErrorMsg + errMsg;
                } else {
                    mErrorMsg = mErrorMsg + "\n" + errMsg;
                }
            }
        }
        return mErrorMsg;
    }

    public static void customAlertDialogWithScroll(final Context context, final String mErrTxt) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_scroll, null);

        String mStrErrorEntity = getErrorEntityName();

        TextView textview = (TextView) view.findViewById(R.id.tv_err_msg);

        textview.setText(context.getString(R.string.msg_error_occured_during_sync_except) + " " + mStrErrorEntity + " \n" + mErrTxt);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyTheme);
        alertDialog.setCancelable(false)
                .setPositiveButton(context.getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        copyMessageToClipBoard(context, mErrTxt);
                    }
                });
        alertDialog.setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    public static void displayErrorDialog(Context context, String error_msg) {
        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            UtilConstants.showAlert(error_msg, context);
        } else {
            Constants.customAlertDialogWithScroll(context, mErrorMsg);
        }
    }

    public static Set<String> Entity_Set = new HashSet<>();

    public static String getErrorEntityName() {
        String mEntityName = "";

        try {
            if (Constants.Entity_Set != null && Constants.Entity_Set.size() > 0) {

                if (Constants.Entity_Set != null && !Constants.Entity_Set.isEmpty()) {
                    Iterator itr = Constants.Entity_Set.iterator();
                    while (itr.hasNext()) {
                        if (mEntityName.length() == 0) {
                            mEntityName = mEntityName + itr.next().toString();
                        } else {
                            mEntityName = mEntityName + "," + itr.next().toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            mEntityName = "";
        }

        return mEntityName;
    }

    public static void copyMessageToClipBoard(Context context, String message) {
        ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Error Message", message);
        clipboard.setPrimaryClip(clip);
        UtilConstants.showAlert(context.getString(R.string.issue_copied_to_clipboard_send_to_chnnel_team), context);
    }

    public static void displayMsgINet(int errCode, Context context) {
        if (errCode == 4) {
            UtilConstants.showAlert(context.getString(R.string.auth_fail_plz_contact_admin, Constants.UnAuthorized_Error_Code + ""), context);
        } else if (errCode == 3) {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + ""), context);
        } else {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + ""), context);
        }
    }

    public static String makeMsgReqError(int errorCode, Context context, boolean isInvError) {
        String mStrErrorMsg = "";

        if (!isInvError) {
            if (errorCode == Constants.UnAuthorized_Error_Code || errorCode == Constants.UnAuthorized_Error_Code_Offline) {
                mStrErrorMsg = context.getString(R.string.auth_fail_plz_contact_admin, errorCode + "");
            } else if (errorCode == Constants.Unable_to_reach_server_offline || errorCode == Constants.Network_Error_Code_Offline) {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + "");
            } else if (errorCode == Constants.Resource_not_found) {
                mStrErrorMsg = context.getString(R.string.techincal_error_plz_contact, errorCode + "");
            } else if (errorCode == Constants.Unable_to_reach_server_failed_offline) {
                mStrErrorMsg = context.getString(R.string.comm_error_server_failed_plz_contact, errorCode + "");
            } else {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + "");
            }
        } else {
            if (errorCode == 4) {
                mStrErrorMsg = context.getString(R.string.auth_fail_plz_contact_admin, Constants.UnAuthorized_Error_Code + "");
            } else if (errorCode == 3) {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + "");
            } else {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + "");
            }
        }

        return mStrErrorMsg;
    }


    public static void displayMsgReqError(int errorCode, Context context) {
        if (errorCode == Constants.UnAuthorized_Error_Code || errorCode == Constants.UnAuthorized_Error_Code_Offline) {
            UtilConstants.showAlert(context.getString(R.string.auth_fail_plz_contact_admin, errorCode + ""), context);
        } else if (errorCode == Constants.Unable_to_reach_server_offline || errorCode == Constants.Network_Error_Code_Offline) {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + ""), context);
        } else if (errorCode == Constants.Resource_not_found) {
            UtilConstants.showAlert(context.getString(R.string.techincal_error_plz_contact, errorCode + ""), context);
        } else if (errorCode == Constants.Unable_to_reach_server_failed_offline) {
            UtilConstants.showAlert(context.getString(R.string.comm_error_server_failed_plz_contact, errorCode + ""), context);
        } else {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + ""), context);
        }
    }


    public static ErrorBean getErrorCode(int operation, Exception exception, Context context) {
        ErrorBean errorBean = new ErrorBean();
        try {
            int errorCode = 0;
            boolean hasNoError = true;
            if ((operation == Operation.Create.getValue())) {

                try {
                    // below error code getting from online manger (While posting data vault data)
//                    errorCode = ((ErrnoException) ((ODataNetworkException) exception).getCause().getCause()).errno;
                    Throwable throwables = (((ODataNetworkException) exception).getCause()).getCause().getCause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (throwables instanceof ErrnoException) {
                            errorCode = ((ErrnoException) throwables).errno;
                        } else {
                            if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                errorCode = Constants.UnAuthorized_Error_Code;
                                hasNoError = false;
                            } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                hasNoError = false;
                                errorCode = Constants.Comm_Error_Code;
                            } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                hasNoError = false;
                                errorCode = Constants.Network_Error_Code;
                            } else {
                                Constants.ErrorNo = 0;
                            }
                        }
                    } else {
                        try {
                            if (exception.getMessage() != null) {
                                if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                    errorCode = Constants.UnAuthorized_Error_Code;
                                    hasNoError = false;
                                } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Comm_Error_Code;
                                } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Network_Error_Code;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if (errorCode != Constants.UnAuthorized_Error_Code) {
                        if (errorCode == Constants.Network_Error_Code || errorCode == Constants.Comm_Error_Code) {
                            hasNoError = false;
                        } else {
                            hasNoError = true;
                        }
                    }
                } catch (Exception e1) {
                    if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                        errorCode = Constants.UnAuthorized_Error_Code;
                        hasNoError = false;
                    } else {
                        Constants.ErrorNo = 0;
                    }
                }
                LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());

            } else if (operation == Operation.OfflineFlush.getValue() || operation == Operation.OfflineRefresh.getValue()) {
                try {
                    // below error code getting from offline manger (While posting flush and refresh collection)
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }

                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Executing_SQL_Commnd_Error)) {
                            hasNoError = false;
                            errorCode = -10001;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                if (errorCode != 0) {
                    LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                // below error code getting from offline manger (While posting flush and refresh collection)
                try {
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }
                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Store_Defining_Req_Not_Matched)) {
                            hasNoError = false;
                            errorCode = -10247;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }

            errorBean.setErrorCode(errorCode);
            if (exception.getMessage() != null && !exception.getMessage().equalsIgnoreCase("")) {
                errorBean.setErrorMsg(exception.getMessage());
            } else {
                errorBean.setErrorMsg(context.getString(R.string.unknown_error));
            }

            errorBean.setHasNoError(hasNoError);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isStoreFaied = false;
        if (errorBean.getErrorCode() == Constants.Resource_not_found
                || errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode() == Constants.Store_Def_Not_matched_Code
            /*|| errorBean.getErrorMsg().contains(Database_Transction_Failed_Error_Code+"")*/) {
            isStoreFaied = OfflineManager.closeStore(context,
                    OfflineManager.options, errorBean.getErrorMsg() + "",
                    offlineStore, Constants.PREFS_NAME, errorBean.getErrorCode() + "");
//            OfflineManager.closeStoreMutSell(context,
//                    OfflineManager.optionsMustSell,errorBean.getErrorMsg()+"",
//                    OfflineManager.offlineStoreMustSell,Constants.PREFS_NAME,errorBean.getErrorCode()+"");
            Constants.ReIntilizeStore = isStoreFaied;

        }
        if (errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code1 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code2 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code3 + "")
                || errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode() == Constants.Store_Def_Not_matched_Code
            /*|| errorBean.getErrorMsg().contains(Database_Transction_Failed_Error_Code+"")*/) {
            if (errorBean.getErrorMsg().contains("500")
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100029)
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100027)) {
                errorBean.setStoreFailed(false);
            } else {
                offlineStore = null;
                OfflineManager.options = null;
//                OfflineManager.offlineStoreMustSell = null;
//                OfflineManager.optionsMustSell = null;
                errorBean.setStoreFailed(true);
            }

        } else {
            errorBean.setStoreFailed(false);
        }

        return errorBean;
    }

    public static ErrorBean getErrorCodeMustSell(int operation, Exception exception, Context context) {
        ErrorBean errorBean = new ErrorBean();
        try {
            int errorCode = 0;
            boolean hasNoError = true;
            if ((operation == Operation.Create.getValue())) {

                try {
                    // below error code getting from online manger (While posting data vault data)
//                    errorCode = ((ErrnoException) ((ODataNetworkException) exception).getCause().getCause()).errno;
                    Throwable throwables = (((ODataNetworkException) exception).getCause()).getCause().getCause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (throwables instanceof ErrnoException) {
                            errorCode = ((ErrnoException) throwables).errno;
                        } else {
                            if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                errorCode = Constants.UnAuthorized_Error_Code;
                                hasNoError = false;
                            } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                hasNoError = false;
                                errorCode = Constants.Comm_Error_Code;
                            } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                hasNoError = false;
                                errorCode = Constants.Network_Error_Code;
                            } else {
                                Constants.ErrorNo = 0;
                            }
                        }
                    } else {
                        try {
                            if (exception.getMessage() != null) {
                                if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                    errorCode = Constants.UnAuthorized_Error_Code;
                                    hasNoError = false;
                                } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Comm_Error_Code;
                                } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Network_Error_Code;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if (errorCode != Constants.UnAuthorized_Error_Code) {
                        if (errorCode == Constants.Network_Error_Code || errorCode == Constants.Comm_Error_Code) {
                            hasNoError = false;
                        } else {
                            hasNoError = true;
                        }
                    }
                } catch (Exception e1) {
                    if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                        errorCode = Constants.UnAuthorized_Error_Code;
                        hasNoError = false;
                    } else {
                        Constants.ErrorNo = 0;
                    }
                }
                LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());

            } else if (operation == Operation.OfflineFlush.getValue() || operation == Operation.OfflineRefresh.getValue()) {
                try {
                    // below error code getting from offline manger (While posting flush and refresh collection)
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }

                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Executing_SQL_Commnd_Error)) {
                            hasNoError = false;
                            errorCode = -10001;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                if (errorCode != 0) {
                    LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                // below error code getting from offline manger (While posting flush and refresh collection)
                try {
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }
                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Store_Defining_Req_Not_Matched)) {
                            hasNoError = false;
                            errorCode = -10247;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }

            errorBean.setErrorCode(errorCode);
            if (exception.getMessage() != null && !exception.getMessage().equalsIgnoreCase("")) {
                errorBean.setErrorMsg(exception.getMessage());
            } else {
                errorBean.setErrorMsg(context.getString(R.string.unknown_error));
            }

            errorBean.setHasNoError(hasNoError);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isStoreFaied = false;
        if (errorBean.getErrorCode() == Constants.Resource_not_found
                || errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode() == Constants.Store_Def_Not_matched_Code
            /* || errorBean.getErrorMsg().contains(Database_Transction_Failed_Error_Code+"")*/) {
//            isStoreFaied = OfflineManager.closeStore(context,
//                    OfflineManager.options,errorBean.getErrorMsg()+"",
//                    OfflineManager.offlineStore,Constants.PREFS_NAME,errorBean.getErrorCode()+"");
            OfflineManager.closeStoreMutSell(context,
                    OfflineManager.optionsMustSell, errorBean.getErrorMsg() + "",
                    OfflineManager.offlineStoreMustSell, Constants.PREFS_NAME, errorBean.getErrorCode() + "");
            Constants.ReIntilizeStore = isStoreFaied;

        }
        if (errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code1 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code2 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code3 + "")
                || errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode() == Constants.Store_Def_Not_matched_Code
            /*|| errorBean.getErrorMsg().contains(Database_Transction_Failed_Error_Code+"")*/) {
            if (errorBean.getErrorMsg().contains("500")
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100029)
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100027)) {
                errorBean.setStoreFailed(false);
            } else {
//                OfflineManager.offlineStore = null;
//                OfflineManager.options = null;
                OfflineManager.offlineStoreMustSell = null;
                OfflineManager.optionsMustSell = null;
                errorBean.setStoreFailed(true);
            }

        } else {
            errorBean.setStoreFailed(false);
        }

        return errorBean;
    }
/*
    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void alertMsgForMobileSecure(final Context context, final PackageManager packageManager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme);
        builder.setMessage(R.string.alert_msg_mobile_secure_not_installed)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent playStoreIntent = packageManager.getLaunchIntentForPackage("com.android.vending");
                                context.startActivity(playStoreIntent);
                                exitApp(context);

                            }
                        });

        builder.show();
    }*/

   /* public static void exitApp(Context context) {
        try {
            Intent LaunchIntent = new Intent(context, CloserActivity.class);
            LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(LaunchIntent);
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getUpdateAvailability(final Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Constants.isBackEndVersionName = OfflineManager.getBackendVersionName(ConfigTypsetTypeValues + "?$filter=" + Types + " eq '" + APPVER + "'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (!versionName.equalsIgnoreCase(Constants.isBackEndVersionName)) {
            return true;
        }
        return false;
    }*/

    /*public static void storeUpdateDetails(final Context context*//*, final boolean isNavigateBack*//*) {

        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        SharedPreferences.Editor editor = settings.edit();
        String versionName = "";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Constants.isBackEndVersionName = OfflineManager.getBackendVersionName(ConfigTypsetTypeValues + "?$filter=" + Types + " eq '" + APPVER + "'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String updateNotificationTime = "0";

        try {
            updateNotificationTime = OfflineManager.getBackendVersionName(ConfigTypsetTypeValues + "?$filter=" + Types + " eq '" + NOTFINTV + "'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String updateType = "0";

        try {
            updateType = OfflineManager.getBackendVersionName(ConfigTypsetTypeValues + "?$filter=" + Types + " eq '" + UPDTYP + "'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (!versionName.equalsIgnoreCase(Constants.isBackEndVersionName)) {
            if (settings.getBoolean(Constants.isManadtoryUpdate, false)) {
                updateType = "01";
                editor.putBoolean(Constants.isManadtoryUpdate, false);
            } else {
                String[] appVersionNumberArray = versionName.split("\\.");
                String[] backEndVersionNumberArray = Constants.isBackEndVersionName.split("\\.");
                int versionDiffCount = Integer.parseInt(backEndVersionNumberArray[backEndVersionNumberArray.length - 1]) -
                        Integer.parseInt(appVersionNumberArray[appVersionNumberArray.length - 1]);
                if (versionDiffCount > 1)
                    updateType = "01";
            }
        }
        editor.putString(Constants.AppUpdateType, updateType);
        editor.putString(Constants.AppUpdateVersion, Constants.isBackEndVersionName);
        editor.putString(Constants.NotfIntvTime, updateNotificationTime);
        editor.commit();
    }

    public static void displayUpdatePopup(final Context context) {
        try {
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                    0);
            String updateType = settings.getString(Constants.AppUpdateType, "01");
            final String updateNotificationTime = settings.getString(Constants.NotfIntvTime, "900000");
            if (updateType.equalsIgnoreCase("01")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme);
                builder.setMessage(R.string.alert_update_app_version)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @SuppressLint("NewApi")
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        updateApplicationNow(context);
                                    }
                                });

                builder.show();
            } else if (updateType.equalsIgnoreCase("02")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme);
                builder.setMessage(R.string.alert_update_app_version)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @SuppressLint("NewApi")
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        updateApplicationNow(context);
                                    }
                                }).setNegativeButton(R.string.lbl_later,
                        new DialogInterface.OnClickListener() {

                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                scheduleLater(updateNotificationTime);
                            }
                        });

                builder.show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateApplicationNow(Context context) {
        try {

            if (Constants.isPackageInstalled("com.Android.Afaria", context.getPackageManager())) {
                Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("com.Android.Afaria");
                LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Constants.exitApp(context);
                context.startActivity(LaunchIntent);
            } else {
                final String appPackageName = "com.Android.Afaria"; // getPackageName() from Context or Activity object
                try {
                    Intent intentNavPrevScreen = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                    intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.exitApp(context);
                    context.startActivity(intentNavPrevScreen);
                } catch (android.content.ActivityNotFoundException anfe) {
                    Intent intentNavPrevScreen = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                    intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.exitApp(context);
                    context.startActivity(intentNavPrevScreen);
                }
//            Constants.exitApp(context);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }*/
//    public static final String APPVER = "APPVER";
//    public static final String NOTFINTV = "NOTFINTV";
//    public static final String UPDTYP = "UPDTYP";
    public static String AppUpdateVersion = "AppUpdateVersion";
    public static String AppUpdateType = "AppUpdateType";
    public static String NotfIntvTime = "NotfIntvTime";

    public static boolean isCancelUpdateDialog = false;
    public static String isBackEndVersionName = "";
    public static final String isManadtoryUpdate = "isManadtoryUpdate";
    public static String ERROR_MSG = "";

   /* public static void scheduleLater(String updateNotificationTime) {//time in minutes
        long intervalValInMiliSeconds = Integer.parseInt(updateNotificationTime)*60*1000;// minutes * 60 *1000

        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 20s
                    // Write your code to display AlertDialog here
                    Intent dialogIntent = new Intent(MSFAApplication.appContext, UpdateDialogActivity.class);
                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MSFAApplication.appContext.startActivity(dialogIntent);

                }
            }, intervalValInMiliSeconds);
            //            }, 15000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }*/


    public static boolean isDateToday(String sleDate) {

        boolean mBoolDBSynced = false;
        if (sleDate != null && !sleDate.equalsIgnoreCase("")) {

            Date date = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = null;
                try {
                    date = sdf.parse(sleDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (date == null) {
                date = new Date();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int mYear = cal.get(Calendar.YEAR);
            int mMonth = cal.get(Calendar.MONTH);
            int mDay = cal.get(Calendar.DAY_OF_MONTH);

            Calendar calCurrent = Calendar.getInstance();

            int mYearCurrent = calCurrent.get(Calendar.YEAR);
            int mMonthCurrent = calCurrent.get(Calendar.MONTH);
            int mDayCurrent = calCurrent.get(Calendar.DAY_OF_MONTH);

            if (mYear == mYearCurrent && mMonth == mMonthCurrent && mDay == mDayCurrent) {
                mBoolDBSynced = true;
            } else {
                mBoolDBSynced = false;
            }

        } else {
            mBoolDBSynced = false;
        }
        return mBoolDBSynced;
    }

    public static Boolean isTimePast(String startTime, boolean isTimeDur) {

        Calendar calendar = Calendar.getInstance();
        if (isTimeDur) {
            calendar.add(Calendar.MINUTE, 20);
        }
        int selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        int selectedMinute = calendar.get(Calendar.MINUTE);

        String time1 = selectedHour + "-" + selectedMinute + "-00";

        String time2 = startTime + "-00";

        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = 0;
        if (date2 != null && date1 != null) {
            difference = date2.getTime() - date1.getTime();
        }

        if (difference < 0)
            return false;
        else
            return true;
    }

    public static Boolean checkTimeDuration(String startTime, String endTime) {


        String time1 = startTime + "-00";
        String time2 = endTime + "-00";

        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = 0;
        if (date2 != null && date1 != null) {
            difference = date2.getTime() - date1.getTime();
        }

        int min = (int) (difference) / (1000 * 60);

        if (min < 20)
            return false;
        else
            return true;
    }

    public static boolean isEndateAndEndTimeValid(String mStrStartDate, String mStrStartTime) {

        boolean isValidEndDateAndTime = false;
        try {
            String mStrTime = UtilConstants.convertTimeOnly(mStrStartTime);
            String mStrCurrentTime = UtilConstants.convertTimeOnly(getOdataDuration().toString());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date startDate = null;
            Date endDate = null;

            try {
                startDate = format.parse(mStrStartDate);
                endDate = format.parse(UtilConstants.getNewDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date startTimeDate = null;
            Date endTimeDate = null;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            try {
                startTimeDate = simpleDateFormat.parse(mStrTime);
                endTimeDate = simpleDateFormat.parse(mStrCurrentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            isValidEndDateAndTime = false;

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            int mYear = cal.get(Calendar.YEAR);
            int mMonth = cal.get(Calendar.MONTH);
            int mDay = cal.get(Calendar.DAY_OF_MONTH);

            Calendar calCurrent = Calendar.getInstance();
            calCurrent.setTime(endDate);
            int mYearCurrent = calCurrent.get(Calendar.YEAR);
            int mMonthCurrent = calCurrent.get(Calendar.MONTH);
            int mDayCurrent = calCurrent.get(Calendar.DAY_OF_MONTH);

            if (mYear == mYearCurrent && mMonth == mMonthCurrent && mDay == mDayCurrent) {
                if (endTimeDate.before(startTimeDate)) {
                    isValidEndDateAndTime = false;
                } else {
                    isValidEndDateAndTime = true;
                }
            } else {
                if (startDate.compareTo(endDate) <= 0) {
                    isValidEndDateAndTime = true;
                } else {
                    isValidEndDateAndTime = false;
                }

            }

        } catch (Exception e) {
            isValidEndDateAndTime = false;
        }
        return isValidEndDateAndTime;
    }

    public static boolean isEndateValid(String mStrStartDate, String mStrStartTime) {

        boolean isValidEndDateAndTime = false;
        try {
            String mStrTime = UtilConstants.convertTimeOnly(mStrStartTime);
            String mStrCurrentTime = UtilConstants.convertTimeOnly(getOdataDuration().toString());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date startDate = null;
            Date endDate = null;

            try {
                startDate = format.parse(mStrStartDate);
                endDate = format.parse(UtilConstants.getNewDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date startTimeDate = null;
            Date endTimeDate = null;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            try {
                startTimeDate = simpleDateFormat.parse(mStrTime);
                endTimeDate = simpleDateFormat.parse(mStrCurrentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            isValidEndDateAndTime = false;

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            int mYear = cal.get(Calendar.YEAR);
            int mMonth = cal.get(Calendar.MONTH);
            int mDay = cal.get(Calendar.DAY_OF_MONTH);

            Calendar calCurrent = Calendar.getInstance();
            calCurrent.setTime(endDate);
            int mYearCurrent = calCurrent.get(Calendar.YEAR);
            int mMonthCurrent = calCurrent.get(Calendar.MONTH);
            int mDayCurrent = calCurrent.get(Calendar.DAY_OF_MONTH);

            if (startDate.compareTo(endDate) <= 0) {
                isValidEndDateAndTime = true;
            } else {
                isValidEndDateAndTime = false;
            }


        } catch (Exception e) {
            isValidEndDateAndTime = false;
        }
        return isValidEndDateAndTime;
    }

    /*permission Request*/
    public static final int CAMERA_PERMISSION_CONSTANT = 890;

    public static void setPermissionStatus(Context mContext, String key, boolean value) {
        SharedPreferences permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        SharedPreferences.Editor editor = permissionStatus.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPermissionStatus(Context mContext, String key) {
        SharedPreferences permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        return permissionStatus.getBoolean(key, false);
    }

    public static boolean checkPermission(Context context) {

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int resultCore = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED && resultCore == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;

            }
        } else {
            return true;
        }

    }

    public static final int PERMISSION_REQUEST_CODE = 110;

    public static void requestPermission(Activity activity, Context context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            try {
                Toast.makeText(context, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    public static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    public static final int REQUEST_LOCATION = 10;


    public static void navigateToAppSettingsScreen(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static boolean restartApp(Activity activity) {
        LogonCoreContext lgCtx1 = null;
        try {
            lgCtx1 = LogonCore.getInstance().getLogonContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lgCtx1 == null) {

            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAppRestart", true);
            editor.commit();
            Log.e("Restart", "Called");
            activity.finishAffinity();
            Intent dialogIntent = new Intent(activity, RegistrationActivity.class);
            activity.startActivity(dialogIntent);
        } else {
            return false;

        }
        return true;
    }

    public static String MaximumAttemptKey = "MaximumAttemptKey";
    public static HashMap<String, SchemeBean> HashMapSchemeValuesBySchemeGuid = new HashMap<>();
    public static HashMap<String, String> HashMapSchemeIsInstantOrQPS = new HashMap<>();
    public static HashMap<String, ArrayList<String>> HashMapSchemeListByMaterial = new HashMap<>();
    public static String SchemeQRY = "";
    public static String CPGUIDVAL = "";

    public static InputFilter getNumberAlphabetOnly() {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[a-zA-Z0-9 ]*")) //put your constraints here
                {
                    return source;
                }
                return "";
            }
        };
        return filter;
    }

    public static void deleteUDBFileFromDevice() {
        File data = Environment.getDataDirectory();
        String currentDBPath = Constants.offlineDBPath;
        String currentrqDBPath = Constants.offlineReqDBPath;
        File currentDB = new File(data, currentDBPath);
        File currentrqDB = new File(data, currentrqDBPath);
        if (currentDB.exists()) {
            currentDB.delete();
        }
        if (currentrqDB.exists()) {
            currentrqDB.delete();
        }
    }

    public static boolean getPendingList(Context mContext, ArrayList<String> mArrEntityTypes, String prefName) {
        int size = 0;
        boolean isPendingListAval = false;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(prefName, 0);
        Set<String> set = new HashSet<>();
        if (mArrEntityTypes.size() > 0) {
            for (String entityType : mArrEntityTypes) {
                set = sharedPreferences.getStringSet(entityType, null);
                if (set != null && !set.isEmpty()) {
                    size = size + set.size();
                    if (size > 0) {
                        return true;
                    }
                }
            }
        }
        return isPendingListAval;
    }
//    public static void closeStore(Context mContext, ODataOfflineStoreOptions options,ErrorBean errorBean){
//
//        try {
//            if(errorBean.getErrorMsg().contains(""+Constants.Build_Database_Failed_Error_Code1)
//                    || errorBean.getErrorMsg().contains(""+Constants.Build_Database_Failed_Error_Code2)
//                    || errorBean.getErrorMsg().contains(""+Constants.Build_Database_Failed_Error_Code3)) {
//                Constants.ReIntilizeStore =true;
//                OfflineManager.closeOfflineStore(mContext, options);
//            }
//        } catch (OfflineODataStoreException e) {
//            LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
//        }
//    }


    public static void setSyncTime(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        if (settings.getBoolean(Constants.isReIntilizeDB, false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.isReIntilizeDB, false);
            editor.commit();
            try {
                String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                String[] DEFINGREQARRAY = Constants.getDefinigReq(context);


                for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                    String colName = DEFINGREQARRAY[incReq];
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }

                    if (colName.contains("(")) {
                        String splitCollName[] = colName.split("\\(");
                        colName = splitCollName[0];
                    }

                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
            }
        }
    }

    public static String DSTSTKVIEW = "DSTSTKVIEW";
    public static String DBSTKTYPE = "DBSTKType";

    /*Creates table for Sync history in SQLite DB*/
    public static void createSyncDatabase(Context context) {
        Hashtable hashtable = new Hashtable<>();
        hashtable.put(Constants.SyncGroup, "");
        hashtable.put(Constants.Collections, "");
        hashtable.put(Constants.TimeStamp, "");
        try {
            Constants.events.crateTableConfig(Constants.SYNC_TABLE, hashtable);
            getSyncHistoryTable(context);
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_creating_sync_db
                    + e.getMessage());
        }
    }

    /*Sync History table for Sync*/
    public static void getSyncHistoryTable(Context context) {
        String[] definingReqArray = Constants.getDefinigReq(context);
        for (int i = 0; i < definingReqArray.length; i++) {
            String colName = definingReqArray[i];
            if (colName.contains("?$")) {
                String splitCollName[] = colName.split("\\?");
                colName = splitCollName[0];
            }

            if (colName.contains("(")) {
                String splitCollName[] = colName.split("\\(");
                colName = splitCollName[0];
            }

            try {
                Constants.events.inserthistortTable(Constants.SYNC_TABLE, "",
                        Constants.Collections, colName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void getLocation(Activity mActivity, final LocationInterface locationInterface) {
        UtilConstants.latitude = 0.0;
        UtilConstants.longitude = 0.0;
        LocationUtils.getCustomLocation(mActivity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                if (status) {
                    android.location.Location location = locationModel.getLocation();
                    UtilConstants.latitude = location.getLatitude();
                    UtilConstants.longitude = location.getLongitude();
                    Log.d("LocationUtils", "location: " + locationModel.getLocationFrom());
                }
                if (locationInterface != null) {
                    locationInterface.location(status, locationModel, errorMsg, errorCode);
                }
            }
        });
    }

    public static String SKUGRP = "SKUGRP";
    public static String SKUGROUP = "SKU GROUP";
    public static String CRSSKUGROUP = "CRS SKU GROUP";


    public static String getTypesetValueForSkugrp(Context ctx) {
        String typeValues = "";
        try {
            typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.MSEC + "' and " + Constants.Types + " eq '" + Constants.SKUGRP + "'", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (typeValues.equalsIgnoreCase("X")) {
            return ctx.getString(R.string.lbl_sku_group);
        } else {
            return ctx.getString(R.string.lbl_crs_sku_group);
        }
    }

    public static Hashtable getCPHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            //noinspection unchecked
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            //noinspection unchecked
            dbHeadTable.put(Constants.Address1, fetchJsonHeaderObject.getString(Constants.Address1));
            //noinspection unchecked
            dbHeadTable.put(Constants.Country, fetchJsonHeaderObject.getString(Constants.Country));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictDesc, fetchJsonHeaderObject.getString(Constants.DistrictDesc));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictID, fetchJsonHeaderObject.getString(Constants.DistrictID));
            dbHeadTable.put(Constants.StateID, fetchJsonHeaderObject.getString(Constants.StateID));
            dbHeadTable.put(Constants.StateDesc, fetchJsonHeaderObject.getString(Constants.StateDesc));
            dbHeadTable.put(Constants.CityID, fetchJsonHeaderObject.getString(Constants.CityID));
            dbHeadTable.put(Constants.CityDesc, fetchJsonHeaderObject.getString(Constants.CityDesc));
            dbHeadTable.put(Constants.Landmark, fetchJsonHeaderObject.getString(Constants.Landmark));
            dbHeadTable.put(Constants.PostalCode, fetchJsonHeaderObject.getString(Constants.PostalCode));
            dbHeadTable.put(Constants.MobileNo, fetchJsonHeaderObject.getString(Constants.MobileNo));
            dbHeadTable.put(Constants.EmailID, fetchJsonHeaderObject.getString(Constants.EmailID));
            dbHeadTable.put(Constants.PAN, fetchJsonHeaderObject.getString(Constants.PAN));
            dbHeadTable.put(Constants.VATNo, fetchJsonHeaderObject.getString(Constants.VATNo));
            dbHeadTable.put(Constants.OutletName, fetchJsonHeaderObject.getString(Constants.OutletName));
            dbHeadTable.put(Constants.OwnerName, fetchJsonHeaderObject.getString(Constants.OwnerName));
            dbHeadTable.put(Constants.RetailerProfile, fetchJsonHeaderObject.getString(Constants.RetailerProfile));
            dbHeadTable.put(Constants.DOB, fetchJsonHeaderObject.getString(Constants.DOB));
            dbHeadTable.put(Constants.Latitude, fetchJsonHeaderObject.getString(Constants.Latitude));
            dbHeadTable.put(Constants.Longitude, fetchJsonHeaderObject.getString(Constants.Longitude));
            dbHeadTable.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            dbHeadTable.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));
            dbHeadTable.put(Constants.Group2, fetchJsonHeaderObject.getString(Constants.Group2));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.Anniversary, fetchJsonHeaderObject.getString(Constants.Anniversary));
            dbHeadTable.put(Constants.WeeklyOff, fetchJsonHeaderObject.getString(Constants.WeeklyOff));
            dbHeadTable.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
            dbHeadTable.put(Constants.CPUID, fetchJsonHeaderObject.getString(Constants.CPUID));
            dbHeadTable.put(Constants.TaxRegStatus, fetchJsonHeaderObject.getString(Constants.TaxRegStatus));
            dbHeadTable.put(Constants.Group4, fetchJsonHeaderObject.getString(Constants.Group4));

            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static String getDistNameFromCPDMSDIV(String mStrCPGUID, String mStrSPGUID) {
        String spGuid = "";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String selParentName = "";
        try {
            selParentName = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.ParentName + " &$filter="
                    + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' ", Constants.ParentName);

        } catch (OfflineODataStoreException e) {
            selParentName = "";
            e.printStackTrace();
        }
        return selParentName;
    }

    public static void printLog(String message) {
        Log.d("OnlineStore", "error : " + message);
        LogManager.writeLogError("error : " + message);
    }

    public static void printLogInfo(String message) {
        Log.d("OnlineStore", "info : " + message);
        LogManager.writeLogInfo("info : " + message);
    }

    public static String getNameSpaceOnline(OnlineODataStore oDataOfflineStore) {
        String mStrNameSpace = "";
        ODataMetadata oDataMetadata = null;

        try {
            oDataMetadata = oDataOfflineStore.getMetadata();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        Set set = oDataMetadata.getMetaNamespaces();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();

            while (itr.hasNext()) {
                String tempNameSpace = itr.next().toString();
                if (!tempNameSpace.equalsIgnoreCase("OfflineOData")) {
                    mStrNameSpace = tempNameSpace;
                }
            }
        }

        return mStrNameSpace;
    }


    public static boolean isFileExits(String fileName) {
        boolean isFileExits = false;
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            // Get The Text file
            File txtFile = new File(sdCardDir, fileName);
            // Read the file Contents in a StringBuilder Object
            if (txtFile.exists()) {
                isFileExits = true;
            } else {
                isFileExits = false;
            }
        } catch (Exception e) {
            isFileExits = false;
            e.printStackTrace();
            LogManager.writeLogError("isFileExits() : " + e.getMessage());
        }
        return isFileExits;
    }

    public static String getTextFileData(String fileName) {
        // Get the dir of SD Card
        File sdCardDir = Environment.getExternalStorageDirectory();
        // Get The Text file
        File txtFile = new File(sdCardDir, fileName);
        // Read the file Contents in a StringBuilder Object
        StringBuilder text = new StringBuilder();
        if (txtFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(txtFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                }
                reader.close();
            } catch (IOException e) {
                Log.e("C2c", "Error occured while reading text file!!");
                LogManager.writeLogError("getTextFileData() : (IOException)" + e.getMessage());
            }
        } else {
            text.append("");
            Constants.ImportDataVaultflag = false;
        }
        return text.toString();
    }

    public static ArrayList<Object> getPendingDataVaultData(Context mContext) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.CollList, null);
        invKeyValues = new String[getPendingListSize(mContext)][2];
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CollList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SOList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.ROList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.FeedbackList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.FeedbackList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SampleDisbursement;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CPList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.InvList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.InvList;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
        }

        return objectsArrayList;

    }

    private static int getPendingListSize(Context mContext) {
        int size = 0;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        Set<String> set = new HashSet<>();

        set = sharedPreferences.getStringSet(Constants.InvList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.CollList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.SOList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.FeedbackList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        return size;
    }

    public static String makePendingDataToJsonString(Context context) {
        String mStrJson = "";
        ArrayList<Object> objectArrayList = getPendingDataVaultData(context);
        if (!objectArrayList.isEmpty()) {
            String[][] invKeyValues = (String[][]) objectArrayList.get(1);
            JSONArray jsonArray = new JSONArray();
            for (int k = 0; k < invKeyValues.length; k++) {
                JSONObject jsonObject = new JSONObject();
                String store = "";
                try {
                    store = LogonCore.getInstance().getObjectFromStore(invKeyValues[k][0].toString());
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }
                try {
                    // Add the values to the jsonObject
                    jsonObject.put(Constants.KeyNo, invKeyValues[k][0]);
                    jsonObject.put(Constants.KeyType, invKeyValues[k][1]);
                    jsonObject.put(Constants.KeyValue, store);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(DataVaultData, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mStrJson = jsonObj.toString();
        }
        return mStrJson;
    }

    public static void setJsonStringDataToDataVault(String mJsonString, Context context) {
        try {
            JSONObject jsonObj = new JSONObject(mJsonString);
            // Getting data JSON Array nodes
            JSONArray jsonArray = jsonObj.getJSONArray(DataVaultData);
            for (int incVal = 0; incVal < jsonArray.length(); incVal++) {
                JSONObject jsonObject = jsonArray.getJSONObject(incVal);
                String mStrKeyNo = jsonObject.getString(KeyNo);
                String mStrKeyKeyType = jsonObject.getString(KeyType);
                String mStrKeyValue = jsonObject.getString(KeyValue);
                Constants.saveDeviceDocNoToSharedPref(context, mStrKeyKeyType, mStrKeyNo);
                UtilDataVault.storeInDataVault(mStrKeyNo, mStrKeyValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String KeyNo = "KeyNo";
    public static String KeyValue = "KeyValue";
    public static String KeyType = "KeyType";
    public static String DataVaultData = "DataVaultData";
    public static String ExportDataFailedErrorMsg = "";
    public static String ImportDataFailedErrorMsg = "";
    public static String DataVaultFileName = "mSecSalesDataVault.txt";

    public static void deleteFileFromDeviceStorage(String mStrFileName) {
        String filePath = Environment.getExternalStorageDirectory() + "/" + mStrFileName;
        try {
            File f = new File(filePath);
            Boolean deleted = f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError("deleteFileFromDeviceStorage() : " + e.getMessage());
        }
    }

    public static boolean isReadWritePermissionEnabled(final Context mContext, Activity mActivity) {
        boolean isPermissionGranted = false;

        if (Build.VERSION_CODES.M <= android.os.Build.VERSION.SDK_INT) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CAMERA_PERMISSION_CONSTANT);
                } else if (Constants.getPermissionStatus(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) || Constants.getPermissionStatus(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Constants.dialogBoxWithButton(mContext, "",
                            mContext.getString(R.string.this_app_needs_storage_permission), mContext.getString(R.string.enable),
                            mContext.getString(R.string.later), new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean clickedStatus) {
                                    if (clickedStatus) {
                                        Constants.navigateToAppSettingsScreen(mContext);
                                    }
                                }
                            });

                } else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.CAMERA_PERMISSION_CONSTANT);
                }
                Constants.setPermissionStatus(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            } else {
                //You already have the permission, just go ahead.
                isPermissionGranted = true;
            }
        } else {
            isPermissionGranted = true;
        }

        return isPermissionGranted;
    }


    public static void removePendingList(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (sharedPreferences.contains(Constants.InvList)) {
                editor.remove(Constants.InvList);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.CollList)) {
                editor.remove(Constants.CollList);
                editor.commit();
            }

            if (sharedPreferences.contains(Constants.SOList)) {
                editor.remove(Constants.SOList);
                editor.commit();
            }
            if (sharedPreferences.contains(Constants.ROList)) {
                editor.remove(Constants.ROList);
                editor.commit();
            }

            if (sharedPreferences.contains(Constants.FeedbackList)) {
                editor.remove(Constants.FeedbackList);
                editor.commit();
            }

            if (sharedPreferences.contains(Constants.SampleDisbursement)) {
                editor.remove(Constants.SampleDisbursement);
                editor.commit();
            }

            if (sharedPreferences.contains(Constants.Expenses)) {
                editor.remove(Constants.Expenses);
                editor.commit();
            }

            if (sharedPreferences.contains(Constants.CPList)) {
                editor.remove(Constants.CPList);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //    public static HashMap<String,String> sortByValues(HashMap<String,String> map) {
//        List list = new LinkedList(map.entrySet());
//        // Defined Custom Comparator here
//        java.util.Collections.sort(list, new Comparator() {
//            public int compare(Object o1, Object o2) {
//                return ((Comparable) ((Map.Entry) (o1)).getValue())
//                        .compareTo(((Map.Entry) (o2)).getValue());
//            }
//        });
//
//        // Here I am copying the sorted list in HashMap
//        // using LinkedHashMap to preserve the insertion order
//        HashMap sortedHashMap = new LinkedHashMap();
//        for (Iterator it = list.iterator(); it.hasNext();) {
//            Map.Entry entry = (Map.Entry) it.next();
//            sortedHashMap.put(entry.getKey(), entry.getValue());
//        }
//        return sortedHashMap;
//    }
    public static SimpleDateFormat getDateFormat(String customFormat) {
        return new SimpleDateFormat(customFormat);
    }

    public static String formatDate(String format, String ourDate, String ourFormat) {
        SimpleDateFormat currentFormat = null;
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            date = simpleDateFormat.parse(ourDate);
            currentFormat = new SimpleDateFormat(ourFormat);
            currentFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentFormat.format(date);
    }

    public static Calendar convertDateTimeFormat(String dateVal) {
        Date date = null;
        Calendar curCal = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            date = format.parse(dateVal);
            curCal.setTime(date);
            System.out.println("Date" + curCal.getTime());
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return curCal;
    }

    public static String getDiffMinutes(Calendar calOne, Calendar calTwo) {
        String mStrDiffMin = "0";
        try {
            long calOneMill = calOne.getTimeInMillis();
            long calTwoMill = calTwo.getTimeInMillis();
            // Calculate difference in milliseconds
            long diff = calTwoMill - calOneMill;

            long diffMinutes = diff / (60 * 1000);
            mStrDiffMin = diffMinutes + "";
        } catch (Exception e) {
            mStrDiffMin = "0";
            e.printStackTrace();
        }
        return mStrDiffMin;
    }

    public static String MSLInd = "MSLInd";
    public static String FocussedInd = "FocussedInd";
    public static String CrossSell = "CrossSell";
    public static String UPSell = "UPSell";
    public static String SOQ = "SOQ";
    public static String SellIndicator = "SellIndicator";
    public static String DR = "DR";
    public static String CS = "CS";
    public static String US = "US";

    public static String addZerosAfterDecimal(String mStrDecNo, int maxLength) {
        String mStrFinalString = "";
        try {
            if (mStrDecNo != null && !mStrDecNo.equalsIgnoreCase("")) {
                String[] mArrayStr = mStrDecNo.split("\\.");
                StringBuilder sb = new StringBuilder(mArrayStr[1]);
                sb.setLength(maxLength);
                String tempStr = sb.toString().replaceAll("[^0-9]", "0");
                mStrFinalString = mArrayStr[0] + "." + tempStr;
            } else {
                mStrFinalString = "0.0000000000000";
            }
        } catch (Exception e) {
            mStrFinalString = "0.0000000000000";
            e.printStackTrace();
        }
        return mStrFinalString;
    }

    /*bundle*/
    public static final String BUNDLE_RESOURCE_PATH = "resourcePath";
    public static final String BUNDLE_OPERATION = "operationBundle";
    public static final String BUNDLE_REQUEST_CODE = "requestCodeBundle";
    public static final String BUNDLE_SESSION_ID = "sessionIdBundle";
    public static final String BUNDLE_SESSION_REQUIRED = "isSessionRequired";
    public static final String BUNDLE_SESSION_TYPE = "sessionTypeBundle";
    public static final String STORE_DATA_INTO_TECHNICAL_CACHE = "storeDataIntoTechnicalCache";

    public static void onlineRequest(final Context mContext, String query, boolean isSessionRequired, int requestId, int sessionType,
                                     final OnlineODataInterface onlineODataInterface, boolean storeDataToTechnicalCache, boolean readFromTechnicalCache) {
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, query);
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, isSessionRequired);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, requestId);
        bundle.putInt(Constants.BUNDLE_SESSION_TYPE, sessionType);
        bundle.putBoolean(STORE_DATA_INTO_TECHNICAL_CACHE, storeDataToTechnicalCache);
        bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, readFromTechnicalCache);
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestQuery(onlineODataInterface, bundle, mContext);
            }
        }).start();
    }

    /*async method*/
    public static void requestQuery(final OnlineODataInterface onlineODataInterface, final Bundle bundle, final Context mContext) {
        String resourcePath = "";
        String sessionId = "";
        boolean isSessionRequired = false;
        int sessionType = 0;
        try {
            if (bundle == null) {
//            throw new IllegalArgumentException("bundle is null");
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "bundle is null", bundle);
            } else {
                resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
                sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
                isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
                sessionType = bundle.getInt(Constants.BUNDLE_SESSION_TYPE, 0);
            }
            if (TextUtils.isEmpty(resourcePath)) {
//            throw new IllegalArgumentException("resource path is null");
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "resource path is null", bundle);
            } else {
                final Map<String, String> createHeaders = new HashMap<String, String>();
//            createHeaders.put(Constants.arteria_dayfilter, Constants.NO_OF_DAYS);
                requestScheduled(resourcePath, createHeaders, onlineODataInterface, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onlineODataInterface != null)
                onlineODataInterface.responseFailed(null, e.getMessage(), bundle);
        }
    }

    private static void requestScheduled(String resourcePath, Map<String, String> createHeaders, OnlineODataInterface onlineODataInterface, Bundle bundle) throws ODataException {
//        OnlineStoreCacheListner openListener = OnlineStoreCacheListner.getInstance();
//        OnlineODataStore store = openListener.getStore();
//        LogManager.writeLogInfo(Constants.ERROR_ARCHIVE_ENTRY_REQUEST_URL + " : " + resourcePath);
        if (OnlineStoreCacheListner.store != null) {
            boolean storeDataToTechCache = bundle.getBoolean(ConstantsUtils.STORE_DATA_INTO_TECHNICAL_CACHE, false);
            if (storeDataToTechCache) {
                if (!OnlineStoreCacheListner.store.isOpenCache()) {
                    OnlineStoreCacheListner.store.reopenCache(Constants.EncryptKey);
                }
            }//else {
            // createHeaders.put("useCache","false");
            //}
            boolean isTechnicalCacheEnable = bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
            if (OnlineStoreCacheListner.store.isOpenCache()) {
                OnlineStoreCacheListner.store.setPassive(isTechnicalCacheEnable);
            }
            OnlineRequestListeners getOnlineRequestListener = new OnlineRequestListeners(onlineODataInterface, bundle);
            scheduledReqEntity(resourcePath, getOnlineRequestListener, createHeaders, OnlineStoreCacheListner.store);

        } else {
            throw new IllegalArgumentException("Store not opened");
        }
    }

    public static void onlineRequestTest(final Context mContext, String query, boolean isSessionRequired, int requestId, int sessionType,
                                         final OnlineODataInterface onlineODataInterface, boolean storeDataToTechnicalCache, boolean readFromTechnicalCache) {
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_RESOURCE_PATH, query);
        bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, isSessionRequired);
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, requestId);
        bundle.putInt(Constants.BUNDLE_SESSION_TYPE, sessionType);
        bundle.putBoolean(STORE_DATA_INTO_TECHNICAL_CACHE, storeDataToTechnicalCache);
        bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, readFromTechnicalCache);
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestQueryTest(onlineODataInterface, bundle, mContext);
            }
        }).start();
    }

    /*async method*/
    public static void requestQueryTest(final OnlineODataInterface onlineODataInterface, final Bundle bundle, final Context mContext) {
        String resourcePath = "";
        String sessionId = "";
        boolean isSessionRequired = false;
        int sessionType = 0;
        try {
            if (bundle == null) {
//            throw new IllegalArgumentException("bundle is null");
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "bundle is null", bundle);
            } else {
                resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
                sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
                isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
                sessionType = bundle.getInt(Constants.BUNDLE_SESSION_TYPE, 0);
            }
            if (TextUtils.isEmpty(resourcePath)) {
//            throw new IllegalArgumentException("resource path is null");
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "resource path is null", bundle);
            } else {
                final Map<String, String> createHeaders = new HashMap<String, String>();
//            createHeaders.put(Constants.arteria_dayfilter, Constants.NO_OF_DAYS);
                requestScheduledTest(resourcePath, createHeaders, onlineODataInterface, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onlineODataInterface != null)
                onlineODataInterface.responseFailed(null, e.getMessage(), bundle);
        }
    }

    private static void requestScheduledTest(String resourcePath, Map<String, String> createHeaders, OnlineODataInterface onlineODataInterface, Bundle bundle) throws ODataException {
//        OnlineStoreCacheListner openListener = OnlineStoreCacheListner.getInstance();
//        OnlineODataStore store = openListener.getStore();
//        LogManager.writeLogInfo(Constants.ERROR_ARCHIVE_ENTRY_REQUEST_URL + " : " + resourcePath);
        if (OnlineStoreCacheListner.store != null) {
            boolean storeDataToTechCache = bundle.getBoolean(ConstantsUtils.STORE_DATA_INTO_TECHNICAL_CACHE, false);
//            if (storeDataToTechCache) {
//            if (!OnlineStoreCacheListner.store .isOpenCache()) {
//                OnlineStoreCacheListner.store .reopenCache(Constants.EncryptKey);
//            }
//            }//else {
            // createHeaders.put("useCache","false");
            //}
            boolean isTechnicalCacheEnable = bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
//            if (OnlineStoreCacheListner.store .isOpenCache()) {
            OnlineStoreCacheListner.store.setPassive(false);
//            }
            OnlineRequestListeners getOnlineRequestListener = new OnlineRequestListeners(onlineODataInterface, bundle);
            scheduledReqEntity(resourcePath, getOnlineRequestListener, createHeaders, OnlineStoreCacheListner.store);

        } else {
            throw new IllegalArgumentException("Store not opened");
        }
    }

    private static ODataRequestExecution scheduledReqEntity(String resourcePath, ODataRequestListener listener, Map<String, String> options, OnlineODataStore store) throws ODataContractViolationException {
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resourcePath is null");
        } else if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        } else {
            ODataRequestParamSingleDefaultImpl requestParam = new ODataRequestParamSingleDefaultImpl();
            requestParam.setMode(ODataRequestParamSingle.Mode.Read);
            requestParam.setResourcePath(resourcePath);
            requestParam.setOptions(options);
            requestParam.getCustomHeaders().putAll(options);

            return store.scheduleRequest(requestParam, listener);
        }
    }

    public static void getMustSellData(final String mSyncType, final Context mcontext) {


        Log.d("AfterCallMustSell Req", UtilConstants.getSyncHistoryddmmyyyyTime());

    }

    public static boolean checkPresentFutureDate(String dateForStore) {
        if (dateForStore != null && !dateForStore.equalsIgnoreCase("")) {
            try {
                try {
                    Date entered = (new SimpleDateFormat("yyyy-MM-dd")).parse(dateForStore);
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    Date today = c.getTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(entered);
                    Date dateSpecified = cal.getTime();
                    if (!dateSpecified.before(today)) {
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }

    }

    public static String appendPrecedingZeros(String mStrInputNo, int stringLength) {
        String mfinalString = "";
        try {
            if (mStrInputNo != null && !mStrInputNo.equalsIgnoreCase("")) {
                try {
                    int numberOfDigits = mStrInputNo.length();
                    int numberOfLeadingZeroes = stringLength - numberOfDigits;
                    StringBuilder sb = new StringBuilder();
                    if (numberOfLeadingZeroes > 0) {
                        for (int i = 0; i < numberOfLeadingZeroes; i++) {
                            sb.append("0");
                        }
                    }
                    sb.append(mStrInputNo);
                    mfinalString = sb.toString();
                } catch (Exception e) {
                    mfinalString = mStrInputNo;
                    e.printStackTrace();
                }
            } else {
                mfinalString = "";
            }
        } catch (Exception e) {
            mfinalString = "";
            e.printStackTrace();
        }

        return mfinalString;
    }

    public static InputFilter getNumberAlphabet() {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[a-zA-Z0-9]*")) //put your constraints here
                {
                    return source;
                }
                return "";
            }
        };
        return filter;
    }

    public static InputFilter getNumberOnly() {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[0-9]*")) //put your constraints here
                {
                    return source;
                }
                return "";
            }
        };
        return filter;
    }

    public static String makeMaterialQry(List<SSOItemBean> alSOItems, String columnName, String batchNo) {
        String mCPQry = "", mBatchQry = "", concatQry = "";
        if (alSOItems != null && alSOItems.size() > 0) {
            for (SSOItemBean retBean : alSOItems) {
                if (mCPQry.length() == 0) {
                    mCPQry += " " + columnName + " eq '" + retBean.getMaterialNo() + "'";
                    mBatchQry += " " + batchNo + " eq '" + retBean.getBatchNo() + "'";
                } else {
                    mCPQry += " or " + columnName + " eq '" + retBean.getMaterialNo() + "'";
                    mBatchQry += " or " + batchNo + " eq '" + retBean.getBatchNo() + "'";
                }

            }
        }
        if (!mCPQry.equalsIgnoreCase("")) {
            concatQry = "( " + mCPQry + " )" + " and ( " + mBatchQry + " )";
        }

        return concatQry;
    }

    public static SchemeBean getPrimaryTaxValByMaterial(String cPStockItemGUID, String mStrMatNo, String mStrOrderQty, boolean ratioSchemeCal, String batchNo) {

        SchemeBean mStrNetAmount = null;
        try {

            mStrNetAmount = OfflineManager.getNetAmount(Constants.CPStockItemSnos + "?$filter=" + Constants.MaterialNo + " eq '" + mStrMatNo + "' and "
                    + Constants.CPStockItemGUID + " eq guid'" + cPStockItemGUID + "' " +
                    "and " + Constants.StockTypeID + " eq '" + Constants.str_1 + "'  and " + Constants.Batch + " eq '" + batchNo + "'" +
                    "&$orderby=" + Constants.ManufacturingDate + "%20asc ", mStrOrderQty, mStrMatNo, ratioSchemeCal);
//            and "+Constants.ManufacturingDate+" ne null
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStrNetAmount;
    }

    public static double getSecDiscAmtPer(double calSecPer, ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList) {
        double values = 0.0;
        for (SchemeCalcuBean schemeCalcuBean : schemeCalcuBeanArrayList) {
            values = values + schemeCalcuBean.getmDouSecDiscount();
        }
        values = values + calSecPer;
        return values;
    }

    public static double getSecSchemeAmt(double mDouSecAmt, ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList) {
        double values = 0.0;
        for (SchemeCalcuBean schemeCalcuBean : schemeCalcuBeanArrayList) {
            values = values + schemeCalcuBean.getmDouSecAmt();
        }
        values = mDouSecAmt + values;
        return values;
    }

    public static String getTaxAmount(String mStrAfterPriDisAmount, String mStrSecDisAmt, ODataEntity oDataEntity, String mStrOrderQty) {
        String mStrAfterSecAmt = (Double.parseDouble(mStrAfterPriDisAmount) - Double.parseDouble(mStrSecDisAmt)) + "";
        Double mStrNetAmtPerQty = Double.parseDouble(mStrAfterSecAmt) / Double.parseDouble(mStrOrderQty);
        String mStrTaxAmt = "0";
        try {
            mStrTaxAmt = OfflineManager.getPriceOnFieldByMatBatchAfterPrimarySecDiscount(oDataEntity, mStrNetAmtPerQty + "", mStrOrderQty);
        } catch (OfflineODataStoreException e) {
            mStrTaxAmt = "0";
        }

        return mStrTaxAmt;
    }

    public static ArrayList<SchemeBean> removeDuplicateScheme(ArrayList<SchemeBean> schPerCalBeanList) {
        ArrayList<SchemeBean> schPerCalBeanListFinal = new ArrayList<>();
        ArrayList<String> schemeIdList = new ArrayList<>();
        if (schPerCalBeanList != null) {
            for (SchemeBean schemeBean : schPerCalBeanList) {
                if (!schemeIdList.contains(schemeBean.getSchemeGuid())) {
                    schPerCalBeanListFinal.add(schemeBean);
                    schemeIdList.add(schemeBean.getSchemeGuid());
                }
            }
        }
        return schPerCalBeanListFinal;
    }

    public static int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

    public static String getCurrencyFormat(String currencyCode, String mStrAmount) {
        String mStrConAmount = mStrAmount;

        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            if (!TextUtils.isEmpty(currencyCode)) {
                format.setCurrency(java.util.Currency.getInstance(currencyCode));
                if (!TextUtils.isEmpty(mStrAmount)) {
                    mStrConAmount = format.format(new BigDecimal(mStrAmount));
                } else {
                    mStrConAmount = format.format(new BigDecimal(0));
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return mStrConAmount;
    }

    public static String trimQtyDecimalPlace(String qty) {
        if (qty.contains("."))
            return qty.substring(0, qty.indexOf("."));
        else
            return qty;
    }

    public static String getAddress(CustomerBean customerBean) {
        String address = "";
        String address1 = customerBean.getAddress1();
        if (!TextUtils.isEmpty(address1)) {
            address = address + address1 + "\n";
        } else {
            address = address;
        }

        String address2 = customerBean.getAddress2();
        if (!TextUtils.isEmpty(address2)) {
            address = address + address2 + "\n";
        } else {
            address = address;
        }

        String address3 = customerBean.getAddress3();
        if (!TextUtils.isEmpty(address3)) {
            address = address + address3 + "\n";
        } else {
            address = address;
        }

        String address4 = customerBean.getAddress4();
        if (!TextUtils.isEmpty(address4)) {
            address = address + address4 + "\n";
        } else {
            address = address;
        }

        String district = customerBean.getDistrictDesc();
        if (!TextUtils.isEmpty(district)) {
            address = address + district + "\n";
        } else {
            address = address;
        }

        String city = customerBean.getCity();
        if (!TextUtils.isEmpty(district)) {
            address = address + city + "\n";
        } else {
            address = address;
        }

        String country = customerBean.getCountry();
        if (!TextUtils.isEmpty(country)) {
            address = address + country;
        } else {
            address = address;
        }

        String postalCode = customerBean.getPostalCode();
        if (!TextUtils.isEmpty(country)) {
            address = address + " - " + postalCode;
        } else {
            address = address;
        }
        return address;
    }

    public static int getTotalRetailerCount(ArrayList<CustomerBean> alRetailerList) {
        int count = 0;
        for (int k = 0; k < alRetailerList.size(); k++) {
            CustomerBean customerBean = alRetailerList.get(k);
            if (!TextUtils.isEmpty(customerBean.getSeqNo()) && !customerBean.getSeqNo().equalsIgnoreCase("0") && !(customerBean.getSeqNo().equalsIgnoreCase("000000"))) {
                count++;
            }
        }
        return count;
    }

    public static String getVisitSeqQry(int seqNo) {
        String seqQry = "";
        if (seqNo > 1) {
            for (int k = 1; k < seqNo; k++) {
                if (k == 1) {
                    seqQry = seqQry + Constants.VisitSeq + " eq '" + Constants.appendPrecedingZeros(String.valueOf(k), 6) + "'";
                } else {
                    seqQry = seqQry + " or " + Constants.VisitSeq + " eq '" + Constants.appendPrecedingZeros(String.valueOf(k), 6) + "'";
                }
            }
        } else {
            seqQry = seqQry + Constants.VisitSeq + " eq '" + Constants.appendPrecedingZeros(String.valueOf(seqNo), 6) + "'";
        }
        return seqQry;
    }

    public static ArrayList<SSOItemBean> ssoItemBeans = new ArrayList<>();
    public static SOTempBean SOBundleValue = new SOTempBean();

    public static void closeStore(Context context) {
        try {
            UtilConstants.closeStore(context,
                    OfflineManager.options, "-100036",
                    offlineStore, Constants.PREFS_NAME,"");

        } catch (Exception e) {
            e.printStackTrace();
        }
//            Constants.Entity_Set.clear();
//            Constants.AL_ERROR_MSG.clear();
        offlineStore = null;
        OfflineManager.options = null;
    }

    public static Hashtable getOutletSurveyHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {

        Hashtable dbHeadTable = new Hashtable();

        try {

            dbHeadTable.put(Constants.CPMKTGUID, fetchJsonHeaderObject.getString(Constants.CPMKTGUID));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getOutletSurveyUpdateHeader(JSONObject fetchJsonHeaderObject) {

        Hashtable dbHeadTable = new Hashtable();

        try {

            dbHeadTable.put(Constants.CPMKTGUID, fetchJsonHeaderObject.getString(Constants.CPMKTGUID));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.SetResourcePath, fetchJsonHeaderObject.getString(Constants.SetResourcePath));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static ArrayList<String> getYesNoDesc() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.None);
        arrayList.add("No");
        arrayList.add("Yes");
        return arrayList;
    }
}

