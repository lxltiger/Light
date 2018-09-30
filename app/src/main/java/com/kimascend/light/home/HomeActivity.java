package com.kimascend.light.home;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.common.NavigatorController;
import com.kimascend.light.databinding.ContentMainBinding;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.sevice.TelinkLightService;
import com.kimascend.light.utils.LightCommandUtils;
import com.kimascend.light.utils.ToastUtil;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 主页含4个UI
 */
public class HomeActivity extends AppCompatActivity implements EventListener<String> {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Handler handler = new Handler();
    private NavigatorController navigatorController;
    private HomeViewModel viewModel;
    private ContentMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.content_main);
        binding.rgMainGroup.setOnCheckedChangeListener(this::onCheckedChanged);
        navigatorController = new NavigatorController(this, R.id.fl_main_container);
        if (savedInstanceState == null) {
            navigatorController.navigateToDevice();
        }
        setUpToolbar();
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.doInit();
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        addBlueToothStatusReceiver();

    }


    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.setTitle(getString(R.string.title_main_device));
    }

    private void addBlueToothStatusReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mBlueToothStatusReceiver, filter);

    }

    private BroadcastReceiver mBlueToothStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: 蓝牙开启");
                        TelinkLightService.Instance().idleMode(true);
//                        autoConnect();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: 蓝牙关闭");
                        break;
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        addEventListener();
        autoConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requireBlueTooth();

    }

    @Override
    protected void onStop() {
        super.onStop();
        removeEventListener();
        TelinkLightService.Instance().disableAutoRefreshNotify();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBlueToothStatusReceiver);
        SmartLightApp.INSTANCE().doDestroy();
        handler.removeCallbacks(null);
        super.onDestroy();
    }

    private void requireBlueTooth() {
        //检查是否支持蓝牙设备
        if (!LeBluetooth.getInstance().isSupport(this)) {
            Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("开启蓝牙，体验智能灯!");
            builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LeBluetooth.getInstance().enable(HomeActivity.this);
                }
            });
            builder.show();
        }
    }

    private void addEventListener() {
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        smartLightApp.addEventListener(NotificationEvent.GET_TIME, this);
        smartLightApp.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        smartLightApp.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        smartLightApp.addEventListener(ServiceEvent.SERVICE_DISCONNECTED, this);
        smartLightApp.addEventListener(MeshEvent.OFFLINE, this);
        smartLightApp.addEventListener(MeshEvent.ERROR, this);
    }

    private void removeEventListener() {
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.removeEventListener(this);
    }

    //    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_scene:
                binding.setTitle(getString(R.string.title_main_scene));
                navigatorController.navigateToSceneList();
                break;
            case R.id.rb_device:
                binding.setTitle(getString(R.string.title_main_device));
                navigatorController.navigateToDevice();
                break;
            case R.id.rb_group:
                binding.setTitle(getString(R.string.title_main_group));
                navigatorController.navigateToGroup();
                break;
            case R.id.rb_alarm:
                binding.setTitle(getString(R.string.title_main_alarm));
                navigatorController.navigateToClockList();
                break;
        }
    }


    @Override
    public void performed(Event<String> event) {
        Log.d(TAG, "event type" + event.getType());
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case NotificationEvent.GET_TIME: {
                NotificationEvent notificationEvent = (NotificationEvent) event;
                Calendar calendar = (Calendar) notificationEvent.parse();
                if (Math.abs(Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis()) > 60 * 1000) {
                    LightCommandUtils.synLampTime();
                }
                String format = DateFormat.getDateTimeInstance().format(calendar.getTimeInMillis());
                Log.d(TAG, format);
                break;
            }
            case MeshEvent.OFFLINE:
                SmartLightApp.INSTANCE().setMeshStatus(-1);
                viewModel.onMeshOff();
                break;
            case MeshEvent.ERROR:
                SmartLightApp.INSTANCE().setMeshStatus(-2);
//               onMeshError((MeshEvent) event);
                ToastUtil.showToast("蓝牙出了问题 重启试试");
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                Log.d(TAG, "performed: connected");
                autoConnect();
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                Log.d(TAG, "performed: disconnected");
//                onServiceDisconnected((ServiceEvent) event);
                break;
        }
    }

    /**
     * 自动重连
     * 使用蓝牙控制灯具之前需要连接mesh
     */
    public void autoConnect() {
        Log.d(TAG, "autoConnect() called");
        if (TelinkLightService.Instance() != null) {
            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {
                //自动重连参数
                Log.d(TAG, "connect");
                DefaultMesh mesh = SmartLightApp.INSTANCE().getDefaultMesh();
                if (null == mesh) {
                    return;
                }
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_CONNECTING);
                String meshName = mesh.name;
                String psw = mesh.password;
                Log.d(TAG, meshName + "--" + psw);
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(meshName);
                connectParams.setPassword(psw);
                connectParams.autoEnableNotification(true);
                //自动重连
                TelinkLightService.Instance().autoConnect(connectParams);
            }

            //刷新Notify参数
            LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(5000);
            //开启自动刷新Notify
            TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }

    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                Log.d(TAG, "connecting success");
                ToastUtil.showToast("连接成功");
                //        获取灯具时间
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_LOGIN);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(LightCommandUtils::getLampTime, 3 * 1000);
                break;
            case LightAdapter.STATUS_CONNECTING:
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_CONNECTING);
//                Toast.makeText(this, "正在连接 " + meshName, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "connecting");
                break;
            case LightAdapter.STATUS_LOGOUT:
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_LOGOUT);
//                Toast.makeText(this, "失去连接 " + meshName, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "disconnect");
                break;
            default:
                break;
        }
    }

    @WorkerThread
    protected void onOnlineStatusNotify(NotificationEvent event) {

        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            Log.d(TAG, meshAddress + "meshAddress:" + brightness);
        }
        viewModel.updateDeviceStatus(notificationInfoList);

    }

    //    加载制定页面，采用隐藏策略，不移处已经初始化的Fragment
   /* private void loadFragment(@Nullable Fragment toFragment) {
        if (toFragment == null) return;

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (toFragment != mCurrentFragment) {
            if (!toFragment.isAdded()) {
                supportFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.fl_main_container, toFragment).commit();
            } else {
                supportFragmentManager.beginTransaction().hide(mCurrentFragment).show(toFragment).commit();
            }
//            记录当前页面
            mCurrentFragment = toFragment;
        }

    }*/
    //记录当前系统时间
    private long mExitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }


}
