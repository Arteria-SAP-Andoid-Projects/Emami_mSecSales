package com.arteriatech.emami.common;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.arteriatech.mutils.upgrade.ApplicationLifecycleHandler;
import com.sap.mobile.lib.configuration.Preferences;
import com.sap.mobile.lib.parser.IODataSchema;
import com.sap.mobile.lib.parser.Parser;
import com.sap.mobile.lib.parser.ParserException;
import com.sap.mobile.lib.request.ConnectivityParameters;
import com.sap.mobile.lib.request.RequestManager;
import com.sap.mobile.lib.supportability.Logger;

public class MSFAApplication extends Application {

    private static final String TAG = MSFAApplication.class.getName();

    public static RequestManager mRequestManager = null;
    public Preferences mPreferences = null;
    private Logger mLogger = null;
    private ConnectivityParameters mConnectivityParameters = null;
    private Parser mParser = null;
    private IODataSchema mSchema = null;

    String username = "", password = "";

    public static Context appContext = null;

    private final static int NUMBER_OF_THREADS = 3;

    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);
        getParameters(username, password);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void getParameters(String username, String password) {
        // TODO Auto-generated method stub
        mLogger = new Logger();

        //CreateOperation Connectivity Parameters

        mConnectivityParameters = new ConnectivityParameters();
        mConnectivityParameters.setLanguage(this.getResources()
                .getConfiguration().locale.getLanguage());
        mConnectivityParameters.enableXsrf(true);

        mConnectivityParameters.setUserName(username);
        mConnectivityParameters.setUserPassword(password);

        //CreateOperation Preferences

        mPreferences = new Preferences(this, mLogger);


        try {
            mParser = new Parser(mPreferences, getLogger());
        } catch (ParserException e) {
            mLogger.e(TAG, Constants.ErrorInParser, e);
        }

    }

    public Logger getLogger() {
        return mLogger;
    }

    /**
     * It returns the only  instance of Preferences for the lifetime of the application
     * @return Preferences
     */


    /**
     * @param username
     */
    public void setUsername(String username) {
        mConnectivityParameters.setUserName(username);
    }

    /**
     * @return String username
     */
    public String getUsername() {
        return mConnectivityParameters.getUserName();
    }


    /**
     * It creates only one instance of RequestManager for the lifetime of the application
     *
     * @return RequestManager
     */
    public RequestManager getRequestManager() {
        if (mRequestManager == null) {
            mRequestManager = new RequestManager(mLogger, mPreferences,
                    mConnectivityParameters, NUMBER_OF_THREADS);
        }
        return mRequestManager;
    }


    /**
     * @return
     */
    public Parser getParser() {
        return this.mParser;
    }


    public IODataSchema getODataSchema() {
        return mSchema;
    }


}
