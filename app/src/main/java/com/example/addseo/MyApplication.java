package com.example.addseo;

import android.app.Application;

import com.onesignal.OneSignal;

public class MyApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "a031d0ab-f9ee-4ce2-ae4c-e0f744bf3b4f"; // Usa tu App ID real

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar OneSignal
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
