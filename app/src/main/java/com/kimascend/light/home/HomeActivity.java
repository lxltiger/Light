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
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.databinding.ContentMainBinding;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.sevice.TelinkLightService;
import com.kimascend.light.utils.LightCommandUtils;
import com.kimascend.light.utils.MeshEventManager;
import com.kimascend.light.utils.SnackbarUtils;
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
public class HomeActivity extends AppCompatActivity  {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private NavigatorController navigatorController;
    private HomeViewModel viewModel;
    private ContentMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.content_main);
        binding.rgMainGroup.setOnCheckedChangeListener(this::onCheckedChanged);
        setUpToolbar();
        addBlueToothStatusReceiver();
        navigatorController = new NavigatorController(this, R.id.fl_main_container);
        if (savedInstanceState == null) {
            navigatorController.navigateToDevice();
        }
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        MeshEventManager.bindListenerForHome(this,viewModel::callBack,SmartLightApp.INSTANCE());
        viewModel.snackbarMessage.observe(this, new SnackbarMessage.SnackbarObserver() {
            @Override
            public void onNewMessage(int snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(binding.getRoot(), getString(snackbarMessageResourceId));
            }
        });
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
        viewModel.autoConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requireBlueTooth();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TelinkLightService.Instance().disableAutoRefreshNotify();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBlueToothStatusReceiver);
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
