package com.kimascend.light.device;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kimascend.light.R;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.common.NavigatorController;
import com.kimascend.light.utils.BundleConstant;
import com.kimascend.light.utils.Bundler;

/**
 * 设备页面 包含灯具和网关的添加等
 */
public class DeviceActivity extends AppCompatActivity {
    private NavigatorController navigatorController;
    public static final String ACTION_ADD_DEVICE = "action_add_device";

    public static final int NAVIGATE_TO_ADD_LAMP = 1;
    public static final int NAVIGATE_TO_ADD_HUB = 2;
    public static final int FINISH = 3;

    public static void start(Context context, String action,int address, int brightness, int status) {
        Intent intent = new Intent(context, DeviceActivity.class);
        intent.putExtra("action", action);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ADDRESS, address)
                .put(BundleConstant.BRIGHTNESS, brightness)
                .put(BundleConstant.STATUS, status).end());
        context.startActivity(intent);
    }

    public static void start(Context context, String action, Lamp lamp) {
        Intent intent = new Intent(context, DeviceActivity.class);
        intent.putExtra("action", action);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.LAMP, lamp)
                .end());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate();
        }
//        DeviceViewModel viewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
//        subscribeUI(viewModel);

    }

    private void handleNavigate() {
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getStringExtra("action");
            switch (action) {
                case ACTION_ADD_DEVICE:
                    navigatorController.navigateToAddLamp();
                    break;

            }
        }
    }


}
