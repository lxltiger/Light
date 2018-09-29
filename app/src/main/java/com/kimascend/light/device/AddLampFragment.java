package com.kimascend.light.device;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.Config;
import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.common.BindingAdapters;
import com.kimascend.light.databinding.FragmentAddLampBinding;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.model.Light;
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
import com.telink.util.EventListener;

import static com.kimascend.light.common.BindingAdapters.ADD;
import static com.kimascend.light.common.BindingAdapters.ADDED;
import static com.kimascend.light.utils.ToastUtil.showToast;

/**
 * 扫面添加灯具页面
 * 扫描之前需要开启auto connect
 * 通过配置LeScanParameters 的scanMode（true）来逐个扫描自动修改mesh 或scanMode（false）扫描当前mesh所有设备来手动修改 ，我们使用后一种
 * <p>
 * 现在只扫描设备来显示 限制15秒时间 超过这个时间停止扫描至用户手动重扫
 * 如果扫描过程中用户点击重扫 停止当前的扫描 清空列表 重新开始15秒扫描添加
 */
public class AddLampFragment extends Fragment implements EventListener<String> {
    public static final String TAG = AddLampFragment.class.getSimpleName();

    /**
     * 每次扫描持续时间
     */
    private static final int SCANNING_SPAN = 15 * 1000;
    /**
     * 是否扫描 一进来就扫描 所以默认为true  也是停止扫描的标记
     */
    private boolean scanning = true;

    /**
     * 新设备适配器
     */
    private AddLampAdapter lampAdapter;
    /**
     * 添加状态
     */
    boolean isAdd = false;

    private Handler handler = new Handler();

    private DeviceViewModel viewModel;

    private SparseIntArray deviceType = new SparseIntArray();

    public static AddLampFragment newInstance() {
        Bundle args = new Bundle();
        AddLampFragment fragment = new AddLampFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        inflater.inflate(R.menu.fragment_add_lamp, menu);
        if (!scanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.menu_scan:
                // 清空设备列表
                lampAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        getActivity().invalidateOptionsMenu();
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        deviceType.put(Config.LAMP_RGB, Config.LAMP_TYPE);
        deviceType.put(Config.LAMP, Config.LAMP_TYPE);
        deviceType.put(Config.SOCKET, Config.SOCKET_TYPE);
        deviceType.put(Config.PANEL, Config.PANEL_TYPE);
        addEventListener();

    }

    private void addEventListener() {
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();

        smartLightApp.addEventListener(LeScanEvent.LE_SCAN, this);
        smartLightApp.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
//        smartLightApp.addEventListener(LeScanEvent.LE_SCAN_COMPLETED, this);
        smartLightApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        smartLightApp.addEventListener(MeshEvent.UPDATE_COMPLETED, this);
        smartLightApp.addEventListener(MeshEvent.OFFLINE, this);
        smartLightApp.addEventListener(MeshEvent.ERROR, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddLampBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_lamp, container, false);
        lampAdapter = new AddLampAdapter(this::addLamp);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(lampAdapter);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        scanLeDevice(true);

    }

    @Override
    public void onStop() {
        super.onStop();
        scanLeDevice(false);
        lampAdapter.clear();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.removeEventListener(this);
    }

    /**
     * 开始扫描
     * 名称为出厂名 kimascend
     *
     * @param
     */
    private void scanLeDevice(boolean scan) {
        scanning = scan;
        if (scan) {
            LeScanParameters params = LeScanParameters.create();
            params.setMeshName(Config.FACTORY_NAME);
            params.setOutOfMeshName("kick");
            params.setTimeoutSeconds(15);
            params.setScanMode(false);
            TelinkLightService.Instance().startScan(params);
            handler.postDelayed(() -> scanLeDevice(false), SCANNING_SPAN);
        } else {
            handler.removeCallbacksAndMessages(null);
            TelinkLightService.Instance().idleMode(true);
        }
    }


    @Override
    public void performed(Event<String> event) {
        Log.d(TAG, "performed() called with: event = [" + event.getType() + "]");
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                onLeScan((LeScanEvent) event);
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                scanning = false;
                TelinkLightService.Instance().idleMode(true);
//                binding.setIsScanning(false);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                showToast("设备离线");
            case MeshEvent.ERROR:
                showToast(getString(R.string.start_bollue2));

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
        light.mDescription = String.format("%s\n%s", deviceInfo.meshName, deviceInfo.macAddress);
        //用来区分设备类型
        light.type = deviceType.get(deviceInfo.productUUID);
        lampAdapter.addLight(light);

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
                Light light = lampAdapter.getLightByMAC(deviceInfo.macAddress);
                if (light != null) {
                    light.status.set(BindingAdapters.ADDED);
//                    light.raw.meshAddress = deviceInfo.meshAddress;
                    light.raw.meshName = deviceInfo.meshName;
                    light.raw.deviceName = deviceInfo.deviceName;
                    DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
                    Lamp lamp = Lamp.from(light, defaultMesh.id);
                    viewModel.insertDevice(lamp);
                }
                isAdd = false;
            }
            break;
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
            case LightAdapter.STATUS_LOGOUT: {
                Light light = lampAdapter.getLightByMAC(deviceInfo.macAddress);
                //如果A灯添加成功 下一次添加B灯时会收到A灯登出 更新失败的回调 防止状态被修改 判断是否已经修改成功
                if (light != null && light.status.get() != ADDED) {
                    light.status.set(ADD);
                    isAdd = false;
                }
                //已添加的设备是登出状态，meshName已改变，也会扫描到，在此排除
                if (deviceInfo.meshName.equals(Config.FACTORY_NAME)) {
                    showToast("添加失败");
                }
            }
            break;
        }
    }

    /**
     * 添加灯具
     * <p>
     * 配合Databinding 在item_light_add.xml中使用
     * Light 的字段使用Observable 这样只要在这里修改值，相关UI会自动更新
     */
    public void addLamp(Light light) {
        if (light.status.get() == ADD) {
            if (!isAdd) {
                isAdd = true;
                light.raw.meshAddress = viewModel.getMeshAddress();
                light.status.set(BindingAdapters.ADDING);
                LeUpdateParameters params = Parameters.createUpdateParameters();
                params.setOldMeshName(Config.FACTORY_NAME);
                params.setOldPassword(Config.FACTORY_PASSWORD);
                DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
                String meshName = defaultMesh.name;
                String meshPsw = defaultMesh.password;
                params.setNewMeshName(meshName);
                params.setNewPassword(meshPsw);
                params.setUpdateDeviceList(light.raw);
                TelinkLightService.Instance().idleMode(true);
                //加灯
                TelinkLightService.Instance().updateMesh(params);

            } else {
                showToast("正在添加");
            }
        }
    }


}