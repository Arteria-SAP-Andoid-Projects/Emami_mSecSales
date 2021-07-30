package com.arteriatech.emami.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.appointment.AppointmentBean;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.service.NotificationService;
import com.arteriatech.emami.store.OfflineManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Created by e10762 on 11-01-2017.
 *
 */

public class NotificationSetClass
{
    private Context context = null;
    private int appointementYear  = 0;
    private int appointementMonth = 0;
    private int appointementDay = 0;
    private int appointementHour = 0;
    private int appointementMin = 0;
    private int appointementEndHour = 0;
    private int appointementEndMin = 0;
    private String[] timePeriods;
    private String mStrRetailerName = "";
    private String mStrActivityType = "";
    private String mStrStartTime = "";
    private String mStrEndTime = "";

    public NotificationSetClass(Context context) {
        this.context = context;
        getAppointmentList();
    }



    private void getAppointmentList()
    {
        String query = Constants.Visits+"?$filter="+Constants.StatusID+" eq '00' and "+Constants.PlannedDate+" ge datetime'"+ UtilConstants.getNewDate()+"' ";
        ArrayList<AppointmentBean> appointmentList = null;
        try
        {
            appointmentList = OfflineManager.getAppointmentList(query);
            if(appointmentList.size()>0)
            {

                for(int i=0;i<appointmentList.size();i++)
                {
                    if(appointmentList.get(i).getPlannedDate()!=null && appointmentList.get(i).getPlannedStartTime()!=null)
                    {
                        setDateParametersForNotification(appointmentList.get(i).getPlannedDate(),appointmentList.get(i).getPlannedStartTime());

                         mStrRetailerName = Constants.getNameByCPGUID(Constants.ChannelPartners, Constants.Name, Constants.CPGUID, Constants.convertStrGUID32to36(appointmentList.get(i).getCPGUID()));
                        mStrActivityType = appointmentList.get(i).getVisitTypeDesc();
                        convertTimeInto24hrFormat(appointmentList.get(i).getPlannedStartTime(),appointmentList.get(i).getPlannedEndTime());
                        startAlarmExactTime(mStrRetailerName);

                    }
                }



            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }


    private void convertTimeInto24hrFormat(String startTimeStr,String endTimeStr)
    {
        String mStrStartConvertTime = UtilConstants.convertTimeOnly(startTimeStr);
        String mStrEndConvertTime = UtilConstants.convertTimeOnly(endTimeStr);
        String[] splitStartTime = mStrStartConvertTime.split(":");
        String[] splitEndTime = mStrEndConvertTime.split(":");
        try {
            mStrStartTime =  splitStartTime[0]+":"+splitStartTime[1];
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            mStrEndTime =  splitEndTime[0]+":"+splitEndTime[1];
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setDateParametersForNotification(String dateInString,String timeInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        String mStrConvertTime = UtilConstants.convertTimeOnly(timeInString);
        String[] splitTime = mStrConvertTime.split(":");
        try {
            appointementHour = Integer.parseInt(splitTime[0]);
            appointementMin = Integer.parseInt(splitTime[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try
        {
            Date date = formatter.parse(dateInString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
             appointementYear = cal.get(Calendar.YEAR);
             appointementMonth = cal.get(Calendar.MONTH);
             appointementDay = cal.get(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void startAlarmExactTime(String mStrRetailerName) {
        String notificationDesc = mStrActivityType+" "+mStrStartTime+" - "+mStrEndTime;
        try
        {
            timePeriods = OfflineManager.getAppointmentTimeConfigList(Constants.ConfigTypesetTypes+"?$filter= Typeset eq 'APNRMD'");
        }
        catch (OfflineODataStoreException e)
        {
            e.printStackTrace();
        }
        long currntTime = System.currentTimeMillis();
        int intervalSize =timePeriods.length;
        int randomNum=0;
        Random r = new Random();
        for (int minute=0; minute<intervalSize;minute++ ) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, appointementHour);
            calendar.set(Calendar.MINUTE, appointementMin);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long calenderMillisecond = calendar.getTimeInMillis();
            long backTime = TimeUnit.MINUTES.toMillis(Integer.parseInt(timePeriods[minute]));
            long actualTime = calenderMillisecond-backTime;
            Log.d("NotificationService", "startAlarmExactTime: actualTime"+actualTime);
            Log.d("NotificationService", "startAlarmExactTime: calenderMillisecond"+calenderMillisecond);
            int oldAlmId = Constants.getSharedPref(context,actualTime+"_Key",0);
            if(oldAlmId>0){
                randomNum=oldAlmId;
            }else {
                randomNum = r.nextInt(100000);
                if(checkServiceIsRunning(context,randomNum)){
                    randomNum=randomNum+1;
                }
            }
            if(currntTime<=actualTime){
                Log.d("NotificationService", "random : "+randomNum+" actualTime: "+actualTime);
                Intent intent = new Intent(context, NotificationService.class);
                intent.putExtra(NotificationService.EXTRA_ALARM_ID,randomNum);
                intent.putExtra(NotificationService.EXTRA_CONTENT_TITLE,mStrRetailerName);
                intent.putExtra(NotificationService.EXTRA_CONTENT_DESC,notificationDesc);
                intent.putExtra(NotificationService.EXTRA_ALARM_TIME,actualTime);
                PendingIntent pendingIntentNew = PendingIntent.getService(context, randomNum, intent, 0);
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, actualTime, pendingIntentNew);
                Constants.saveSharedPref(context,actualTime+"_Key",randomNum);
            }
        }
    }
    private boolean checkServiceIsRunning(Context context,int alrmId){
        boolean isRunning = false;
        Intent intent = new Intent(context, NotificationService.class);
        isRunning = (PendingIntent.getService(context, alrmId, intent,PendingIntent.FLAG_NO_CREATE) != null);
        return isRunning;
    }

}
