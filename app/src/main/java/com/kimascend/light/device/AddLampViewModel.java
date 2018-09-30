package com.kimascend.light.device;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.common.BindingAdapters;
import com.kimascend.light.common.Config;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.model.Light;
import com.kimascend.light.repository.HomeRepository;
import com.kimascend.light.sevice.TelinkLightService;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LeUpdateParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.kimascend.light.common.BindingAdapters.ADD;
import static com.kimascend.light.common.BindingAdapters.ADDED;

/**
 * 扫描之前需要开启auto connect
 * 通过配置LeScanParameters 的scanMode（true）来逐个扫描自动修改mesh 或scanMode（false）扫描当前mesh所有设备来手动修改 ，我们使用后一种
 * <p>
 * 现在只扫描设备来显示 限制15秒时间 超过这个时间停止扫描至用户手动重扫
 */
public class AddLampViewModel extends AndroidViewModel {
    private static final String TAG = AddLampViewModel.class.getSimpleName();
    /**
     * 每次扫描持续时间
     */
    private static final int SCANNING_SPAN = 15 * 1000;
    private Handler handler;
    /**
     * 扫描到的灯具
     */
    private List<Light> lights;
    private final HomeRepository repository;
    private final MutableLiveData<List<Light>> lightsObservable;
    /**
     * 扫描蓝牙设备的状态
     */
    private final MutableLiveData<Boolean> scanningObservable;
    /**
     * 用户弹出消息
     */
    private final SnackbarMessage snackbarMessage;
    /**
     * 添加状态
     */
    private AtomicBoolean isAdding = new AtomicBoolean(false);

    public AddLampViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        handler = new Handler();
        lights = new ArrayList<>();
        lightsObservable = new MutableLiveData<>();
        scanningObservable = new MutableLiveData<>();
        snackbarMessage = new SnackbarMessage();

    }

    LiveData<Boolean> getScanningObservable() {
        return scanningObservable;
    }


    LiveData<List<Light>> getLightObserver() {
        return lightsObservable;
    }

    SnackbarMessage getSnackbarMessage() {
        return snackbarMessage;
    }

    /**
     * 名称为出厂名 kimascend
     *
     * @param scan true-情况已扫描设备，开始扫描 false-停止
     */
    void scanLeDevice(boolean scan) {
        scanningObservable.setValue(scan);
        if (scan) {
            lights.clear();
            lightsObservable.setValue(lights);
            LeScanParameters params = LeScanParameters.create();
            params.setMeshName(Config.FACTORY_NAME);
            params.setOutOfMeshName("out_of_mesh");
            params.setTimeoutSeconds(15);
            //连续扫描
            params.setScanMode(false);
            TelinkLightService.Instance().startScan(params);
            //延时关闭
            handler.postDelayed(() -> scanLeDevice(false), SCANNING_SPAN);
        } else {
            handler.removeCallbacksAndMessages(null);
            TelinkLightService.Instance().idleMode(true);
        }
    }

    //蓝牙事件回调
    void callBack(Event<String> event) {
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                onLeScan((LeScanEvent) event);
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                scanningObservable.setValue(false);
                TelinkLightService.Instance().idleMode(true);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                snackbarMessage.setValue(R.string.mesh_offline);
            case MeshEvent.ERROR:
                snackbarMessage.setValue(R.string.start_bollue2);
                break;
        }
    }

    /**
     * 处理扫描事件
     *
     * @param event
     */
    private void onLeScan(LeScanEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        Log.d(TAG, "DeviceInfo:" + deviceInfo);
        Light light = new Light(deviceInfo);
        light.setDescription(String.format("%s\n%s", deviceInfo.meshName, deviceInfo.macAddress));
        lights.add(light);
        lightsObservable.setValue(lights);
    }

    /**
     * 设备状态变更  主要用来监听mesh网络修改的结果
     *
     * @param event
     */
    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        Log.d(TAG, " onDeviceStatusChanged: " + deviceInfo.toString());
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED: {
                Light light = getLightByMAC(deviceInfo.macAddress);
                if (light != null) {
                    light.status.set(BindingAdapters.ADDED);
                    light.getDeviceInfo().meshName = deviceInfo.meshName;
                    light.getDeviceInfo().deviceName = deviceInfo.deviceName;
                    DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
                    Lamp lamp = Lamp.from(light, defaultMesh.id);
                    repository.insertDevice(lamp);
                }
                isAdding.set(false);
            }
            break;
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
            case LightAdapter.STATUS_LOGOUT: {
                Light light = getLightByMAC(deviceInfo.macAddress);
                //如果A灯添加成功 下一次添加B灯时会收到A灯登出 更新失败的回调 防止状态被修改 判断是否已经修改成功
                if (light != null && light.status.get() != ADDED) {
                    light.status.set(ADD);
                    isAdding.set(false);
                }
                //已添加的设备是登出状态，meshName已改变，也会扫描到，在此排除
                if (deviceInfo.meshName.equals(Config.FACTORY_NAME)) {
                    snackbarMessage.setValue(R.string.fail_to_add_lamp);
                }
            }
            break;
        }
    }

    private Light getLightByMAC(String mac) {
        if (TextUtils.isEmpty(mac)) return null;
        for (Light light : lights) {
            if (mac.endsWith(light.getDeviceInfo().macAddress)) {
                return light;
            }
        }
        return null;
    }

    /**
     * 添加灯具, 一次只能加一个
     * <p>
     * Light 的字段使用Observable 这样只要在这里修改值，相关UI会自动更新
     */
    void addLamp(Light light) {
        Log.d(TAG, "addLamp: ");
        if (isAdding.compareAndSet(false,true)) {
            //当前灯具的addr就是数据库总数加1
            light.getDeviceInfo().meshAddress = repository.getLampsNum() + 1;
            light.status.set(BindingAdapters.ADDING);
            LeUpdateParameters params = Parameters.createUpdateParameters();
            params.setOldMeshName(Config.FACTORY_NAME);
            params.setOldPassword(Config.FACTORY_PASSWORD);
            DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
            String meshName = defaultMesh.name;
            String meshPsw = defaultMesh.password;
            params.setNewMeshName(meshName);
            params.setNewPassword(meshPsw);
            params.setUpdateDeviceList(light.getDeviceInfo());
            TelinkLightService.Instance().idleMode(true);
            //加灯
            TelinkLightService.Instance().updateMesh(params);
        } else {
            snackbarMessage.setValue(R.string.is_adding);
        }
    }

}
