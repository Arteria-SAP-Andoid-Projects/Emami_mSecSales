package com.arteriatech.emami.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.mbo.RemarkReasonBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.xscript.core.GUID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

public class DayEndRemarksActivity extends AppCompatActivity implements UIListener {
	private EditText etRemarks;
	private Spinner spDealers,spReason;
	private String selDealer, selDelName = "",selDelGuid="", selRoutePlanKey = "",selDelGuid36="",selReson="",
			closingDayType = "", closingDate = "";
	private String[][] delList = null;
	private String mStrPopUpText = "";
	public static  String[] retailerRemarks;
	private ArrayList<String> checkedRetailers;
	private int currentRetailerId = 0;
	private Boolean nextRetailer = false;
	private boolean isFisrtTime = false;
	private ArrayList<RemarkReasonBean> reasonCodedesc = new ArrayList<>();
	MenuItem menu_save, menu_next;
	private String selectedReasonDesc ="";
	private String selectedReasonCode="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_day_end_remarks);
		//Initialize action bar without back button(false)
		ActionBarView.initActionBarView(this, true,getString(R.string.lbl_not_visited_retailer));
		Bundle bundle = getIntent().getExtras();
		closingDayType = bundle.getString(Constants.ClosingeDayType);
		closingDate = bundle.getString(Constants.ClosingeDay);
		if (!Constants.restartApp(DayEndRemarksActivity.this)) {
			//Initialize UI
			getReasonValues();
			initUI();
		}

	}

	/*Initializes UI for screen*/
	void initUI(){
		checkedRetailers = new ArrayList<>();
		Constants.MAX_LENGTH = 255;
		etRemarks = (EditText) findViewById(R.id.etRemarks);
		InputFilter[] FilterArray = new InputFilter[2];
		FilterArray[0] = new InputFilter.LengthFilter(Constants.MAX_LENGTH);
		FilterArray[1] = Constants.getNumberAlphabetOnly();
		etRemarks.setFilters(FilterArray);

		etRemarks.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				/*try {
					delList[currentRetailerId][5]= s.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				try {
					if (!selectedReasonCode.equalsIgnoreCase("00") && !selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {
                        String mStrRemarks = "";
                        try {
                            mStrRemarks = s.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mStrRemarks ="";
                        }
						delList[currentRetailerId][8] = mStrRemarks;
						if(mStrRemarks.equalsIgnoreCase("")){
                            mStrRemarks= selectedReasonDesc;
                        }else{
                            mStrRemarks= selectedReasonDesc +" "+mStrRemarks;
                        }
                        delList[currentRetailerId][5] = mStrRemarks;
                    }else if(selectedReasonCode.equalsIgnoreCase(Constants.str_06)){
						String mStrRemarks = "";
						try {
							mStrRemarks = s.toString();
						} catch (Exception e) {
							e.printStackTrace();
							mStrRemarks ="";
						}
						delList[currentRetailerId][8] = mStrRemarks;
						if(mStrRemarks.equalsIgnoreCase("")){
							mStrRemarks= selectedReasonDesc;
						}else{
							mStrRemarks= selectedReasonDesc +" "+mStrRemarks;
						}
						delList[currentRetailerId][5] = mStrRemarks;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				etRemarks.setBackgroundResource(R.drawable.edittext);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		spDealers = (Spinner) findViewById(R.id.spDealers);
		spReason = (Spinner) findViewById(R.id.spReason);

		try {
			new CheckValidation().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_collection_create, menu);
		menu_save = menu.findItem(R.id.menu_collection_save);
		menu_next = menu.findItem(R.id.menu_collection_next);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!nextRetailer) {
			menu_save.setVisible(true);
			menu_next.setVisible(false);
		} else {
			menu_save.setVisible(false);
			menu_next.setVisible(true);
		}

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_collection_next:
				onNext();
				break;
			case R.id.menu_collection_save:
				onSave();
				break;

			case android.R.id.home:
				onBackPressed();
				break;

		}
		return true;
	}


	private void setDealers()
	{


		if(delList ==null){
			delList = new String[1][9];
			delList[0][0]="";
			delList[0][1]="";
			delList[0][2]="";
			delList[0][3]="";
			delList[0][4]="";
			delList[0][5]="";
			delList[0][6]="";
			delList[0][7]="";
			delList[0][8]="";
		}

		//displaying retailer name and number in spinner
		String [] dealerList = new String[delList.length];
		for(int i=0; i<delList.length; i++){
			dealerList[i] = delList[i][2];
		}
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				this, R.layout.custom_textview, dealerList);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDealers.setAdapter(spinnerAdapter);
		spDealers.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				currentRetailerId = position;
				selDealer = delList[position][0];
				selDelName = delList[position][2];
				selDelGuid =  delList[position][3];
				selRoutePlanKey = delList[position][4];
				try {
					etRemarks.setText(delList[position][8]);
				} catch (Exception e) {
					etRemarks.setText("");
					e.printStackTrace();
				}
				selDelGuid36 = delList[position][6];
				try {
					selReson = delList[position][7]!=null?delList[position][7]:"";
				} catch (Exception e) {
					e.printStackTrace();
					selReson = "";
				}
				displayReasonValues();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});




		if(nextRetailer)
		{
			if(currentRetailerId<=delList.length)
			{
				spDealers.setSelection(currentRetailerId+1);
				etRemarks.setText("");
			}

		}
		int remarksIncompleted = 0;
		for(int i=0;i<delList.length;i++)
		{
			if(delList[i][7].equals(""))
			{
				remarksIncompleted++;

			}
		}
		if(remarksIncompleted>1){
			nextRetailer =true;
		}else{
			nextRetailer = false;
		}
		menuVisible();


	}

	private void displayReasonValues(){
		ArrayAdapter<RemarkReasonBean> reasonadapter = new ArrayAdapter<>(DayEndRemarksActivity.this, R.layout.custom_textview, reasonCodedesc);
		reasonadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spReason.setAdapter(reasonadapter);
		for (int i = 0; i < reasonCodedesc.size(); i++) {
			if (selReson.equalsIgnoreCase(reasonCodedesc.get(i).getReasonCode())) {
				spReason.setSelection(i);
				break;
			}
		}

		spReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				selectedReasonDesc =reasonCodedesc.get(i).getReasonDesc();
				selectedReasonCode=reasonCodedesc.get(i).getReasonCode();
				spReason.setBackgroundResource(R.drawable.spinner_bg);

				if (!selectedReasonCode.equalsIgnoreCase("00") && !selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {
					delList[currentRetailerId][7]=selectedReasonCode;

					String mStrRemarks = "";
					if(etRemarks.getText()!=null) {
						try {
							mStrRemarks = etRemarks.getText().toString();
						} catch (Exception e) {
							e.printStackTrace();
							mStrRemarks ="";
						}
						delList[currentRetailerId][8] = mStrRemarks;
						if(mStrRemarks.equalsIgnoreCase("")){
							mStrRemarks= selectedReasonDesc;
						}else{
							mStrRemarks= selectedReasonDesc +" "+mStrRemarks;
						}
					}else{
						delList[currentRetailerId][8] = "";
						mStrRemarks= selectedReasonDesc;
					}
					delList[currentRetailerId][5] = mStrRemarks;

				}else if(selectedReasonCode.equalsIgnoreCase(Constants.str_06)){
					delList[currentRetailerId][7]=selectedReasonCode;

					String mStrRemarks = "";
					if(etRemarks.getText()!=null) {
						try {
							mStrRemarks = etRemarks.getText().toString();
						} catch (Exception e) {
							e.printStackTrace();
							mStrRemarks ="";
						}
						delList[currentRetailerId][8] = mStrRemarks;
						if(mStrRemarks.equalsIgnoreCase("")){
							mStrRemarks= selectedReasonDesc;
						}else{
							mStrRemarks= selectedReasonDesc +" "+mStrRemarks;
						}
					}else{
						mStrRemarks= "";
						delList[currentRetailerId][8] = mStrRemarks;
					}
					delList[currentRetailerId][5] = mStrRemarks;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

	/*gets dealer who are not visited today from route plan*/
	public static String[][] getDealer(String closingDate) {
		String retList[][] = null;

		try{
			String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + closingDate + "'";

			ArrayList<CustomerBean> alRSCHList = null;
			try {
				alRSCHList = OfflineManager.getTodayRoutes1(routeQry);
			} catch (OfflineODataStoreException e) {
				e.printStackTrace();
			}
			if (alRSCHList != null && alRSCHList.size() > 0) {
				String routeSchopeVal = alRSCHList.get(0).getRoutSchScope();
				if (alRSCHList.size() > 1) {
					String mRSCHQry = "";
					if (routeSchopeVal.equalsIgnoreCase("000001")) {
						for (CustomerBean routeList : alRSCHList) {
							if (mRSCHQry.length() == 0)
								mRSCHQry += " guid'" + routeList.getRschGuid().toUpperCase() + "'";
							else
								mRSCHQry += " or " + Constants.RouteSchGUID + " eq guid'" + routeList.getRschGuid().toUpperCase() + "'";

						}

						String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=(" +
								Constants.RouteSchGUID + " eq "+mRSCHQry+") &$orderby=" + Constants.SequenceNo + "";

						retList = OfflineManager.getNotVisitedRetailerList(qryForTodaysBeat, closingDate);

						if(retList==null){
							retailerRemarks = new String[1];
						}else{
							retailerRemarks = new String[retList.length];
						}


					} else if (routeSchopeVal.equalsIgnoreCase("000002")) {
						// Get the list of retailers from RoutePlans
					}


				} else {


					if (routeSchopeVal.equalsIgnoreCase("000001")) {
						String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteSchGUID + " eq guid'"
								+ alRSCHList.get(0).getRschGuid().toUpperCase() + "' &$orderby=" + Constants.SequenceNo + "";

						retList = OfflineManager.getNotVisitedRetailerList(qryForTodaysBeat, closingDate);
						if(retList==null){
							retailerRemarks = new String[1];
						}else {
							retailerRemarks = new String[retList.length];
						}

					} else if (routeSchopeVal.equalsIgnoreCase("000002")) {
						// Get the list of retailers from RoutePlans
					}

				}

			}else{
				retList = null;
				retailerRemarks = new String[1];
			}

		} catch (OfflineODataStoreException e) {
			LogManager.writeLogError(Constants.error_txt + e.getMessage());
		}

return retList;

	}

	private void onNext()
	{
		try {
			new CheckRemarksValAsyncTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkRemarksValidation(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!selectedReasonCode.equalsIgnoreCase("00") && !selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {

			String mStrRemarks = "";
			if(etRemarks.getText()!=null) {
				try {
					mStrRemarks = etRemarks.getText().toString();
				} catch (Exception e) {
					e.printStackTrace();
					mStrRemarks ="";
				}
				delList[currentRetailerId][8] = mStrRemarks;
				if(mStrRemarks.equalsIgnoreCase("")){
					mStrRemarks= selectedReasonDesc;
				}else{
					mStrRemarks= selectedReasonDesc +" "+mStrRemarks;
				}
			}else{
				delList[currentRetailerId][8] = "";
				mStrRemarks= selectedReasonDesc;
			}

					delList[currentRetailerId][5] = mStrRemarks;
			delList[currentRetailerId][7] = selectedReasonCode;
			if (currentRetailerId < delList.length - 1) {

			} else {
				nextRetailer = false;
				menuVisible();
			}

			closingProgressDialog();
			if (currentRetailerId < delList.length - 1) {
				nextRetailer = true;
				setDealers();
			} else {
				nextRetailer = false;

				menuVisible();
			}
		}else if(selectedReasonCode.equalsIgnoreCase(Constants.str_06)){

					String mStrRemarks = "";
					if(etRemarks.getText()!=null) {
						try {
							mStrRemarks = etRemarks.getText().toString();
						} catch (Exception e) {
							e.printStackTrace();
							mStrRemarks ="";
						}

					}else{
						mStrRemarks= "";
					}

					if(!mStrRemarks.equalsIgnoreCase("")){

						delList[currentRetailerId][8] = mStrRemarks;
						if(mStrRemarks.equalsIgnoreCase("")){
							mStrRemarks= selectedReasonDesc;
						}else{
							mStrRemarks= selectedReasonDesc +" "+mStrRemarks;
						}

						delList[currentRetailerId][5] = mStrRemarks;
						delList[currentRetailerId][7] = selectedReasonCode;
						if (currentRetailerId < delList.length - 1) {

						} else {
							nextRetailer = false;
							menuVisible();
						}

						closingProgressDialog();
						if (currentRetailerId < delList.length - 1) {
							nextRetailer = true;
							setDealers();
						} else {
							nextRetailer = false;

							menuVisible();
						}
					}else{
						delList[currentRetailerId][7] = selectedReasonCode;
						delList[currentRetailerId][5] = "";
						delList[currentRetailerId][8] = "";
						closingProgressDialog();
						etRemarks.setBackgroundResource(R.drawable.edittext_border);
						UtilConstants.showAlert(getString(R.string.alert_please_enter_remarks),DayEndRemarksActivity.this);
					}




				}else{
			closingProgressDialog();
			if (selectedReasonCode.equalsIgnoreCase("00")) {
				spReason.setBackgroundResource(R.drawable.error_spinner);
			}
					UtilConstants.showAlert(getString(R.string.msg_remarks),DayEndRemarksActivity.this);


		}

			}
		});
	}

	private void checkRemeksFilledOrNot(){

	}

	/*AsyncTask to save vales into datavault*/
	private class CheckRemarksValAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(pdLoadDialog!=null){
				pdLoadDialog = null;
			}
			pdLoadDialog = new ProgressDialog(DayEndRemarksActivity.this, R.style.ProgressDialogTheme);
			pdLoadDialog.setMessage(getString(R.string.app_loading));
			pdLoadDialog.setCancelable(false);
			pdLoadDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			checkRemarksValidation();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			closingProgressDialog();
		}
	}
	private void closingProgressDialog(){
		try {
			pdLoadDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void menuVisible(){
		try {
			if (!nextRetailer) {
                menu_save.setVisible(true);
                menu_next.setVisible(false);
            } else {
                menu_save.setVisible(false);
                menu_next.setVisible(true);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*AsyncTask to get vales from datavault*/
	private class CheckValidation extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdLoadDialog = new ProgressDialog(DayEndRemarksActivity.this, R.style.ProgressDialogTheme);
			pdLoadDialog.setMessage(getString(R.string.app_loading));
			pdLoadDialog.setCancelable(false);
			pdLoadDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if(!isFisrtTime){
				delList = getDealer(closingDate);
			}else{
				checkValidation();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(!isFisrtTime){
				isFisrtTime =true;
				closingProgDialog();
				setDealers();
			}else{
				saveAllRetailers();
			}


		}
	}
	private void closingProgDialog(){
		try {
			pdLoadDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String mStrRetName = "";
	private void checkValidation(){
		mStrRetName = "";
		for(int i=0;i<delList.length;i++)
		{
			if(delList[i][7].equals(""))
			{
				mStrRetName = delList[i][2];
				break;
			}else if(delList[i][7].equalsIgnoreCase(Constants.str_06)){
				try {
					if(delList[i][8].equals("")){
                        mStrRetName = delList[i][2];
                        break;
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private String mStrAttendanceId = "";
	private void onSave(){
		Constants.MapEntityVal.clear();
		String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null ";
		try {
			mStrAttendanceId = OfflineManager.getAttendance(dayEndqry);
		} catch (OfflineODataStoreException e) {
			LogManager.writeLogError(Constants.error_txt + e.getMessage());
		}
		if(!Constants.MapEntityVal.isEmpty()){
			if(Constants.isEndateAndEndTimeValid(UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate)),  Constants.MapEntityVal.get(Constants.StartTime)+"" )){
				validDateTime();
			}else{
				// display error pop up
				String mStrDate = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) Constants.MapEntityVal.get(Constants.StartDate));
				UtilConstants.showAlert(getString(R.string.msg_end_date_should_be_greterthan_startdate,mStrDate), DayEndRemarksActivity.this);
			}
		}else{
			validDateTime();
		}

	}

	private void validDateTime(){
		pdLoadDialog = Constants.showProgressDialog(DayEndRemarksActivity.this, "", getString(R.string.checking_pemission));
		LocationUtils.checkLocationPermission(DayEndRemarksActivity.this, new LocationInterface() {
			@Override
			public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
				closeProgressDialog();
				if (status) {
					locationPerGranted();
				}
			}
		});
	}

	private void locationPerGranted(){
		pdLoadDialog = Constants.showProgressDialog(DayEndRemarksActivity.this,"",getString(R.string.gps_progress));
		Constants.getLocation(DayEndRemarksActivity.this, new LocationInterface() {
			@Override
			public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
				closeProgressDialog();
				if(status){
					try {
						new CheckValidation().execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case UtilConstants.Location_PERMISSION_CONSTANT: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					LocationUtils.checkLocationPermission(DayEndRemarksActivity.this, new LocationInterface() {
						@Override
						public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
							if(status){
								locationPerGranted();
							}
						}
					});
					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}


		}
		// other 'case' lines to check for other
		// permissions this app might request
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==LocationUtils.REQUEST_CHECK_SETTINGS){
			if(resultCode == Activity.RESULT_OK){
				locationPerGranted();
			}
		}
	}
	private void saveAllRetailers()
	{
		if(mStrRetName.equalsIgnoreCase("")){
			onSaveAsyncTask();
		}else{
			closingProgDialog();
			UtilConstants.showAlert(getString(R.string.msg_remarks_par_retailer,mStrRetName),DayEndRemarksActivity.this);
		}

	}

	private void onSaveVisit(){
		for (int incVal = 0; incVal < delList.length; incVal++) {
			GUID guid = GUID.newRandom();

			Hashtable table = new Hashtable();
			//noinspection unchecked
			table.put(Constants.CPNo, UtilConstants.removeLeadingZeros(delList[incVal][0]));

			table.put(Constants.CPName, delList[incVal][2]);
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
			table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.latitude));
			//noinspection unchecked
			table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.longitude));
//				//noinspection unchecked
			table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());


			//noinspection unchecked
			table.put(Constants.ENDTIME, oDataDuration);
			//noinspection unchecked
			table.put(Constants.VISITKEY, guid.toString());

			table.put(Constants.ROUTEPLANKEY, Constants.convertStrGUID32to36(delList[incVal][4].toUpperCase()));

			table.put(Constants.StatusID,  Constants.str_02);

			table.put(Constants.REMARKS, delList[incVal][5].trim());

			try {
				table.put(Constants.REASON, delList[incVal][7]);
			} catch (Exception e) {
				e.printStackTrace();
			}

			table.put(Constants.CPTypeID,  Constants.str_02);
			table.put(Constants.VisitCatID,  Constants.str_01);

			table.put(Constants.VisitDate, closingDate);

			try {
				table.put(Constants.CPGUID, delList[incVal][3]);
			} catch (Exception e) {
				table.put(Constants.CPGUID, "");
			}

			try {
				table.put(Constants.NoOfOutlet, delList[incVal][9]);
			} catch (Exception e) {
				e.printStackTrace();
				table.put(Constants.NoOfOutlet, "");
			}

			String[][] mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(delList[incVal][6].toUpperCase());

			try {
				table.put(Constants.SPGUID, mArraySPValues[4][0].toUpperCase());
			} catch (Exception e) {
				table.put(Constants.SPGUID, Constants.getSPGUID());
			}


			SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);


			String loginIdVal = sharedPreferences.getString(Constants.username, "");
			//noinspection unchecked
			table.put(Constants.LOGINID, loginIdVal);

			table.put(Constants.VisitSeq,  "");


			try {
				//noinspection unchecked
				if(!table.get(Constants.CPGUID).equals("")) {
					OfflineManager.createVisitStartEnd(table);
				}
			} catch (OfflineODataStoreException e) {
				LogManager.writeLogError(Constants.error_txt + e.getMessage());
			}

		}



	}

	/*Close day if remarks for all not visited retailer filled*/
	private void onSaveClose() {
			Constants.MapEntityVal.clear();

			String qry = Constants.Attendances + "?$filter=EndDate eq null ";
			try {
				OfflineManager.getAttendance(qry);
			} catch (OfflineODataStoreException e) {
				LogManager.writeLogError(Constants.error_txt + e.getMessage());
			}

			Hashtable hashTableAttendanceValues;


			hashTableAttendanceValues = new Hashtable();
			SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

			String loginIdVal = sharedPreferences.getString(Constants.username, "");
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.latitude));
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.longitude));
			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.EndDate, UtilConstants.getNewDateTimeFormat());

			hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SalesPersons, Constants.SPGUID));


			hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

			if (Constants.MapEntityVal.get(Constants.Etag) != null) {
				hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
			} else {
				hashTableAttendanceValues.put(Constants.Etag, "");
			}

			hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
			hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
			hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

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

			//noinspection unchecked
			hashTableAttendanceValues.put(Constants.EndTime, oDataDuration);


			try {
				//noinspection unchecked
				OfflineManager.updateAttendance(hashTableAttendanceValues, DayEndRemarksActivity.this);
			} catch (OfflineODataStoreException e) {
				LogManager.writeLogError(Constants.error_txt + e.getMessage());
			}
	}


	/*Ends day*/
	private void onSaveAsyncTask() {
		try {
			new ClosingDate().execute();
		} catch (Exception e) {
			LogManager.writeLogError(Constants.error_txt + e.getMessage());
		}

	}

	private ProgressDialog pdLoadDialog;
	/*AsyncTask to Close Attendance for day*/
	private class ClosingDate extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pdLoadDialog == null) {
				pdLoadDialog = new ProgressDialog(DayEndRemarksActivity.this, R.style.ProgressDialogTheme);
				pdLoadDialog.setMessage(getString(R.string.msg_update_non_visted_retilers));
				pdLoadDialog.setCancelable(false);
				pdLoadDialog.show();
			}else{
				pdLoadDialog.setMessage(getString(R.string.msg_update_non_visted_retilers));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(200);
				if(!OfflineManager.isOfflineStoreOpen()) {
					try {
						OfflineManager.openOfflineStore(DayEndRemarksActivity.this, DayEndRemarksActivity.this);
					} catch (OfflineODataStoreException e) {
						LogManager.writeLogError(Constants.error_txt + e.getMessage());
					}
				}else {
					onSaveVisit();
					onSaveClose();
				}
			} catch (InterruptedException e) {
				LogManager.writeLogError(Constants.error_txt + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	@Override
	public void onRequestError(int operation, Exception exception) {
		ErrorBean errorBean = Constants.getErrorCode(operation, exception,DayEndRemarksActivity.this);
		if (errorBean.hasNoError()) {

			if (operation == Operation.Create.getValue()) {
				closeProgressDialog();
				mStrPopUpText = getString(R.string.close_update_with_err);
				displayPopUpMsg();
			} else if (operation == Operation.Update.getValue()) {
				closeProgressDialog();
				try {
					mStrPopUpText = getString(R.string.err_msg_concat,getString(R.string.lbl_attence_end),exception.getMessage());
				} catch (Exception e) {
					mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
				}
				displayPopUpMsg();
			} else if (operation == Operation.OfflineFlush.getValue()) {
				closeProgressDialog();
				try {
					mStrPopUpText = getString(R.string.err_msg_concat,getString(R.string.lbl_attence_end),exception.getMessage());
				} catch (Exception e) {
					mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
				}
				displayPopUpMsg();
			} else if (operation == Operation.OfflineRefresh.getValue()) {
				closeProgressDialog();
				try {
					mStrPopUpText = getString(R.string.err_msg_concat,getString(R.string.lbl_attence_end),exception.getMessage());
				} catch (Exception e) {
					mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
				}
				displayPopUpMsg();
			}else {
				try {
					mStrPopUpText = getString(R.string.err_msg_concat,getString(R.string.lbl_attence_end),exception.getMessage());
				} catch (Exception e) {
					mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
				}
				Constants.isSync = false;
				closeProgressDialog();
				displayPopUpMsg();
			}
		}else{
			Constants.isSync = false;

			if(errorBean.isStoreFailed()){
				if(!OfflineManager.isOfflineStoreOpen()) {
					mStrPopUpText = getString(R.string.app_loading);
					try {
						new ClosingDate().execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					closeProgressDialog();
					mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(),DayEndRemarksActivity.this,false);
					displayPopUpMsg();
				}
			}else{
				closeProgressDialog();
				mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(),DayEndRemarksActivity.this,false);
				displayPopUpMsg();
			}
		}
	}

	@Override
	public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
		if (operation == Operation.Create.getValue()){
			mStrPopUpText = getString(R.string.Day_ended);
				displayPopUpMsg();
		}
		else if (operation == Operation.Update.getValue()){
			if (!UtilConstants.isNetworkAvailable(DayEndRemarksActivity.this)) {
				Constants.isSync = false;
				closeProgressDialog();
				UtilConstants.onNoNetwork(DayEndRemarksActivity.this);
				onBackPressed();
			} else {
				if (Constants.iSAutoSync) {
					closeProgressDialog();
					mStrPopUpText = getString(R.string.alert_auto_sync_is_progress);
					displayPopUpMsg();
				} else {
					Constants.isSync = true;
					OfflineManager.flushQueuedRequests(DayEndRemarksActivity.this);
				}
			}

		}else if (operation == Operation.OfflineFlush.getValue()) {
			if (!UtilConstants.isNetworkAvailable(DayEndRemarksActivity.this)) {
				Constants.isSync = false;
				closeProgressDialog();
				UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), DayEndRemarksActivity.this);
				onBackPressed();
			} else {
				String allCollection = "";
					allCollection = Constants.Attendances + "," + Constants.Visits ;
				OfflineManager.refreshRequests(DayEndRemarksActivity.this, allCollection, DayEndRemarksActivity.this);
			}

		} else if (operation == Operation.OfflineRefresh.getValue()) {
			Constants.isSync = false;
			closeProgressDialog();
			mStrPopUpText = getString(R.string.Day_ended);
			UtilConstants.dialogBoxWithCallBack(DayEndRemarksActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
				@Override
				public void clickedStatus(boolean b) {
					Constants.isSync = false;
					if (!AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore,DayEndRemarksActivity.this,"",true)){
						onBackPressed();
					}
				}
			});
//			displayPopUpMsg();
		}else if (operation == Operation.GetStoreOpen.getValue() &&   OfflineManager.isOfflineStoreOpen()) {
			try {
				OfflineManager.getAuthorizations(getApplicationContext());
			} catch (OfflineODataStoreException e) {
				e.printStackTrace();
			}
			Constants.setSyncTime(DayEndRemarksActivity.this);
			closeProgressDialog();

			UtilConstants.dialogBoxWithCallBack(DayEndRemarksActivity.this, "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
				@Override
				public void clickedStatus(boolean b) {
					Constants.isSync = false;
					if (!AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore,DayEndRemarksActivity.this,"",true)){
						onBackPressed();
					}
				}
			});
		}

	}
	private void closeProgressDialog(){
		try {
			pdLoadDialog.dismiss();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/*displays alert with Message*/
	public void displayPopUpMsg(){
		AlertDialog.Builder builder = new AlertDialog.Builder(DayEndRemarksActivity.this,R.style.MyTheme);
		builder.setMessage(mStrPopUpText)
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface Dialog,
									int id) {
								try {
									Dialog.cancel();
									Constants.isSync = false;
									onBackPressed();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
		builder.show();
	}

	private void getReasonValues() {

		String query = Constants.ValueHelps + "?$filter= PropName eq '" + "Reason" + "' and EntityType eq 'Visit' &$orderby="+Constants.ID+"";
		try {
			reasonCodedesc = OfflineManager.getRemarksReason(query);
		} catch (OfflineODataStoreException e) {
			e.printStackTrace();
		}

	}
}
