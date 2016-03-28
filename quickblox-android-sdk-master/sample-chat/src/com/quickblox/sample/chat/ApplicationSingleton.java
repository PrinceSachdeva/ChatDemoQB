package com.quickblox.sample.chat;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.quickblox.core.QBSettings;

import vc908.stickerfactory.StickersManager;

public class ApplicationSingleton extends Application {
    private static final String TAG = ApplicationSingleton.class.getSimpleName();

    public static final String APP_ID = "37725";
    public static final String AUTH_KEY = "9HcKM7GdwQGq4UC";
    public static final String AUTH_SECRET = "KCcTXUpVat7Mq9P";
    public static final String ACCOUNT_KEY = "vpmhXwpR4sqXTFiznzV9";

    public static final String STICKER_API_KEY = "847b82c49db21ecec88c510e377b452c";

    public static final String USER_LOGIN = "akash";
    public static final String USER_PASSWORD = "akashsingla";
   /*public static final String USER_LOGIN = "vishnu";
    public static final String USER_PASSWORD = "vishnuanand";*/

    private static ApplicationSingleton instance;
    public static ApplicationSingleton getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        instance = this;

        // Initialise QuickBlox SDK
        //
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

       // QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);


        // Initialise Stickers sdk
        //
        StickersManager.initialize(STICKER_API_KEY, this);
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
