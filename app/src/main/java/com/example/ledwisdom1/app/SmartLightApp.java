package com.example.ledwisdom1.app;

import android.util.Log;

import com.example.ledwisdom1.sevice.TelinkLightService;
import com.telink.TelinkApplication;
import com.telink.bluetooth.TelinkLog;
import com.telink.crypto.AES;

/**
 * xiaolin.li@kimascend.cn
 * OPPO和VIVO手机在电量低时不能启动service
 */
public final class SmartLightApp extends TelinkApplication {
    private static final String TAG = SmartLightApp.class.getSimpleName();
    private AppExecutors mAppExecutors;
    //是否使用蓝牙
    private boolean isBlueTooth=true;
    private static SmartLightApp sLightApp;
    public static SmartLightApp INSTANCE() {
        return sLightApp;
    }
    public AppExecutors appExecutors() {
        return mAppExecutors;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sLightApp = this;
        doInit();
        AdvanceStrategy.setDefault(new MySampleAdvanceStrategy());
        mAppExecutors=new AppExecutors();
    }


    public boolean isBlueTooth() {
        return isBlueTooth;
    }

    public void setBlueTooth(boolean blueTooth) {
        isBlueTooth = blueTooth;
    }


    @Override
    public void doInit() {
        String fileName = "telink-";
        fileName += System.currentTimeMillis();
        fileName += ".log";
        TelinkLog.LOG2FILE_ENABLE = false;
        TelinkLog.onCreate(fileName);
        super.doInit();
        AES.Security = true;
        //启动LightService
        this.startLightService(TelinkLightService.class);
    }

    @Override
    public void doDestroy() {
        TelinkLog.onDestroy();
        super.doDestroy();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate: ");
    }






}