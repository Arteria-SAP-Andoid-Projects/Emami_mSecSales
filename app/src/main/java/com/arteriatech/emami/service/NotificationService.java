package com.arteriatech.emami.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.registration.RegistrationActivity;

public class NotificationService extends IntentService {

    private Context mContext;
    private String contentTitle="";
    private String contentDesc="";
    public static final String EXTRA_CONTENT_TITLE="contentTitle";
    public static final String EXTRA_CONTENT_DESC="contentDesc";
    public static final String EXTRA_ALARM_ID="alarmId";
    public static final String EXTRA_ALARM_TIME="alarmTime";
    private int notificationId = 1;
    private String TAG = "NotificationService";
    private int alarmId=0;
    private long actualTime=0;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mContext=NotificationService.this;
            Log.d(TAG, "onHandleIntent: started notification");
            contentTitle=intent.getStringExtra(EXTRA_CONTENT_TITLE);
            contentDesc=intent.getStringExtra(EXTRA_CONTENT_DESC);
            notificationId = intent.getExtras().getInt(EXTRA_ALARM_ID);
            actualTime = intent.getLongExtra(EXTRA_ALARM_TIME,0);
            Log.d(TAG, "onHandleIntent: alarmId :"+alarmId+" contentTitle: "+contentTitle+" contentDesc :"+contentDesc+" actualTime :"+actualTime);
            incrementAlertsNotficationValue(mContext);
            NotificationCompat.Builder notification = getNotification(mContext,contentTitle,contentDesc);
//            Intent notificationIntent = new Intent(mContext,MAFLogonActivity.class);
            Intent notificationIntent = new Intent(mContext,RegistrationActivity.class);
            notificationIntent.putExtra(Constants.EXTRA_OPEN_NOTIFICATION,true);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainMenu.class);
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification.build());
            Constants.removeAlarmIdFromSharedPref(mContext,actualTime+"_Key");
        }
    }
    private NotificationCompat.Builder getNotification(Context context, String content,String contentDesc) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(content);
        builder.setContentText(contentDesc);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND);
        builder.setSmallIcon(R.drawable.emami);
        return builder;
    }

    private void incrementAlertsNotficationValue(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        int mIntAlertsCount=0;
        try {
            mIntAlertsCount =  sharedPref.getInt(Constants.AppointmentAlertsCount,0) ;
        } catch (Exception e) {
            mIntAlertsCount = 0;
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.AppointmentAlertsCount, mIntAlertsCount + 1);
        editor.commit();
    }

}
