package com.kimascend.light.utils;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.Log;

import com.telink.TelinkApplication;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.util.EventListener;

/**
 * 监听UI生命周期处理mesh事件的监听和移除
 */
public class MeshEventManager {
    private MeshEventManager() {}

    public static void bindListenerForLightSetting(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
         new LightSettingEventObserver(lifecycleOwner,eventListener,application);
    }

    public static void bindListenerForAddLamp(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
         new AddLampEventObserver(lifecycleOwner,eventListener,application);
    }


    //灯具控制的事件监听
    private static class LightSettingEventObserver implements DefaultLifecycleObserver{
        private final EventListener<String> eventListener;
        private final TelinkApplication application;

        private LightSettingEventObserver(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
            lifecycleOwner.getLifecycle().addObserver(this);
            this.eventListener = eventListener;
            this.application = application;
        }


        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            application.addEventListener(NotificationEvent.ONLINE_STATUS, eventListener);
            application.addEventListener(DeviceEvent.STATUS_CHANGED, eventListener);
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            application.removeEventListener(eventListener);
        }
    }
    
    /*添加设备的时候对蓝牙事件的监听*/
    private static class AddLampEventObserver implements DefaultLifecycleObserver{
        private static final String TAG = "AddLampEventObserver";
        private final EventListener<String> listener;
        private final TelinkApplication application;

        public AddLampEventObserver(LifecycleOwner lifecycleOwner, EventListener<String> listener, TelinkApplication application) {
            lifecycleOwner.getLifecycle().addObserver(this);
            this.listener = listener;
            this.application = application;
        }

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            application.addEventListener(LeScanEvent.LE_SCAN, listener);
            application.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, listener);
//        application.addEventListener(LeScanEvent.LE_SCAN_COMPLETED, listener);
            application.addEventListener(DeviceEvent.STATUS_CHANGED, listener);
            application.addEventListener(MeshEvent.UPDATE_COMPLETED, listener);
            application.addEventListener(MeshEvent.OFFLINE, listener);
            application.addEventListener(MeshEvent.ERROR, listener);
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "onDestroy: ");
            application.removeEventListener(listener);

        }
    }
    
}
