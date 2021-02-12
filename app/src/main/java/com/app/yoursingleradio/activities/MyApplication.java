package com.app.yoursingleradio.activities;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.app.yoursingleradio.utilities.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MyApplication extends Application {

    private static FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        OneSignal.startInit(this)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}