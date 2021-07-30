package com.arteriatech.emami.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.arteriatech.emami.main.MainMenuFragment;

import org.jsoup.Jsoup;


public class MyWebService extends IntentService {

    private static final String LOG_TAG = "MyWebService";
    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    private String URL = null;
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;

    public MyWebService() {
        super("MyWebService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PackageInfo packageInfo = null;
        String requestString = intent.getStringExtra(REQUEST_STRING);

         String strVersionNumber="";
        String strLocalVersionNumber = "";
        String version_code ="";
        try {
            strVersionNumber = Jsoup
                    .connect(requestString)
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(7)
                    .ownText();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            strVersionNumber=strVersionNumber.replace(".","");
             version_code = String.valueOf(packageInfo.versionCode);
            strLocalVersionNumber = packageInfo.versionName;
            System.out.println("version no " + strVersionNumber);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainMenuFragment.MyWebReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_MESSAGE, strVersionNumber);
        sendBroadcast(broadcastIntent);

    }

}
