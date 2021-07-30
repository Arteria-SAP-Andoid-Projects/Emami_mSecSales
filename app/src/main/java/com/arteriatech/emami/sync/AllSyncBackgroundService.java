package com.arteriatech.emami.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineSynLogListener;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.online.OnlineODataStore;
import com.sap.xscript.core.GUID;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/**
 * Created by heman on 15-Feb-18.
 */

public class AllSyncBackgroundService extends Service implements UIListener {
    private final IBinder mBinder = new LocalBinder();
    String time = "time";
    private String TAG = AllSyncBackgroundService.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            int triggerTime;
            if (intent != null) {
                triggerTime = intent.getIntExtra(Constants.TRIGGER_TIME, 0);
            } else {
                triggerTime = 0;//1000*60*2;
            }
            Log.d(TAG, "onStartCommand:trigger time: " + triggerTime);
            Intent intents = new Intent(this, AllSyncBackgroundService.class);
            intents.putExtra(Constants.TRIGGER_TIME, Constants.SyncFreqency);
            PendingIntent pendingIntent = PendingIntent.getService(this, Constants.PENDING_INTENT_SYNC_ID, intents, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + triggerTime, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + triggerTime, pendingIntent);
            }
            GUID guid = GUID.newRandom();
            String guidValue = guid.toString36();

            Calendar cal = new GregorianCalendar();
            String date = Constants.getDateFormat("yyyy-MM-dd").format(cal.getTime());
            String time = Constants.getDateFormat("HH:mm:ss").format(cal.getTime());
            String dateTime = date + "T" + time;
            Constants.TimeDifference2 = Calendar.getInstance();
            Hashtable hashtable = new Hashtable();
            hashtable.put(Constants.Userid, Constants.USER_ID);
            hashtable.put(Constants.Name, Constants.USER_NAME);
            hashtable.put(Constants.SalesPersonMobileNo, Constants.USER_MOBILE_NUMBER);
            hashtable.put(Constants.Guid, guidValue);
            hashtable.put(Constants.StartDate, dateTime);
            hashtable.put(Constants.StartTime, UtilConstants.getOdataDuration());
            hashtable.put(Constants.SyncType, Constants.SyncTypeID);
            hashtable.put(Constants.AlertAt, Constants.getDiffMinutes(Constants.TimeDifference1, Constants.TimeDifference2));
            Log.d(TAG, "onStartCommand: time diff" + Constants.getDiffMinutes(Constants.TimeDifference1, Constants.TimeDifference2));
            new SyncLogAsyncTask(hashtable).execute();

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Service killed");
        super.onDestroy();
    }

    /**
     * Mandatory method implement for service class.
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRequestError(int i, Exception e) {
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
    }

    public class LocalBinder extends Binder {
        AllSyncBackgroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AllSyncBackgroundService.this;
        }

    }

    /**
     * This method will call when ever we force close an application. So that this can
     * cancel the service(Alarm)
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /**
     * Sync log AsyncTask will perform the task to sending logs to server.
     */
    public class SyncLogAsyncTask extends AsyncTask<Void, Void, Void> {

        Hashtable<String, String> hashtable;

        public SyncLogAsyncTask(Hashtable<String, String> hashtable) {
            this.hashtable = hashtable;
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendSyncLogToServer(hashtable);
            return null;
        }
    }

    private void sendSyncLogToServer(Hashtable<String, String> hashtable) {
        try {
            boolean onlineStoreOpen = false;
            OnlineODataStore store = null;


            OnlineSynLogListener openListener = OnlineSynLogListener.getInstance();
            store = openListener.getStore();
            if (store != null) {
                OnlineManager.createSyncLog(hashtable, AllSyncBackgroundService.this);
            } else {
                Constants.onlineStoreSyncLog = null;
                OnlineSynLogListener.instance = null;
                Constants.IsOnlineStoreSyncLogFailed = false;
                onlineStoreOpen = OnlineManager.openOnlineStoreForSyncLog(AllSyncBackgroundService.this);
                if (onlineStoreOpen) {
                    OnlineManager.createSyncLog(hashtable, AllSyncBackgroundService.this);
                }


            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
