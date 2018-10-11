package com.kimascend.light.utils;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.sevice.TelinkLightService;
import com.telink.TelinkApplication;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.EventListener;

/**
 * 监听UI生命周期处理mesh事件的监听和移除
 */
public class MeshEventManager {
    private MeshEventManager() {
    }

    public static void bindListenerForLightSetting(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
        new LightSettingEventObserver(lifecycleOwner, eventListener, application);
    }

    public static void bindListenerForAddLamp(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
        new AddLampEventObserver(lifecycleOwner, eventListener, application);
    }

    public static void bindListenerForHome(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
        new HomeEventObserver(lifecycleOwner, eventListener, application);
    }


    //灯具控制的事件监听
    private static class LightSettingEventObserver implements DefaultLifecycleObserver {
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
    private static class AddLampEventObserver implements DefaultLifecycleObserver {
        private static final String TAG = "AddLampEventObserver";
        private final EventListener<String> listener;
        private final TelinkApplication application;

         AddLampEventObserver(LifecycleOwner lifecycleOwner, EventListener<String> listener, TelinkApplication application) {
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


    private static class HomeEventObserver implements DefaultLifecycleObserver {
        private static final String TAG = HomeEventObserver.class.getSimpleName();
        private final EventListener<String> listener;
        private final TelinkApplication application;

         HomeEventObserver(LifecycleOwner lifecycleOwner, EventListener<String> listener, TelinkApplication application) {
            lifecycleOwner.getLifecycle().addObserver(this);
            this.listener = listener;
            this.application = application;

        }

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            SmartLightApp.INSTANCE().doInit();
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            addEventListener();
        }

        private void addEventListener() {
            application.addEventListener(DeviceEvent.STATUS_CHANGED, listener);
            application.addEventListener(NotificationEvent.GET_TIME, listener);
            application.addEventListener(NotificationEvent.GET_ALARM, listener);
            application.addEventListener(NotificationEvent.ONLINE_STATUS, listener);
            application.addEventListener(ServiceEvent.SERVICE_CONNECTED, listener);
            application.addEventListener(ServiceEvent.SERVICE_DISCONNECTED, listener);
            application.addEventListener(MeshEvent.OFFLINE, listener);
            application.addEventListener(MeshEvent.ERROR, listener);
        }


        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            application.removeEventListener(listener);

        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "onDestroy: ");
            SmartLightApp.INSTANCE().doDestroy();
        }

    }

}
