package com.noe.rxjava.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.noe.rxjava.BuildConfig;
import com.noe.rxjava.data.LocationService;

import timber.log.Timber;

/**
 * Created by 58 on 2016/7/14.
 * 应用初始化
 */
public class RxApplication extends Application {

    public LocationService locationService;

    private static RxApplication instance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        locationService = new LocationService(getApplicationContext());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static RxApplication getInstance() {
        return instance;
    }

    public static int getWidth(){
        DisplayMetrics displayMetrics = instance.getApplicationContext().getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

}
