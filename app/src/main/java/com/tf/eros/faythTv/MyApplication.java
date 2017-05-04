package com.tf.eros.faythTv;

import android.app.Application;
import android.util.Log;

import com.longtailvideo.jwplayer.cast.CastManager;

public class MyApplication extends Application {

    public static String PACKAGE_NAME;

    @Override
    public void onCreate() {
        super.onCreate();

        PACKAGE_NAME = getApplicationContext().getPackageName();
        Log.d("pkg name ->",PACKAGE_NAME);
        // Initialize the CastManager.
        // The CastManager must be initialized in the Application's context to prevent
        // issues with garbage collection.
        CastManager.initialize(this);
    }
}
