package com.kimascend.light.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kimascend.light.R;
import com.kimascend.light.common.NavigatorController;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.utils.BundleConstant;
import com.kimascend.light.utils.Bundler;

/**
 *
 */
public class DeviceActivity extends AppCompatActivity {
    private NavigatorController navigatorController;
    public static final String ACTION_ADD_DEVICE = "action_add_device";

    public static final int NAVIGATE_TO_ADD_LAMP = 1;
    public static final int NAVIGATE_TO_ADD_HUB = 2;
    public static final int FINISH = 3;

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
        setContentView(R.layout.activity_device);
        setUpToolbar();
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate();
        }

    }

//    定制的标题栏 为了显示白色的text
    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener((view)->finish());
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
